package ca.digitalcave.moss.restlet.resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.onelogin.saml2.Auth;

import ca.digitalcave.moss.restlet.CookieAuthenticator;
import ca.digitalcave.moss.restlet.model.AuthUser;
import ca.digitalcave.moss.restlet.model.SsoProvider;
import ca.digitalcave.moss.restlet.plugin.AuthenticationHelper;
import ca.digitalcave.moss.restlet.util.RequestUtil;
import ca.digitalcave.moss.restlet.util.SamlUtil;
import ca.digitalcave.moss.restlet.wrapper.HttpServletRequestParameterWrapper;

/**
 * The resource which consumes the assertion which is returned from the IdP.  This resources configures
 * the ChallengeResponse object and stores the cookie.
 */
public class SamlAssertionConsumerServiceResource extends ServerResource{

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final AuthenticationHelper helper = CookieAuthenticator.getAuthenticationHelper(getRequest());
		
		if (!helper.getConfig().showSSO){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}

		final String ssoProviderId = getAttribute("ssoProviderId");

		try {
			final HttpServletRequest servletRequest = new HttpServletRequestParameterWrapper(getRequest(), entity);
			final HttpServletResponse servletResponse = ServletUtils.getResponse(getResponse());

			final Auth auth = new Auth(SamlUtil.getSettings(helper, ssoProviderId), servletRequest, servletResponse);
			auth.processResponse();

			final String relayState = servletRequest.getParameter("RelayState");
			final boolean ipLock = isIpLock(relayState);
			
			if (auth.isAuthenticated()) {

				final String ssoIdentifier = auth.getNameId();	//The user's identifier (i.e. email address)
				
				final AuthUser user = helper.selectUserBySsoIdentifier(ssoIdentifier);
				if (user != null){
					final String sessionIndex = auth.getSessionIndex();
	
					final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_COOKIE, user.getIdentifier(), "");
					final SsoProvider ssoProvider = helper.selectSSOProvider(ssoProviderId);
					CookieAuthenticator.setSsoAuthenticated(helper, cr, ssoProviderId, (ssoProvider != null ? ssoProvider.getDescription() : "SSO"), ssoIdentifier, sessionIndex);
					
					//TODO This code checks to see if 2FA was done by the SSO IdP.  Currently we don't trust this, but maybe we can / should?
//					final Collection<String> authenticationMethods = auth.getAttribute("http://schemas.microsoft.com/claims/authnmethodsreferences");
//					if (authenticationMethods != null){
//						for (String authenticationMethod : authenticationMethods) {
//							if (authenticationMethod.contains("multipleauthn")){
//								CookieAuthenticator.setTwoFactorValidated(cr);
//							}
//						}
//					}
					
//					if (user.isTwoFactorRequiredForSso()){
//						CookieAuthenticator.setTwoFactorNeeded(cr);
//					}
					
					getRequest().setChallengeResponse(cr);
					final String applicationName = getRootRef().getPath().replaceFirst("^/", "");
					if (helper.authenticate(applicationName, cr, null) != null){
						CookieAuthenticator.setEncryptedCookieFromChallengeResponse(getRequest(), getResponse(), helper, ipLock);
					}
				}
			}

			final List<String> errors = auth.getErrors();
			if (!errors.isEmpty()) {
				final StringBuilder sb = new StringBuilder("The following errors occurred when processing the SAML ACL request: ");
				for (String error : errors) {
					sb.append("\n").append(error);
				}
				if (auth.getLastValidationException() != null){
					sb.append("\n").append(auth.getLastValidationException().getMessage());
				}
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, sb.toString());
			}
			
			//Always redirect to the root; if auth worked, it will log in, otherwise it will go back to login screen.
			getResponse().redirectSeeOther(StringUtils.isNotBlank(relayState) ? relayState : RequestUtil.getOriginalRoot(getRequest()));
		}
		catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
		
		return new EmptyRepresentation();
	}
	
	private boolean isIpLock(String relayState){
		if (relayState == null){
			return true;
		}
		
		final String[] split = relayState.split("\\?");
		if (split.length == 2){
			final Form query = new Form(split[1]);
			return Boolean.parseBoolean(query.getFirstValue("ipLock", "true"));
		}
		
		return true;
	}
}
