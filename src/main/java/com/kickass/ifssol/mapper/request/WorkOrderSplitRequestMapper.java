package com.kickass.ifssol.mapper.request;

import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.IResponseMapper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.WorkOrdersDocument;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class WorkOrderSplitRequestMapper implements IResponseMapper, Function<XmlObject, WorkOrdersDocument> {
    private static final Logger LOGGER = LogManager.getLogger(WorkOrderSplitRequestMapper.class);

    private SolNodesRoot solNodeRoot;

    public void setSolNodesRoot(SolNodesRoot solNodesRoot) {
        this.solNodeRoot = solNodesRoot;
    }

    @Override
    public WorkOrdersDocument apply(XmlObject xmlObject) {
        LOGGER.info("WorkOrderSplitRequestMapper apply");
        return null;
    }
}