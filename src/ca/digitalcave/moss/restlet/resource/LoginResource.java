package ca.digitalcave.moss.restlet.resource;

import java.io.IOException;

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
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.onelogin.saml2.Auth;

import ca.digitalcave.moss.restlet.CookieAuthenticator;
import ca.digitalcave.moss.restlet.model.AuthUser;
import ca.digitalcave.moss.restlet.plugin.AuthenticationHelper;
import ca.digitalcave.moss.restlet.util.RequestUtil;
import ca.digitalcave.moss.restlet.util.SamlUtil;

/**
 * POST from the login window validates the credentials, and (if needed) returns data to show password change or TOTP screens.
 * GET will redirect to the specified SSO IdP.
 */
public class LoginResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final AuthenticationHelper helper = CookieAuthenticator.getAuthenticationHelper(getRequest());

		if (!helper.getConfig().showSSO){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}
		else if (getQuery().getFirstValue("ssoProviderId") == null 
				|| getQuery().getFirstValue("ssoProviderId").length() == 0){
			throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST);
		}
		
		final String ssoProviderId = getQuery().getFirstValue("ssoProviderId");

		try {
			final HttpServletRequest servletRequest = ServletUtils.getRequest(getRequest());
			final HttpServletResponse servletResponse = ServletUtils.getResponse(getResponse());

			final Auth auth = new Auth(SamlUtil.getSettings(helper, ssoProviderId), servletRequest, servletResponse);
			auth.login(getQuery().getFirstValue("relayState", RequestUtil.getOriginalRoot(getRequest())));

			return new EmptyRepresentation();
		}
		catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}

	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		try {
			final AuthenticationHelper helper = CookieAuthenticator.getAuthenticationHelper(getRequest());
			final Form form = new Form(entity.getText());

			final String identifier = form.getFirstValue(CookieAuthenticator.FIELD_IDENTIFIER);
			final String secret = form.getFirstValue(CookieAuthenticator.FIELD_PASSWORD);
			
			if (identifier == null 
					|| secret == null
					|| identifier.trim().equals("")
					|| secret.trim().equals("")) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, "Values for \"" + CookieAuthenticator.FIELD_IDENTIFIER +"\" and \"" + CookieAuthenticator.FIELD_PASSWORD + "\" are required.");
			}
			
			final String applicationName = getRootRef().getPath().replaceFirst("^/", "");
 
			final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.HTTP_COOKIE, identifier, secret);
			final AuthUser user = helper.authenticate(applicationName, cr, form);
			if (user != null){
				if ("on".equals(form.getFirstValue(CookieAuthenticator.FIELD_REMEMBER))){
					cr.getParameters().set(CookieAuthenticator.FIELD_REMEMBER, "true");
				}
				getRequest().setChallengeResponse(cr);
				CookieAuthenticator.setEncryptedCookieFromChallengeResponse(getRequest(), getResponse(), helper);
				
				final String nextStep = getNextStep(cr, user);
				if (nextStep != null){
					return new StringRepresentation("{\"success\": false, \"next\": \"" + nextStep + "\"}", MediaType.APPLICATION_JSON);
				}

				return new StringRepresentation("{\"success\": true}", MediaType.APPLICATION_JSON);
			}
			else {
				// Delay a random amount of time, between 0 and 1000 millis, so that user enumeration attacks relying on response time are more difficult
				try { Thread.sleep((long) (Math.random() * 1000));} catch (Throwable e){}

				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
				return new StringRepresentation("{\"success\": false}", MediaType.APPLICATION_JSON);
			}
		}
		catch (IOException e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e);
		}
	}
	
	public static String getNextStep(ChallengeResponse cr, AuthUser user){
		if (cr != null
				&& StringUtils.isNotBlank(cr.getIdentifier())				//Primary auth is valid (there is an identifier)...
				&& CookieAuthenticator.isSecondaryAuthenticationValid(cr)	//... and secondary auth is valid, if applicable
				&& CookieAuthenticator.isPasswordExpired(cr)){				// ... and password is expired
			return "passwordExpired";										// ... then we prompt for new password
		}
		else if (CookieAuthenticator.isAuthenticationValid(cr)			//Auth is valid...
				&& CookieAuthenticator.isTwoFactorSetup(cr)				// ... and 2FA is set up
				&& user != null											// ... and user is not null ...
				&& user.getTwoFactorBackupCodes().size() == 0){			// ... and the user has no backup codes
			return "totpBackupCodes";
		}
		else if (CookieAuthenticator.isPrimaryAuthenticationValid(cr)	//Primary auth is valid...
				&& !CookieAuthenticator.isTwoFactorSetup(cr)			// ... and 2FA is not set up ...
				&& !CookieAuthenticator.isImpersonating(cr)				// ... and we are not impersonating another user ...
				&& CookieAuthenticator.isTwoFactorRequired(cr)){		// ... and two factor is required for our current primary auth method
			return "totpSetup";											// ... then we need to set up TOTP
		}
		else if (CookieAuthenticator.isPrimaryAuthenticationValid(cr)	//Primary auth is valid...
				&& !CookieAuthenticator.isTwoFactorSetup(cr)			// ... and 2FA is not set up ...
				&& !CookieAuthenticator.isImpersonating(cr)				// ... and we are not impersonating another user ...
				&& CookieAuthenticator.isTwoFactorPrompt(cr)){			// ... and we are supposed to prompt for two factor for our current primary auth method
			return "totpPromptSetup";									// ... then we prompt for TOTP setup
		}
		else if (CookieAuthenticator.isPrimaryAuthenticationValid(cr) 			//Primary auth is valid...
				&& CookieAuthenticator.isTwoFactorSetup(cr)						// ... and 2FA is setup
				&& !CookieAuthenticator.isSecondaryAuthenticationValid(cr)){	// ... and 2FA is not valid
			return "totpToken";													// ... then prompt for TOTP token
		}
		
		return null;
	}
}
