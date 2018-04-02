package cn.seisys.auth.authentication;

import java.util.Map;

import org.jasig.cas.authentication.Credential;
import org.jasig.cas.authentication.principal.Principal;
import org.jasig.cas.authentication.principal.PrincipalResolver;
import org.jasig.cas.authentication.principal.SimplePrincipal;


public class QueryDatabasePrincipalResolver implements PrincipalResolver {

    public Principal resolve(final Credential credential, Map<String, Object> attributes) {
        return new SimplePrincipal(credential.getId(), attributes);
    }

    @Override
    public boolean supports(final Credential credential) {
        return credential.getId() != null;
    }

	@Override
	public Principal resolve(Credential credential) {
        return new SimplePrincipal(credential.getId());
	}

}
