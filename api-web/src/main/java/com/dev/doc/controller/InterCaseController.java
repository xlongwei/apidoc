package com.dev.doc.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.dev.base.controller.BaseController;
import com.dev.base.enums.ParamPosition;
import com.dev.base.enums.ReqScheme;
import com.dev.base.exception.code.ErrorCode;
import com.dev.base.json.JsonUtils;
import com.dev.base.util.WebUtil;
import com.dev.base.utils.HttpClientUtils;
import com.dev.base.utils.TemplateUtils;
import com.dev.base.utils.ValidateUtils;
import com.dev.doc.entity.ApiDoc;
import com.dev.doc.entity.Inter;
import com.dev.doc.entity.InterCase;
import com.dev.doc.entity.InterParam;
import com.dev.doc.service.ApiDocService;
import com.dev.doc.service.InterCaseService;
import com.dev.doc.service.InterParamService;
import com.dev.doc.service.InterService;

@Controller
@RequestMapping("auth/doc/inter/case")
public class InterCaseController extends BaseController {
	@Autowired
	InterCaseService interCaseService;
	@Autowired
	InterService interService;
	@Autowired
	ApiDocService apiDocService;
	@Autowired
	InterParamService interParamService;
	
	@RequestMapping("/json/list.htm")
	public @ResponseBody Object list(HttpServletRequest request,Long docId,Long interId){
		ValidateUtils.notNull(interId, ErrorCode.SYS_001,"接口id不能为空");
		List<InterCase> interRespList = interCaseService.listAllByInterId(interId);
		return JsonUtils.createSuccess(interRespList);
	}
	
	@RequestMapping(value = "/json/add.htm",method = RequestMethod.POST)
	public @ResponseBody Object add(HttpServletRequest request,InterCase interCase){
		ValidateUtils.notNull(interCase.getInterId(), ErrorCode.SYS_001,"接口id不能为空");
		interCaseService.add(interCase);
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping(value = "/json/update.htm",method = RequestMethod.POST)
	public @ResponseBody Object update(HttpServletRequest request,InterCase interCase,Long caseId){
		ValidateUtils.notNull(caseId, ErrorCode.SYS_001,"id不能为空");
		interCase.setId(caseId);
		interCaseService.update(interCase);
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping(value = "/json/del.htm")
	public @ResponseBody Object del(HttpServletRequest request,Long caseId){
		ValidateUtils.notNull(caseId, ErrorCode.SYS_001,"id不能为空");
		interCaseService.deleteById(caseId);
		return JsonUtils.createSuccess();
	}
	
	@RequestMapping("/json/info.htm")
	public @ResponseBody Object getInfo(Long caseId){
		ValidateUtils.notNull(caseId, ErrorCode.SYS_001,"id不能为空");
		InterCase interResp = interCaseService.getById(caseId);
		return JsonUtils.createSuccess(interResp);
	}
	
	@RequestMapping("/json/test.htm")
	public @ResponseBody Object test(HttpServletRequest request, Long caseId, @RequestParam(defaultValue="false") boolean mock){
		ValidateUtils.notNull(caseId, ErrorCode.SYS_001,"id不能为空");
		InterCase interCase = interCaseService.getById(caseId);
		Inter inter = interService.getById(interCase.getInterId());
		ApiDoc apiDoc = apiDocService.getById(interCase.getDocId());
		List<InterParam> interParams = interParamService.listAllByInterId(interCase.getDocId(), interCase.getInterId());
		String resp = test(request, interCase, mock, inter, apiDoc, interParams);
		interCase.setReqSchema(resp);
		return JsonUtils.createSuccess(interCase);
	}
	
	@SuppressWarnings("unchecked")
	public String test(HttpServletRequest request, InterCase interCase, boolean mock, Inter inter, ApiDoc apiDoc, List<InterParam> interParams) {
		Map<String, Object> params = JsonUtils.toObject(interCase.getReqSchema(), Map.class);
		String path = TemplateUtils.process(inter.getPath(), params), referer = request.getHeader("referer");
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(referer).build();
		String hostAndPort = uriComponents.getHost()+(uriComponents.getPort()==-1 ? "" : ":"+uriComponents.getPort());
		ReqScheme reqScheme = WebUtil.getUrlScheme();
		if(mock == false) {
			if(inter.getScheme() != null) {
				reqScheme = inter.getScheme();
			}else {
				ReqScheme docScheme = WebUtil.getDocScheme(apiDoc.getScheme());
				if(docScheme != null) {
					reqScheme = docScheme;
				}
			}
			if(StringUtils.isNotBlank(apiDoc.getHost())) {
				hostAndPort = apiDoc.getHost();
			}
		}
		String url = reqScheme.name() + "://" + hostAndPort + (mock ? "/apidoc/mock" : apiDoc.getBasePath()) + path;
		log.info("case {} {}", inter.getMethod(), url);
		//mock使用docId参数，或从referer获取docId
		params.put("docId", apiDoc.getId());
		HttpUriRequest uriRequest = buildRequest(url, params, inter, interParams);
		String res = HttpClientUtils.execute(uriRequest, HttpClientUtils.DEFAULT_ENCODE);
		log.info("result {}", res);
		return res;
	}

	private HttpUriRequest buildRequest(String url, Map<String, Object> params, Inter inter, List<InterParam> interParams) {
		RequestBuilder builder = RequestBuilder.create(inter.getMethod().name());
		builder.setUri(url);
		builder.addParameter("docId", toString(params.get("docId")));
		for(InterParam interParam : interParams) {
			ParamPosition position = interParam.getPosition();
			switch(position) {
			case query:
			case formData:
				builder.addParameter(interParam.getCode(), toString(params.get(interParam.getCode())));
				break;
			case header:
				builder.addHeader(interParam.getCode(), toString(params.get(interParam.getCode())));
				break;
			case body:
				try {
					builder.setEntity(new StringEntity(toString(params.get(interParam.getCode()))));
				} catch (UnsupportedEncodingException e) {
					//
				}
				break;
			default:
				break;
			}
		}
		return builder.build();
	}

	private String toString(Object object) {
		return object==null ? "" : object.toString();
	}
}
