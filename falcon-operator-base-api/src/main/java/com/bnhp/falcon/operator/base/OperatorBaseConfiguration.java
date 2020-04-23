package com.bnhp.falcon.operator.base;

import com.bnhp.falcon.operator.base.event.KubeEventHandler;
import com.github.containersolutions.operator.Operator;
import com.github.containersolutions.operator.api.ResourceController;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.*;

import java.util.List;

@Configuration
@Slf4j
@EnableAspectJAutoProxy
public class OperatorBaseConfiguration {

    private final static Logger log = LoggerFactory.getLogger(OperatorBaseConfiguration.class);
    @Bean
    public KubernetesClient kubernetesClient() {
        return new DefaultKubernetesClient();
    }

    @Bean
    public KubeEventHandler eventHandler() {
        return new KubeEventHandler(kubernetesClient());
    }
//    @Bean
//    public FalconVoidController controller(KubernetesClient client, KubeEventHandler handler) {
//        return new FalconVoidController(client,handler);
//    }
    @Bean
    public Operator operator(KubernetesClient client, List<ResourceController> controllers) {
        Operator operator = new Operator(client);
        log.info("Registering all resource controllers --> {}",controllers);
        controllers.forEach(operator::registerControllerForAllNamespaces);
        return operator;
    }

}