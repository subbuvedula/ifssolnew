package com.kickass.ifssol.mapper.response;

import com.ibaset.solumina.oagis.LocationUserAreaType;
import com.kickass.ifssol.entity.SolNodesRoot;
import com.kickass.ifssol.mapper.IResponseMapper;
import ifs.fnd.ap.Record;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.openapplications.oagis.x9.*;
import org.springframework.stereotype.Component;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class SyncLocationResponseMapper implements IResponseMapper, Function<Record, XmlObject> {

    public static final List<String> FIELDS = new ArrayList<>();
    private SolNodesRoot solNodeRoot;

    public void setSolNodesRoot(SolNodesRoot solNodesRoot) {
        this.solNodeRoot = solNodesRoot;
    }

    public static final String ACTION_CODE = "cf$_action_code";
    public static final String TYPE = "cf$_type";
    public static final String ID = "cf$_id";
    public static final String NAME = "cf$_name";
    public static final String NOTE = "cf$_note";
    public static final String PARENT1_TYPE = "cf$_parent1_type";
    public static final String PARENT1_ID = "cf$_parent1_id";
    public static final String PARENT2_TYPE = "cf$_parent2_type";
    public static final String PARENT2_ID = "cf$_parent2_id";
    private static Logger LOGGER = LogManager.getLogger(SyncLocationResponseMapper.class);

    static {
        FIELDS.add(ACTION_CODE);
        FIELDS.add(TYPE);
        FIELDS.add(ID);
        FIELDS.add(NAME);
        FIELDS.add(NOTE);
        FIELDS.add(PARENT1_TYPE);
        FIELDS.add(PARENT1_ID);
        FIELDS.add(PARENT2_TYPE);
        FIELDS.add(PARENT2_ID);
    }

    /*
     * Documents {
     *    "root" : "SyncLocationDocument",
     *    "locationType" : "",
     *     "idType.stringValue" : "",
     *     "nameType" : ""
     * }
     */

    private static void test() {
        SyncLocationDocument doc = SyncLocationDocument.Factory.newInstance();
        SyncLocationType syncLocationType = doc.addNewSyncLocation();
        SyncLocationDataAreaType dataAreaType = syncLocationType.addNewDataArea();
        LocationType locationType =  dataAreaType.addNewLocation();
        locationType.setType("Type");

        IdentifierType idType = IdentifierType.Factory.newInstance();
        idType.setStringValue("1223");
        NameType nameType = locationType.addNewName();
        nameType.setStringValue("Myname");

        UserAreaType userAreaType = locationType.addNewUserArea();
        LocationUserAreaType locationUserAreaType = LocationUserAreaType.Factory.newInstance();
        LocationType parentLocationType1 = locationUserAreaType.addNewParentLocation();
        parentLocationType1.setType("pt1");
        parentLocationType1.addNewID().setStringValue("ptid1");
        userAreaType.set(locationUserAreaType);

        LocationType parentLocationType2 = locationUserAreaType.addNewParentLocation();
        parentLocationType2.setType("pt2");
        parentLocationType2.addNewID().setStringValue("ptid2");


        try {
            StringWriter sw = new StringWriter();
            Result result = new StreamResult(sw);
            String text = doc.toString();

            System.out.println("MARSHALLED++++++++++++++++++ \n" + text);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public SyncLocationDocument map(Record r)  {
        SyncLocationDocument doc = SyncLocationDocument.Factory.newInstance();
        SyncLocationType syncLocationType = doc.addNewSyncLocation();

        //SyncLocationType syncLocationType = SyncLocationType.Factory.newInstance();
        String type=(String)r.findValue(TYPE.toUpperCase());
        String id=(String)r.findValue(ID.toUpperCase());
        String name=(String)r.findValue(NAME.toUpperCase());
        String parent1Type=(String)r.findValue(PARENT1_TYPE.toUpperCase());
        String parent1ID=(String)r.findValue(PARENT1_ID.toUpperCase());
        String parent2Type=(String)r.findValue(PARENT2_TYPE.toUpperCase());
        String parent2ID=(String)r.findValue(PARENT2_ID.toUpperCase());

        SyncLocationDataAreaType dataAreaType = syncLocationType.addNewDataArea();

        LocationType locationType = dataAreaType.addNewLocation();
        locationType.setType(type);
        IdentifierType idType = locationType.addNewID();
        idType.setStringValue(id);
        NameType nameType = locationType.addNewName();
        nameType.setStringValue(name);

        UserAreaType userAreaType = locationType.addNewUserArea();
        LocationUserAreaType locationUserAreaType = LocationUserAreaType.Factory.newInstance();

        LocationType parentLocationType1 = locationUserAreaType.addNewParentLocation();
        parentLocationType1.setType(parent1Type);
        parentLocationType1.addNewID().setStringValue(parent1ID);

        LocationType parentLocationType2 = locationUserAreaType.addNewParentLocation();
        parentLocationType2.setType(parent2Type);
        parentLocationType2.addNewID().setStringValue(parent2ID);

        userAreaType.set(locationUserAreaType);

        return doc;
    }


    @Override
    public XmlObject apply(Record record)  {
        return this.map(record);
    }

    @Override
    public <V> Function<V, XmlObject> compose(Function<? super V, ? extends Record> before) {
        return Function.super.compose(before);
    }

    @Override
    public <V> Function<Record, V> andThen(Function<? super XmlObject, ? extends V> after) {
        return Function.super.andThen(after);
    }

    public static void main(String[] args) {
        test();
    }
}