package com.dev.doc.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dev.base.controller.BaseController;
import com.dev.base.exception.code.ErrorCode;
import com.dev.base.json.JsonUtils;
import com.dev.base.utils.ValidateUtils;
import com.dev.doc.entity.InterMock;
import com.dev.doc.service.InterMockService;

@Controller
@RequestMapping("auth/doc/inter/mock")
public class InterMockController extends BaseController {
	@Autowired
	InterMockService interMockService;
	
	@RequestMapping("/json/list.htm")
	public @ResponseBody Object list(HttpServletRequest request,Long docId,Long interId){
		ValidateUtils.notNull(interId, ErrorCode.SYS_001,"接口id不能为空");
		List<InterMock> interRespList = interMockService.listAllByInterId(interId);
		return JsonUtils.createSuccess(interRespList);
	}
	
	@RequestMapping(value = "/json/add.htm",method = RequestMethod.POST)
	public @ResponseBody Object add(HttpServletRequest request,InterMock interMock){
		ValidateUtils.notNull(interMock.getInterId(), ErrorCode.SYS_001,"接口id不能为空");
		interMockService.add(interMock);
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping(value = "/json/update.htm",method = RequestMethod.POST)
	public @ResponseBody Object update(HttpServletRequest request,InterMock interMock,Long mockId){
		ValidateUtils.notNull(mockId, ErrorCode.SYS_001,"id不能为空");
		interMock.setId(mockId);
		interMockService.update(interMock);
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping(value = "/json/del.htm")
	public @ResponseBody Object del(HttpServletRequest request,Long mockId){
		ValidateUtils.notNull(mockId, ErrorCode.SYS_001,"id不能为空");
		interMockService.deleteById(mockId);
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping("/json/info.htm")
	public @ResponseBody Object getInfo(Long mockId){
		ValidateUtils.notNull(mockId, ErrorCode.SYS_001,"id不能为空");
		InterMock interResp = interMockService.getById(mockId);
		return JsonUtils.createSuccess(interResp);
	}
}
