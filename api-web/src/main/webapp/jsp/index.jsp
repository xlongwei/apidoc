<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
	<base href="${Cfg.WEB_BASE_URL}" />
	<%@include file="/jsp/common/head.jsp"%>
	<style type="text/css">
		.jumbotron {
			text-align: center;
		}
		.effect {
			text-align: center;
		}
		.masthead-links {
			list-style: none;
		}
	</style>
</head>
<body>
	<div class="navbar navbar-inverse navbar-fixed-top">
    	<div class="container">
        	<div class="navbar-header">
	          	<button class="navbar-toggle collapsed" type="button" data-toggle="collapse" data-target=".navbar-collapse">
	            	<span class="sr-only">Toggle navigation</span>
	            	<span class="icon-bar"></span>
	            	<span class="icon-bar"></span>
	            	<span class="icon-bar"></span>
	          	</button>
	          	<a class="navbar-brand hidden-sm" href="javascript:;" >ApiDoc</a>
	        </div>
	        <div class="navbar-collapse collapse" role="navigation">
	          	<ul class="nav navbar-nav">
	            	<li class="menuItem active" data-menu="home"><a href="#home">首页</a></li>
	            	<li class="menuItem" data-menu="contact"><a href="#contact">联系我们</a></li>
	            	<li class="menuItem" data-menu="faq"><a href="pass/faq/home.htm">常见问题</a></li>
	            	<li class="menuItem" data-menu="quickStart"><a href="pass/faq/editWizard.htm">快速上手</a></li>
	            	<li class="menuItem" data-menu="demo"><a href="pass/apidoc/demo.htm">Demo</a></li>
	          	</ul>
	          	<div class="navbar-right">
					<c:choose>
						<c:when test="${not empty userInfo}">
							<c:if test="${userInfo.role == 'admin'}">
								<a class="navbar-brand" href="admin/home.htm" style="margin-right: 30px;">${userInfo.nickName}</a> 
							</c:if>
							<c:if test="${userInfo.role != 'admin'}">
								<a class="navbar-brand" href="auth/home/home.htm" style="margin-right: 30px;">${userInfo.nickName}</a>												
							</c:if>
						</c:when>
						<c:otherwise>
							<a class="navbar-brand" href="forwardLogin.htm">登录</a> 
							<a class="navbar-brand" href="regist/forwardRegist.htm" style="margin-right: 30px;">注册</a>
						</c:otherwise>
					</c:choose>
				</div>
	        </div>
		</div>
	</div>
    <!--banner-->
    <div class="jumbotron masthead banner" >
      	<div class="container">
        	<h1>ApiDoc</h1>
        	<h2>接口文档管理系统</h2>
        	<!-- <p class="masthead-button-links">
          		<a class="btn btn-lg btn-fast" href="auth/apidoc/preview.htm?docId=1" target="_blank" role="button" >打开接口文档</a>
        	</p> -->
        	<ul class="masthead-links">
          		<li>
            		<a href="pass/apidoc/demo.htm?doc=1" target="_blank" role="button" >demo1</a>
            		<a href="pass/apidoc/demo.htm?doc=1&mock=true" target="_blank" role="button" >mock1</a>
          		</li>
          		<li>
            		<a href="/swagger/doc.html?doc=1" target="_blank" role="button" >demo2</a>
            		<a href="/swagger/doc.html?doc=1&mock=true" target="_blank" role="button" >mock2</a>
          		</li>
        	</ul>
      	</div>
    </div>
    <!--banner-->
    <div class="container effect-wp">
        <div class="row effect" >
        	<div class="col-lg-4 ">
	           	<img src="${Cfg.IMG_DOMAIN}image/index/word.png">
	           	<h2>告别word</h2>
	           	<p></p>
	            <h4>您是否已厌倦了更改一个api接口就得多人通知，文档互传，最终导致版本混乱接口调错的情况？
	             		自从有了ApiDoc再也不用担心接口调错了~
	            </h4>
	          	<p></p>
	      	</div>
          	<div class="col-lg-4">
            	<img src="${Cfg.IMG_DOMAIN}image/index/simple.png">
            	<h2>快速编写</h2>
            	<p></p>
            	<h4>
             		 表单傻瓜式接口文档创建，只要动动鼠标，敲敲几个字，一个接口文档就ok了。
              	</h4>
            	<p></p>
          	</div>

			<div class="col-lg-4">
            	<img src="${Cfg.IMG_DOMAIN}image/index/opensource.png">
            	<h2>线上线下测试</h2>
            	<p></p>
            	<h4>
              		生成基于<a href="https://api.xlongwei.com/swagger/doc.html?doc=1" target="_blank">swagger ui </a>文档格式的json，既可在线进行测试也可下载相关的json部署到自己的服务器上，既方便又快捷。
              	</h4>
            	<p></p>
			</div>
        </div>
    </div>
    <!--footer-->
    <%-- <div class="container-fliud">
    	<div id="contact" class="row featurette-links">
        	<div class=" links clearfix" >
            	<div class="col-lg-offset-4 col-lg-2  text-center">
                	<a target="_blank" href="http://jq.qq.com/?_wv=1027&amp;k=dPKj9Q">
                    	<img class="icon-qq" src="${Cfg.IMG_DOMAIN}image/index/qq.png">
                  	</a>
                  	<h3>QQ</h3>
                </div>
                
                <div class="col-lg-2 text-center ">
                  	<a target="_blank" href="http://mail.qq.com/cgi-bin/qm_share?t=qm_mailme&amp;email=hLe2sb20s7eyt7zE9fWq5_vp" style="text-decoration:none;">
                    	<img class="icon-email" src="${Cfg.IMG_DOMAIN}image/index/mail.jpg" >
                  	</a>
                  	<h3>E-Mail</h3>
                </div>
           	</div>
       	</div>
   	</div> --%>
	<!-- PAGE LEVEL SCRIPTS -->
	<jsp:include page="/jsp/common/footer.jsp"/>
</body>
</html>