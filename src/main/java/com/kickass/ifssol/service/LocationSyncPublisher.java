package com.kickass.ifssol.service;

import com.kickass.ifssol.dataaccessor.CommonDataAccessor;
import ifs.fnd.ap.RecordCollection;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "location.sync")
public class LocationSyncPublisher {
    private static Logger logger = LogManager.getLogger(LocationSyncPublisher.class);

    @Autowired
    private CommonDataAccessor commonDataAccessor;

    private String query;
    private String sendq;

    //public static final String QRY = "SELECT cf$_log_id, cf$_log_timestamp, cf$_log_type, cf$_action_code, cf$_type, cf$_id, " +
    //        "cf$_name, cf$_note, cf$_parent1_type, cf$_parent1_id, cf$_parent2_type, cf$_parent2_id " +
     //       "FROM IFSAPP.SOLINT_LOCATION_LOG_CLV WHERE cf$_process_flag = :STATE  ORDER BY cf$_log_id";


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getSendq() {
        return sendq;
    }

    public void setSendq(String sendq) {
        this.sendq = sendq;
    }

    public void processMessage() {
        try {
            RecordCollection recordCollection = commonDataAccessor.getData(query);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
