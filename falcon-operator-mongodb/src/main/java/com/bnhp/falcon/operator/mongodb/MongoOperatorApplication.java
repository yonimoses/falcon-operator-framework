package com.bnhp.falcon.operator.mongodb;

import com.bnhp.falcon.operator.base.OperatorBaseConfiguration;
import com.bnhp.falcon.operator.base.event.KubeEventHandler;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.*;

@Configuration
@EnableAspectJAutoProxy
@SpringBootApplication
@Import(OperatorBaseConfiguration.class)
public class MongoOperatorApplication {

    private final static Logger log = LoggerFactory.getLogger(MongoOperatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MongoOperatorApplication.class, args);
    }

    @Bean
    public MongoServiceController controller(KubernetesClient client, KubeEventHandler handler) {
       return new MongoServiceController(client,handler);
    }

}
