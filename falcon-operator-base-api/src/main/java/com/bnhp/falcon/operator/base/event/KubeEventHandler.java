package com.bnhp.falcon.operator.base.event;

import com.bnhp.falcon.operator.base.FalconVoidController;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
@Slf4j
public class KubeEventHandler {
    private final static Logger log = LoggerFactory.getLogger(KubeEventHandler.class);

    private Cache<String, KubeEventLogger> cache;
    private final KubernetesClient kubernetesClient;

    public KubeEventHandler(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
        log.info("Event handler initiated in Namespace {}", kubernetesClient.getNamespace());
        this.cache = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(150)
                .expireAfterAccess(60, TimeUnit.MINUTES)
                .weakKeys()
                .removalListener(new CustomRemovalListener()).build();
    }
    public KubeEventLogger get(String namespace){
        KubeEventLogger logger = cache.getIfPresent(namespace);
        if(logger == null){
            logger = new KubeEventLogger(kubernetesClient,namespace);
             cache.put(namespace,logger);
        }
        return logger;
    }


    class CustomRemovalListener implements RemovalListener<Object, Object> {
        @Override
        public void onRemoval(Object key, Object value, RemovalCause cause) {
            log.info("removal listener called with key {}, cause {}, evicted {}\n",
                    key, cause.toString(), cause.wasEvicted());
        }
    }


//    public void log(EventLogger.Reason reason, String message, EventLogger.Type type, EventLogger.ObjectKind objectKind, String objectName) {
//
//    }

}
