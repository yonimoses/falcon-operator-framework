package com.bnhp.falcon.operator.base.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LogAroundAspect {
    private final static Logger log = LoggerFactory.getLogger(LogAroundAspect.class);


    @Around( value = "execution(* *(..)) && @annotation(logAround)", argNames = "pjp,logAround" )
    public Object benchmark( ProceedingJoinPoint pjp, LogAround logAround) throws Throwable
    {

        long nanosBefore = System.nanoTime();
        try
        {
            Object returned = pjp.proceed();
            log.info( "[LogAround] {} ", logAround.message() );
            return returned;
        } catch( Throwable t )
        {
            log.warn( "[LogAround] {} ", logAround.message()  );
            throw t;
        }
    }
}