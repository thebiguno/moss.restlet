package ca.digitalcave.moss.restlet.resource;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.factory.SamlMessageFactory;
import com.onelogin.saml2.http.HttpRequest;
import com.onelogin.saml2.logout.LogoutRequest;
import com.onelogin.saml2.settings.Saml2Settings;

import ca.digitalcave.moss.restlet.CookieAuthenticator;
import ca.digitalcave.moss.restlet.plugin.AuthenticationHelper;
import ca.digitalcave.moss.restlet.util.SamlUtil;
import ca.digitalcave.moss.restlet.wrapper.HttpServletRequestParameterWrapper;

/**
 * The resource which consumes the single logout entity which is returned from the IdP.
 */
public class SamlSingleLogoutResource extends ServerResource{

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.ALL));
	}

	@Override
	protected Representation get(Variant variant) throws ResourceException {
		final AuthenticationHelper helper = CookieAuthenticator.getAuthenticationHelper(getRequest());
		if (!helper.getConfig().showSSO){
			throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		}

//		if (getRequest().getReferrerRef() != null){
//			CookieAuthenticator.setCookie(helper, getRequest(), getResponse(), "", 0);
//			
//			//We can't delete the cookie at the same time as doing the SAML logout (I think it is a Restlet vs. Servlet conflict).  We therefore need to redirect to this same URL
//			// with extra query params, after deleting the cookie, to finalize the signing out of SAML.
//			try {
//				getResponse().redirectSeeOther(RequestUtil.getRequestURL(getRequest()) + "?" + getQuery().encode());
//				return new EmptyRepresentation();
//			}
//			catch (IOException e) {
//				throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST, e);
//			}
//
//		}
//		else {
			try {

				final String ssoProviderId = getAttribute("ssoProviderId");
				final HttpServletRequest servletRequest = new HttpServletRequestParameterWrapper(getRequest(), null);
				final HttpServletResponse servletResponse = ServletUtils.getResponse(getResponse());

				final Auth auth = new Auth(SamlUtil.getSettings(helper, ssoProviderId), servletRequest, servletResponse);
				auth.setSamlMessageFactory(new SamlMessageFactory() {
					@Override
					public LogoutRequest createIncomingLogoutRequest(Saml2Settings settings, HttpRequest request) throws Exception {
						final LogoutRequest result = SamlMessageFactory.super.createIncomingLogoutRequest(settings, request);
						final List<String> sessionIndexes = LogoutRequest.getSessionIndexes(result.getLogoutRequestXml());
						for(String sessionIndex : sessionIndexes){
							CookieAuthenticator.invalidateSsoSessionIndex(helper, CookieAuthenticator.getAuthenticator(getChallengeResponse()), sessionIndex);	//Once we log out, the sessionIndex(es) associated with this login are no longer valid.
						}
						return result;
					}
				});
				auth.processSLO();
			}
			catch (Exception e){
				Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Error executing SLO", e);
			}

			return new EmptyRepresentation();
//		}
	}
}
