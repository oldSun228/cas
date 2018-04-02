<%@page import="java.util.Enumeration" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="org.springframework.util.StringUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    //获取请求类型
    String requestType = request.getHeader("X-Requested-With");

// 如果是异步请求或是手机端，则直接返回信息
    if (!StringUtils.isEmpty(requestType) && requestType.equals("XMLHttpRequest")) {
        response.setStatus(402);
        out.print("连接超时，请重新登录！");
    } else {
%>
<!doctype html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>上海市交通委综合交通业务平台</title>
    <script>
        function logoutRoot(win) {
            if (win == win.top) {
                win.logout();
            } else {
                logoutRoot(win.parent);
            }
        }
        if (window === window.top) {
        } else {
            alert("连接超时，请重新登录！");
            window.stop ? window.stop() : document.execCommand("Stop");
            logoutRoot(window.parent);
        }
    </script>
    <link rel="stylesheet" type="text/css" href="<c:url value='/assets/css/animate.min.css'/>">
    <link rel="stylesheet" type="text/css" href="<c:url value='/assets/css/crosscover.css'/>">
</head>
<body>
<div class="crosscover">

    <!--BEGIN 登录框-->
    <div class="crosscover-overlay">
        <div class="crosscover-island docs-Island">

            <div class="login-inner-box">

                <!--logo-->
                <div class="login-logo"></div>

                <!--BEGIN 登录框内部-->
                <div class="login-box">
                    <div class="login-info">
                        <form:form method="post" id="loginForm" commandName="${commandName}" htmlEscape="true">
									<span class="txt-login-box">
										<form:input id="username" name="username" class="txt-login" placeholder="${message}用户名：" value="" tabindex="1" path="username" autocomplete="off" htmlEscape="true"/>
									</span>
									<span class="txt-login-box">
										<form:password id="password" name="password" class="txt-login" placeholder="请输入密码：" value="" tabindex="2" path="password" autocomplete="off" htmlEscape="true"/>
									</span>

                            <div class="error-message">
                                <span class="info-login" id="errorSpan"><form:errors path="*" id="msg" cssClass="errors" element="div" htmlEscape="false"/></span>
                            </div>
									<span class="btn-login-box">
									    <input type="hidden" name="lt" id="lt" value="${loginTicket}"/>
									    <input type="hidden" name="execution" id="execution" value="${flowExecutionKey}"/>
									    <input type="hidden" name="_eventId" value="submit"/>
										<button id="login" type="button" class="btn btn-login">登录</button>
										<button id="m_login" type="button" class="btn btn-login">申请人登录1</button>
									</span>
                        </form:form>
                    </div>
                </div>
                <!--END 登录框内部-->


            </div>

        </div>
    </div>
    <!--END 登录框-->

    <!--BEGIN 背景图-->
    <ul class="crosscover-list ">
        <li><img src="<c:url value='/assets/images/01.jpg'/>" alt="image01"></li>
        <li><img src="<c:url value='/assets/images/02.jpg'/>" alt="image02"></li>
        <li><img src="<c:url value='/assets/images/03.jpg'/>" alt="image03"></li>
        <li><img src="<c:url value='/assets/images/04.jpg'/>" alt="image04"></li>
    </ul>
    <!--END 背景图-->

    <!--<div class="copyright">上海电科智能系统股份有限公司</div>-->
    <iframe style="display:hidden;width:0;height:0" id="rlogin" name="rlogin"></iframe>

</div>

<!--BEGIN 背景切换-->
<script src="<c:url value='/assets/js/jquery.min.js'/>" type="text/javascript"></script>
<script src="<c:url value='/assets/js/crosscover.js'/>" type="text/javascript"></script>
<script type="text/javascript">
    $(".crosscover").crosscover({
        animateInClass: 'fadeIn',
        animateOutClass: 'fadeOut',
        interval: 8000,
        startIndex: 0,
        autoPlay: true
    });
</script>
<!--END 背景切换-->

<script type="text/javascript">
    $(document).ready(function () {
        /*BEGIN 页面加载时出现登录框*/
        if ($("#errorSpan").html() == "") {
            $('.login-inner-box').addClass('animated fadeInRight');
            setTimeout(function () {
                $('.login-inner-box').removeClass('fadeInRight');
            }, 1000);
        }
        /*END 页面加载时出现登录框*/


        /*BEGIN点击登录按钮，报错提示*/
        $('#login').click(function () {
            if (!isValid()) {
                $(".info-login").css("visibility", "visible");
                $('.login-inner-box').addClass('animated swing');
                setTimeout(function () {
                    $('.login-inner-box').removeClass('swing');
                }, 1000);
            } else {
                $("#loginForm").submit();
            }
            /*END 点击登录按钮，报错提示*/
        });
        //免登录
        $('#m_login').click(function () {
            $('[id="username"]').val("applicant");
            $('[id="password"]').val("111111");
            $("#loginForm").submit();
            /*END 点击登录按钮，报错提示*/
        });


        $(document).keyup(function (event) {
            if (event.keyCode == 13) {
                $("#login").trigger("click");
            }
        });
    });

    /** 验证 */
    function isValid() {
        var errorMsg = "";
        if ($("#username").val() == "") {
            errorMsg = "用户名密码不能为空";
        } else if ($("#password").val() == "") {
            errorMsg = "用户名密码不能为空";
        }
        if (errorMsg != "") {
            $("#errorSpan").html(errorMsg);
        }
        return errorMsg == "";
    }

</script>
<spring:theme code="cas.javascript.file" var="casJavascriptFile" text=""/>
<script type="text/javascript" src="<c:url value="${casJavascriptFile}" />"></script>
</body>
</html>
<% } %>