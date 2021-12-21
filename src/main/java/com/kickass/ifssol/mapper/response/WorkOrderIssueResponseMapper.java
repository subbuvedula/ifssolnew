package com.kickass.ifssol.mapper.response;

import com.kickass.ifssol.mapper.IResponseMapper;
import com.kickass.ifssol.valueprovider.CurrentTimeProvider;
import ifs.fnd.ap.Record;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;



@Component
public class WorkOrderIssueResponseMapper implements IResponseMapper, Function<Record, XmlObject> {

    private CurrentTimeProvider currentTimeProvider = new CurrentTimeProvider();

    @Override
    public XmlObject apply(Record record)  {
       // 4	CF$_ORDER_NO	/WorkOrderHeader /DocumentID[@agencyRole='ERP' ] /ID[@schemeID='WorkOrder' ]	200110	IFS SO#
       // 5	CF$_ALT_ORDER_NO	/WorkOrderHeader /AlternateDocumentID[@agencyRole='ERP' ] /ID[@schemeID='Routing' @schemeAgencyID='SAP' ]		String
       // 6	CF$_CUST_ORDER_NO	/WorkOrderHeader /DocumentReference[@type='CustomerOrder' ] /DocumentID /ID	ABC	String
       // 7	CF$_STATUS	/WorkOrderHeader /Status /Code	RELEASE
       // 8	CF$_ORDER_TYPE	/WorkOrderHeader /ReasonCode	MANUFACTURING
       // 9	CF$_START_DATE	/WorkOrderHeader /ForecastedTimePeriod /StartDateTime	6/12/2021	Date/time
       // 10	CF$_END_DATE	/WorkOrderHeader /ForecastedTimePeriod /EndDateTime	7/11/2021	Date/time
       // 11	CF$_PART_NO	/WorkOrderHeader /BOMReference /DocumentID /ID	ABC	String
       // 12	CF$_ORDER_QTY	/WorkOrderHeader /OrderQuantity	10	Number


        String orderNo = (String)record.findValue("CF$_ORDER_NO");
        String alOrdNo = (String)record.findValue("CF$_ALT_ORDER_NO");
        String custOrderNo = (String)record.findValue("CF$_CUST_ORDER_NO");
        String status = (String)record.findValue("CF$_STATUS");
        String orderType = (String)record.findValue("CF$_ORDER_TYPE");

        Date startDate = (Date)record.findValue("CF$_START_DATE");
        Date endDate = (Date)record.findValue("CF$_END_DATE");
        String partNo = (String)record.findValue("CF$_PART_NO");
        Double orderQty = (Double)record.findValue("CF$_ORDER_QTY");

        Calendar endDateCal = Calendar.getInstance();
        endDateCal.setTime(endDate);

        Calendar startDateCal = Calendar.getInstance();
        startDateCal.setTime(startDate);

        SyncWorkOrderDocument syncWorkOrderDocument = SyncWorkOrderDocument.Factory.newInstance();
        SyncWorkOrderType syncWorkOrderType = syncWorkOrderDocument.addNewSyncWorkOrder();

        syncWorkOrderType.setLanguageCode("en-US");
        syncWorkOrderType.setVersionID("9_4");
        syncWorkOrderType.setReleaseID("9_4");
        syncWorkOrderType.setSystemEnvironmentCode("Production");


        ApplicationAreaType applicationAreaType = syncWorkOrderType.addNewApplicationArea();
        SenderType senderType = applicationAreaType.addNewSender();
        senderType.addNewLogicalID();
        senderType.addNewComponentID();
        senderType.addNewTaskID();
        senderType.addNewReferenceID();
        senderType.addNewConfirmationCode();
        senderType.addNewAuthorizationID();

        applicationAreaType.setCreationDateTime(currentTimeProvider.apply(null));
        applicationAreaType.addNewBODID();

        SyncWorkOrderDataAreaType syncWorkOrderDataAreaType = syncWorkOrderType.addNewDataArea();
        SyncType syncType = syncWorkOrderDataAreaType.addNewSync();
        ActionCriteriaType actionCriteriaType = syncType.addNewActionCriteria();
        ActionExpressionType actionExpressionType = actionCriteriaType.addNewActionExpression();
        actionExpressionType.setActionCode("Add");
        actionExpressionType.setStringValue("/SyncWorkOrder/DataArea/WorkOrder[WorkOrderHeader/DocumentID/ID='"+ orderNo + "']");
        //WorkOrderHeaderDocument doc = WorkOrderHeaderDocument.Factory.newInstance();
        WorkOrderType workOrderType = syncWorkOrderDataAreaType.addNewWorkOrder();

        WorkOrderHeaderType header = workOrderType.addNewWorkOrderHeader();


        DocumentIDType documentID = header.addNewDocumentID();
        IdentifierType idType = documentID.addNewID();

        idType.setStringValue(orderNo);
        idType.setSchemeID("WorkOrder");
        documentID.setAgencyRole("ERP");

        DocumentIDType altDocID = header.addNewAlternateDocumentID();
        IdentifierType altDocIdType = altDocID.addNewID();
        altDocIdType.setStringValue(alOrdNo);
        altDocID.setAgencyRole("ERP");
        altDocIdType.setSchemeID("Routing");
        altDocIdType.setSchemeAgencyID("SAP");

        //header.addNewDocumentReference().addNewDocumentID().addNewID().setStringValue(custOrderNo);

        header.addNewStatus().addNewCode().setStringValue(status);
        //header.addNewReasonCode().setStringValue(orderType);
        header.addNewOrderQuantity().setBigDecimalValue(new BigDecimal(orderQty));
        //TimePeriodType forecastedTimePeriod = header.addNewForecastedTimePeriod();
        //header.addNewOrderQuantity().setBigDecimalValue(new BigDecimal("5"));

        //forecastedTimePeriod.setEndDateTime(endDateCal);
        //forecastedTimePeriod.setStartDateTime(startDateCal);

        //header.addNewBOMReference().addNewDocumentID().addNewID().setStringValue(partNo);

        ItemInstanceType itemInstanceType = header.addNewItemInstance();
        ItemIDType itemIDType = itemInstanceType.addNewItemID();
        //itemIDType.setAgencyRole("Upgrade"); //TODO
        itemIDType.addNewID().setStringValue("CART-01_LC"); //TODO
        //itemIDType.addNewRevisionID().setStringValue("SFWID_ORDER_DESC.ALIAS_PART_CHG"); //TODO

        //SequencedCodesType codesType = itemInstanceType.addNewClassification().addNewCodes();
        //SequencedCodeType codeType1 = codesType.addNewCode();
        //SequencedCodeType codeType2 = codesType.addNewCode();
        //codeType1.setSequence(new BigInteger("1")); //TODO
        //codeType1.setStringValue("SFWID_ORDER_DESC.ITEM_TYPE"); //TODO
        //codeType2.setSequence(new BigInteger("2")); //TODO
        //codeType2.setName("PartSubType");
        //codeType2.setStringValue("SFWID_ORDER_DESC.ITEM_SUBTYPE"); //TODO

        //SpecificationType specificationType = itemInstanceType.addNewSpecification();
        //PropertyType propertyType = specificationType.addNewProperty();
        //NameValuePairType nameValuePairType = propertyType.addNewNameValue();
        //nameValuePairType.setName("IsSameAsBuildPart"); //TODO
        //nameValuePairType.setStringValue("true or false"); //TODO

        //LocationType locationType = header.addNewSite();
        //locationType.addNewID().setStringValue("SFWID_ORDER_DESC.ASGND_WORK_LOC"); //TODO

       // PartyType partyType = header.addNewParty();
        //PartyIDsType partyIDsType = partyType.addNewPartyIDs();
        //partyIDsType.addNewID().setStringValue("SFWID_ORDER_DESC.ORDER_CUST_ID"); //TODO

        //header.addNewPriorityCode().setStringValue("SFWID_ORDER_DESC.SCHED_PRIORITY"); //TODO
        /*
        header.addNewRequiredQuantity().setBigDecimalValue();
        header.addNewDueQuantity().setBigDecimalValue();
        header.addNewExecutionTimePeriod();
        //header.addNewForecastedTimePeriod();
        header.setNeedDeliveryDate();
        header.setDueDateTime();
        header.setRequiredDateTime();
        header.setAvailableDateTime();

        RequiredQuantity@http://www.openapplications.org/oagis/9
        DueQuantity@http://www.openapplications.org/oagis/9
        ExecutionTimePeriod@http://www.openapplications.org/oagis/9
        ForecastedTimePeriod@http://www.openapplications.org/oagis/9
        NeedDeliveryDate@http://www.openapplications.org/oagis/9
        DueDateTime@http://www.openapplications.org/oagis/9
        RequiredDateTime@http://www.openapplications.org/oagis/9
        AvailableDateTime@http://www.openapplications.org/oagis/9
        ReasonCode@http://www.openapplications.org/oagis/9
        KanbanNumber@http://www.openapplications.org/oagis/9
        SerializedLot@http://www.openapplications.org/oagis/9
        Distribution@http://www.openapplications.org/oagis/9
        InQueueLocation@http://www.openapplications.org/oagis/9
        OutQueueLocation@http://www.openapplications.org/oagis/9
        ContractReference@http://www.openapplications.org/oagis/9
        ProjectReference@http://www.openapplications.org/oagis/9
        UserArea@http://www.openapplications.org/oagis/9
        WorkOrderRouteOperation@http://www.openapplications.org/oagis/9' instead of
        'OrderQuantity@http://www.openapplications.org/oagis/9' here in element
        WorkOrderHeader@http://www.openapplications.org/oagis/9
      at &lt;OrderQuantity xmlns="http://www.openapplications.org/oagis/9"
         */
        return syncWorkOrderDocument;
    }
}