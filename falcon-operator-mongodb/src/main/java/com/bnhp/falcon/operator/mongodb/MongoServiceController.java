package com.bnhp.falcon.operator.mongodb;

//import com.bnhp.falcon.operator.base.MongoService;
import com.bnhp.falcon.operator.base.FalconVoidController;
import com.bnhp.falcon.operator.base.event.EventLogger;
import com.bnhp.falcon.operator.base.event.KubeEventHandler;
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

	private CustomResourceDefinitionContext _CONTEXT;
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
	@PostConstruct
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
	public boolean deleteResource(MongoService resource) {
		try{

			CustomResourceDefinition definition = getCrd(destinationCRD);
			System.out.println("definition = " + definition);
		}catch (Throwable t){
			exception(t, EventLogger.FalconReason.DELETE,resource);
		}


		return true;
	}

	@Override
	public Optional<MongoService> createOrUpdateResource(MongoService resource) {
		try{

			log.debug("createOrUpdateResource {} ", resource.getMetadata().getName());
			handler.get(resource.getMetadata().getNamespace())
					.createOrUpdate("Creating object ", EventLogger.Type.Normal,resource);

			apply(applyToNamespace,resource.getSpec().getPayload());

			handler.get(resource.getMetadata().getNamespace())
					.createOrUpdate("Payload loaded "+ resource.getSpec().getPayload().substring(0,10) + "...", EventLogger.Type.Normal,resource);
			return Optional.of(resource);

		}catch (Throwable t){
			exception(t, EventLogger.FalconReason.CREATE_OR_UPDATE,resource);
			return Optional.empty();
		}
	}
}
