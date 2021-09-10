package com.kickass.ifssol.mapper;

import com.kickass.ifssol.entity.TxnMapping;
import com.kickass.ifssol.util.reflect.DocTemplate;
import com.kickass.ifssol.util.reflect.DocTemplateMap;
import ifs.fnd.ap.Record;

public interface IMapper<T> {
    public T map(Record record) throws MappingException ;
    public T map(Record record, TxnMapping txnMapping, DocTemplateMap docTemplateMap) throws MappingException;

}
