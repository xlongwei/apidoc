package com.dev.base.filter;

import java.util.Date;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.dev.base.json.JsonUtils;
import com.dev.base.util.Pager;
import com.dev.base.utils.DateUtil;

/**
 * 使用AOP记日志并捕获异常
 */
@Aspect
@Component
@Order(100)
public class AopLogger {
	private static Logger log = LoggerFactory.getLogger(AopLogger.class);
    @Pointcut("within(com.dev.base.mybatis.service.BaseMybatisService+)")
    public void logService(){}
    
    /** 记录自定义service接口日志，如果要记录CoreService所有接口日志请仿照logMapper切面 */
    @Around("logService()")
    public Object service(ProceedingJoinPoint point) throws Throwable {
    	log.info("call {}.{}{}", point.getTarget().getClass().getSimpleName(), point.getSignature().getName(), toString(point.getArgs()));
    	
    	long beginTime = System.currentTimeMillis();
    	Object result = point.proceed();
    	long time = System.currentTimeMillis() - beginTime;
    	
    	log.info("result({}) {}", time, JsonUtils.toJson(result));
    	return result;
    }
    
    public static String toString(Object[] a) {
    	if(a==null || a.length==0) {
    		return "()";
    	}
    	int iMax = a.length - 1;
    	StringBuilder b = new StringBuilder();
    	b.append('(');
    	for (int i = 0; ; i++) {
            b.append(toString(a[i]));
            if (i == iMax)
                return b.append(')').toString();
            b.append(", ");
        }
    }
    
    public static String toString(Object obj) {
    	if(obj==null) {
    		return "null";
    	}else if(Date.class==obj.getClass()) {
    		return DateUtil.formatByLong((Date)obj);
    	}else if(Pager.class==obj.getClass()){
    		Pager pager = (Pager)obj;
    		return new StringBuilder("pager.").append(pager.getPageNumber()).append(',').append(pager.getPageSize()).toString();
    	}else {
    		return obj.toString();
    	}
    }
}
