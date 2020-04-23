package com.bnhp.falcon.operator.base.service;

import io.fabric8.kubernetes.client.CustomResource;
import lombok.Data;

public class FalconResource<T extends FalconResourceSpec> extends CustomResource {

    private T spec;

    public String falconId(){
        return getMetadata().getNamespace() + "-" + getKind() +"-" + getMetadata().getName();
    }

    public T getSpec() {
        return spec;
    }

    public void setSpec(T spec) {
        this.spec = spec;
    }



}
