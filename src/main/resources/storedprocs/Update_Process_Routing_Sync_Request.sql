DECLARE
-- Parameters
contract_ VARCHAR2(5);
part_no_ VARCHAR2(25);
location_no_ VARCHAR2(20);
quantity_ NUMBER;
phase_in_date_ DATE;
phase_out_date_ DATE;
plan_no_ VARCHAR2(2000);
plan_revision_ VARCHAR2(2000);
-- Variables
rep_source_ VARCHAR2(20);
rep_location_no_ VARCHAR2(20);
bom_type_ VARCHAR2(20);
eng_chg_level_ NUMBER;
alternative_no_ VARCHAR2(20);
objid_ VARCHAR2(2000);
objversion_ VARCHAR2(2000);
ifs_objid_ VARCHAR2(2000);
-- Methods
PROCEDURE Create_Kanban_Circuit(contract_ IN VARCHAR2,
part_no_ IN VARCHAR2,
supply_to_location_no_ IN VARCHAR2,
replenish_source_ IN VARCHAR2,
replesnish_location_no_ IN VARCHAR2,
qty_per_kanban_ IN VARCHAR2) IS
attr_ VARCHAR2(32000);
info_ VARCHAR2(32000);
objid_ VARCHAR2(32000);
objversion_ VARCHAR2(32000);
BEGIN
<_SCHEMA_>.Client_SYS.Clear_Attr(attr_);
<_SCHEMA_>.KANBAN_CIRCUIT_API.NEW__(info_, objid_, objversion_, attr_, 'PREPARE');
<_SCHEMA_>.Client_SYS.Add_To_Attr('CONTRACT', contract_, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('PART_NO', part_no_, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('SUPPLY_TO_LOCATION_NO',
supply_to_location_no_,
attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('QTY_PER_KANBAN', qty_per_kanban_, attr_);
<_SCHEMA_>.client_SYS.Add_To_Attr('REPLENISH_SOURCE', replenish_source_, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('REPLENISH_FROM_LOCATION_NO',
replesnish_location_no_,
attr_);
<_SCHEMA_>.KANBAN_CIRCUIT_API.NEW__(info_, objid_, objversion_, attr_, 'DO');
END Create_Kanban_Circuit;
PROCEDURE Change_Prod_Strct_To_Build(contract_ IN VARCHAR2,
part_no_ IN VARCHAR2,
eng_chg_level_ IN VARCHAR2,
bom_type_ IN VARCHAR2,
alternative_no_ IN VARCHAR2) IS
objid_ VARCHAR2(32000);
objversion_ VARCHAR2(32000);
info_ VARCHAR2(32000);
attr_ VARCHAR2(32000);
CURSOR c1(contract_ VARCHAR2,
part_no_ VARCHAR2,
eng_chg_level_ VARCHAR2,
bom_type_ VARCHAR2,
alternative_no_ VARCHAR2) IS
SELECT objid, objversion
from <_SCHEMA_>.PROD_STRUCT_ALTERNATE
WHERE contract = contract_
AND part_no = part_no_
AND eng_chg_level = eng_chg_level_
AND bom_type_db = bom_type_
AND alternative_no = alternative_no_;
BEGIN
OPEN c1(contract_,
part_no_,
eng_chg_level_,
bom_type_,
alternative_no_);
FETCH c1
INTO objid_, objversion_;
CLOSE c1;
<_SCHEMA_>.Client_SYS.Clear_Attr(attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('STATE', 'Buildable', attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('__OBJEVENTS', 'Retire', attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('__OBJSTATE', 'Buildable', attr_);
<_SCHEMA_>.PROD_STRUCT_ALTERNATE_API.BUILD__(info_,
objid_,
objversion_,
attr_,
'DO');
END Change_Prod_Strct_To_Build;
PROCEDURE Set_Effective_Date(contract_ IN VARCHAR2,
part_no_ IN VARCHAR2,
eng_chg_level_ IN VARCHAR2,
bom_type_ IN VARCHAR2,
phase_in_date_ IN DATE,
phase_out_date_ IN DATE) IS
rec_attr_ VARCHAR2(32000);
bom_type_db VARCHAR2(32000);
BEGIN
bom_type_db := <_SCHEMA_>.Shop_Ord_Code_API.Decode(bom_type_);
<_SCHEMA_>.Client_SYS.Clear_Attr(rec_attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('EFF_PHASE_IN_DATE', phase_in_date_, rec_attr_);
IF (phase_out_date_ IS NOT NULL) THEN
<_SCHEMA_>.Client_SYS.Add_To_Attr('EFF_PHASE_OUT_DATE', phase_out_date_, rec_attr_);
END IF;
<_SCHEMA_>.PROD_STRUCTURE_HEAD_API.Modify(rec_attr_,
contract_,
part_no_,
eng_chg_level_,
bom_type_db);
END Set_Effective_Date;
BEGIN
IF (phase_in_date_ IS NULL) THEN
-- Plan complete
Create_Kanban_Circuit(contract_, part_no_, supply_to_location_no_, replenish_source_, replesnish_location_no_,
qty_per_kanban_);
ELSE
-- Plan effective
Set_Effective_Date(contract_, part_no_, eng_chg_level_, bom_type_, phase_in_date_, phase_out_date_);
Ifs_objid_ := contract_ || ‘-‘ || part_no_ || ‘-‘ || eng_chg_level_;
END IF;
-- Response
IF (ifs_objid_ IS NOT NULL) THEN
objid_ := ‘’;
objversion_ := ‘’;
<_SCHEMA_>.Client_SYS.Clear_Attr(attr_);
<_SCHEMA_>.SOLINT_PS_SYNC_LOG _CLP.NEW__( info_ , objid_ , objversion_ , attr_ , 'PREPARE' );
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_LOG_ID', to_char(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF3'), attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_LOG_TIMESTAMP', SYSDATE, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_LOG_TYPE', 'ProcessRoutingSync', attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_PROCESS_FLAG', 'NEW', attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_TRANS_TYPE', 'RESPONSE', attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_PLAN_NO', plan_no_, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_PLAN_REVISION', plan_revision_, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_PLAN_VERSION', ‘1’, attr_);
<_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_IFS_OBJID', ifs_objid_, attr_);
<_SCHEMA_>. SOLINT_PS_SYNC_LOG _CLP.NEW__( info_ , objid_ , objversion_ , attr_ , 'DO' );
END IF;
END;