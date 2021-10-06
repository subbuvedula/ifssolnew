package com.kickass.ifssol.service;

import com.ibaset.solumina.oagis.LocationUserAreaType;
import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import com.kickass.ifssol.mapper.response.SyncLocationResponseMapper;
import com.kickass.ifssol.messaging.MessagePublisher;
import ifs.fnd.ap.Record;
import ifs.fnd.ap.RecordCollection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openapplications.oagis.x9.LocationType;
import org.openapplications.oagis.x9.NameType;
import org.openapplications.oagis.x9.SyncLocationType;
import org.openapplications.oagis.x9.UserAreaType;
import org.openapplications.oagis.x9.impl.IdentifierTypeImpl;
import org.openapplications.oagis.x9.impl.LocationTypeImpl;
import org.openapplications.oagis.x9.impl.SyncLocationDataAreaTypeImpl;
import org.openapplications.oagis.x9.impl.SyncLocationTypeImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class CommonDataPublisher {

    @Autowired
    private Environment env;

    @Autowired
    private CommonDataAccessor commonDataAccessor;

    private static Logger LOGGER = LogManager.getLogger(CommonDataPublisher.class);

    @Autowired
    private MessagePublisher messagePublisher;


    private RecordCollection getTempData() {
        SyncLocationTypeImpl syncLocationType = (SyncLocationTypeImpl) SyncLocationType.Factory.newInstance();

        SyncLocationDataAreaTypeImpl dataAreaType = (SyncLocationDataAreaTypeImpl) syncLocationType.addNewDataArea();

        LocationTypeImpl locationType = (LocationTypeImpl) dataAreaType.addNewLocation();
        locationType.setType("Type");
        IdentifierTypeImpl idType = (IdentifierTypeImpl) locationType.addNewID();
        idType.setStringValue("1223");
        NameType nameType = locationType.addNewName();
        nameType.setStringValue("Myname");

        UserAreaType userAreaType = locationType.addNewUserArea();
        LocationUserAreaType locationUserAreaType = LocationUserAreaType.Factory.newInstance();
        userAreaType.set(locationUserAreaType);

        LocationType parentLocationType1 = locationUserAreaType.addNewParentLocation();
        parentLocationType1.setType("pt1");
        parentLocationType1.addNewID().setStringValue("ptid1");

        LocationType parentLocationType2 = locationUserAreaType.addNewParentLocation();
        parentLocationType2.setType("pt2");
        parentLocationType2.addNewID().setStringValue("ptid2");

        RecordCollection rc = new RecordCollection();

        Record record = new Record();
        record.add(SyncLocationResponseMapper.ACTION_CODE, "ADD");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.NAME, "SyncLocation");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.TYPE, "Type");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.ID, "ID123456");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.PARENT1_ID, "Parent1Id");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.PARENT2_ID, "Parent2Id");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.PARENT1_TYPE, "Parent1Type");
        rc.add(record);

        record = new Record();
        record.add(SyncLocationResponseMapper.PARENT2_TYPE, "Parent2Type");
        rc.add(record);
        return rc;
    }
}