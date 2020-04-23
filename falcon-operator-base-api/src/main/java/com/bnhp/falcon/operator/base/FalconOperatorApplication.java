package com.bnhp.falcon.operator.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(OperatorBaseConfiguration.class)
@SpringBootApplication
public class FalconOperatorApplication {
    private final static Logger log = LoggerFactory.getLogger(FalconOperatorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(FalconOperatorApplication.class, args);
    }

}
