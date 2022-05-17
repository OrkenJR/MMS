package kz.iitu.orken.medical_managament_system.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Aspect
@Component
public class LoggingAspect {

    @Around("@annotation(LogAnnotation) || kz.iitu.orken.medical_managament_system.aop.AspectPointcuts.serviceOperation()")
    public Object log(ProceedingJoinPoint point) throws Throwable {
        CodeSignature codeSignature = (CodeSignature) point.getSignature();
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        Method method = methodSignature.getMethod();
        Logger logger = LoggerFactory.getLogger(method.getDeclaringClass());
        LogAnnotation annotation = method.getAnnotation(LogAnnotation.class);
        LogLevel level = annotation != null ? annotation.value() : LogLevel.INFO;
        ChronoUnit unit = ChronoUnit.SECONDS;

        String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();
        Object[] methodArgs = point.getArgs();
        String[] methodParams = codeSignature.getParameterNames();
        log(logger, level, startMessage(methodName, methodParams, methodArgs));
        Instant start = Instant.now();
        Object response = point.proceed();
        Instant end = Instant.now();
        String duration = String.format("%s %s", unit.between(start, end), unit.name().toLowerCase());
        log(logger, level, finishMessage(methodName, duration, response));
        return response;
    }

    static String startMessage(String methodName, String[] params, Object[] args) {
        StringJoiner message = new StringJoiner(" ")
                .add("Started executing").add(methodName).add("method");
        if (Objects.nonNull(params) && Objects.nonNull(args) && params.length == args.length) {
            Map<String, Object> values = new HashMap<>(params.length);
            for (int i = 0; i < params.length; i++) {
                values.put(params[i], args[i]);
            }
            message.add("with args:")
                    .add(values.toString());
        }
        return message.toString();
    }

    static String finishMessage(String methodName, String duration, Object result) {
        StringJoiner message = new StringJoiner(" ")
                .add("Finished").add(methodName).add("method");
        message.add("in").add(duration);
        message.add("with return:").add(Optional.ofNullable(result).orElse("Nullpointerexception").toString());
        return message.toString();
    }

    static void log(Logger logger, LogLevel level, String message) {
        switch (level) {
            case DEBUG:
                logger.debug(message);
                break;
            case TRACE:
                logger.trace(message);
                break;
            case WARN:
                logger.warn(message);
                break;
            case ERROR:
                logger.error(message);
                break;
            default:
                logger.info(message);
                break;
        }

    }

}
