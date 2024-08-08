package com.and20roid.backend.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("execution(* com.and20roid.backend.service.*.*(..))")
    public void service() {
    }

    @Around("service()")
    public Object loggingBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String logMessage = buildLogMessage(methodName, joinPoint.getArgs(),
                (MethodSignature) joinPoint.getSignature());

        log.info(logMessage);

        return joinPoint.proceed();
    }

    private String buildLogMessage(String methodName, Object[] args, MethodSignature signature) {
        StringBuilder sb = new StringBuilder();
        appendMethodName(sb, methodName);
        appendParameters(sb, signature, args);
        return sb.toString();
    }

    private void appendMethodName(StringBuilder sb, String methodName) {
        sb.append("start ").append(methodName).append(" by ");
    }

    private void appendParameters(StringBuilder sb, MethodSignature signature, Object[] args) {
        String[] parameterNames = signature.getParameterNames();
        if (args.length > 0) {
            appendArguments(sb, parameterNames, args);
        } else {
            sb.append("no arguments");
        }
    }

    private void appendArguments(StringBuilder sb, String[] parameterNames, Object[] args) {
        for (int i = 0; i < args.length; i++) {
            sb.append(parameterNames[i]).append(": [");
            appendArgument(sb, args[i]);
            sb.append("], ");
        }

        // remove comma and space
        sb.setLength(sb.length() - 2);
    }

    private void appendArgument(StringBuilder sb, Object arg) {
        if (arg instanceof MultipartFile) {
            appendMultipartFile(sb, (MultipartFile) arg);
        } else {
            sb.append(arg);
        }
    }

    private void appendMultipartFile(StringBuilder sb, MultipartFile file) {
        sb.append(file.getName()).append(": [").append(file.getOriginalFilename()).append("]");
    }
}