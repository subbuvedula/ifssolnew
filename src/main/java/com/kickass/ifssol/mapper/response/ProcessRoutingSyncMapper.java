package com.kickass.ifssol.mapper.response;

import com.kickass.ifssol.mapper.IResponseMapper;
import com.kickass.ifssol.valueprovider.CurrentTimeProvider;
import ifs.fnd.ap.Record;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.*;
import org.opengis.metadata.constraint.Classification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

@Component
public class ProcessRoutingSyncMapper implements IResponseMapper, Function<Record, XmlObject> {

    private CurrentTimeProvider currentTimeProvider = new CurrentTimeProvider();

    @Override
    public XmlObject apply(Record record) {

        String docId = (String) record.findValue("CF$DOCID");
        String alOrdNo = (String) record.findValue("CF$_ALT_ORDER_NO");
        String custOrderNo = (String) record.findValue("CF$_CUST_ORDER_NO");
        String status = (String) record.findValue("CF$_STATUS");
        String orderType = (String) record.findValue("CF$_ORDER_TYPE");

        Date startDate = (Date) record.findValue("CF$_START_DATE");
        Date endDate = (Date) record.findValue("CF$_END_DATE");
        String partNo = (String) record.findValue("CF$_PART_NO");
        Double orderQty = (Double) record.findValue("CF$_ORDER_QTY");
        String revisionID = (String) record.findValue("CF$_SFPL_PLAN_DESC.PLAN_REVISION");

        SyncRoutingDocument document = SyncRoutingDocument.Factory.newInstance();

        SyncRoutingType syncRoutingType = document.addNewSyncRouting();


        syncRoutingType.setLanguageCode("en-US");
        syncRoutingType.setVersionID("9_4");
        syncRoutingType.setReleaseID("9_4");
        syncRoutingType.setSystemEnvironmentCode("Production");

        ApplicationAreaType applicationAreaType = syncRoutingType.addNewApplicationArea();
        SenderType senderType = applicationAreaType.addNewSender();
        senderType.addNewLogicalID();
        senderType.addNewComponentID();
        senderType.addNewTaskID();
        senderType.addNewReferenceID();
        senderType.addNewConfirmationCode();
        senderType.addNewAuthorizationID();

        applicationAreaType.setCreationDateTime(currentTimeProvider.apply(null));
        applicationAreaType.addNewBODID();


        SyncRoutingDataAreaType dataAreaType = syncRoutingType.addNewDataArea();


        SyncType syncType = dataAreaType.addNewSync();
        ActionCriteriaType actionCriteriaType = syncType.addNewActionCriteria();
        ActionExpressionType actionExpressionType = actionCriteriaType.addNewActionExpression();
        actionExpressionType.setActionCode("Change");


        RoutingType routingType = dataAreaType.addNewRouting();
        RoutingHeaderType routingHeaderType = routingType.addNewRoutingHeader();
        OperationType operationType = routingType.addNewOperation();

        DocumentIDType documentIDType = routingHeaderType.addNewDocumentID();
        documentIDType.setAgencyRole("MES");
        IdentifierType identifierTypeID = documentIDType.addNewID();
        IdentifierType identifierTypeRevisionID = documentIDType.addNewRevisionID();
        IdentifierType identifierTypeVariationID = documentIDType.addNewVariationID();

        identifierTypeID.setStringValue("");
        identifierTypeID.setSchemeID("WorkPlan");
        identifierTypeID.setSchemeAgencyID("Solumina");
        identifierTypeRevisionID.setStringValue("");
        identifierTypeVariationID.setStringValue("");


        DocumentReferenceType documentReferenceType = routingHeaderType.addNewDocumentReference();
        CodeType codeType = documentReferenceType.addNewType();
        codeType.setStringValue("ChangeRequest");
        documentReferenceType.setType(codeType);

        DocumentIDType documentID1 = documentReferenceType.addNewDocumentID();
        IdentifierType identifierID1 = documentID1.addNewID();
        identifierID1.setStringValue("");


        routingHeaderType.addNewDescription().setStringValue("");

        BOMReferenceType bomReferenceType = routingHeaderType.addNewBOMReference();
        DocumentIDType documentID2 = bomReferenceType.addNewDocumentID();
        documentID2.addNewID().setStringValue("");
        documentID2.addNewRevisionID().setStringValue("");

        ItemType itemType = bomReferenceType.addNewItem();
        ItemIDType itemIDType = itemType.addNewItemID();
        itemIDType.addNewID().setStringValue(""); //TODO
        itemIDType.addNewRevisionID().setStringValue(""); //TODO


        //Manufacturing Item
        ManufacturingItemType manufacturingItemType = routingHeaderType.addNewManufacturingItem();

        ItemIDType itemIDType2 = manufacturingItemType.addNewItemID();
        itemIDType2.addNewID().setStringValue(""); //TODO
        itemIDType2.addNewRevisionID().setStringValue(""); //TODO


        ItemIDType manufacturerItemID = manufacturingItemType.addNewManufacturerItemID();
        manufacturerItemID.addNewID().setStringValue(""); //TODO

        ClassificationType classification = manufacturingItemType.addNewClassification();
        classification.addNewCodes().addNewCode().setSequence(new BigInteger("1"));

        LocationType site = routingHeaderType.addNewSite();
        site.setType("PlannedLocation");
        site.addNewID().setStringValue("");

        //Route Operation
        RouteOperationType routeOperationType = routingHeaderType.addNewRouteOperation();

        routeOperationType.addNewDocumentID().addNewID().setStringValue("");
        routeOperationType.addNewDescription().setStringValue("");
        routeOperationType.addNewPreviousOperation().addNewDocumentID().addNewID().setStringValue("");

        // operationType
        DocumentIDType operationDocIdType = operationType.addNewDocumentID();
        operationDocIdType.setAgencyRole("MES");
        operationDocIdType.addNewID().setSchemeAgencyID("Solumina");

        operationType.addNewDescription().setStringValue("");

        operationType.addNewType().setStringValue("");


        //SetupDuration
        //RuntimeDuration
        //MoveDuration

        LocationType operLocation1 = operationType.addNewSite();
        operLocation1.setType("PlannedLocation");
        operLocation1.addNewID().setStringValue("");

        LocationType operLocation2 = operationType.addNewSite();
        operLocation2.setType("PlannedDepartment");
        operLocation2.addNewID().setStringValue("");

        LocationType operLocation3 = operationType.addNewSite();
        operLocation3.setType("PlannedWorkCenter");
        operLocation3.addNewID().setStringValue("");

        QualifiedResourceType qualifiedResourceType = operationType.addNewQualifiedResource();
        AllocatedResourcesType allocatedResourcesType = qualifiedResourceType.addNewAllocatedResources();
        InventoryAllocationType inventoryAllocationType = allocatedResourcesType.addNewInventoryAllocation();
        ItemType inventoryItemType = inventoryAllocationType.addNewItem();
        ItemIDType inventoryItemIDType = inventoryItemType.addNewItemID();
        inventoryItemIDType.addNewID().setStringValue("");
        inventoryItemIDType.addNewRevisionID().setStringValue("");
        return document;
    }
    }