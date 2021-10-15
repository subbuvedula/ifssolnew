package com.kickass.ifssol.ifsproxy;


public class Main {

    public static void main(String[] args) {
        /*

        //AccessProvider tap = new AccessProvider("https://ndevr10.nayotech.local:48080", "solint", "interface");
        //tap.testAccessProviderConnection("https://ndevr10.nayotech.local:48080", "solint", "interface");

        */


        /* testing for PL/SQL Block */
        String cmdString =
                "BEGIN " +
                        "  DECLARE " +
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
                        " END;  " +
                        "END;";


        System.out.println("---- Testing PLSQLBlock -------");
        AccessProvider tap = new AccessProvider("https://ndevr10.nayotech.local:48080",  "solint",  "interface");

        //    "    source_order_no_ VARCHAR2(100) := '200110'; " +
        //    "    split2_qty_ NUMBER := 1; " +
        tap.testPLSQLBlock(cmdString, "200110", 1);
        System.out.println("---- Testing PLSQLBlock END -------");
        //tap.testAccessProviderConnection("https://ndevr10.nayotech.local:48080", "solint", "interface");

    }

}
