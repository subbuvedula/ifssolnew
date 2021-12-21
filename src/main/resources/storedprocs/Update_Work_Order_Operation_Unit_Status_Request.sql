DECLARE
 -- Parameters
 order_no_ VARCHAR2(20);
 part_no_ VARCHAR2(25);
 quantity_ NUMBER;

 -- Variables
 release_no_ VARCHAR2(20);
 sequence_no_ VARCHAR2(20);
 line_item_no_ NUMBER;
 contract_ VARCHAR2(5);

 -- Methods
 PROCEDURE Shop_Order_Manual_Issue(qty_issued_ IN NUMBER,
 order_no_ IN VARCHAR2,
 release_no_ IN VARCHAR2,
 sequence_no_ IN VARCHAR2,
 line_item_no_ IN NUMBER,
 contract_ IN VARCHAR2) IS

 info_ VARCHAR2(32000);
 part_no_ VARCHAR2(32000);
 location_no_ VARCHAR2(32000);
 lot_batch_no_ VARCHAR2(32000);
 serial_no_ VARCHAR2(32000);
 eng_chg_level_ VARCHAR2(32000);
 waiv_dev_rej_no_ VARCHAR2(32000);
 activity_seq_ NUMBER;
 handling_unit_id_ NUMBER;
 catch_qty_ NUMBER;
 input_qty_ NUMBER := null;
 input_uom_ VARCHAR2(32000) := null;
 input_value_ VARCHAR2(32000) := null;
 part_tracking_session_id_ NUMBER := null;

 CURSOR c1(order_no_ IN VARCHAR2,
 release_no_ IN VARCHAR2,
 sequence_no_ IN VARCHAR2, 
 line_item_no_ IN NUMBER,
 contract_ IN VARCHAR2) IS

 SELECT PART_NO,
 LOCATION_NO,
 LOT_BATCH_NO,
 SERIAL_NO,
 ENG_CHG_LEVEL,
 WAIV_DEV_REJ_NO,
 ACTIVITY_SEQ,
 HANDLING_UNIT_ID,
 DKEN1APP.SHOP_MATERIAL_ALLOC_API.Get_Catch_Qty_Issued(order_no_,
 release_no_,
 sequence_no_,
 line_item_no_)
 FROM DKEN1APP.SINGLE_MANUAL_ISSUE_SO
 WHERE order_no = order_no_
 AND release_no = release_no_
 AND sequence_no = sequence_no_
 AND line_item_no = line_item_no_
 AND contract_ = contract_;

 BEGIN

 OPEN c1(order_no_, release_no_, sequence_no_, line_item_no_, contract_);
 FETCH c1
 INTO part_no_,
 location_no_,
 lot_batch_no_,
 serial_no_,
 eng_chg_level_,
 waiv_dev_rej_no_,
 activity_seq_,
 handling_unit_id_,
 catch_qty_;
 CLOSE c1;

 DKEN1APP.Shop_Ord_Util_API.Manual_Issue(info_,
 order_no_,
 release_no_,
 sequence_no_,
 line_item_no_,
 contract_,
 part_no_,
 location_no_,
 lot_batch_no_,
 serial_no_,
 eng_chg_level_,
 waiv_dev_rej_no_,
 activity_seq_,
 handling_unit_id_,
 catch_qty_,
 qty_issued_,
 input_qty_,
 input_uom_,
 input_value_,
 part_tracking_session_id_);
  END Shop_Order_Manual_Issue;

BEGIN
 -- Initialization
 release_no_ := '*';
 sequence_no_ := '*';

 Shop_Order_Manual_Issue(quantity_, order_no_, release_no_, sequence_no_, line_item_no_, contract_);
END;