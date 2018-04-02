package cn.seisys.auth.jcaptcha;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

/**
 * 验证码过滤器
 * <p>User: Zhang Kaitao
 * <p>Date: 13-3-22 下午4:01
 * <p>Version: 1.0
 */

public class JCaptchaValidateFilter extends AccessControlFilter {

	// 是否开启验证码支持
	private boolean jcaptchaEbabled = true;
	// 前台提交的验证码参数名
	private String jcaptchaParam = "captcha";
	// 验证码验证失败后存储到的属性名
	private String failureKeyAttribute = "shiroLoginFailure"; 

	public void setJcaptchaEbabled(boolean jcaptchaEbabled) {
		this.jcaptchaEbabled = jcaptchaEbabled;
	}

	public void setJcaptchaParam(String jcaptchaParam) {
		this.jcaptchaParam = jcaptchaParam;
	}

	public void setFailureKeyAttribute(String failureKeyAttribute) {
		this.failureKeyAttribute = failureKeyAttribute;
	}

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		
		// 1、设置验证码是否开启属性，页面可以根据该属性来决定是否显示验证码
		request.setAttribute("jcaptchaEbabled", jcaptchaEbabled);

		HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
		// 2、判断验证码是否禁用 或不是表单提交（允许访问）
		if (jcaptchaEbabled == false || !"post".equalsIgnoreCase(httpServletRequest.getMethod())) {
			return true;
		}
		// 3、此时是表单提交，验证验证码是否正确
		return JCaptcha.validateResponse(httpServletRequest, httpServletRequest.getParameter(jcaptchaParam));
	}

	@Override
	protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
		// 如果验证码失败了，存储失败key属性
		request.setAttribute(failureKeyAttribute, "jCaptcha.error");
		return false;
	}
	
	protected Subject getSubject(ServletRequest request, ServletResponse response) {
		return SecurityUtils.getSubject();
	}

	private void ensureUserIsLoggedOut(ServletRequest request, ServletResponse response) {
		try {
			Subject currentUser = getSubject(request, response);
			if (currentUser == null)
				return;

			if (currentUser.isAuthenticated() || currentUser.isRemembered()) {
				currentUser.logout();
				issueRedirect(request, response, "login");
			}
		} catch (Exception e) {
		}
	}
	
	protected void issueRedirect(ServletRequest request, ServletResponse response, String redirectUrl) throws Exception {
		WebUtils.issueRedirect(request, response, redirectUrl);
	}
}