package com.kickass.ifssol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class IfsSolMapping {
    private String ifs = "";
    private String sol = "";
    private String solType = "";
    private String defaultIfs = "";
    private String direction = "IN";

    private static final Set<String> VALID_DIRECTION_VALUES = new HashSet<>();

    static {
        VALID_DIRECTION_VALUES.add("IN");
        VALID_DIRECTION_VALUES.add("OUT");
        VALID_DIRECTION_VALUES.add("IN_OUT");
    }

    @JsonIgnore
    private SolNode solNode;

    private String fieldMergeStrategy;

    @JsonIgnore
    private Function valueProvider;

    private String valueProviderClass;


    public String getIfs() {
        return ifs;
    }

    public void setIfs(String ifs) {
        this.ifs = ifs;
    }

    public String getSol() {
        return sol;
    }

    public void setSol(String sol) {
        this.sol = sol;
    }

    public String getSolType() {
        return solType;
    }

    public void setSolType(String solType) {
        this.solType = solType;
    }

    public String getDefaultIfs() {
        return defaultIfs;
    }

    public void setDefaultIfs(String defaultIfs) {
        this.defaultIfs = defaultIfs;
    }

    @JsonIgnore
    public Function getValueProvider() {
        return valueProvider;
    }

    @JsonIgnore
    public void setValueProvider(Function valueProvider) {
        this.valueProvider = valueProvider;
    }

    public String getValueProviderClass() {
        return valueProviderClass;
    }

    public void setValueProviderClass(String valueProviderClass) {
        this.valueProviderClass = valueProviderClass;
    }

    public String getFieldMergeStrategy() {
        return fieldMergeStrategy;
    }

    public void setFieldMergeStrategy(String fieldMergeStrategy) {
        if (!StringUtils.isEmpty(fieldMergeStrategy)) {
            try {
                SolNodesRoot.FieldMegeStrategy fms = SolNodesRoot.FieldMegeStrategy.valueOf(fieldMergeStrategy.toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new RuntimeException("Invalid Merge Strategy " + fieldMergeStrategy, ex);
            }
            this.fieldMergeStrategy = fieldMergeStrategy;
        }
        else {
            this.fieldMergeStrategy = SolNodesRoot.FieldMegeStrategy.APPEND.name();
        }
    }

    @JsonIgnore
    public SolNode getSolNode() {
        return solNode;
    }

    @JsonIgnore
    public void setSolNode(SolNode solNode) {
        this.solNode = solNode;
    }

    public SolNodesRoot.FieldMegeStrategy getFieldMergeStrategyEnum() {
        if (!StringUtils.isEmpty(fieldMergeStrategy)) {
            return SolNodesRoot.FieldMegeStrategy.valueOf(fieldMergeStrategy);
        }
        return solNode.getSolNodesRoot().getFieldMergeStrategyEnum();
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        if (!StringUtils.isEmpty(direction) && !VALID_DIRECTION_VALUES.contains(direction.toUpperCase())) {
            throw new RuntimeException("Invalid Direction Value Specified " + direction);
        }
        this.direction = direction;
    }
}
