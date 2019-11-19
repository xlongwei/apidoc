package com.dev.base.filter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dev.base.concurrent.TaskUtils;
import com.dev.base.constant.CfgConstants;
import com.google.common.cache.CacheBuilder;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Aspect
@Component
@Order(200)
public class AopCache implements InitializingBean {
	private static Logger log = LoggerFactory.getLogger(AopCache.class);
	private @Autowired(required=false) CacheManager cacheManager;
	private int cacheStatus = -1;//-1待初始化 0不可用 1可用
	private String[] cacheMethods = {"list", "count", "get"};
	private String[] evictMethods = {"add", "batchAdd", "update", "delete", "delBy"};
	private Map<String, Boolean> classNames = new HashMap<>();
	private Map<String, Class<?>> returnTypes = new HashMap<>();
	
    @Pointcut("within(com.dev.base.mybatis.service.BaseMybatisService+)")
    public void cacheService(){}
    
    @Around("cacheService()")
    public Object cacheService(ProceedingJoinPoint point) throws Throwable {
    	if(cacheStatus == 1) {
    		Class<? extends Object> clazz = point.getTarget().getClass();
    		String className = clazz.getSimpleName();
    		String methodName = point.getSignature().getName();
    		boolean supportCache = supportCache(clazz, className);
    		if(supportCache) {
	    		if(supportMethod(cacheMethods, methodName)) {
	    			String cacheKey = methodName + AopLogger.toString(point.getArgs());
	    			String typeKey = className+"."+methodName;
	    			Object ret = null;
	    			Cache cache = cacheManager.getCache(className);
	    			Class<?> methodReturn = returnTypes.get(typeKey);
	    			if(methodReturn!=null && (ret=cachedObject(cache, cacheKey, methodReturn))!=null) {
    					log.info("get from cache {}:{}", className, cacheKey);
    					return ret;
	    			}
	    			if(ret==null) {
	    				ret = point.proceed();
	    				if(ret!=null) {
	    					log.info("put to cache {}:{}", className, cacheKey);
	    					cache.put(cacheKey, ret);
	    					if(methodReturn==null) {
	    						returnTypes.put(typeKey, ret.getClass());
	    					}
	    				}
	    			}
	    			return ret;
	    		}else {
	    			cacheManager.getCache(className).clear();
	    			log.info("evice cache {}", className);
	    			return point.proceed();
	    		}
    		}
			return point.proceed();
    	}else {
    		if(cacheStatus == -1) {
    			TaskUtils.execAsyn(new Runnable() {
					@Override
					public void run() {
						initCacheManager();
					}
    			});
    		}
    		return point.proceed();
    	}
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
    	initCacheManager();
	}

	private boolean supportCache(Class<?> clazz, String className) {
    	Boolean support = classNames.get(className);
    	if(support==null) {
    		for(Method method : clazz.getDeclaredMethods()) {
    			if(Modifier.isPublic(method.getModifiers())) {
	    			String methodName = method.getName();
	    			if(supportMethod(cacheMethods, methodName)==false
	    					&& supportMethod(evictMethods, methodName)==false) {
	    				support = Boolean.FALSE;
	    				break;
	    			}
    			}
    		}
    		classNames.put(className, support==null ? (support=Boolean.TRUE) : support);
    	}
		return support.booleanValue();
	}
    
    private boolean supportMethod(String[] methods, String methodName) {
    	for(String method : methods) {
    		if(methodName.startsWith(method)) {
    			return true;
    		}
    	}
    	return false;
    }
    
    private <T> T cachedObject(Cache cache, String key, Class<T> returnType) {
    	ValueWrapper valueWrapper = cache.get(key);
    	if(valueWrapper!=null) {
    		Object object = valueWrapper.get();
    		//解决GuavaCache取出null时抛异常
    		if(object!=null && returnType.isInstance(object)) {
    			return returnType.cast(object);
    		}
    	}
    	return null;
    }

	private synchronized void initCacheManager() {
    	if(cacheManager==null) {
    		boolean cacheEnabled = BooleanUtils.toBoolean(CfgConstants.getProperty("cache.enabled", "true"));
    		if(cacheEnabled) {
	    		try{
	    			JedisPoolConfig poolConfig = new JedisPoolConfig();
	    			String host = CfgConstants.getProperty("redis.host", null);
	    			if(StringUtils.isNotBlank(host)) {
	    				String password = StringUtils.trimToNull(CfgConstants.getProperty("redis.password", null));
		    			int port = NumberUtils.toInt(CfgConstants.getProperty("redis.port", "6379"));
		    			int db = NumberUtils.toInt(CfgConstants.getProperty("redis.db", "0"));
		    			int timeout = NumberUtils.toInt(CfgConstants.getProperty("redis.timeout", "1000"));
		    			int expire = NumberUtils.toInt(CfgConstants.getProperty("cache.expire", "3600"));
		    			JedisPool jedisPool = new JedisPool(poolConfig, host, port, timeout, password, db);
		    			Jedis jedis = jedisPool.getResource();
		    			jedis.ping();
		    			jedis.close();
		    			cacheManager = new RedisCacheManager(jedisPool, expire);
		    			log.info("using redis cache");
	    			}
	    		}catch(Exception e) {
	    			log.warn(e.getMessage());
	    		}
	    		if(cacheManager==null) {
	    			log.info("using guava cache");
	    			GuavaCacheManager guavaCacheManager = new GuavaCacheManager();
	    			CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder();
	    			cacheBuilder.maximumSize(NumberUtils.toInt(CfgConstants.getProperty("guava.maximunSize", "1000")))
	    						.expireAfterWrite(NumberUtils.toInt(CfgConstants.getProperty("cache.expire", "3600")), TimeUnit.SECONDS);
	    			guavaCacheManager.setCacheBuilder(cacheBuilder);
	    			guavaCacheManager.setAllowNullValues(false);
	    			cacheManager = guavaCacheManager;
	    		}
    		}else {
    			log.info("cache not enabled");
    		}
    	}else {
    		log.info("cache manager: {}", cacheManager);
    	}
    	cacheStatus = cacheManager==null ? 0 : 1;
    }
	
	public static class RedisCacheManager implements CacheManager {
		int expire = 0;
		private JedisPool jedisPool;
		private Map<String, RedisCache> redisCaches = new HashMap<>();
		public RedisCacheManager(JedisPool jedisPool, int expire) {
			this.jedisPool = jedisPool;
			this.expire = expire;
		}

		@Override
		public Cache getCache(String name) {
			RedisCache redisCache = redisCaches.get(name);
			if(redisCache==null) {
				redisCache = new RedisCache(this, name);
				redisCaches.put(name, redisCache);
			}
			return redisCache;
		}

		@Override
		public Collection<String> getCacheNames() {
			return redisCaches.keySet();
		}
		
		public <T> T execute(JedisCallback<T> callback) {
			try(Jedis jedis = jedisPool.getResource()) {
				return callback.doInJedis(jedis);
			}catch(Exception e){
				return null;
			}
		}
		
		public static byte[] byteKey(String cache, String key) {
			return (cache+":"+key).getBytes(StandardCharsets.UTF_8);
		}
		
		public static byte[] byteValue(Object value) {
			if(value == null || !(value instanceof Serializable)) {
				return null;
			}
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(value);
				oos.flush();
				return baos.toByteArray();
			}catch(Exception e) {
				return null;
			}
		}
		
		public static Object objectValue(byte[] byteValue) {
			try {
				if(byteValue==null || byteValue.length==0) {
					return null;
				}
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(byteValue));
				return ois.readObject();
			}catch(Exception e) {
				return null;
			}
		}
	}
	
	public static class RedisCache implements Cache {
		private RedisCacheManager redisCacheManager;
		private String name = null;
		public RedisCache(RedisCacheManager redisCacheManager, String name) {
			this.redisCacheManager = redisCacheManager;
			this.name = name;
		}
		@Override
		public String getName() {
			return name;
		}
		@Override
		public Object getNativeCache() {
			return this;
		}
		@Override
		public ValueWrapper get(final Object key) {
			return redisCacheManager.execute(new JedisCallback<ValueWrapper>() {
				@Override
				public ValueWrapper doInJedis(Jedis jedis) {
					byte[] byteKey = RedisCacheManager.byteKey(name, key.toString());
					byte[] byteValue = jedis.get(byteKey);
					Object object = RedisCacheManager.objectValue(byteValue);
					return object==null ? null : new SimpleValueWrapper(object);
				}
			});
		}
		@Override
		public <T> T get(final Object key, final Class<T> type) {
			ValueWrapper valueWrapper = get(key);
			if(valueWrapper!=null) {
				Object object = valueWrapper.get();
				if(object!=null && type.isInstance(object)) {
					return type.cast(object);
				}
			}
			return null;
		}
		@Override
		public void put(final Object key, final Object value) {
			redisCacheManager.execute(new JedisCallback<String>() {
				@Override
				public String doInJedis(Jedis jedis) {
					byte[] byteKey = RedisCacheManager.byteKey(name, key.toString());
					byte[] byteValue = RedisCacheManager.byteValue(value);
					if(ArrayUtils.isNotEmpty(byteValue)) {
						jedis.set(byteKey, byteValue);
						jedis.expire(byteKey, redisCacheManager.expire);
					}
					return null;
				}
			});
		}
		@Override
		public void evict(final Object key) {
			redisCacheManager.execute(new JedisCallback<String>() {
				@Override
				public String doInJedis(Jedis jedis) {
					jedis.del(keystr(key));
					return null;
				}
			});
		}
		@Override
		public void clear() {
			redisCacheManager.execute(new JedisCallback<String>() {
				@Override
				public String doInJedis(Jedis jedis) {
					Set<String> keys = jedis.keys(name+"*");
					if(!CollectionUtils.isEmpty(keys)) {
						jedis.del(keys.toArray(new String[keys.size()]));
					}
					return null;
				}
			});
		}
		private String keystr(Object key) {
			return new StringBuilder(name).append(":").append(key).toString();
		}
	}
	
	public interface JedisCallback<T> {
		T doInJedis(Jedis jedis);
	}
}
