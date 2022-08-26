package ca.digitalcave.moss.restlet.resource;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.onelogin.saml2.logout.LogoutRequestParams;

import ca.digitalcave.moss.restlet.CookieAuthenticator;
import ca.digitalcave.moss.restlet.plugin.AuthenticationHelper;
import ca.digitalcave.moss.restlet.util.RequestUtil;
import ca.digitalcave.moss.restlet.util.SamlUtil;

/**
 * This resource accepts the GET to delete the auth cookie and log the user out.
 */
public class SamlLogoutResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final AuthenticationHelper helper = CookieAuthenticator.getAuthenticationHelper(getRequest());
		final String ssoProviderId = getAttribute("ssoProviderId");
		
		if (getQuery().getFirstValue("sessionIndex") == null){
			final String nameId = getChallengeResponse().getParameters().getFirstValue(CookieAuthenticator.FIELD_SSO_USERNAME);
			final String sessionIndex = getChallengeResponse().getParameters().getFirstValue(CookieAuthenticator.FIELD_SSO_SESSION_INDEX);

			final Form query = getQuery();
			query.add("nameId", nameId);
			query.add("sessionIndex", sessionIndex);
			query.removeAll("logoutSso");
			
			CookieAuthenticator.invalidateSsoSessionIndex(helper, CookieAuthenticator.getAuthenticator(getChallengeResponse()), sessionIndex);
			getRequest().setChallengeResponse(null);
			CookieAuthenticator.setCookie(helper, getRequest(), getResponse(), "", 0);
			
			//We can't delete the cookie at the same time as doing the SAML logout (I think it is a Restlet vs. Servlet conflict).  We therefore need to redirect to this same URL
			// with extra query params, after deleting the cookie, to finalize the signing out of SAML.
			try {
				getResponse().redirectSeeOther(RequestUtil.getRequestURL(getRequest()) + "?" + query.encode());
				return new EmptyRepresentation();
			}
			catch (IOException e) {
				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
			}
		}
		else {
			try {
				final HttpServletRequest servletRequest = ServletUtils.getRequest(getRequest());
				final HttpServletResponse servletResponse = ServletUtils.getResponse(getResponse());

				final Auth auth = new Auth(SamlUtil.getSettings(helper, ssoProviderId), servletRequest, servletResponse);

				final String nameId = getQuery().getFirstValue("nameId");
				final String sessionIndex = getQuery().getFirstValue("sessionIndex");

				final LogoutRequestParams logoutRequestParams = new LogoutRequestParams(sessionIndex, nameId);
				auth.logout(getQuery().getFirstValue("relayState", RequestUtil.getRequestURL(getRequest()).replace("authentication/logout", "")), logoutRequestParams);
			}
			catch (Exception e){
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error executing SLO", e);
			}
		}

		return new EmptyRepresentation();
	}
}
