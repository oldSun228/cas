<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<!--定义语言编码，默认使用utf-8编码*start-->          
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<!--end-->
<!--针对老版本浏览器写的，以保证各种浏览器都能正确解释页面*start-->
<meta http-equiv="content-type" content="zh-CN">
<!--end-->
<title>智能交通管理系统－－登录页面</title>
<link href="<c:url value='/css/login.css'/>" type="text/css" rel="stylesheet" />
<script type="text/javascript" src="<c:url value='/js/jquery-1.6.2.min.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/img_rotation.js'/>"></script>
<script type="text/javascript">
window.document.onkeydown = keyP;
function keyP()
{
  if(window.event.keyCode == 13)
  document.getElementById("btnLogin").click();
}
$(document).ready(function(){
	$("#btnLogin").click(function(e){
		e.preventDefault();
		if ($("#username").val() == "") {
			$(".log_err").html("请输入用户名。");
			return false;
		} else if ($("#password").val() == "") {
			$(".log_err").html("请输入密码。");
			return false;
		}
		$("#fm1").submit();
	});
});
</script>
<!--[if IE 6]>
       <script src="<c:url value='/resources/js/EvPng.js'/>" type="text/javascript"></script>
        <script language="javascript" type="text/javascript">
        EvPNG.fix("div,ul,li,a,span,li,strong,i,font,img");
        </script>
<![endif]-->
</head>

<body>
	<div class="log_container">
    	<div class="log_box" style="padding-top:120px">
    		<!--图片轮换-->
            <div class="img_rotation">
              <div id="xxx"  >
				 <script>
				 var box =new PPTBox();
                 box.width = 477; //宽度
                 box.height = 368;//高度
                 box.autoplayer = 6;//自动播放间隔时间
            
                 box.add({"url":"<c:url value='/images/login/img_change01.jpg'/>","href":"","title":""})
                 box.add({"url":"<c:url value='/images/login/img_change02.jpg'/>","href":"","title":""})
                 box.add({"url":"<c:url value='/images/login/img_change03.jpg'/>","href":"","title":""})
                 box.show();
                </script>
            </div>
            </div>
            <!--登陆框-->
            <div class="log_inner_box">
  				<form:form method="post" id="fm1" commandName="${commandName}" htmlEscape="true">
  				
                <ul class="log_mid">
                    <li>
                        <span class="log_user"></span>
                        <span class="log_txt">
                       		<form:input id="username" name="username" class="txt_log" value="" tabindex="1" path="username" autocomplete="off" htmlEscape="true" />
                       	</span>
                        <!--correct 正确的图标-->
                    </li>
                    <li>
                        <span class="log_pwd"></span>
                        <span class="log_txt">
                        	<form:password id="password" name="password" class="txt_log" value="" tabindex="2" path="password" autocomplete="off" htmlEscape="true" />
                        </span>
                    </li>
                    <li id="error_box">
                    	<font class="log_err" style="display:block"><form:errors path="*" id="msg" cssClass="errors" element="div" htmlEscape="false" /></font>
                    </li>
                    <li id="log_marginbot">
					    <input type="hidden" name="lt" value="${loginTicket}" />
					    <input type="hidden" name="execution" value="${flowExecutionKey}" />
					    <input type="hidden" name="_eventId" value="submit" />
                        <span class="f_left">
                        <input id="btnLogin" type="button" class="log_btn" alt="确定" tabindex="3" value=""/>
                        </span>
                    </li>
                    <li style="margin-top:40px;text-align:center;">
                    	<font id="copyright"><spring:message code="copyright" /></font>
                    </li>
                </ul>
  				</form:form>
            </div>
       </div>
	</div>
	<spring:theme code="cas.javascript.file" var="casJavascriptFile" text="" />
	<script type="text/javascript" src="<c:url value="${casJavascriptFile}" />"></script>
</body>
</html>
