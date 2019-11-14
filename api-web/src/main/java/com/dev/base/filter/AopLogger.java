package com.dev.base.filter;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dev.base.json.JsonUtils;

/**
 * 使用AOP记日志并捕获异常
 */
@Aspect
@Component
public class AopLogger {
	private static Logger log = LoggerFactory.getLogger(AopLogger.class);
    //@Pointcut("execution(public * com.dev.*.service.*.*(..))")
    @Pointcut("within(com.dev.base.mybatis.service.BaseMybatisService+)")
    public void logService(){}
    
    /** 记录自定义service接口日志，如果要记录CoreService所有接口日志请仿照logMapper切面 */
    @Around("logService()")
    public Object service(ProceedingJoinPoint point) throws Throwable {
    	log.info("call {}.{}{}", point.getTarget().getClass().getSimpleName(), point.getSignature().getName(), Arrays.toString(point.getArgs()));
    	
    	long beginTime = System.currentTimeMillis();
    	Object result = point.proceed();
    	long time = System.currentTimeMillis() - beginTime;
    	
    	log.info("result({}) {}", time, JsonUtils.toJson(result));
    	return result;
    }
}
