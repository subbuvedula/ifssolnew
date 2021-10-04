package com.kickass.ifssol.mapper;

import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.entity.IfsSolMapping;
import com.kickass.ifssol.entity.SolNode;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import ifs.fnd.ap.BindVariableDirection;
import ifs.fnd.ap.Record;
import ifs.fnd.ap.RecordAttribute;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openapplications.oagis.x9.ProcessType;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolToIfsMapper {
    private static Logger LOGGER = LogManager.getLogger(SolToIfsMapper.class);

    @Autowired
    private TypeConverter typeConverter;

    private CommonDataAccessor commonDataAccessor;

    @Autowired
    public SolToIfsMapper(CommonDataAccessor commonDataAccessor) {
        this.commonDataAccessor = commonDataAccessor;
    }

    public void map(Object parentInstance,
                      SolNodesRoot solNodesRoot,
                      DocTemplateMap docTemplateMap,
                    Record record) throws MappingException {

        List<SolNode> solNodes = solNodesRoot.getSolNodes();
        solNodesRoot.getUpdateStatement();
        //Record record = new Record();
        applyMapping(parentInstance, solNodes, record, docTemplateMap,solNodesRoot);
        System.out.println("Record : " + record);
        //return null;
    }

    private void applyMapping(Object parentInstance, List<SolNode> solNodes , Record record,
                              DocTemplateMap docTemplateMap, SolNodesRoot solNodesRoot) throws MappingException  {
        if (parentInstance == null) {
            return;
        }
        DocTemplate parentDocTemplate = docTemplateMap.get(parentInstance.getClass());
        Map<Class,Integer> instanceCounterMap = new HashMap<Class,Integer>();
        if (solNodes == null) {
            return;
        }
        for(SolNode solNode : solNodes) {
            solNode.setSolNodesRoot(solNodesRoot);
            Method getGetMethod = getGetMethod(parentDocTemplate, solNode);
            if  (solNode.isMultiple()) {
                System.out.println("Multiple true");
            }
            List list=null;
            try {
                list =  getInstance(getGetMethod, parentInstance, instanceCounterMap);
                //currentInstance = getGetMethod.invoke(parentInstance);

                List<IfsSolMapping> ifsSolMappings = solNode.getIfsSolMappings();
                for(Object currentInstance : list) {
                    if (ifsSolMappings != null) {
                        for (IfsSolMapping ifsSolMapping : ifsSolMappings) {
                            ifsSolMapping.setSolNode(solNode);
                            setValueOnRecord(record, ifsSolMapping, currentInstance, docTemplateMap);
                            //get it from current instance
                            //add it to the record.
                        }
                    }
                    applyMapping(currentInstance, solNode.getSolNodes(), record, docTemplateMap, solNodesRoot);
                }
            }
            catch (Exception ex) {
                LOGGER.error("mapping failed for " + solNode.getName(), ex);
            }
        }
    }

    private void applyMappingOld(Object parentInstance, List<SolNode> solNodes , Record record,
                              DocTemplateMap docTemplateMap, SolNodesRoot solNodesRoot) throws MappingException  {
        if (parentInstance == null) {
            return;
        }
        DocTemplate parentDocTemplate = docTemplateMap.get(parentInstance.getClass());
        Map<Class,Integer> instanceCounterMap = new HashMap<Class,Integer>();
        if (solNodes == null) {
            return;
        }
        for(SolNode solNode : solNodes) {
            solNode.setSolNodesRoot(solNodesRoot);
            Method getGetMethod = getGetMethod(parentDocTemplate, solNode);

            Object currentInstance=null;
            try {
                currentInstance =  getInstance(getGetMethod, parentInstance, instanceCounterMap);
                //currentInstance = getGetMethod.invoke(parentInstance);


                List<IfsSolMapping> ifsSolMappings = solNode.getIfsSolMappings();

                if (ifsSolMappings != null) {
                    for (IfsSolMapping ifsSolMapping : ifsSolMappings) {
                        ifsSolMapping.setSolNode(solNode);
                        setValueOnRecord(record, ifsSolMapping, currentInstance, docTemplateMap);
                        //get it from current instance
                        //add it to the record.
                    }
                }
                applyMapping(currentInstance, solNode.getSolNodes(), record, docTemplateMap, solNodesRoot);
            }
            catch (Exception ex) {
                LOGGER.error("mapping failed for " + solNode.getName(), ex);
            }
        }
    }
    private Class determineTheListType(Method method, Object parentInstance) {
        String methodName = method.getName();
        methodName = methodName.replace("get", "addNew");
        methodName = methodName.replace("List", "");

        Method[] methods = parentInstance.getClass().getMethods();
        for(Method m : methods) {
            if (m.getName().equals(methodName)) {
                Class returnType = m.getReturnType();
                return returnType;
            }
        }
        return null;
    }

    private List getInstance(Method getMethod, Object parentInstance, Map<Class,Integer> instanceCounterMap) {
        List list = new ArrayList();
        try {

            if (parentInstance instanceof ProcessType) {
                String s = "";
            }
            String getMethodName = getMethod.getName(); //getActionCriteriaList
            Class returnType = getMethod.getReturnType();

            Object returningInstance = getMethod.invoke(parentInstance);
            if (returningInstance instanceof  List) {
                list = (List) returningInstance;
            }
            else {
                list.add(returningInstance);
            }
        }
        catch (Exception ex) {
            LOGGER.error("Could not invoke the method " + getMethod.getName() + " On " + parentInstance.getClass().getName(), ex);
        }

        return list;
    }


    private Object getInstanceOld(Method getMethod, Object parentInstance, Map<Class,Integer> instanceCounterMap) {
        try {
            if (parentInstance instanceof ProcessType) {
                String s = "";
            }
            String getMethodName = getMethod.getName(); //getActionCriteriaList
            Class returnType = getMethod.getReturnType();

            Object returningInstance = getMethod.invoke(parentInstance);
            if (returningInstance instanceof List) {
                Integer count = instanceCounterMap.get(determineTheListType(getMethod, parentInstance));

                if (count == null) {
                    count = 0;
                }
                List instances = (List)returningInstance;

                Object actualInstance = instances.get(count);

                Class actualType = returnType.getComponentType();
                instanceCounterMap.put(actualType, count++);
                return actualInstance;
            } else {
                return returningInstance;
            }
        }
        catch (Exception ex) {
            LOGGER.error("Could not invoke the method " + getMethod.getName() + " On " + parentInstance.getClass().getName(), ex);
            return null;
        }
    }

    /**
     * Get the simple Getter, if that fails it may be an get<Object>Array method
     * @param parentDocTemplate
     * @param solNode
     * @return
     */
    private Method getGetMethod(DocTemplate parentDocTemplate, SolNode solNode) {
        Method getGetMethod = parentDocTemplate.getGetMethod("get" + solNode.getName());
        if (getGetMethod == null) {
            getGetMethod = parentDocTemplate.getGetMethod("get" + solNode.getName() + "List");
        }
        return getGetMethod;
    }

    private String makeFirstLetterCap(String val) {
        if (StringUtils.isEmpty(val)) {
            return  val;
        }

        String fistLetter = (val.charAt(0) + "").toUpperCase();
        String remaining = val.substring(1,val.length());
        return fistLetter + remaining;
    }

    /**
     * If Sol is not specified, derive from the
     */
    private void deriveSol() {

    }

    private void setValueOnRecord(Record record, IfsSolMapping ifsSolMapping,
                                  Object currentInstance,
                                  DocTemplateMap docTemplateMap) {

        SolNodesRoot.FieldMegeStrategy fieldMegeStrategy = ifsSolMapping.getFieldMergeStrategyEnum();

        String ifs = ifsSolMapping.getIfs();
        String sol = ifsSolMapping.getSol();
        String directionStr = ifsSolMapping.getDirection();
        if (currentInstance == null) {
            System.out.println("Instance is null ");
            return;
        }
        Object value = null;
        DocTemplate docTemplate = docTemplateMap.get(currentInstance.getClass());
        if (docTemplate != null) {
            Method getMethod = docTemplate.getGetMethod("get" + makeFirstLetterCap(sol));
            if (getMethod != null) {
                try {
                    value = getMethod.invoke(currentInstance);
                    System.out.println("Here:" + currentInstance.getClass().getName() + "." + sol + " : " + value);
                    addValueToRecord( ifs,  value, directionStr, record, fieldMegeStrategy);
                }
                catch(MethodInvocationException | InvocationTargetException | IllegalAccessException ie) {
                    LOGGER.error(getMethod.getName() + " Failed on " + docTemplate.getClazz().getName());
                }
            }
            else {
                LOGGER.error("getMethod " + "get" + sol + " is null for " + currentInstance.getClass().getName() );
            }
        }
        else {
            LOGGER.error("DocTemplate is null for " + currentInstance.getClass().getName());
        }

    }

    private BindVariableDirection getBindVariableDirection(String directionStr) {
        if (BindVariableDirection.IN.toString().equalsIgnoreCase(directionStr)) {
            return BindVariableDirection.IN;
        }
        if (BindVariableDirection.OUT.toString().equalsIgnoreCase(directionStr)) {
            return BindVariableDirection.OUT;
        }
        if (BindVariableDirection.IN_OUT.toString().equalsIgnoreCase(directionStr)) {
            return BindVariableDirection.IN_OUT;
        }
        return  null;
    }

    private void addValueToRecord(String ifsName,
                                  Object value,
                                  String directionStr,
                                  Record record,
                                  SolNodesRoot.FieldMegeStrategy fieldMegeStrategy) {
        BindVariableDirection bindVariableDirection = getBindVariableDirection(directionStr);
        if (bindVariableDirection == null) {
            return;
        }

            if (value instanceof String) {
            Object existingValue = record.findValue(ifsName);

            if (existingValue !=null) {
                if (fieldMegeStrategy == SolNodesRoot.FieldMegeStrategy.APPEND) {
                        RecordAttribute recordAttribute = record.add(ifsName, existingValue.toString() + "," + value.toString());
                        recordAttribute.setBindVariableDirection(bindVariableDirection);
                }
            }
            else  {
                record.add(ifsName, value.toString());
            }
        }
        else if (value instanceof BigDecimal) {
            record.add(ifsName, (BigDecimal) value);
        }
        else if (value instanceof Boolean) {
            record.add(ifsName, (Boolean) value);
        }
        else if (value instanceof Long) {
            record.add(ifsName, (Long) value);
        }
        else if (value instanceof Double) {
            record.add(ifsName, (Double) value);
        }

    }

}
