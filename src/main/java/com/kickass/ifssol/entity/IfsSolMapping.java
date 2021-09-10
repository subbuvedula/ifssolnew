package com.kickass.ifssol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.function.Function;

public class IfsSolMapping {
    private String ifs = "";
    private String sol = "";
    private String solType = "";
    private String defaultIfs = "";

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
}
