package com.bnhp.falcon.operator.base.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.stream.IntStream;

@Aspect
@Component
public class LogAroundAspect {
    private final static Logger LOG = LoggerFactory.getLogger(LogAroundAspect.class);

    private ExpressionParser elParser = new SpelExpressionParser();

    @Around( value = "execution(* *(..)) && @annotation(annotation)", argNames = "pjp,annotation" )
    public Object benchmark( ProceedingJoinPoint pjp, LogAround annotation) throws Throwable
    {
        Object returnValue = pjp.proceed();
        Object target = pjp.getTarget();
        Object[] args = pjp.getArgs();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String message = getLogMessage(returnValue, signature.getParameterNames(), args, annotation);
        log(target, annotation.level(), message);
        LOG.info("Logging Aspect Activated on {}.{}, return value: {}", signature.getDeclaringTypeName(), signature.getName(), returnValue);
        return returnValue;
    }

    private String getLogMessage(Object returnValue, String[] argNames, Object[] args, LogAround annotation) {
        Expression exp = elParser.parseExpression(annotation.value());
        EvaluationContext ctx = new StandardEvaluationContext(returnValue);
        populateELContext(ctx, argNames, args);
        return exp.getValue(ctx, String.class);
    }

    private void log(Object target, LogAroundLevel logAroundLevel, String message) {
        Logger logger = LoggerFactory.getLogger(target.getClass());
        logAroundLevel.getLoggerFunction().accept(logger, message);
    }

    private void populateELContext(EvaluationContext ctx, String[] argNames, Object[] args) {
        IntStream.range(0, argNames.length)
                .forEach(i -> ctx.setVariable(argNames[i], args[i]));
    }

    @Autowired
    public void setEnvironment(Environment e) {
        LOG.info("Annotation Logging Aspect Initialized: " + Arrays.asList(e.getActiveProfiles()));
    }
}