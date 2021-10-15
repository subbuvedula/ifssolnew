package com.kickass.ifssol.ifsproxy;

import ifs.fnd.ap.Record;
import ifs.fnd.ap.RecordAttribute;
import ifs.fnd.ap.PlsqlCommand;
import ifs.fnd.ap.PlsqlSelectCommand;
import ifs.fnd.ap.PlsqlBaseMethodCommand;
import ifs.fnd.ap.PlsqlBaseMethodAction;
import ifs.fnd.ap.PlsqlBaseMethodType;
import ifs.fnd.ap.RecordCollection;
import ifs.fnd.ap.Server;


import ifs.fnd.ap.APException;
import ifs.fnd.ap.BindVariableDirection;
import ifs.fnd.ap.DataType;

class AccessProvider{
    private boolean _debugMode = false;
    private Server  _server;
    private String  _host = "";
    private String  _username = "";
    private String  _password = "";

    public AccessProvider(){};
    public AccessProvider(String host, String username, String password){
        _host = host;
        _username = username;
        _password = password;

        _server = new Server();
        _server.setCredentials(username, password);
        _server.setConnectionString(host);
    }

    public void setDebugMode(boolean debugMode){
        _debugMode = debugMode;
    }

    public boolean getDebugMode(){
        return _debugMode;
    }

    public void testAccessProviderConnection(String host, String username, String password){
        try{
            // Create a server and invoke server
            Server srv = new Server();
            srv.setCredentials(username, password);
            srv.setConnectionString(host);

            System.out.println("---");
            System.out.println(host);
            System.out.println(username);
            System.out.println(password);
            System.out.println("---");

            PlsqlSelectCommand cmd = new PlsqlSelectCommand(srv, "SELECT * FROM FND_USER WHERE DESCRIPTION LIKE :DESC");
            cmd.getBindVariables().add("DESC", "A%");
            System.out.println();
            System.out.println("Invoking FndPLSQLSelectCommand..." );

            System.out.println(cmd.getCommandText());
            //System.out.println(cmd.get);

            RecordCollection result = cmd.executeQuery();

            System.out.println("Invoke done!" );
            System.out.println();

            if(result!=null){
                System.out.println("Users");
                System.out.println("======");
                for(int i = 0; i < result.size(); i++){
                    System.out.println((String)result.get(i).findValue("DESCRIPTION"));
                }
            }
            else
                System.out.println("No Users found!");
            System.out.println();
        } catch(APException err) {
            System.out.println("---- ERROR APException -----");
            err.printStackTrace(System.out);
            System.out.println(err.getErrSource());
            System.out.println(err.getMessage());
        } catch(Exception err){
            System.out.println("---- ERROR -----");
            err.printStackTrace(System.out);
            System.out.println(err.getMessage());
        }
    } //end testAccessProviderConnection

    public void testPLSQLBlock(String plsqlBlock, String source_order_no_, float split_qty_ ){
        try{

            String cmdString = "";

            if(plsqlBlock.length() > 0){
                cmdString = plsqlBlock;
            }
            else {
                cmdString =
                        "BEGIN " +
                                "  DECLARE " +
                                //    "    source_order_no_ VARCHAR2(100) := '200110'; " +
                                //    "    split2_qty_ NUMBER := 1; " +

                                "    source_order_no_ VARCHAR2(100) := :SOURCE_ORDER_NO_; " +
                                "    split2_qty_ NUMBER := :SPLIT_QTY_; " +

                                "    order_no_ VARCHAR2(100); " +
                                "    rel_no_   VARCHAR2(10); " +
                                "    seq_no_   VARCHAR2(10); " +
                                "    new_part_no_ VARCHAR2(100); " +
                                "    split_qty_ NUMBER; " +
                                "    split_reason_ VARCHAR2(100); " +

                                "    new_shop_order_no_ VARCHAR2(100); " +
                                "    info_                  VARCHAR2(32000); " +
                                "    objid_                 VARCHAR2(32000); " +
                                "    objversion_            VARCHAR2(32000); " +
                                "    attr_                  VARCHAR2(32000); " +

                                "    part_no_ VARCHAR2(200); " +
                                "    order_code_ VARCHAR2(200); " +

                                "    msg_  VARCHAR2(32000);  " +

                                "    new_rel_no_ VARCHAR2(10); " +
                                "    new_seq_no_ VARCHAR2(10); " +

                                "  BEGIN " +
                                "    order_no_ := source_order_no_; " +
                                "    rel_no_ := '*'; " +
                                "    seq_no_ := '*'; " +
                                "    split_qty_ := split2_qty_; " +
                                "   split_reason_ := '100'; " +

                                "    part_no_ := IFSAPP.Shop_Ord_API.Get_Part_No(order_no_, rel_no_, seq_no_); " +
                                "    order_code_ := IFSAPP.Shop_Ord_API.Get_Order_Code(order_no_, rel_no_, seq_no_); " +

                                "   IFSAPP.Shop_Ord_API.Get_Order_No( new_shop_order_no_ , '*','*'); " +

                                "   IFSAPP.Client_SYS.Clear_Attr(attr_); " +
                                "    IFSAPP.Client_SYS.Add_To_Attr('ORDER_NO', new_shop_order_no_, attr_); " +
                                "    IFSAPP.Client_SYS.Add_To_Attr('PART_NO', part_no_, attr_); " +
                                "   IFSAPP.Client_SYS.Add_To_Attr('ORDER_CODE', order_code_, attr_); " +
                                "   IFSAPP.SHOP_ORD_API.NEW__( info_ , objid_ , objversion_ , attr_ , 'PREPARE' ); " +

                                "   new_rel_no_ := IFSAPP.Client_sys.Get_Item_Value('RELEASE_NO',attr_); " +
                                "   new_seq_no_ := IFSAPP.Client_sys.Get_Item_Value('SEQUENCE_NO',attr_); " +

                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'PART_NO', NVL(new_part_no_, part_no_)); " +
                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'REVISED_QTY_DUE', split_qty_); " +
                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'ORDER_NO', new_shop_order_no_); " +
                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'RELEASE_NO', new_rel_no_); " +
                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'SEQUENCE_NO', new_seq_no_); " +
                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'ORDER_CODE', order_code_); " +
                                "   IFSAPP.Message_SYS.Add_Attribute(msg_, 'SPLIT_REASON_CODE', split_reason_); " +

                                "   info_ := NULL; " +
                                "    IFSAPP.Shop_Order_Split_Util_API.Receive_Splits_In_Message__( info_ ,order_no_ , rel_no_ , seq_no_ , msg_ ); " +

                                "   IF (info_ IS NULL) THEN " +
                                "    INSERT INTO ifsapp.solint_wo_split_log_clt ( " +
                                "        cf$_log_id, cf$_log_timestamp, cf$_log_type, rowversion, cf$_process_flag,  " +
                                "        cf$_trans_type,  " +
                                "        cf$_source_order_no, cf$_source_qty, cf$_source_lot_no, cf$_source_serial_no, " +
                                "        cf$_split1_order_no, cf$_split1_qty, cf$_split1_lot_no, cf$_split1_serial_no, " +
                                "        cf$_split2_order_no, cf$_split2_qty, cf$_split2_lot_no, cf$_split2_serial_no " +
                                "    )  " +
                                "    VALUES (  " +
                                "       to_char(SYSTIMESTAMP, 'YYYYMMDDHH24MISSFF3'), SYSDATE, 'WorkOrderSplit', SYSDATE, 'NEW',  " +
                                "       'RESPONSE',  " +
                                "       order_no_, NULL, '', '',  " +
                                "       order_no_, NULL, '', '',  " +
                                "       new_shop_order_no_, split_qty_, '', ''  " +
                                "   );  " +
                                "   END IF;  " +
                                " END;  " +
                                "END;";
            }

            System.out.println("-- cmdString : ");
            System.out.println(cmdString);

            if(_server == null){
                _server = new Server();
                _server.setCredentials(_username, _password);
                _server.setConnectionString(_host);
            }

            System.out.println(" ");
            System.out.println("-- Creating PlsqlCommand.......");
            PlsqlCommand cmd = new PlsqlCommand(_server, cmdString);
            Record bindVars = cmd.getBindVariables();
            System.out.println("-- Add Bind Variables of : ");
            bindVars.add("SOURCE_ORDER_NO_", source_order_no_).setBindVariableDirection(BindVariableDirection.IN);
            bindVars.add("SPLIT_QTY_", split_qty_, DataType.FLOAT).setBindVariableDirection(BindVariableDirection.IN);
            System.out.println("SOURCE_ORDER_NO_ : " + source_order_no_);
            System.out.println("SPLIT_QTY_ : " + split_qty_);
            System.out.println(bindVars);
            System.out.println(" ");
            System.out.println("-- Executing PLSQLBlock.......");
            System.out.println(" ");
            cmd.execute();
            System.out.println("-- PLSQLBlock executed.");

        } catch(APException err){
            System.out.println("---- ERROR APException -----");
            err.printStackTrace(System.out);
            System.out.println(err.getErrSource());
            System.out.println(err.getMessage());

        } catch(Exception err){
            System.out.println("---- ERROR -----");
            err.printStackTrace(System.out);
            System.out.println(err.getMessage());
        }
    }

}