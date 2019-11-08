package com.dev.doc.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.base.controller.BaseController;
import com.dev.base.enums.SchemaType;
import com.dev.base.utils.MapUtils;
import com.dev.doc.entity.Inter;
import com.dev.doc.entity.InterResp;
import com.dev.doc.entity.RespSchema;
import com.dev.doc.service.InterRespService;
import com.dev.doc.service.InterService;
import com.dev.doc.service.RespSchemaService;

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

	@RequestMapping("**")
	public Object mock(HttpServletRequest request, HttpServletResponse response) {
		response.addHeader("Access-Control-Allow-Origin", "*");
		String requestURI = request.getRequestURI();
		String path = requestURI.substring(requestURI.indexOf(MOCK)+MOCK.length());
		Inter inter = interService.getByMethodPath(request.getMethod(), path);
		if(inter!=null) {
			List<InterResp> interRespList = interRespService.listAllByInterId(inter.getDocId(), inter.getId());
			if(!CollectionUtils.isEmpty(interRespList)) {
				InterResp interResp = null;//优先顺序：唯一个 > default > 第一个
				if(interRespList.size()==1) {
					interResp = interRespList.get(0);
				}else {
					for(InterResp resp : interRespList) {
						if("default".equals(resp.getCode())) {
							interResp = resp;
							break;
						}
					}
					if(interResp == null) {
						interResp = interRespList.get(0);
					}
				}
				Object resp = interResp(interResp);
				if(resp != null) {
					return resp;
				}
			}
		}
		return Collections.emptyMap();
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

}
