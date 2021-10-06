package com.kickass.ifssol.mapper;

import com.kickass.ifssol.entity.IfsSolMapping;
import com.kickass.ifssol.entity.TxnMapping;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import com.kickass.ifssol.util.reflect.Reflector;
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
public class GenericDataResponseMapper implements IResponseMapper {

    @Autowired
    private Reflector reflector;

    private static Logger LOGGER = LogManager.getLogger(GenericDataResponseMapper.class);


   // @Override
    public XmlObject map(Record record, TxnMapping txnMapping, DocTemplateMap docTemplateMap) throws MappingException {
        Object rootInstance = docTemplateMap.getRootDocTemplate().getRootInstance();
        List<IfsSolMapping> mappings = txnMapping.getIfsSolMappings();
        for (IfsSolMapping ifsSolMapping : mappings) {
            String soluminaField = ifsSolMapping.getSol();
            String ifsField = ifsSolMapping.getIfs();
            if (soluminaField != null && ifsField != null) {
                createAndPopulateInstance(rootInstance, soluminaField, ifsField, docTemplateMap);
            }
        }
        return null;
    }

    private  void createAndPopulateInstance(Object rootInstance,
                                            String soluminaField,
                                            String ifsField,
                                            DocTemplateMap<String,DocTemplate> docTemplateMap) throws MappingException {
        //SyncLocation.DataArea.Location.Type
        //SyncLocation.DataArea.Location.ID

        String[] pathElementArr = soluminaField.trim().split("\\.");
        populateInstance(rootInstance, pathElementArr, 0, docTemplateMap);
    }

    private void populateInstance(Object parentInstance,
                                  String[] pathElementArray,
                                  int index,
                                  DocTemplateMap<String,DocTemplate> docTemplateMap) throws  MappingException {
        DocTemplate docTemplate = docTemplateMap.get(parentInstance.getClass());
        String currentElement = pathElementArray[index];
        if (docTemplate != null) {
            Object currentInstance = createInstance(parentInstance, docTemplate, currentElement);
            populateInstance(currentInstance, pathElementArray, ++index, docTemplateMap);
        }
        else {
            throw new MappingException("DocTemplate is null for " + currentElement);
        }
    }

    /**
     * Using addNew or some other mechanism
     */
    private Object createInstance(Object parentInstance,
                                DocTemplate docTemplate,
                                String elementName) throws MappingException {
        try {
            String methodName = "addNew" + elementName;
            Method addMethod = docTemplate.getAddMethod(methodName);
            Object instance = addMethod.invoke(parentInstance);
            return instance;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException(e);
        }
    }
}