package com.kickass.ifssol.mapper.response;

import com.kickass.ifssol.mapper.IResponseMapper;
import ifs.fnd.ap.Record;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

@Component
public class WorkOrderIssueResponseMapper implements IResponseMapper, Function<Record, XmlObject> {

    @Override
    public XmlObject apply(Record record)  {
           return  null;
    }
}