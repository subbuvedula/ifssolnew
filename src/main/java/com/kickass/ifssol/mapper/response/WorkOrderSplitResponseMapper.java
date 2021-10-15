package com.kickass.ifssol.mapper.response;

import com.kickass.ifssol.mapper.IResponseMapper;
import ifs.fnd.ap.Record;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.function.Function;

@Component
public class WorkOrderSplitResponseMapper implements IResponseMapper, Function<Record, XmlObject> {

    @Override
    public XmlObject apply(Record record)  {
        AcknowledgeSplitWIPDocument doc = AcknowledgeSplitWIPDocument.Factory.newInstance();
        AcknowledgeSplitWIPType processSplitWIPType = doc.addNewAcknowledgeSplitWIP();
        AcknowledgeSplitWIPDataAreaType dataAreaType = processSplitWIPType.addNewDataArea();
        SplitWIPType splitWIPType = dataAreaType.addNewSplitWIP();
        SplitWIPSourceType splitWIPSourceType = splitWIPType.addNewSplitWIPSource();
        ProductionOrderReferenceType productionOrderReferenceType = splitWIPSourceType.addNewProductionOrderReference();
        DocumentIDType documentIDType = productionOrderReferenceType.addNewDocumentID();
        IdentifierType identifierType = documentIDType.addNewID();
        identifierType.setStringValue((String)record.findValue("CF$_SOURCE_ORDER_NO"));

        SerializedLotType serializedLotType = productionOrderReferenceType.addNewSerializedLot();
        serializedLotType.addNewSerialNumber().setStringValue((String)record.findValue("CF$_SOURCE_SERIAL_NO")); //set

        SplitWIPDestinationType splitWIPDestinationType1 = splitWIPType.addNewSplitWIPDestination();
        SplitWIPDestinationType splitWIPDestinationType2 = splitWIPType.addNewSplitWIPDestination();

        ProductionOrderReferenceType productionOrderReferenceType1 = splitWIPDestinationType1.addNewProductionOrderReference();
        productionOrderReferenceType1.addNewDocumentID().addNewID().setStringValue((String)record.findValue("CF$_SPLIT1_ORDER_NO")); //ID
        productionOrderReferenceType1.addNewSerializedLot().addNewLot().addNewSerialNumber().setStringValue((String)record.findValue("CF$_SPLIT1_SERIAL_NO"));

        splitWIPDestinationType1.addNewItemQuantity().setBigDecimalValue((BigDecimal) record.findValue("CF$_SPLIT1_QTY")); //Item Quantity

        ProductionOrderReferenceType productionOrderReferenceType2 = splitWIPDestinationType2.addNewProductionOrderReference();
        productionOrderReferenceType2.addNewDocumentID().addNewID().setStringValue((String)record.findValue("CF$_SPLIT2_ORDER_NO")); //ID
        productionOrderReferenceType2.addNewSerializedLot().addNewLot().addNewSerialNumber().setStringValue((String)record.findValue("CF$_SPLIT2_SERIAL_NO"));
        splitWIPDestinationType2.addNewItemQuantity().setBigDecimalValue(new BigDecimal((Double)record.findValue("CF$_SPLIT2_QTY"))); //Item Quantity
        return  doc;
    }
}