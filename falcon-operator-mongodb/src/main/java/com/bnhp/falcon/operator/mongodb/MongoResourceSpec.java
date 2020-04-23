package com.bnhp.falcon.operator.mongodb;

import com.bnhp.falcon.operator.base.service.FalconResourceSpec;
import lombok.Data;
public class MongoResourceSpec extends FalconResourceSpec {

    private String url;
    private String payload;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
