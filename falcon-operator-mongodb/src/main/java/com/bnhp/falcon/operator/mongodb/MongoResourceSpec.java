package com.bnhp.falcon.operator.mongodb;

import com.bnhp.falcon.operator.base.service.FalconResourceSpec;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import lombok.Data;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;

public class MongoResourceSpec extends FalconResourceSpec {


    private String payload;
    private CRD crd;


    public CRD getCrd() {
        return crd;
    }

    public void setCrd(CRD crd) {
        this.crd = crd;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public static class CRD{
        public CTX ctx;

        public CTX getCtx() {
            return ctx;
        }

        public void setCtx(CTX ctx) {
            this.ctx = ctx;
        }
    }

    public static class CTX{

        private String scope;
        private String plural;
        private String group;
        private String version;

        public String getScope() {
            return scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }

        public String getPlural() {
            return plural;
        }

        public void setPlural(String plural) {
            this.plural = plural;
        }

        public String getGroup() {
            return group;
        }

        public void setGroup(String group) {
            this.group = group;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public CustomResourceDefinitionContext asContext(){
            return new CustomResourceDefinitionContext.Builder()
                    .withGroup(getGroup())
                    .withName(getPlural() +"." + getGroup())
                    .withPlural(getPlural())
                    .withVersion(getVersion())
                    .withScope(getScope()).build();
        }
    }


}
