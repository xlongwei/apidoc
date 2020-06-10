package com.dev.doc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.dev.base.controller.BaseController;
import com.dev.base.json.JsonUtils;
import com.dev.base.util.Pager;
import com.dev.base.utils.MapUtils;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;

/**
 * 
		* <p>Title: 接口事例相关</p>
		* <p>Description: 描述（简要描述类的职责、实现方式、使用注意事项等）</p>
		* <p>CreateDate: 2015年7月11日下午5:39:29</p>
 */
@Controller
@RequestMapping("/demo")
public class DemoController extends BaseController{
	/**
	 * 
			*@name 新增用户
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/simple/add.htm")
	public @ResponseBody Map<String, Object> simpleAddUser(HttpServletRequest request,
			String email,String nickName,Integer age){
		Map<String, Object> result = MapUtils.newMap();
		result.put("userId", 123);
		
		return result;
	}
	
	/**
	 * 
			*@name 查询用户列表
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/simple/list.htm")
	public @ResponseBody List<Map<String, Object>> simpleListUser(HttpServletRequest request){
		List<Map<String, Object>> result = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Map<String, Object> info = MapUtils.newMap();
			info.put("email", "邮箱" + i);
			info.put("nickName", "昵称" + i);
			info.put("userId", i);
			
			result.add(info);
		}
		
		return result;
	}
	
	@RequestMapping("/user/simple/pager.htm")
	public @ResponseBody Pager simplePagerUser(HttpServletRequest request,@RequestParam(defaultValue="1") Integer pageNumber,@RequestParam(defaultValue="10") Integer pageSize,@RequestParam(defaultValue="0") Integer totalCount) {
		Pager pager = new Pager(pageNumber, pageSize);
		if(totalCount<=0) pager.setTotalCount(123);
		int start = pager.getStart();
		List<Map<String, Object>> result = new ArrayList<>();
		for (int i = 1; i <= pageSize; i++) {
			int serial = start+i;
			Map<String, Object> info = MapUtils.newMap();
			info.put("email", "邮箱" + serial);
			info.put("nickName", "昵称" + serial);
			info.put("userId", serial);
			
			result.add(info);
		}
		pager.setList(result);
		return pager;
	}
	
	/**
	 * 
			*@name 删除用户
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/simple/{userId}/del.htm")
	public @ResponseBody ErrorInfo simpleDelUser(HttpServletRequest request,@PathVariable("userId")Long userId){
		return new ErrorInfo("200", "删除成功");
	}
	
	/**
	 * 
			*@name 查询用户
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/simple/{userId}/info.htm")
	public @ResponseBody SimpleUserInfo simpleGetUser(HttpServletRequest request,@PathVariable("userId")Long userId){
		SimpleUserInfo userInfo = new SimpleUserInfo();
		userInfo.setAge(12);
		userInfo.setEmail("demo");
		userInfo.setNickName("demo");
		
		return userInfo;
	}
	
	/**
	 * 
			*@name 更新用户
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/simple/{userId}/update.htm")
	public @ResponseBody ErrorInfo simpleUpdateUser(HttpServletRequest request,@PathVariable("userId")Long userId,String nickName){
		return new ErrorInfo("200", "更新成功");
	}
	
	/**
	 * 
			*@throws IOException 
	 * @name 新增用户
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/complex/add.htm")
	public @ResponseBody Map<String, Object> complexAddUser(HttpServletRequest request,@RequestBody SimpleUserInfo info) throws IOException{
//		String userInfo = IOUtils.toString(request.getInputStream(), "UTF-8");
		
		Map<String, Object> result = MapUtils.newMap();
		result.put("userId", 123);
		
		return result;
	}
	
	/**
	 * 
			*@name 查询用户列表
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/complex/list.htm")
	public @ResponseBody Map<String, Object> complexListUser(HttpServletRequest request){
		List<SimpleUserInfo> list = new ArrayList<SimpleUserInfo>();
		for (int i = 1; i < 3; i++) {
			SimpleUserInfo info = new SimpleUserInfo();
			info.setAge(i);
			info.setEmail("email" + i);
			info.setNickName("nickName" + i);
			list.add(info);
		}
		
		Map<String,Object> data = MapUtils.newMap();
		data.put("totalCount", list.size());
		data.put("list", list);
		
		Map<String,Object> result = MapUtils.newMap();
		result.put("data", data);
		result.put("errorCode", "200");
		
		return result;
	}
	
	/**
	 * 
			*@name 查询用户
			*@Description  
			*@CreateDate 2015年7月11日下午2:05:24
	 */
	@RequestMapping("/user/complex/{userId}/info.htm")
	public @ResponseBody Map<String, Object> complexGetUser(HttpServletRequest request,@PathVariable("userId")Long userId){
		List<Map<String, Object>> addressList = new ArrayList<>();
		for (int i = 1; i < 3; i++) {
			Map<String, Object> info = MapUtils.newMap();
			info.put("street", "street" + i);
			info.put("city", "city" + i);
			addressList.add(info);
		}
		
		Map<String,Object> result = MapUtils.newMap();
		result.put("userId", 1024);
		result.put("age", 10);
		result.put("nickName", "demo");
		result.put("addressList",addressList);
		
		return result;
	}
	
	/**
	 * 
			*@name 上传接口
			*@Description  
			*@CreateDate 2015年10月15日下午11:03:06
	 */
	@RequestMapping(value = "/user/simple/{userId}/upload.htm", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Map<String, Object> uploadImg(@RequestParam(value = "img") MultipartFile file) throws Exception {
		System.out.println(file.getName() + "," + file.getContentType());
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping(value = "/log.htm", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public Object log(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String loggerName = request.getParameter("logger");
		List<ch.qos.logback.classic.Logger> loggers = null;
		if(!StringUtils.isBlank(loggerName)) {
			ch.qos.logback.classic.Logger logger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(loggerName);
			loggers = Arrays.asList(logger);
			if(!loggers.isEmpty()) {
				String levelName = request.getParameter("level");
				Level level = Level.toLevel(levelName, null);
				log.info("change logger:{} level from:{} to:{}", logger.getName(), logger.getLevel(), level);
				logger.setLevel(level);
			}
		}
		if(loggers == null) {
			LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
			loggers = lc.getLoggerList();
		}
		log.info("check logger level, loggers:{}", loggers.size());
		List<Map<String, String>> list = new ArrayList<>();
		for(ch.qos.logback.classic.Logger logger : loggers) {
			HashMap<String, String> map = new HashMap<>();
			map.put("logger", logger.getName());
			map.put("level", Objects.toString(logger.getLevel(), ""));
			list.add(map);
		}
		return MapUtils.getSingleMap("loggers", list);
	}
	/**
	 * 
			*@name 
			*@Description  
			*@CreateDate 2015年10月15日下午11:03:06
	 */
//	@RequestMapping(value = "/test.htm", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
//	@ResponseBody
//	public Map<String, Object> uploadImg(HttpServletRequest request) throws Exception {
//		String apiDoc = request.getParameter("apiDoc");
//		String fileName = request.getParameter("fileName");
//		return JsonUtils.createSuccess();
//	}
}

class SimpleUserInfo {
	private String nickName;
	private String email;
	
	private int age;

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}

class ErrorInfo {
	private String errorCode;
	private String errorMsg;
	
	public ErrorInfo(String errorCode,String errorMsg) {
		this.errorCode = errorCode;
		this.errorMsg = errorMsg;
	}
	
	public String getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getErrorMsg() {
		return errorMsg;
	}
	
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
