package cn.seisys.auth.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;

/**
 * 用于验证码验证的Shiro拦截器在用于身份认证的拦截器之前运行
 * <p>User: Zhang Kaitao
 * <p>Date: 14-3-3
 * <p>Version: 1.0
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
	
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
    	if(request.getAttribute(getFailureKeyAttribute()) != null) {
            return true;
        }
        
        return super.onAccessDenied(request, response, mappedValue);
    }
    
}
