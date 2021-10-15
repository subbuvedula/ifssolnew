DECLARE
  param_log_id_ NUMBER := :LOG_ID;
  info_                  VARCHAR2(32000);
  objid_                 VARCHAR2(32000);
  objversion_            VARCHAR2(32000);
  attr_                  VARCHAR2(32000);

  CURSOR get_record (log_id_ NUMBER) IS
     SELECT t.objid, t.objversion
     FROM <_SCHEMA_>.SOLINT_WO_SPLIT_LOG_CLV t
     WHERE cf$_log_id = log_id_;

BEGIN
  OPEN get_record (param_log_id_);
  FETCH get_record INTO objid_, objversion_;
  CLOSE get_record;

  IF objid_ IS NOT NULL THEN
     <_SCHEMA_>.Client_SYS.Clear_Attr(attr_);
     <_SCHEMA_>.Client_SYS.Add_To_Attr('CF$_PROCESS_FLAG', 'DONE', attr_);
     <_SCHEMA_>.SOLINT_WO_SPLIT_LOG_CLP.Modify__(info_, objid_, objversion_, attr_, 'DO');
  END IF;
END;