package com.bnhp.falcon.operator.base.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to benchmark it using {@link LogAroundAspect}.
 * Here is a code sample for method benchmark :
 * <pre>
 * <code>
 * &#064;(message = "saveOrUpdate(#{args[0]}, #{args[1]}): #{returned}")
 *  public int saveOrUpdate(String arg1, String arg2) { ... }
 * </code>
 * </pre>
 */
@Retention( RetentionPolicy.RUNTIME )
@Target( { ElementType.METHOD } )
public @interface LogAround {

    /**
     * <p>
     * Available variables
     * </p>
     * <ul>
     * <li><code>args</code></li>
     * <li><code>invokedObject</code></li>
     * <li><code>throwned</code></li>
     * <li><code>returned</code></li>
     * </ul>
     * <p>
     * Sample :<code>"saveOrUpdate(#{args[0]}, #{args[1]}): #{returned}"</code>
     * </p>
     */
    String message() default "";
}