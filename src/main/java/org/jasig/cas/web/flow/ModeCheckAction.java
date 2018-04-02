package org.jasig.cas.web.flow;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.web.support.WebUtils;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class ModeCheckAction {
	public static final String NORMAL = "normal";
	public static final String APP = "app";
	public static final String RLOGIN = "rlogin";

	public ModeCheckAction() {
	}

	public Event check(final RequestContext context) {
		final HttpServletRequest request = WebUtils.getHttpServletRequest(context);
		// 根据mode判断请求模式，如mode=rlogin，是AJAX远程登录模式，
		// app是app登录模式，不存在是原模式，认证中心本地登录
		String mode = request.getParameter("mode");
		if (mode != null && mode.equals("rlogin")) {
			context.getFlowScope().put("mode", mode);
			return new Event(this, RLOGIN);
		}
		if (mode != null && mode.equals("app")) {
			context.getFlowScope().put("mode", mode);
			return new Event(this, APP);
		}
		return new Event(this, NORMAL);
	}
}