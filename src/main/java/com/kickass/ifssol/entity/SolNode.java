package com.kickass.ifssol.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class SolNode {
    private String name;

    private boolean multiple;

    @JsonIgnore
    private SolNode parentNode;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<IfsSolMapping> ifsSolMappings = new ArrayList<>();

    @JsonIgnore
    private SolNodesRoot solNodesRoot;

    @JsonProperty("SolNodes")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<SolNode> solNodes; // = new ArrayList<>();

    private String classType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IfsSolMapping> getIfsSolMappings() {
        return ifsSolMappings;
    }

    public void setIfsSolMappings(List<IfsSolMapping> ifsSolMappings) {
        this.ifsSolMappings = ifsSolMappings;
    }

    public List<SolNode> getSolNodes() {
        return solNodes;
    }

    public void setSolNodes(List<SolNode> solNodes) {
        this.solNodes = solNodes;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public void addSolNode(SolNode solNode) {
        if (solNodes == null) {
            solNodes = new ArrayList<>();
        }
        solNodes.add(solNode);
        solNode.parentNode = this;
    }

    @JsonIgnore
    public SolNode getParentNode() {
        return parentNode;
    }

    @JsonIgnore
    public SolNode getRoot() {
        if (parentNode == null) {
            return this;
        }
        return parentNode.getRoot();
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }


    public SolNodesRoot getSolNodesRoot() {
        return solNodesRoot;
    }

    public void setSolNodesRoot(SolNodesRoot solNodesRoot) {
        this.solNodesRoot = solNodesRoot;
    }

    public void addIfsSolMapping(IfsSolMapping ifsSolMapping) {
        ifsSolMappings.add(ifsSolMapping);
        ifsSolMapping.setSolNode(this);
    }

}
