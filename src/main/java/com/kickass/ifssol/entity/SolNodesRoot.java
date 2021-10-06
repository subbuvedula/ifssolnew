package com.kickass.ifssol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SolNodesRoot {
    @JsonProperty("SolNodes")
    private List<SolNode> solNodes = new ArrayList<>();

    @JsonIgnore
    private static Logger LOGGER = LogManager.getLogger(TxnMapping.class);

    private String responseNodeName;
    private String name;
    private String rootClass;
    private String sendQueue;
    private String receiveQueue;
    private String mapperFunctionName;
    private boolean enabled;
    private String updateStoredproc;

    private String fieldMergeStrategy = FieldMegeStrategy.APPEND.name();

    @JsonIgnore
    private Function mapperFunction;

    private String cronExpression;
    private String query;
    private String updateStatement;
    private boolean updateStatus;

    public List<SolNode> getSolNodes() {
        return solNodes;
    }

    public void setSolNodes(List<SolNode> solNodes) {
        this.solNodes = solNodes;
    }

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
        if (mapperFunctionName != null && !mapperFunctionName.trim().isEmpty()) {
            try {
                Class functionClass = Class.forName(mapperFunctionName);
                Function function = (Function) functionClass.getDeclaredConstructor().newInstance();
                setMapperFunction(function);
            } catch (Exception ex) {
                LOGGER.error("Invalid Mapper function name : " + mapperFunctionName, ex);
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        if (StringUtils.isEmpty(updateStatement) && updateStoredproc != null) {
            updateStatement = loadStoredProc();
        }
        return updateStatement;
    }

    public void setUpdateStatement(String updateStatement) {
        this.updateStatement = updateStatement;
    }

    public boolean isUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(boolean updateStatus) {
        this.updateStatus = updateStatus;
    }

    public void addSolNode(SolNode solNode) {
        solNodes.add(solNode);
    }

    public String getFieldMergeStrategy() {
        return fieldMergeStrategy;
    }

    public void setFieldMergeStrategy(String fieldMergeStrategy) {
        if (!StringUtils.isEmpty(fieldMergeStrategy)) {
            try {
                FieldMegeStrategy fms = FieldMegeStrategy.valueOf(fieldMergeStrategy.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Invalid Merge Strategy " + fieldMergeStrategy, ex);
            }
            this.fieldMergeStrategy = fieldMergeStrategy;
        }
        else {
            this.fieldMergeStrategy = FieldMegeStrategy.APPEND.name();
        }
    }

    public FieldMegeStrategy getFieldMergeStrategyEnum() {
        return SolNodesRoot.FieldMegeStrategy.valueOf(fieldMergeStrategy.toUpperCase());
    }

    public String getUpdateStoredproc() {
        return updateStoredproc;
    }

    public void setUpdateStoredproc(String updateStoredproc) {
        this.updateStoredproc = updateStoredproc;
    }

    private String loadStoredProc() {
        String sql = "";
        ClassLoader cl = SolNodesRoot.class.getClassLoader();
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
        try {
            InputStream is = SolNodesRoot.class.getResourceAsStream("/storedprocs/"+updateStoredproc);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            sql = new String(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sql;
    }

    public String getResponseNodeName() {
        return responseNodeName;
    }

    public void setResponseNodeName(String responseNodeName) {
        this.responseNodeName = responseNodeName;
    }

    public enum FieldMegeStrategy {
        APPEND, OVERRIDE, CUSTOM
    }
}
