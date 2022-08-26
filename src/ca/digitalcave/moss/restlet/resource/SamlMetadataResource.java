package ca.digitalcave.moss.restlet.resource;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import com.onelogin.saml2.Auth;
import com.onelogin.saml2.settings.Saml2Settings;

import ca.digitalcave.moss.restlet.CookieAuthenticator;
import ca.digitalcave.moss.restlet.plugin.AuthenticationHelper;
import ca.digitalcave.moss.restlet.util.SamlUtil;

/**
 * The resource which consumes the assertion which is returned from the IdP.  This resources configures
 * the ChallengeResponse object and stores the cookie.
 */
public class SamlMetadataResource extends ServerResource{

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

		final String ssoProviderId = getAttribute("ssoProviderId");

		try {
			final HttpServletRequest servletRequest = ServletUtils.getRequest(getRequest());
			final HttpServletResponse servletResponse = ServletUtils.getResponse(getResponse());

			final Auth auth = new Auth(SamlUtil.getSettings(helper, ssoProviderId, true), servletRequest, servletResponse);

			final Saml2Settings settings = auth.getSettings();
			settings.setSPValidationOnly(false);
			final List<String> errors = settings.checkSettings();

			if (errors.isEmpty()) {
				final String metadata = settings.getSPMetadata();
				return new StringRepresentation(metadata, MediaType.APPLICATION_XML);
			}
			else {
				final StringBuilder sb = new StringBuilder();
				for (String error : errors) {
					sb.append("<p>").append(error).append("</p>");
				}
				return new StringRepresentation(sb.toString(), MediaType.TEXT_HTML);
			}
		}
		catch (Exception e){
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, e.getMessage(), e);
		}
	}
}
