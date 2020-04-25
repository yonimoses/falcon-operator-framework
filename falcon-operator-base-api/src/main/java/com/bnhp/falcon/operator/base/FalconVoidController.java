package com.bnhp.falcon.operator.base;

import com.bnhp.falcon.operator.base.event.EventLogger;
import com.bnhp.falcon.operator.base.service.FalconResource;
import com.bnhp.falcon.operator.base.event.KubeEventHandler;
import com.github.containersolutions.operator.api.ResourceController;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.utils.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;

import static com.bnhp.falcon.operator.base.event.KubeEventLogger._FALCON;

/**
 */

public abstract class FalconVoidController<T extends FalconResource> implements ResourceController<T>{

    private final static Logger log = LoggerFactory.getLogger(FalconVoidController.class);
    protected final KubernetesClient client;
    protected final KubeEventHandler handler;

    public FalconVoidController(KubernetesClient client, KubeEventHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    protected boolean isProcessed(T resource){
        log.debug("isProcessed {} ", resource.falconId());
        return false;
    }

    protected <T> T loadYaml(Class<T> clazz, String yaml) {
	      try (InputStream is = getClass().getResourceAsStream(yaml)) {
	        return Serialization.unmarshal(is, clazz);
	     } catch (IOException ex) {
	        throw new IllegalStateException("Cannot find yaml on classpath: " + yaml);
	      }
	  }

     protected CustomResourceDefinition getCrd(String destinationCRD) {


         return client
                 .customResourceDefinitions()
                 .list()
                 .getItems()
                 .stream()
                 .filter(d -> destinationCRD.equals(d.getMetadata().getName()))
                 .findAny()
                 .orElseThrow(
                         () -> new RuntimeException("Custom resource definition "  +destinationCRD + " not found."));

     }

     protected void exception(Throwable t, EventLogger.FalconReason reason,T resource) {
        log.error("Exception on action " + reason.name() + ", message " + t.getMessage(),t);
         String message ;
         if(t instanceof KubernetesClientException){
              message = ((KubernetesClientException) t).getStatus().getMessage();
         }else{
             message = rootCause(t);
         }
        handler.get(resource.getMetadata().getNamespace())
                 .log(reason,message, EventLogger.Type.Error, EventLogger.FalconKind.of(resource.getKind()),resource.getKind(),_FALCON);

       //  log.error("Exception will be logged in NS " + resource.getMetadata().getNamespace());
     }

    private String rootCause(Throwable t) {

        Throwable rootCause = t;
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }
        return rootCause.getMessage();
    }

    protected void apply(String namespace, String yamlString)  {
         log.debug("Applying in namespace {} resource \r\n\n {} \r\n\n", namespace,yamlString);
         List<HasMetadata> result = client.load(new ByteArrayInputStream(yamlString.getBytes())).get();
         log.info("Loaded yaml for applying in namespace {} ", namespace);
         // Apply Kubernetes Resources
         client.resourceList(result).inNamespace(namespace).createOrReplace();
         log.info("Resource applied in namespace {}", namespace);

     }

    protected void crApply(String namespace, CustomResourceDefinitionContext ctx, String yamlString) throws Throwable {
        log.debug("Applying in namespace {} resource \r\n\n {} \r\n\n", namespace,yamlString);
        client.customResource(ctx).create(namespace,yamlString);
        log.info("Resource applied in namespace {}", namespace);

    }


    private String normalize(String toYaml){
         DumperOptions options = new DumperOptions();
         options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
         options.setPrettyFlow(true);
         Yaml yaml = new Yaml(options);
         Map<String, Object> obj = yaml.load(toYaml);

         StringWriter writer = new StringWriter();
         yaml.dump(obj, writer);
         return writer.toString();
     }





//    @Override
//    public boolean deleteResource(FalconService resource) {
//        log.info("Execution deleteResource for: {}", resource.getMetadata().getName());
//        kubernetesClient.services().inNamespace(resource.getMetadata().getNamespace())
//                .withName(resource.getMetadata().getName()).delete();
//
//        this.handler.get(resource.getMetadata().getNamespace())
//                .log(EventLogger.FalconReason.DELETE, resource.getMetadata().getName() + " deleted", EventLogger.Type.Normal, EventLogger.FalconKind.CR,
//                        resource.getMetadata().getName() ,resource.getMetadata().getName());
//        this.handler.get(kubernetesClient.getNamespace())
//                .log(EventLogger.FalconReason.CREATE_OR_UPDATE, resource.getMetadata().getName() + " deleted ", EventLogger.Type.Normal, EventLogger.FalconKind.CR,
//                        resource.getMetadata().getName() ,resource.getMetadata().getName());
//        return true;
//    }
////
//    @Override
//    public Optional<FalconService> createOrUpdateResource(FalconService resource) {
//        log.info("Execution createOrUpdateResource for: {}", resource.getMetadata().getName());
//
////        ServicePort servicePort = new ServicePort();
////        servicePort.setPort(8080);
////        ServiceSpec serviceSpec = new ServiceSpec();
////        serviceSpec.setPorts(Arrays.asList(servicePort));
//
//
//        this.handler.get(resource.getMetadata().getNamespace())
//                .log(EventLogger.FalconReason.CREATE_OR_UPDATE, resource.getMetadata().getName() + " created ", EventLogger.Type.Normal, EventLogger.FalconKind.CR,
//                        resource.getMetadata().getName() ,resource.getMetadata().getName());
//        this.handler.get(kubernetesClient.getNamespace())
//                .log(EventLogger.FalconReason.CREATE_OR_UPDATE, resource.getMetadata().getName() + " created ", EventLogger.Type.Normal, EventLogger.FalconKind.CR,
//                        resource.getMetadata().getName() ,resource.getMetadata().getName());
//        this.dispatcher.dispatch(resource);
////        kubernetesClient.events().inNamespace(resource.getMetadata().getNamespace()).createOrReplaceWithNew()
////                .withNewMetadata()
////                .withName(resource.getSpec().getName())
////                .addToLabels("testLabel", resource.getSpec().getLabel())
////                .endMetadata()
////                .withSpec(serviceSpec)
////                .done();
//
////        resource.setSpec(new FalconServiceSpec().setLabel(""););
////        CustomResourceDefinitionContexsourceDefinitionContext context =
////        new CustomResourceDefinitionContext.Builder().
//       // kubernetesClient.c
//    //    kubernetesClient.
////        kubernetesClient.services().inNamespace(resource.getMetadata().getNamespace()).createOrReplaceWithNew()
////                .withNewMetadata()
////                .withName(resource.getSpec().getName())
////                .addToLabels("testLabel", resource.getSpec().getLabel())
////                .endMetadata()
////                .withSpec(serviceSpec)
////                .done();
//        return Optional.of(resource);
//    }
}
