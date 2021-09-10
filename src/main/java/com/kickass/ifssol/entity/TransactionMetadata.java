package com.kickass.ifssol.entity;

import java.util.function.Function;

@Deprecated
//TODO Delete
public class TransactionMetadata {
    private String name;
    private String sendQueue;
    private String receiveQueue;
    private String mapperFunctionName;
    private Function mapperFunction;
    private String cronExpression;
    private String query;
    private String updateStatement;
    private String shouldUpdateStatus;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getShouldUpdateStatus() {
        return shouldUpdateStatus;
    }

    public void setShouldUpdateStatus(String shouldUpdateStatus) {
        this.shouldUpdateStatus = shouldUpdateStatus;
    }
}
