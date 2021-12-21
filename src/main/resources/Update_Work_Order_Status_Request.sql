DECLARE
-- Parameters
order_no_ VARCHAR2(20);
-- Variables
release_no_ VARCHAR2(20);
sequence_no_ VARCHAR2(20);
close_tolerance_ NUMBER;
close_operations_ VARCHAR2(20);
simplified_material_ VARCHAR2(20);
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