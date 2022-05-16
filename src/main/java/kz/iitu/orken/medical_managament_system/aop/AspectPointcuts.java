package kz.iitu.orken.medical_managament_system.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AspectPointcuts {

    @Pointcut("execution(* kz.iitu.orken.medical_managament_system.service.*.*(..))")
    public void serviceOperation(){}

}
