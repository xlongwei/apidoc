<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<h1>${projTempMap["" + param.projId + ""].code}</h1>
<div class="pull-right">
	<a href="auth/apidoc/preview.htm?docId=${param.docId}" target="_bank" class="btn btn-sm btn-success">
		<i class="fa fa-eye"></i> 预览
	</a>
	<a href="auth/apidoc/preview.htm?docId=${param.docId}&mock=true" target="_bank" class="btn btn-sm btn-info">
		<i class="fa fa-eye"></i> 模拟
	</a>
	<a href="javascript:void(window.open('pass/swagger2html.htm?jsonUrl='+('${Cfg.WEB_BASE_URL}'.startsWith('http')?'${Cfg.WEB_BASE_URL}':location.protocol+'//'+location.host+'${Cfg.WEB_BASE_URL}')+'pass/knife4j/swagger.htm?docId=${param.docId}'))" class="btn btn-sm btn-primary">
		<i class="fa fa-eye"></i> 文档
	</a>
                 	
	<c:if test='${projTempMap["" + param.projId + ""].role == "admin"}'>
		<a href="auth/proj/mem/forwardSendNotice.htm?projId=${param.projId}&docId=${param.docId}" class="btn btn-sm btn-warning">
			<i class="fa fa-envelope-square"></i> 变更通知
		</a>
	</c:if>
	
	<div class="btn-group">
        <button class="btn btn-sm btn-default">
        	<i class="fa fa-share"></i> 导出
        </button>
        
        <button data-toggle="dropdown" class="btn btn-sm btn-default dropdown-toggle"><span class="caret"></span></button>
        <ul class="dropdown-menu">
            <li>
            	<a href="auth/apidoc/export.htm?docId=${param.docId}&docFormat=html">
            		<i class="fa fa-file-text"></i> html文档
            	</a>
            </li>
            <li>
            	<a href="auth/apidoc/export.htm?docId=${param.docId}&docFormat=doc">
            		<i class="fa fa-file-word-o"></i> word文档
            	</a>
            </li>
            <li>
            	<a href="auth/apidoc/export.htm?docId=${param.docId}&docFormat=json">
            		<i class="fa fa-file-code-o"></i> json文档
            	</a>
            </li>
        </ul>
    </div>
</div>




