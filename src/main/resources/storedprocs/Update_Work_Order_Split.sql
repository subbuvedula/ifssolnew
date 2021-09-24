DECLARE
  -- Parameters
  order_no_ VARCHAR2(100) := :WO_NO;
  rel_no_   VARCHAR2(10)  := '*';
  seq_no_   VARCHAR2(10)  := '*';
  new_part_no_ VARCHAR2(100);
  rounded_lot_qty_ NUMBER := :SPLIT_QTY;
  percentage_ NUMBER := 0;
  lot_qty_rounding_ NUMBER := 0;
  split_reason_ VARCHAR2(100) := '100';

  new_shop_order_no_ VARCHAR2(100);
  info_                  VARCHAR2(32000);
  objid_                 VARCHAR2(32000);
  objversion_            VARCHAR2(32000);
  attr_                  VARCHAR2(32000);

  part_no_ VARCHAR2(200) := <_SCHEMA_>.Shop_Ord_API.Get_Part_No(order_no_, rel_no_, seq_no_);
  order_code_ VARCHAR2(200) := <_SCHEMA_>.Shop_Ord_API.Get_Order_Code(order_no_, rel_no_, seq_no_);

  msg_  VARCHAR2(32000);

  new_rel_no_ VARCHAR2(10);
  new_seq_no_ VARCHAR2(10);

BEGIN
  <_SCHEMA_>.Shop_Ord_API.Get_Order_No( new_shop_order_no_ , '*','*');

  <_SCHEMA_>.Client_SYS.Clear_Attr(attr_);
  <_SCHEMA_>.Client_SYS.Add_To_Attr('ORDER_NO', new_shop_order_no_, attr_);
  <_SCHEMA_>.Client_SYS.Add_To_Attr('PART_NO', part_no_, attr_);
  <_SCHEMA_>.Client_SYS.Add_To_Attr('ORDER_CODE', order_code_, attr_);
  <_SCHEMA_>.SHOP_ORD_API.NEW__( info_ , objid_ , objversion_ , attr_ , 'PREPARE' );

  new_rel_no_ := <_SCHEMA_>.Client_sys.Get_Item_Value('RELEASE_NO',attr_);
  new_seq_no_ := <_SCHEMA_>.Client_sys.Get_Item_Value('SEQUENCE_NO',attr_);

  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'PART_NO', NVL(new_part_no_, part_no_));
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'ROUNDED_LOT_QTY', rounded_lot_qty_);
  --<_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'PERCENTAGE', percentage_);
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'LOT_QTY_ROUNDING', percentage_);
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'ORDER_NO', new_shop_order_no_);
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'RELEASE_NO', new_rel_no_);
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'SEQUENCE_NO', new_seq_no_);
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'ORDER_CODE', order_code_);
  <_SCHEMA_>.Message_SYS.Add_Attribute(msg_, 'SPLIT_REASON_CODE', split_reason_);

  info_ := NULL;
  <_SCHEMA_>.Shop_Order_Split_Util_API.Receive_Splits_In_Message__( info_ ,order_no_ , rel_no_ , seq_no_ , msg_ );

  :NEW_WO_NO := new_shop_order_no_;
  :NEW_REL_NO := new_rel_no_;
  :NEW_SEQ_NO := new_seq_no_;

END;