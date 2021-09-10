package com.kickass.ifssol.mapper;

import com.kickass.ifssol.entity.IfsSolMapping;
import com.kickass.ifssol.entity.SolNode;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import ifs.fnd.ap.Record;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

@Component
public class IfsToSolMapper {
    private static Logger LOGGER = LogManager.getLogger(IfsToSolMapper.class);

    @Autowired
    private TypeConverter typeConverter;

    public XmlObject map(Record record,
                         SolNodesRoot solNodesRoot,
                         DocTemplateMap docTemplateMap) throws MappingException {
        Object rootInstance = docTemplateMap.getRootDocTemplate().getRootInstance();
        List<SolNode> solNodes = solNodesRoot.getSolNodes();
        SolNode parentNode = null; //has no parent
        build(record, parentNode, solNodes, rootInstance, docTemplateMap);

        String xml = rootInstance.toString();
        LOGGER.info("+++++++++++++XML+++++++++++++++++\n");
        LOGGER.info(xml);

        return (XmlObject) rootInstance;
    }

    private void build(Record record, SolNode parentNode, List<SolNode> solNodes, Object parentInstance, DocTemplateMap docTemplateMap)
            throws MappingException{
        if (solNodes == null) {
            return;
        }
        for(SolNode solNode : solNodes)  {
            DocTemplate parentDocTemplate = docTemplateMap.get(parentInstance.getClass());
            Object currentInstance = createInstance(parentInstance,parentDocTemplate, parentNode, solNode);

            if (solNode.getIfsSolMappings() != null) {
                applyMappings(record, solNode, docTemplateMap, currentInstance);
            }
            build(record, solNode, solNode.getSolNodes(), currentInstance, docTemplateMap);

            //if parent is UserArea type , set the child instance on it after child is populated.
            if (parentNode != null && parentNode.getName().equals("UserArea")) {
                setChildOnUserArea(parentInstance, currentInstance);
            }
        }
    }

    private void setChildOnUserArea(Object parentInstance, Object currentInstance) throws MappingException {

        try {
            Method setSet = parentInstance.getClass().getMethod("set", XmlObject.class);
            setSet.invoke(parentInstance, currentInstance);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            throw new MappingException("Mapping failed while setChildOnUserArea ", e);
        }

    }

    private void applyMappings(Record record, SolNode solNode, DocTemplateMap docTemplateMap, Object currentInstance) {
        for(IfsSolMapping ifsSolMapping : solNode.getIfsSolMappings()) {
            String solFieldName = ifsSolMapping.getSol();
            String ifsFieldName = ifsSolMapping.getIfs();
            String defaultIfs = ifsSolMapping.getDefaultIfs();
            String valueProviderClassName = ifsSolMapping.getValueProviderClass();
            try {
                DocTemplate docTemplate = docTemplateMap.get(currentInstance.getClass());
                Object value = record.findValue(ifsFieldName.toUpperCase());
                Method setMethod = docTemplate.getSetMethodByVarName(solFieldName);
                int cnt = setMethod.getParameterCount();
                if (cnt == 1) {
                    Class[] parameterTypes = setMethod.getParameterTypes();

                    Object convertedValue = typeConverter.convert(value,defaultIfs, valueProviderClassName,parameterTypes[0]);
                    setMethod.invoke(currentInstance, convertedValue);
                }
            }
            catch (Exception ex) {
                LOGGER.error("Exception applying conversion for " + solFieldName + " from " + ifsFieldName);
            }
        }
    }

    /**
     * Using addNew or some other mechanism
     */
    private Object createInstance(Object parentInstance,
                                  DocTemplate docTemplate,
                                  SolNode parentNode,
                                  SolNode solNode) throws MappingException {
        //Handle addNew
        //if the parent solNode is of type UserArea, it will be handled differently.
        try {
            if (parentNode != null && "UserArea".equals(parentNode.getName())) {
                return createUsingFactoryMethod(parentInstance, docTemplate,
                        parentNode, solNode);
            }

            String methodName = "addNew" + solNode.getName();
            Method addMethod = docTemplate.getAddMethod(methodName);
            Object instance = addMethod.invoke(parentInstance);
            return instance;

        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }


    private Object createUsingFactoryMethod(Object parentInstance,
                                            DocTemplate parentDocTemplate,
                                            SolNode parentNode,
                                            SolNode solNode) throws MappingException {
        try {
            DocTemplateMap docTemplateMap = parentDocTemplate.getReflector().process(solNode.getClassType(), false);
            //DocTemplate childDocTemplate = new DocTemplate(solNode.getClassType(), parentDocTemplate.getReflector(), parentDocTemplate.getDocTemplateMap());
            DocTemplate childDocTemplate = docTemplateMap.getByClassName(solNode.getClassType());

            Object instance = childDocTemplate.createInstanceUsingFactoryMethod(solNode.getClassType());
            //Method[] methods = parentInstance.getClass().getMethods();

            Method setSet = parentInstance.getClass().getMethod("set", XmlObject.class);
            setSet.invoke(parentInstance, instance);
            return instance;
        }
        catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }
}
