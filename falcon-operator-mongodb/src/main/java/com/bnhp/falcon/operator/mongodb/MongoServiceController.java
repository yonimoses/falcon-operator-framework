package com.bnhp.falcon.operator.mongodb;

//import com.bnhp.falcon.operator.base.MongoService;
import com.bnhp.falcon.operator.base.FalconVoidController;
import com.bnhp.falcon.operator.base.aspect.LogAround;
import com.bnhp.falcon.operator.base.event.EventLogger;
import com.bnhp.falcon.operator.base.event.KubeEventHandler;
import com.bnhp.falcon.operator.base.utils.SpecUtils;
import com.github.containersolutions.operator.api.Controller;
import com.github.containersolutions.operator.api.ResourceController;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import java.util.Optional;
//@Controller(customResourceClass = CustomService.class,
//		crdName = "customservices.sample.javaoperatorsdk")
@Controller(crdName = "mongoservices.falcon.services",customResourceClass = MongoService.class)
public class MongoServiceController extends FalconVoidController<MongoService> implements ResourceController<MongoService>
{

	@Value("${falcon.operator.mongo.apply-to-namespace}")
	private String applyToNamespace;
	@Value("${falcon.operator.destination.crd}")
	private String destinationCRD;

//	private CustomResourceDefinitionContext _CONTEXT;
	@Autowired
	Environment environment;
	private final static Logger log = LoggerFactory.getLogger(MongoServiceController.class);
//	private final KubernetesClient client;
//	private KubeEventHandler handler;

	public MongoServiceController(KubernetesClient client, KubeEventHandler handler) {
		super(client,handler);
//		this.client = client;
//		this.handler = handler;
//     	logger = handler.get("")
	}



//	@PostConstruct
//	public void postConstruct(){
//
//	}
	/**
	 * get the context and tload the shiftup.
	 *
	 */

	/**
	 * private <T> T loadYaml(Class<T> clazz, String yaml) {
	 *     try (InputStream is = getClass().getResourceAsStream(yaml)) {
	 *       return Serialization.unmarshal(is, clazz);
	 *     } catch (IOException ex) {
	 *       throw new IllegalStateException("Cannot find yaml on classpath: " + yaml);
	 *     }
	 *   }
	 * @param resource
	 * @return
	 */

	@Override
	@LogAround("'Deleting resource ' + #resource.getMetadata().getName()")
	public boolean deleteResource(MongoService resource) {
		try{
			String name = (String)SpecUtils.toProperties(resource.getSpec().getPayload()).get("metadata.name");
			handler.get(resource.getMetadata().getNamespace())
					.createOrUpdate("Deleting deployed resource" + resource.getMetadata().getName(), EventLogger.Type.Normal,resource);
			log.debug("Event created in {}  ", resource.getMetadata().getNamespace());
			handler.get(applyToNamespace)
					.createOrUpdate("Deleting requested resource " + name + " in ns " + applyToNamespace , EventLogger.Type.Normal,resource);
			log.debug("Event created in {}  ", applyToNamespace);

//			CustomResourceDefinition definition = getCrd(destinationCRD);
//			destinationCRcrDelete(applyToNamespace,resource.getSpec().getCrd().getCtx().asContext(),resource.getSpec().getPayload());
			client.customResource(resource.getSpec().getCrd().getCtx().asContext())
					.delete(applyToNamespace, name);
			log.info("Resource {} deleted ", resource.getMetadata().getName());
		}catch (Throwable t){
			exception(t, EventLogger.FalconReason.DELETE,resource);
		}
		return false;
	}



	/**
	 * Create the object in the NS and applied the CR of MongoUser in the namespace
	 * as specified in the properties
	 * @param resource
	 * @return
	 */
	@Override
	@LogAround("'Creating/Updating resource ' + #resource.getMetadata().getName()")
	public Optional<MongoService> createOrUpdateResource(MongoService resource) {
		try{

			handler.get(resource.getMetadata().getNamespace())
					.createOrUpdate("Creating object of type " + resource.getMetadata().getName()
							+ ", environment " + resource.getSpec().getEnvironment(), EventLogger.Type.Normal,resource);
			log.debug("Event created in {}  ", resource.getMetadata().getNamespace());

			crApply(applyToNamespace,resource.getSpec().getCrd().getCtx().asContext(),resource.getSpec().getPayload());

			handler.get(resource.getMetadata().getNamespace())
					.createOrUpdate("Payload loaded "+ resource.getSpec().getPayload().substring(0,10) + "...", EventLogger.Type.Normal,resource);

			log.debug("Resource {} created/modified  ", resource.getMetadata().getName());
			return Optional.of(resource);

		}catch (Throwable t){
			exception(t, EventLogger.FalconReason.CREATE_OR_UPDATE,resource);
			return Optional.empty();
		}
	}
}
