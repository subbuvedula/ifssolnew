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
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

@Component
public class WorkOrderOperationUnitStatusRequestMapper implements  Function<SoluminaMessageListener.MapperInput,Record> {
    private static final Logger LOGGER = LogManager.getLogger(WorkOrderOperationUnitStatusRequestMapper.class);

    private SolNodesRoot solNodeRoot;
    private OAGIUtils oagiUtils = new OAGIUtils();


    public void setSolNodesRoot(SolNodesRoot solNodesRoot) {
        this.solNodeRoot = solNodesRoot;
    }

    @Override
    public Record apply(SoluminaMessageListener.MapperInput input) {
        LOGGER.info("WorkOrderStatusRequestMapper apply");
        Record record = input.record;
        UpdateConfirmWIPDocument doc = (UpdateConfirmWIPDocument) input.xmlObject;
        String changeReason = "";
        String serialNo = "";
        String statusCode = "";
        String parentLotNo = "";
        String parentSerialNo = "";
        String partNo = "";
        String partAction = "";
        String lotNo = "";
        BigDecimal partActionQty = null;
        String ucfAsWorkedBomVcH1 = "";
        String ucfAsWorkedBomVcH2 = "";
        String ucfAsWorkedBomVcH3 = "";
        String ucfAsWorkedBomVcH4 = "";

        UpdateConfirmWIPDataAreaType dataAreaType = doc.getUpdateConfirmWIP().getDataArea();

        List<ActionCriteriaType> actionCriteriaTypeList =  dataAreaType.getUpdate().getActionCriteriaList();
        ActionCriteriaType actionCriteriaType = actionCriteriaTypeList.get(0);
        List<TextType> textTypeList = actionCriteriaType.getChangeStatus().getReasonList();
        if (textTypeList.size() > 0) {
            changeReason = textTypeList.get(0).getStringValue();
        }


        if (dataAreaType.getConfirmWIPList().size() > 0) {
            if (dataAreaType.getConfirmWIPList().get(0).getConfirmWIPLineList().size() > 0) {
                ShopFloorControlLineType shopFloorControlLineType = dataAreaType.getConfirmWIPList().get(0).getConfirmWIPLineList().get(0);
                UserAreaType userAreaType1 = shopFloorControlLineType.getUserArea();
                OperationReferenceUserAreaType  operationReferenceUserAreaType = (OperationReferenceUserAreaType) oagiUtils.getObjectFromAnyType(userAreaType1);
                if (operationReferenceUserAreaType.getSerializedLotList().size() > 0) {
                    List<LotType>  lotList = operationReferenceUserAreaType.getSerializedLotList().get(0).getLotList();
                    if (lotList.size() > 0) {
                        if (lotList.get(0).getSerialNumberList().size() > 0) {
                            serialNo = lotList.get(0).getSerialNumberList().get(0).getStringValue();
                            UserAreaType lotUserAreaType = lotList.get(0).getUserArea();
                            LotUserAreaType actuallotUserAreaType = (LotUserAreaType)oagiUtils.getObjectFromAnyType(lotUserAreaType);
                            statusCode = actuallotUserAreaType.getStatusCode().getStringValue();
                        }
                    }
                }

                List<ShopFloorControlResourceType> shopFloorControlResourceTypeList = shopFloorControlLineType.getShopFloorControlResourceList();
                if (shopFloorControlResourceTypeList.size() > 0) {
                    List<DocumentReferenceType> documentReferenceTypeList = shopFloorControlResourceTypeList.get(0).getDocumentReferenceList();
                    if (documentReferenceTypeList.size() > 0) {

                        List<LotType> lotTypeList = documentReferenceTypeList.get(0).getItem().getLotList();
                        if (lotTypeList.size() > 0) {
                            List<SequencedIDType>  lotIdsList = lotTypeList.get(0).getLotIDs().getIDList();
                            if (lotIdsList.size() > 0) {
                                parentLotNo = lotIdsList.get(0).getStringValue();

                            }

                            List<IdentifierType> seIdentifierTypeList =  lotTypeList.get(0).getSerialNumberList();
                            if (seIdentifierTypeList.size() > 0) {
                                parentSerialNo = seIdentifierTypeList.get(0).getStringValue();
                            }
                        }
                    }

                    List<InventoryActualType> inventoryActualTypeList = shopFloorControlResourceTypeList.get(0).getActualResources().getInventoryActualList();
                    if (inventoryActualTypeList.size() > 0) {
                        List<ItemInstanceType> itemInstanceTypeList = inventoryActualTypeList.get(0).getItemInstanceList();
                        if (itemInstanceTypeList.size() > 0) {
                            List<ItemIDType> itemIDTypeList = itemInstanceTypeList.get(0).getItemIDList();
                            if (itemIDTypeList.size() > 0) {
                                partNo = itemIDTypeList.get(0).getID().getStringValue();
                            }

                            List<NoteType> noteTypeList = itemInstanceTypeList.get(0).getNoteList();
                            if (noteTypeList.size() > 0) {
                                partAction = noteTypeList.get(0).getStringValue();
                            }

                            partActionQty = itemInstanceTypeList.get(0).getQuantity().getBigDecimalValue();

                            List<SerializedLotType> serializedLotTypeList = itemInstanceTypeList.get(0).getSerializedLotList();
                            if (serializedLotTypeList.size() > 0) {
                                SerializedLotType serializedLotType = serializedLotTypeList.get(0);
                                List<LotType> lotTypeList = serializedLotType.getLotList();
                                if (lotTypeList.size() > 0) {
                                    List<SequencedIDType> sequencedIDTypeList =  lotTypeList.get(0).getLotIDs().getIDList();
                                    if (sequencedIDTypeList .size() > 0) {
                                       lotNo = sequencedIDTypeList.get(0).getStringValue();
                                    }
                                }
                            }
                        }

                        UserAreaType invUserArea =  inventoryActualTypeList.get(0).getUserArea();
                        InventoryActualUserAreaType inventoryActualUserAreaType = (InventoryActualUserAreaType)oagiUtils.getObjectFromAnyType(invUserArea);
                        List<UserConfigurableIdentifierType> userConfigurableIdentifierTypeList = inventoryActualUserAreaType.getUserConfigurableFields().getUserConfigurableIdentifierList();
                        for (UserConfigurableIdentifierType userConfigurableIdentifierType : userConfigurableIdentifierTypeList) {
                            String name = userConfigurableIdentifierType.getName();
                            if (name.equals("AsWorkedBomVarChar1")) {
                                ucfAsWorkedBomVcH1 = userConfigurableIdentifierType.getStringValue();
                            }
                            else if (name.equals("AsWorkedBomVarChar2")) {
                                ucfAsWorkedBomVcH2 = userConfigurableIdentifierType.getStringValue();
                            }
                            else if (name.equals("AsWorkedBomVarChar3")) {
                                ucfAsWorkedBomVcH3 = userConfigurableIdentifierType.getStringValue();
                            }
                            else if (name.equals("AsWorkedBomVarChar4")) {
                                ucfAsWorkedBomVcH4 = userConfigurableIdentifierType.getStringValue();
                            }
                        }
                    }
                }
            }
        }

        record.add("CHANGE_REASON", changeReason );
        record.add("SERIAL_NO", serialNo );
        record.add("STATUS_CODE", statusCode );
        record.add("PARENT_LOT_NO",parentLotNo );
        record.add("PARENT_SERIAL_NO", parentSerialNo );
        record.add("PART_NO", partNo );
        record.add("PART_ACTION", partAction );
        record.add("PART_ACTION_QTY", partActionQty );
        record.add("UCF_ASWRKD_BOM_VCH1", ucfAsWorkedBomVcH1 );
        record.add("UCF_ASWRKD_BOM_VCH2", ucfAsWorkedBomVcH2 );
        record.add("UCF_ASWRKD_BOM_VCH3", ucfAsWorkedBomVcH3 );
        record.add("UCF_ASWRKD_BOM_VCH4", ucfAsWorkedBomVcH4 );


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