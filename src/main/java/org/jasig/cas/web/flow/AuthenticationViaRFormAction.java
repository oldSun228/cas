package org.jasig.cas.web.flow;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.shiro.util.ThreadContext;
import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.UsernamePasswordCredential;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketCreationException;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.ticket.registry.AbstractTicketRegistry;
import org.jasig.cas.web.bind.CredentialsBinder;
import org.jasig.cas.web.support.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.util.StringUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

public class AuthenticationViaRFormAction {

    /** Authentication success result. */
    public static final String SUCCESS = "success";

    /** Authentication success with "warn" enabled. */
    public static final String WARN = "warn";

    /** Authentication failure result. */
    public static final String AUTHENTICATION_FAILURE = "authenticationFailure";

    /** Error result. */
    public static final String ERROR = "error";

    /**
     * Binder that allows additional binding of form object beyond Spring
     * defaults.
     */
    private CredentialsBinder credentialsBinder;

    /** Core we delegate to for handling all ticket related tasks. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private CookieGenerator warnCookieGenerator;

    @NotNull
    private AbstractTicketRegistry ticketRegistry;
    
    /** Logger instance. **/
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public final void doBind(final RequestContext context, final Credential credential) throws Exception {
        final HttpServletRequest request = WebUtils.getHttpServletRequest(context);

        if (this.credentialsBinder != null && this.credentialsBinder.supports(credential.getClass())) {
            this.credentialsBinder.bind(request, credential);
        }
    }
    public final Event submit(final RequestContext context, final UsernamePasswordCredential credential,
            final MessageContext messageContext) throws Exception {
		// Validate login ticket 
		final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context); 
		final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context); 
		if (!authoritativeLoginTicket.equals(providedLoginTicket)) { 
			logger.warn("Invalid login ticket {}", providedLoginTicket); 
			messageContext.addMessage(
					new MessageBuilder().code ("error.invalid.loginticket").build()); 
			context.getFlowScope().put("ret", -1); 
			context.getFlowScope().put("msg", "LT过期，请重新登录!"); 
		} 
		try { 
			final String tgtId = this.centralAuthenticationService.createTicketGrantingTicket(credential); 
			WebUtils.putTicketGrantingTicketInFlowScope(context, tgtId); 
			final Service service = WebUtils.getService(context); 
			final String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(tgtId,service); 
			WebUtils.putServiceTicketInRequestScope(context,serviceTicketId); 
			context.getFlowScope().put("ticket", serviceTicketId); 
			return newEvent(SUCCESS); 
		} catch (final AuthenticationException e) { 
			context.getFlowScope().put("ret", -2); 
			context.getFlowScope().put("msg", "用户名密码错误，请重新登录!"); 
			return newEvent(SUCCESS); 
		} catch (final Exception e) { 
			e.printStackTrace();
			context.getFlowScope().put("ret", -3); 
			context.getFlowScope().put("msg", "系统内部错误，请稍后登录!"); 
			return newEvent(SUCCESS);
		}
    }

    private void putWarnCookieIfRequestParameterPresent(final RequestContext context) {
        final HttpServletResponse response = WebUtils.getHttpServletResponse(context);

        if (StringUtils.hasText(context.getExternalContext().getRequestParameterMap().get("warn"))) {
            this.warnCookieGenerator.addCookie(response, "true");
        } else {
            this.warnCookieGenerator.removeCookie(response);
        }
    }

    private AuthenticationException getAuthenticationExceptionAsCause(final TicketException e) {
        return (AuthenticationException) e.getCause();
    }

    private Event newEvent(final String id) {
        return new Event(this, id);
    }

    private Event newEvent(final String id, final Exception error) {
        return new Event(this, id, new LocalAttributeMap("error", error));
    }

    public final void setCentralAuthenticationService(final CentralAuthenticationService centralAuthenticationService) {
        this.centralAuthenticationService = centralAuthenticationService;
    }

    /**
     * Set a CredentialsBinder for additional binding of the HttpServletRequest
     * to the Credential instance, beyond our default binding of the
     * Credential as a Form Object in Spring WebMVC parlance. By the time we
     * invoke this CredentialsBinder, we have already engaged in default binding
     * such that for each HttpServletRequest parameter, if there was a JavaBean
     * property of the Credential implementation of the same name, we have set
     * that property to be the value of the corresponding request parameter.
     * This CredentialsBinder plugin point exists to allow consideration of
     * things other than HttpServletRequest parameters in populating the
     * Credential (or more sophisticated consideration of the
     * HttpServletRequest parameters).
     *
     * @param credentialsBinder the credential binder to set.
     */
    public final void setCredentialsBinder(final CredentialsBinder credentialsBinder) {
        this.credentialsBinder = credentialsBinder;
    }

    public final void setWarnCookieGenerator(final CookieGenerator warnCookieGenerator) {
        this.warnCookieGenerator = warnCookieGenerator;
    }
    
	public void setTicketRegistry(AbstractTicketRegistry ticketRegistry) {
		this.ticketRegistry = ticketRegistry;
	}
	//~获取客户端IP==================================================
	public String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("http_client_ip");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		// 如果是多级代理，那么取第一个ip为客户ip
		if (ip != null && ip.indexOf(",") != -1) {
			ip = ip.substring(ip.lastIndexOf(",") + 1, ip.length()).trim();
		}
		if ("0.0.0.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
			ip = "127.0.0.1";
		}
		return ip;
	}

}
