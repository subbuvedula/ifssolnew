package com.kickass.ifssol.mapper.request;

import com.ibaset.solumina.oagis.EffectivitySpecificationType;
import com.ibaset.solumina.oagis.EffectivityType;
import com.ibaset.solumina.oagis.InventoryAllocationUserAreaType;
import com.ibaset.solumina.oagis.RoutingHeaderUserAreaType;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.IRequestMapper;
import com.kickass.ifssol.mapper.IResponseMapper;
import com.kickass.ifssol.messaging.SoluminaMessageListener;
import com.kickass.ifssol.util.OAGIUtils;
import ifs.fnd.ap.PlsqlCommand;
import ifs.fnd.ap.Record;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;

import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

@Component
public class ProcessRoutingSyncRequestMapper implements  Function<SoluminaMessageListener.MapperInput,Record> {
    private static final Logger LOGGER = LogManager.getLogger(ProcessRoutingSyncRequestMapper.class);

    private SolNodesRoot solNodeRoot;

    private OAGIUtils oagiUtils = new OAGIUtils();

    public void setSolNodesRoot(SolNodesRoot solNodesRoot) {
        this.solNodeRoot = solNodesRoot;
    }

    @Override
    public Record apply(SoluminaMessageListener.MapperInput input) {
        LOGGER.info("ProcessRoutingSyncRequestMapper apply");
        Record record = input.record;
        SyncRoutingDocument doc = (SyncRoutingDocument)input.xmlObject;
        List<RoutingType> routingList =  doc.getSyncRouting().getDataArea().getRoutingList();
        if (routingList.size() > 0) {
            RoutingType routingType = routingList.get(0);
            RoutingHeaderType routingHeaderType = routingType.getRoutingHeader();
            String agencyRole = routingHeaderType.getDocumentID().getAgencyRole();
            String schemaAgencyName = routingHeaderType.getDocumentID().getID().getSchemeAgencyName();
            String idStringValue = routingHeaderType.getDocumentID().getID().getStringValue();
            String revision = routingHeaderType.getDocumentID().getRevisionID().getStringValue();
            String planType = routingHeaderType.getType().getStringValue();
            Calendar effStartDate = null;
            Calendar effThruDate = null;

            System.out.println("agencyRole :"  + agencyRole);
            System.out.println("idStringValue :"  + idStringValue);
            System.out.println("revision :"  + revision);
            System.out.println("planType :"  + planType);


            record.add("PLAN_NO", idStringValue);
            record.add("PLAN_REVISION", revision);
            record.add("PLAN_TYPE", planType);

            List<LocationType> locationList = routingHeaderType.getSiteList();
            String location = "";
            if (locationList.size() > 0) {
                 location = locationList.get(0).getType();
                System.out.println("location :"  + location);
                record.add("PLAN_LOCATION", location);

            }

            UserAreaType userAreaType = routingHeaderType.getUserArea();
            RoutingHeaderUserAreaType routingHeaderUserAreaType = (RoutingHeaderUserAreaType) oagiUtils.getObjectFromAnyType(userAreaType);

            //RoutingHeaderUserAreaType routingHeaderUserAreaType = (RoutingHeaderUserAreaType)routingHeaderType.getUserArea();
            List<EffectivityType>  effectivityTypeList = routingHeaderUserAreaType.getEffectivityList();
            if (effectivityTypeList.size() > 0) {
                List<EffectivitySpecificationType> effectivitySpecificationTypeList = effectivityTypeList.get(0).getEffectivitySpecificationList();
                if (effectivitySpecificationTypeList.size() > 0) {
                    List<TimePeriodType>  timePeriodTypeList = effectivitySpecificationTypeList.get(0).getEffectiveTimePeriodList();
                    if (timePeriodTypeList.size() > 0) {
                        TimePeriodType effFromDate = timePeriodTypeList.get(0);
                        effStartDate = effFromDate.getStartDateTime();
                        effThruDate = effFromDate.getEndDateTime();
                        System.out.println("effStartDate :"  + effStartDate);
                        System.out.println("effThruDate :"  + effThruDate);
                        record.add("EFF_FROM_DATE", effStartDate.getTime());
                        record.add("EFF_THRU_DATE", effThruDate.getTime());
                    }
                }
            }

            List<DocumentIDType> alternateDocumentIDList = routingHeaderType.getAlternateDocumentIDList();
            if (alternateDocumentIDList.size() > 0) {
                DocumentIDType alternativeDocumentID = alternateDocumentIDList.get(0);
                String altSchemaAgencyName = alternativeDocumentID.getID().getSchemeAgencyName();
                String altIdStringValue = alternativeDocumentID.getID().getStringValue();
                String altRevision = alternativeDocumentID.getRevisionID().getStringValue();
                System.out.println("altIdStringValue :"  + altIdStringValue);
                System.out.println("altRevision :"  + altRevision);
                record.add("IFS_OBJID", altIdStringValue);


            }

            List<OperationType> operationTypeList = routingType.getOperationList();
            getPartAndQuants(record, operationTypeList);

        }
        return null;
    }

    private void getPartAndQuants(Record record, List<OperationType> operationTypeList) {
        StringBuffer partNumbers = new StringBuffer();
        StringBuffer quants = new StringBuffer();
        StringBuffer actions = new StringBuffer();
        StringBuffer sites = new StringBuffer();
        for(OperationType operationType : operationTypeList) {
            List<LocationType> siteList = operationType.getSiteList();

            if (siteList.size() > 0) {
                List<IdentifierType>  idTypeList = siteList.get(0).getIDList();
                if (idTypeList.size() > 0) {
                    IdentifierType siteId= idTypeList.get(0);
                    String siteIdValue = siteId.getStringValue();
                    record.add("PLAN_WORK_CENTER", siteIdValue);
                }
            }

            List<QualifiedResourceType> qualifiedResourceTypeList = operationType.getQualifiedResourceList();
            if ( qualifiedResourceTypeList.size() > 0) {
                QualifiedResourceType qualifiedResourceType = qualifiedResourceTypeList.get(0);
                AllocatedResourcesType allocatedResourcesType = qualifiedResourceType.getAllocatedResources();
                List<InventoryAllocationType> inventoryAllocationTypeList = allocatedResourcesType.getInventoryAllocationList();
                if (inventoryAllocationTypeList.size() > 0) {
                    InventoryAllocationType inventoryAllocationType = inventoryAllocationTypeList.get(0);
                    List<ItemIDType>  invItemIdTypeList = inventoryAllocationType.getItem().getItemIDList();
                    if (invItemIdTypeList.size() > 0) {
                        String partNo = invItemIdTypeList.get(0).getID().getStringValue();
                        partNumbers.append(partNo);
                        partNumbers.append(",");
                    }
                    String requiredQuty = inventoryAllocationType.getRequiredQuantity().getStringValue();
                    quants.append(requiredQuty);
                    quants.append(",");

                    InventoryAllocationUserAreaType inventoryAllocationUserAreaType = (InventoryAllocationUserAreaType) oagiUtils.getObjectFromAnyType(inventoryAllocationType.getUserArea());
                    String action = inventoryAllocationUserAreaType.getItemAction().getStringValue();
                    actions.append(action);
                }
            }
        }

        record.add("PART_NO", partNumbers.toString());
        record.add("PART_ACTION", actions.toString());
        System.out.println("Quants :"  + quants);
        System.out.println("Actions :"  + actions);
        System.out.println("Parts :"  + partNumbers);

    }
}