package org.jasig.cas.web.flow;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.CentralAuthenticationService;
import org.jasig.cas.authentication.Authentication;
import org.jasig.cas.authentication.AuthenticationBuilder;
import org.jasig.cas.authentication.AuthenticationException;
import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.Service;
import org.jasig.cas.ticket.TicketCreationException;
import org.jasig.cas.ticket.TicketException;
import org.jasig.cas.web.support.WebUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.util.StringUtils;
import org.springframework.web.util.CookieGenerator;
import org.springframework.webflow.core.collection.LocalAttributeMap;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

import cn.seisys.auth.authentication.BadCaptchaException;
import cn.seisys.auth.jcaptcha.JCaptcha;

public class JCaptchaAuthenticationViaFormAction extends
		AuthenticationViaFormAction {
	
    /** Default principal implementation that allows us to create {@link Authentication}s (principal cannot be null). */
    private static final Principal NULL_PRINCIPAL = new NullPrincipal();
    
    /** Core we delegate to for handling all ticket related tasks. */
    @NotNull
    private CentralAuthenticationService centralAuthenticationService;

    @NotNull
    private CookieGenerator warnCookieGenerator;
    
	// 前台提交的验证码参数名
	private String jcaptchaParam = "captcha";

	public final Event validatorCaptcha(final RequestContext context,
			final Credential credential, final MessageContext messageContext) {

		final HttpServletRequest request = WebUtils
				.getHttpServletRequest(context);

		String captcha = request.getParameter(jcaptchaParam);

		if(StringUtils.isEmpty(captcha)) {
			messageContext.addMessage(new MessageBuilder().code(
					"required.captcha").build());
			return newEvent(ERROR);
		}
		if (JCaptcha.validateResponse(request, captcha)) {
			return newEvent(SUCCESS);
		} else {
			AuthenticationBuilder builder = new AuthenticationBuilder();
			builder.addFailure(this.getClass().getSimpleName(), BadCaptchaException.class);
			return newEvent(AUTHENTICATION_FAILURE, new AuthenticationException(builder.getFailures(),builder.getSuccesses()));
		}

	}

    public final Event auth(final RequestContext context, final Credential credential,
            final MessageContext messageContext) throws Exception {
    	final AuthenticationBuilder builder = new AuthenticationBuilder(NULL_PRINCIPAL);
        // Validate login ticket
        final String authoritativeLoginTicket = WebUtils.getLoginTicketFromFlowScope(context);
        final String providedLoginTicket = WebUtils.getLoginTicketFromRequest(context);
        if (!authoritativeLoginTicket.equals(providedLoginTicket)) {
            logger.warn("Invalid login ticket {}", providedLoginTicket);
            messageContext.addMessage(new MessageBuilder().code("error.invalid.loginticket").build());
            return newEvent(ERROR);
        }

        final String ticketGrantingTicketId = WebUtils.getTicketGrantingTicketId(context);
        final Service service = WebUtils.getService(context);
        if (StringUtils.hasText(context.getRequestParameters().get("renew")) && ticketGrantingTicketId != null
                && service != null) {

            try {
                final String serviceTicketId = this.centralAuthenticationService.grantServiceTicket(
                        ticketGrantingTicketId, service, credential);
                WebUtils.putServiceTicketInRequestScope(context, serviceTicketId);
                putWarnCookieIfRequestParameterPresent(context);
                return newEvent(WARN);
            } catch (final AuthenticationException e) {
                return newEvent(AUTHENTICATION_FAILURE, e);
            } catch (final TicketCreationException e) {
                logger.warn(
                        "Invalid attempt to access service using renew=true with different credential. "
                        + "Ending SSO session.");
                this.centralAuthenticationService.destroyTicketGrantingTicket(ticketGrantingTicketId);
            } catch (final TicketException e) {
                return newEvent(ERROR, e);
            }
        }

        try {
            WebUtils.putTicketGrantingTicketInRequestScope(context,
                    this.centralAuthenticationService.createTicketGrantingTicket(credential));
            putWarnCookieIfRequestParameterPresent(context);
            return newEvent(SUCCESS);
        } catch (final AuthenticationException e) {
            return newEvent(AUTHENTICATION_FAILURE, e);
        } catch (final Exception e) {
            return newEvent(ERROR, e);
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

    private Event newEvent(final String id) {
        return new Event(this, id);
    }
    
    private Event newEvent(final String id, final Exception error) {
        return new Event(this, id, new LocalAttributeMap("error", error));
    }

    /**
     * Null prinicpal implementation that allows us to construct {@link Authentication}s in the event that no
     * principal is resolved during the authentication process.
     */
    static class NullPrincipal implements Principal {

        /** The nobody principal. */
        private static final String NOBODY = "nobody";

        @Override
        public String getId() {
            return NOBODY;
        }

        @Override
        public Map<String, Object> getAttributes() {
            return Collections.emptyMap();
        }
    }

}