package com.bnhp.falcon.operator.base.event;

import com.bnhp.falcon.operator.base.service.FalconResource;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.EventBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.Instant;
public class KubeEventLogger implements EventLogger {
    private final KubernetesClient kubeClient;
    private final String namespace;
    public static final String _FALCON = "Falcon";


    private final static Logger log = LoggerFactory.getLogger(KubeEventLogger.class);

    public KubeEventLogger(KubernetesClient kubeClient, String namespace) {
        this.kubeClient = kubeClient;
        this.namespace = namespace;
    }

    @Override
    public void log(Reason reason, String message, Type type, ObjectKind objectKind, String objectName, String componentName) {
        String eventName = componentName + "." + (reason + message + type + objectKind + objectName).hashCode();
        log.info("Creating event (if not exist) in NS {} , event {} ", namespace,eventName);
        Event existing = kubeClient.events().inNamespace(namespace).withName(eventName).get();
        String timestamp = Instant.now(Clock.systemUTC()).toString();
        try {
            if (existing != null && existing.getType().equals(type.name()) && existing.getReason().equals(reason.name()) && existing.getInvolvedObject().getName().equals(objectName) && existing.getInvolvedObject().getKind().equals(objectKind.name())) {
                existing.setCount(existing.getCount() + 1);
                existing.setLastTimestamp(timestamp);
                kubeClient.events().inNamespace(namespace).withName(eventName).replace(existing);
            } else {
                Event newEvent = new EventBuilder()
                        .withNewMetadata()
                        .withName(eventName)
                        .endMetadata()
                        .withCount(1)
                        .withReason(reason.name())
                        .withMessage(message)
                        .withType(type.name())
                        .withNewInvolvedObject()
                        .withNamespace(namespace)
                        .withKind(objectKind.name())
                        .withName(objectName)
                        .endInvolvedObject()
                        .withFirstTimestamp(timestamp)
                        .withLastTimestamp(timestamp)
                        .withNewSource()
                        .withComponent(componentName)
                        .endSource()
                        .build();
                kubeClient.events().inNamespace(namespace).withName(eventName).create(newEvent);
            }
        } catch (KubernetesClientException e) {
            log.warn("Error reporting event", e);
        }
    }

    @Override
    public void createOrUpdate(String message, Type type, FalconResource resource) {
        this.log(FalconReason.CREATE_OR_UPDATE,"[" + resource.getSpec().getEnvironment() + "]"  + message,type,FalconKind.of(resource.getKind()),resource.getKind(),_FALCON);
    }

    @Override
    public void delete(String message, Type type, FalconResource resource) {
        this.log(FalconReason.DELETE,"[" + resource.getSpec().getEnvironment() + "]"  + message,type,FalconKind.of(resource.getKind()),resource.getKind(),_FALCON);
    }
}