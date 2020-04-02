package com.dev.doc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.SimpleBindings;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dev.base.controller.BaseController;
import com.dev.base.enums.ParamPosition;
import com.dev.base.enums.SchemaType;
import com.dev.base.json.JsonUtils;
import com.dev.base.utils.MapUtils;
import com.dev.doc.entity.Inter;
import com.dev.doc.entity.InterMock;
import com.dev.doc.entity.InterParam;
import com.dev.doc.entity.InterResp;
import com.dev.doc.entity.RespSchema;
import com.dev.doc.service.InterMockService;
import com.dev.doc.service.InterParamService;
import com.dev.doc.service.InterRespService;
import com.dev.doc.service.InterService;
import com.dev.doc.service.RespSchemaService;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterators;

import io.swagger.util.Json;

@RestController
@RequestMapping("/mock")
public class MockController extends BaseController {
	private static final String MOCK = "/mock";
	private static Logger logger = LogManager.getLogger(MockController.class);
	
	@Autowired
	InterService interService;
	
	@Autowired
	InterRespService interRespService;
	
	@Autowired
	RespSchemaService respSchemaService;
	
	@Autowired
	InterMockService interMockService;
	
	@Autowired
	InterParamService interParamService;

	@RequestMapping("**")
	public Object mock(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String requestURI = request.getRequestURI(), referer = request.getHeader("referer");
		String path = requestURI.substring(requestURI.indexOf(MOCK)+MOCK.length()), docId = request.getParameter("docId");
		if(StringUtils.isBlank(docId)) {
			docId = UriComponentsBuilder.fromUriString(referer).build().getQueryParams().getFirst("doc");
		}
		Inter inter = interService.getByMethodPath(docId, request.getMethod(), path);
		if(inter!=null) {
			Map<String, String> params = params(request, inter);
			Object resp = mockResp(params, inter);
			if(resp == null) {
				if(!CollectionUtils.isEmpty(params)) {
					response.addHeader("reqSchema", JsonUtils.toJson(params));
				}
				resp = interResp(inter);
			}
			if(resp != null) {
				return resp;
			}
		}
		return Collections.emptyMap();
	}
	
	/** 尝试响应接口模拟信息 */
	private Object mockResp(Map<String, String> params, Inter inter) {
		String json = null;
		List<InterMock> list = interMockService.listAllByInterId(inter.getId());
		InterMock match = null, blank = null;
		if(!CollectionUtils.isEmpty(list)) {
			for(InterMock interMock : list) {
				String reqSchema = interMock.getReqSchema();
				boolean isBlank = StringUtils.isBlank(reqSchema);
				JsonNode jsonNode = isBlank ? null : JsonUtils.toObject(reqSchema, JsonNode.class);
				if(isBlank || jsonNode==null || jsonNode.size()==0) {
					if(blank == null) {
						blank = interMock;
					}
				}else if(!CollectionUtils.isEmpty(params)){
					if(jsonNode.isObject()) {
						Iterator<String> fieldNames = jsonNode.fieldNames();
						boolean miss = false;//reqSchema要求的参数都满足时则匹配
						while(fieldNames.hasNext()) {
							String fieldName = fieldNames.next();
							String fieldValue = jsonNode.get(fieldName).asText("");
							if(!fieldValue.equals(params.get(fieldName))) {
								miss = true;
								break;
							}
						}
						if(!miss) {
							match = interMock;
							break;
						}
					}
				}
			}
			if(match==null && blank!=null) {
				match = blank;//空的reqSchema匹配所有请求
			}
			if(match!=null) {
				json = match.getRespSchema();
			}
			if((json=StringUtils.trimToNull(json))!=null) {
				char s = json.charAt(0);
				if('{'==s || '['==s) {
					//支持json模拟响应
				}else {
					//支持js动态模拟，params作为上下文参数req供js使用
					json = script(params, json);
				}
			}
		}
		return json==null ? null : JsonUtils.toObject(json, JsonNode.class);
	}
	
	/** 尝试响应接口返回信息 */
	private Object interResp(Inter inter) {
		List<InterResp> interRespList = interRespService.listAllByInterId(inter.getDocId(), inter.getId());
		if(!CollectionUtils.isEmpty(interRespList)) {
			InterResp interResp = null;//优先顺序：唯一个 > 200 > default > 第一个
			if(interRespList.size()==1) {
				interResp = interRespList.get(0);
			}else {
				InterResp respDefault = null;
				for(InterResp resp : interRespList) {
					if("200".equals(resp.getCode())) {
						interResp = resp;
						break;
					}else if("default".equals(resp.getCode()) && respDefault==null) {
						respDefault = resp;
					}
				}
				if(interResp == null) {
					interResp = respDefault!=null ? respDefault : interRespList.get(0);
				}
			}
			Object resp = interResp(interResp);
			if(resp != null) {
				return resp;
			}
		}
		return null;
	}
	
	private Map<String, String> params(HttpServletRequest request, Inter inter) {
		Map<String, String> params = new HashMap<>();
		List<InterParam> interParamList = interParamService.listAllByInterId(inter.getDocId(), inter.getId());
		if(!CollectionUtils.isEmpty(interParamList)) {
			for(InterParam interParam : interParamList) {
				String paramName = interParam.getCode(), paramValue = null;
				ParamPosition position = interParam.getPosition();
				if(ParamPosition.query==position || ParamPosition.formData==position) {
					paramValue = request.getParameter(paramName);
				}else if(ParamPosition.header==position) {
					paramValue = request.getHeader(paramName);
				}else if(ParamPosition.cookie==position) {
					for(Cookie cookie : request.getCookies()) {
						if(paramName.equalsIgnoreCase(cookie.getName())) {
							paramValue = cookie.getValue();
							break;
						}
					}
				}else if(ParamPosition.path==position) {
					String path = inter.getPath();
					if(path.contains("{"+paramName+"}")) {
						String pattern = StringUtils.replace(path, "{"+paramName+"}", "(.*)");
						Matcher matcher = Pattern.compile(pattern).matcher(request.getRequestURI());
						if(matcher.find()) {
							paramValue = matcher.group(1);
						}
					}
				}else if(ParamPosition.body==position) {
					try{
						String body = IOUtils.toString(request.getInputStream());
						if(StringUtils.isNotBlank(body) && '{'==body.charAt(0)) {
							List<String> names = new ArrayList<>();
							if(SchemaType.cust_json==interParam.getType()) {
								JsonNode jsonNode = JsonUtils.toObject(interParam.getExtSchema(), JsonNode.class);
								Iterators.addAll(names, jsonNode.fieldNames());
							}else if(SchemaType.sys_ref==interParam.getType()) {
								RespSchema respSchema = respSchemaService.getById(interParam.getRefSchemaId());
								List<Map<String, Object>> custSchema = custSchema(respSchema.getCustSchema());
								for(Map<String, Object> map : custSchema) {
									names.add(map.get("code").toString());
								}
							}
							if(!CollectionUtils.isEmpty(names)) {
								JsonNode jsonNode = JsonUtils.toObject(body, JsonNode.class);
								for(String name : names) {
									JsonNode node = jsonNode.get(name);
									if(node!=null) {
										params.put(name, node.asText(""));
									}
								}
							}
						}
						logger.info(body);
					}catch(Exception e) {
						logger.warn(e.getMessage());
					}
				}
				params.put(paramName, Objects.toString(paramValue, ""));
			}
		}
		return params;
	}
	
	/** 返回响应结构体 */
	private Object interResp(InterResp interResp) {
		if(SchemaType.sys_ref==interResp.getType()) {
			RespSchema respSchema = respSchemaService.getById(interResp.getRefSchemaId());
			List<Map<String, Object>> custSchema = custSchema(respSchema.getCustSchema());
			return interResp(custSchema);
		}else if(SchemaType.sys_array==interResp.getType()) {
			List<Map<String, Object>> custSchema = custSchema(interResp.getCustSchema());
			return Collections.singletonList(interResp(custSchema));
		}else if(SchemaType.sys_object==interResp.getType()){
			List<Map<String, Object>> custSchema = custSchema(interResp.getCustSchema());
			return interResp(custSchema);
		}else {
			return MapUtils.getSingleMap(interResp.getCode(), interResp.getDescription());
		}
	}
	
	private Map<String, Object> interResp(List<Map<String, Object>> custSchema) {
		Map<String, Object> map = MapUtils.newMap();
		for(Map<String, Object> resp : custSchema) {
			if(!StringUtils.isBlank(Objects.toString(resp.get("parentId"), null))) {
				continue;
			}
			String type = Objects.toString(resp.get("type"), null);
			String code = Objects.toString(resp.get("code"), null);
			if("sys_array".equals(type)) {
				map.put(code, Collections.singletonList(interResp(resp, custSchema)));
			}else if("sys_object".equals(type)){
				map.put(code, interResp(resp, custSchema));
			}else {
				map.put(code, resp.get("description"));
			}
		}
		return map;
	}
	
	private Object interResp(Map<String, Object> resp, List<Map<String, Object>> custSchema) {
		String parentId = Objects.toString(resp.get("nodeId"), null);
		Map<String, Object> ret = new HashMap<>();
		for(Map<String, Object> map : custSchema) {
			if(parentId.equals(map.get("parentId"))) {
				String type = Objects.toString(map.get("type"), null);
				String code = Objects.toString(map.get("code"), null);
				if("sys_array".equals(type)) {
					ret.put(code, Collections.singletonList(interResp(map, custSchema)));
				}else if("sys_object".equals(type)){
					ret.put(code, interResp(map, custSchema));
				}else if("sys_ref".equals(type)){
					Long respSchemaId = Long.valueOf(Objects.toString(map.get("refSchemaId"), null));
					RespSchema respSchema = respSchemaService.getById(respSchemaId);
					return interResp(custSchema(respSchema.getCustSchema()));
				}else {
					ret.put(code, map.get("description"));
				}
			}
		}
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> custSchema(String custSchema) {
		List<Map<String, Object>> array = new ArrayList<>();
		try{
			List<Object> list = Json.mapper().readValue(custSchema, List.class);
			for(Object item : list) {
				array.add((Map<String, Object>)item);
			}
		}catch(Exception e) {
			logger.warn(e.getMessage());
		}
		return array;
	}

	//执行js脚本
	private String script(Map<String, String> params, String json) {
		Bindings bindings = new SimpleBindings();
		bindings.put("req", params);
		try{
			ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByExtension("js");
			Object result = scriptEngine.eval(json, bindings);
			return result.toString();
		}catch(Exception e) {
			logger.warn(e.getMessage());
		}
		return null;
	}
}
