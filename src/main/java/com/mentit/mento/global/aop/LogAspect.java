package com.mentit.mento.global.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Slf4j
@Component
public class LogAspect {

    @Pointcut("within(com.mentit.mento..*Controller)")
    public void controller() {}
    @Around("controller()")
    public Object loggingBefore(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        String controllerName = joinPoint.getSignature().getDeclaringType().getName();
        String methodName = joinPoint.getSignature().getName();
        Map<String, Object> params = new HashMap<>();

        try {
            String decodedURI = URLDecoder.decode(request.getRequestURI(), StandardCharsets.UTF_8);

            params.put("controller", controllerName);
            params.put("method", methodName);
            params.put("params", getParams(request));
            params.put("log_time", System.currentTimeMillis());
            params.put("request_uri", decodedURI);
            params.put("http_method", request.getMethod());
        } catch (Exception e) {
            log.error("LoggerAspect error", e);
        }

        log.info("[{}] {}", params.get("http_method"), params.get("request_uri"));
        log.info("method: {}.{}", params.get("controller"), params.get("method"));
        log.info("params: {}", params.get("params"));

        return joinPoint.proceed();
    }

    private static JSONObject getParams(HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            String replaceParam = param.replaceAll("\\.", "-");
            jsonObject.put(replaceParam, request.getParameter(param));
        }
        return jsonObject;
    }

    @AfterReturning(pointcut = "controller()", returning = "returnValue")
    public void afterReturningLogging(JoinPoint joinpoint, Object returnValue) {
        log.info("###End Request {}", joinpoint.getSignature().toShortString());

        if (returnValue == null) return;

        log.info("\t{}", returnValue);
    }

    // Service의 메서드를 포인트컷으로 지정
    @Pointcut("within(com.mentit.mento..*Service)")
    public void service() {}

    // Service 메서드 호출 전후 로깅
    @Around("service()")
    public Object loggingService(ProceedingJoinPoint joinPoint) throws Throwable {
        String serviceName = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs(); // 메서드 인자 값

        // 서비스 시작 로그
        log.info("Service method started: {}.{}()", serviceName, methodName);
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                log.info("Arg[{}]: {}", i, args[i]);
            }
        }

        long startTime = System.currentTimeMillis();

        // 실제 메서드 실행
        Object result = joinPoint.proceed();

        long elapsedTime = System.currentTimeMillis() - startTime;

        // 서비스 종료 로그
        log.info("Service method finished: {}.{}() [Execution time: {} ms]", serviceName, methodName, elapsedTime);
        log.info("Return value: {}", result);

        return result;
    }

    @AfterReturning(pointcut = "service()", returning = "returnValue")
    public void afterReturningServiceLogging(JoinPoint joinPoint, Object returnValue) {
        log.info("### Service method finished: {}", joinPoint.getSignature().toShortString());
        if (returnValue != null) {
            log.info("Service return value: {}", returnValue);
        }
    }

}
