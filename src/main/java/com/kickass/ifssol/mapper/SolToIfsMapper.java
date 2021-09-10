package com.kickass.ifssol.mapper;

import com.kickass.ifssol.entity.IfsSolMapping;
import com.kickass.ifssol.entity.SolNode;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import ifs.fnd.ap.Record;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.ProcessType;
import org.springframework.beans.MethodInvocationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SolToIfsMapper {
    private static Logger LOGGER = LogManager.getLogger(SolToIfsMapper.class);

    @Autowired
    private TypeConverter typeConverter;

    public Record map(Object parentInstance,
                      SolNodesRoot solNodesRoot,
                      DocTemplateMap docTemplateMap) throws MappingException {
        List<SolNode> solNodes = solNodesRoot.getSolNodes();
        Record record = new Record();
        applyMapping(parentInstance, solNodes, record, docTemplateMap);
        return null;
    }

    private void applyMapping(Object parentInstance, List<SolNode> solNodes , Record record,
                              DocTemplateMap docTemplateMap) throws MappingException  {
        if (parentInstance == null) {
            return;
        }
        DocTemplate parentDocTemplate = docTemplateMap.get(parentInstance.getClass());
        Map<Class,Integer> instanceCounterMap = new HashMap<Class,Integer>();
        if (solNodes == null) {
            return;
        }
        for(SolNode solNode : solNodes) {
            Method getGetMethod = getGetMethod(parentDocTemplate, solNode);

            Object currentInstance=null;
            try {
                currentInstance =  getInstance(getGetMethod, parentInstance, instanceCounterMap);
                //currentInstance = getGetMethod.invoke(parentInstance);

                List<IfsSolMapping> ifsSolMappings = solNode.getIfsSolMappings();
                if (ifsSolMappings != null) {
                    for (IfsSolMapping ifsSolMapping : ifsSolMappings) {
                        setValueOnRecord(record, ifsSolMapping, currentInstance, docTemplateMap);
                        //get it from current instance
                        //add it to the record.
                    }
                }
                applyMapping(currentInstance, solNode.getSolNodes(), record, docTemplateMap);
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
    private Object getInstance(Method getMethod, Object parentInstance, Map<Class,Integer> instanceCounterMap) {
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
        String ifs = ifsSolMapping.getIfs();
        String sol = ifsSolMapping.getSol();
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
}
