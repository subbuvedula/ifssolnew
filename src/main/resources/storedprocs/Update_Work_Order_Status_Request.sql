DECLARE

  -- Parameters

  change_order_ VARCHAR2(2000) := :CHANGE_REASON;

  order_no_ VARCHAR2(20) := :ORDER_NO;

  order_status_ VARCHAR2(2000) := :ORDER_STATUS;

  part_no_ VARCHAR2(2000) := :PART_NO;

  location_ VARCHAR2(2000) := :LOCATION;

  order_qty_ NUMBER := :ORDER_QTY;

  serial_no_ VARCHAR2(2000) := :SERIAL_NO;

  completed_qty_ NUMBER := COMPLETED_QTY;

 

  -- Variables

  release_no_ VARCHAR2(20) := '*';

  sequence_no_ VARCHAR2(20) := '*';

  close_tolerance_ NUMBER := 99;

  close_operations_ VARCHAR2(20) := 'Yes';

  simplified_material_ VARCHAR2(20) := 'No';

  objstate_ VARCHAR2(32000);

 

BEGIN

    <_SCHEMA_>.SHOP_ORD_API.Close(objstate_,

                       order_no_,

                       release_no_,

                       sequence_no_,

                       close_tolerance_,

                       close_operations_,

                       simplified_material_);

 

END;