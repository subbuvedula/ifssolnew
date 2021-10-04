DECLARE
  -- Parameters
--  source_order_no_ VARCHAR2(100) := :SOURCE_WO_NO;
--  source_qty_ NUMBER := :SOURCE_QTY;
--  source_serial_no_ VARCHAR2(2000) := :SOURCE_SERIAL_NO;
--  split1_order_no_ VARCHAR2(100) := :SPLIT1_WO_NO;
--  split1_qty_ NUMBER := :SPLIT1_QTY;
--  split1_serial_no_ VARCHAR2(2000) := :SPLIT1_SERIAL_NO;
--  split2_order_no_ VARCHAR2(100) := :SPLIT2_WO_NO;
--  split2_qty_ NUMBER := :SPLIT2_QTY;
--  split2_serial_no_ VARCHAR2(2000) := :SPLIT2_SERIAL_NO;
  source_order_no_ VARCHAR2(100) := :WO_NO;
  split2_qty_ NUMBER := :SPLIT_QTY;

  -- variables
  order_no_ VARCHAR2(100);
  rel_no_   VARCHAR2(10);
  seq_no_   VARCHAR2(10);
  new_part_no_ VARCHAR2(100);
  split_qty_ NUMBER;
  split_reason_ VARCHAR2(100);

  new_shop_order_no_ VARCHAR2(100);
  info_                  VARCHAR2(32000);
  objid_                 VARCHAR2(32000);
  objversion_            VARCHAR2(32000);
  attr_                  VARCHAR2(32000);

  part_no_ VARCHAR2(200);
  order_code_ VARCHAR2(200);

  msg_  VARCHAR2(32000);

  new_rel_no_ VARCHAR2(10);
  new_seq_no_ VARCHAR2(10);

BEGIN
  -- initialization
  order_no_ := source_order_no_;
  rel_no_ := '*';
  seq_no_ := '*';
  split_qty_ := split2_qty_;
  split_reason_ := '100';

  part_no_ := IFSAPP.Shop_Ord_API.Get_Part_No(order_no_, rel_no_, seq_no_);
  order_code_ := IFSAPP.Shop_Ord_API.Get_Order_Code(order_no_, rel_no_, seq_no_);

  IFSAPP.Shop_Ord_API.Get_Order_No( new_shop_order_no_ , '*','*');

  IFSAPP.Client_SYS.Clear_Attr(attr_);
  IFSAPP.Client_SYS.Add_To_Attr('ORDER_NO', new_shop_order_no_, attr_);
  IFSAPP.Client_SYS.Add_To_Attr('PART_NO', part_no_, attr_);
  IFSAPP.Client_SYS.Add_To_Attr('ORDER_CODE', order_code_, attr_);
  info_ := '';
  IFSAPP.SHOP_ORD_API.NEW__( info_ , objid_ , objversion_ , attr_ , 'PREPARE' );

  new_rel_no_ := IFSAPP.Client_sys.Get_Item_Value('RELEASE_NO',attr_);
  new_seq_no_ := IFSAPP.Client_sys.Get_Item_Value('SEQUENCE_NO',attr_);

  IFSAPP.Message_SYS.Add_Attribute(msg_, 'PART_NO', NVL(new_part_no_, part_no_));
  IFSAPP.Message_SYS.Add_Attribute(msg_, 'REVISED_QTY_DUE', split_qty_);
  IFSAPP.Message_SYS.Add_Attribute(msg_, 'ORDER_NO', new_shop_order_no_);
  IFSAPP.Message_SYS.Add_Attribute(msg_, 'RELEASE_NO', new_rel_no_);
  IFSAPP.Message_SYS.Add_Attribute(msg_, 'SEQUENCE_NO', new_seq_no_);
  IFSAPP.Message_SYS.Add_Attribute(msg_, 'ORDER_CODE', order_code_);
  IFSAPP.Message_SYS.Add_Attribute(msg_, 'SPLIT_REASON_CODE', split_reason_);

  info_ := '';
  IFSAPP.Shop_Order_Split_Util_API.Receive_Splits_In_Message__( info_ ,order_no_ , rel_no_ , seq_no_ , msg_ );

  -- response
  --IF (info_ IS NULL) THEN
  --  INSERT INTO ifsapp.solint_wo_split_log_clt (
   --   cf$_log_id, cf$_log_timestamp, cf$_log_type, rowversion, cf$_process_flag,
     -- cf$_trans_type,
     -- cf$_source_order_no, cf$_source_qty, cf$_source_lot_no, cf$_source_serial_no,
     -- cf$_split1_order_no, cf$_split1_qty, cf$_split1_lot_no, cf$_split1_serial_no,
     -- cf$_split2_order_no, cf$_split2_qty, cf$_split2_lot_no, cf$_split2_serial_no
    --)
    -- VALUES (
    --  to_char(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF3'), SYSDATE, 'WorkOrderSplit', SYSDATE, 'NEW',
    --  'RESPONSE',
    --  order_no_, NULL, '', '',
    --  order_no_, NULL, '', '',
    --  new_shop_order_no_, split_qty_, '', ''
    -- );
  -- END IF;

END;