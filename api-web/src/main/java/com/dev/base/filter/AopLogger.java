package com.dev.base.filter;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import com.dev.base.util.WebUtil;
import com.dev.base.utils.DateUtil;

/**
 * 使用AOP记日志
 */
@Aspect
@Component
@Order(100)
public class AopLogger {
	private static Logger log = LoggerFactory.getLogger(AopLogger.class);
	
    @Pointcut("within(com.dev.base.mybatis.service.BaseMybatisService+)")
    public void logService(){}
    
    @Around("logService()")
    public Object service(ProceedingJoinPoint point) throws Throwable {
    	log.info("call {}.{}{}", point.getTarget().getClass().getSimpleName(), point.getSignature().getName(), toString(point.getArgs()));
    	
    	long beginTime = System.currentTimeMillis();
    	Object result = point.proceed();
    	long time = System.currentTimeMillis() - beginTime;
    	
    	log.info("result({}) {}", time, JsonUtils.toJson(result));
    	return result;
    }
    
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void logController(){}
    
    @Around("logController()")
    public Object controller(ProceedingJoinPoint point) throws Throwable {
    	HttpServletRequest httpServletRequest = WebUtil.getHttpServletRequest();
    	log.info("{} {}", httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
    	log.info("{}.{}{}", point.getTarget().getClass().getSimpleName(), point.getSignature().getName(), toString(point.getArgs()));
    	
    	long beginTime = System.currentTimeMillis();
    	Object result = point.proceed();
    	long time = System.currentTimeMillis() - beginTime;
    	
    	log.info("END({}) {}", time, JsonUtils.toJson(result));
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
            if (i == iMax) {
                return b.append(')').toString();
            }
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
    		if(obj instanceof HttpServletRequest) {
    			return "request";
    		}else if(obj instanceof HttpServletResponse) {
    			return "response";
    		}else if(obj instanceof HttpSession){
    			return "session";
    		}else {
    			return obj.toString();
    		}
    	}
    }
}
