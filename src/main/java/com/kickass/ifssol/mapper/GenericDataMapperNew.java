package com.kickass.ifssol.mapper;

import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import ifs.fnd.ap.Record;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GenericDataMapperNew {
    private static Logger LOGGER = LogManager.getLogger(GenericDataMapperNew.class);

    @Autowired
    private TypeConverter typeConverter;

    @Autowired
    private SolToIfsMapper solToIfsMapper;

    @Autowired
    private IfsToSolMapper ifsToSolMapper;


    public XmlObject mapToSol(Record record, SolNodesRoot solNodesRoot, DocTemplateMap docTemplateMap) throws MappingException {
        return ifsToSolMapper.map(record, solNodesRoot, docTemplateMap);
    }

    public Record mapToIFS(Object parentInstance,
                           SolNodesRoot solNodesRoot,
                           DocTemplateMap docTemplateMap)
            throws MappingException  {
        return solToIfsMapper.map(parentInstance, solNodesRoot, docTemplateMap);
    }

}