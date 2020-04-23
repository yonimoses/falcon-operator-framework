package com.bnhp.falcon.operator.base.aspect;

import ch.qos.logback.classic.Level;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
public @interface LogAround {

  
    String value() default "toString()";
    LogAroundLevel level() default LogAroundLevel.INFO;
}