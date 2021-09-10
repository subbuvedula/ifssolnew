package com.kickass.ifssol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.function.Function;

public class TxnMapping {
    @JsonIgnore
    private static Logger LOGGER = LogManager.getLogger(TxnMapping.class);

    private String name;
    private String rootClass;
    private String sendQueue;
    private String receiveQueue;
    private String mapperFunctionName;
    private boolean enabled;

    @JsonIgnore
    private Function mapperFunction;

    private String cronExpression;
    private String query;
    private String updateStatement;
    private boolean updateStatus;
    private List<IfsSolMapping> ifsSolMappings;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRootClass() {
        return rootClass;
    }

    public void setRootClass(String rootClass) {
        this.rootClass = rootClass;
    }

    public List<IfsSolMapping> getIfsSolMappings() {
        return ifsSolMappings;
    }

    public void setIfsSolMappings(List<IfsSolMapping> ifsSolMappings) {
        this.ifsSolMappings = ifsSolMappings;
    }

    public String getSendQueue() {
        return sendQueue;
    }

    public void setSendQueue(String sendQueue) {
        this.sendQueue = sendQueue;
    }

    public String getReceiveQueue() {
        return receiveQueue;
    }

    public void setReceiveQueue(String receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    public String getMapperFunctionName() {
        return mapperFunctionName;
    }

    public void setMapperFunctionName(String mapperFunctionName) {
        this.mapperFunctionName = mapperFunctionName;
        if (mapperFunctionName != null) {
            try {
                Class functionClass = Class.forName(mapperFunctionName);
                Function function = (Function) functionClass.getDeclaredConstructor().newInstance();
                setMapperFunction(function);
            } catch (Exception ex) {
                LOGGER.error("Invalid Mapper function name : " + mapperFunctionName, ex);
            }
        }
    }

    public Function getMapperFunction() {
        return mapperFunction;
    }

    public void setMapperFunction(Function mapperFunction) {
        this.mapperFunction = mapperFunction;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getUpdateStatement() {
        return updateStatement;
    }

    public void setUpdateStatement(String updateStatement) {
        this.updateStatement = updateStatement;
    }


    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isUpdateStatus() {
        return updateStatus;
    }
}