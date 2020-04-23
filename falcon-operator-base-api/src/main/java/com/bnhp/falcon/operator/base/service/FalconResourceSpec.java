package com.bnhp.falcon.operator.base.service;

import lombok.Data;

@Data
public abstract class FalconResourceSpec {

    private String type;
    private String name;
    private String label;


    public String getType(){
        return this.type;
    }

    public void setType(String  type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
