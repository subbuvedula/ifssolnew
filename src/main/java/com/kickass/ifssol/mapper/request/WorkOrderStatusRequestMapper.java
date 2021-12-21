package com.kickass.ifssol.mapper.request;

import com.ibaset.solumina.oagis.*;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.messaging.SoluminaMessageListener;
import com.kickass.ifssol.util.OAGIUtils;
import ifs.fnd.ap.Record;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openapplications.oagis.x9.*;
import org.openapplications.oagis.x9.LocationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

@Component
public class WorkOrderStatusRequestMapper implements  Function<SoluminaMessageListener.MapperInput,Record> {
    private static final Logger LOGGER = LogManager.getLogger(WorkOrderStatusRequestMapper.class);

    private SolNodesRoot solNodeRoot;
    private OAGIUtils oagiUtils = new OAGIUtils();


    public void setSolNodesRoot(SolNodesRoot solNodesRoot) {
        this.solNodeRoot = solNodesRoot;
    }

    @Override
    public Record apply(SoluminaMessageListener.MapperInput input) {
        LOGGER.info("WorkOrderStatusRequestMapper apply");
        Record record = input.record;
        UpdateProductionOrderDocument doc = (UpdateProductionOrderDocument) input.xmlObject;
        UpdateProductionOrderType updateProductionOrderType = doc.getUpdateProductionOrder();
        List<ActionCriteriaType> actionCriteriaTypeList = updateProductionOrderType.getDataArea().getUpdate().getActionCriteriaList();
        String reason = "";
        String orderNo = "";
        String orderStatus = "";
        String partNo = "";
        String location = "";
        BigDecimal orderQty = null;
        String serialNo = "";
        BigDecimal compleQty = null;
        if (actionCriteriaTypeList.size() > 0) {
            List<TextType> reasonList = actionCriteriaTypeList.get(0).getChangeStatus().getReasonList();
            if (reasonList.size() > 0) {
                reason = reasonList.get(0).getStringValue();
            }
        }
        List<ProductionOrderType> productionOrderTypeList  = updateProductionOrderType.getDataArea().getProductionOrderList();
        if ( productionOrderTypeList.size() > 0) {
            ProductionOrderHeaderType productionOrderHeaderType = productionOrderTypeList.get(0).getProductionOrderHeader();

            orderNo = productionOrderHeaderType.getDocumentID().getID().getStringValue();
            List<StatusType>  statusTypeList = productionOrderHeaderType.getStatusList();
            if (statusTypeList.size() > 0) {
                orderStatus = statusTypeList.get(0).getCode().getStringValue();
            }
            List<ItemIDType>  itemIDTypeList = productionOrderHeaderType.getItemInstance().getItemIDList();
            if (itemIDTypeList.size() > 0) {
                partNo = itemIDTypeList.get(0).getID().getStringValue();
            }

            List<LocationType> siteTypeList = productionOrderHeaderType.getSiteList();
            if (siteTypeList.size() > 0) {
                if (siteTypeList.get(0).getIDList().size() > 0) {
                    location = siteTypeList.get(0).getIDList().get(0).getStringValue();
                }
            }

            orderQty = productionOrderHeaderType.getOrderQuantity().getBigDecimalValue();

            List<SerializedLotType>  serializedLotTypeList = productionOrderHeaderType.getSerializedLotList();
            if (serializedLotTypeList.size() > 0) {
                List<LotType> lotTypeList = serializedLotTypeList.get(0).getLotList();
                if (lotTypeList.size() > 0) {
                    List<IdentifierType> lotIdentifierTypeList = lotTypeList.get(0).getSerialNumberList();
                    if (lotIdentifierTypeList.size() > 0) {
                        serialNo = lotIdentifierTypeList.get(0).getStringValue();
                    }
                }
            }

            UserAreaType userAreaType = productionOrderHeaderType.getUserArea();
            WorkOrderHeaderUserAreaType workOrderHeaderUserAreaType = (WorkOrderHeaderUserAreaType) oagiUtils.getObjectFromAnyType(userAreaType);
             compleQty = workOrderHeaderUserAreaType.getCompletedQuantity().getBigDecimalValue();

             record.add("CHANGE_REASON", reason);
             record.add("ORDER_NO", orderNo);
             record.add("ORDER_STATUS", orderStatus);
             record.add("PART_NO", partNo);
             record.add("LOCATION", location);
             record.add("ORDER_QTY", orderQty);
            record.add("SERIAL_NO", serialNo);
            record.add("COMPLETED_QTY", compleQty);
        }
        return record;
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