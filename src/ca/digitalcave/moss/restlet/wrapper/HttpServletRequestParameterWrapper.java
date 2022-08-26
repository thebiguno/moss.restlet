package ca.digitalcave.moss.restlet.wrapper;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequestWrapper;

import org.restlet.Request;
import org.restlet.data.Form;
import org.restlet.ext.servlet.ServletUtils;
import org.restlet.representation.Representation;

import ca.digitalcave.moss.restlet.util.RequestUtil;

/**
 * This is a stupid hack that is needed because we are trying to use a Servlet-based library (Onelogin's java-saml library)
 * with Restlet.  This library uses the HttpServletRequest 'getParametersMap' to get at the request entity data, while 
 * Restlet removes it and adds it to the entity object.  By wrapping the HttpServletRequest, we are able to re-insert
 * the data to parameters map from the original request.
 */
public class HttpServletRequestParameterWrapper extends HttpServletRequestWrapper {

	final Request wrapped;
	
	@SuppressWarnings("unchecked")
	public HttpServletRequestParameterWrapper(Request wrapped, Representation entity) throws IOException {
		super(ServletUtils.getRequest(wrapped));
		this.wrapped = wrapped;
		this.parameters.putAll(super.getParameterMap());

		if (entity != null){
			final Form form = new Form(entity.getText());
			for (String name : form.getNames()){
				for (String value : form.getValuesArray(name)){
					addParameter(name, value);
				}
			}
		}
	}

	//Overridden (and new) methods

	private final Map<String, String[]> parameters = new HashMap<String, String[]>();
	public String getParameter(String key) {
		if (parameters.get(key) != null && parameters.get(key).length > 0){
			return parameters.get(key)[0];
		}
		return null;
	}
	public Map<String, String[]> getParameterMap() {
		return Collections.unmodifiableMap(parameters);
	}

	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(parameters.keySet());
	}
	public String[] getParameterValues(String key) {
		return parameters.get(key);
	}
	private void addParameter(String key, String value){
		String[] values;
		if (parameters.get(key) != null){
			values = new String[parameters.get(key).length + 1];
			for (int i = 0; i < parameters.get(key).length; i++) {
				values[i] = parameters.get(key)[i];
			}
		}
		else {
			values = new String[1];
		}
		values[values.length - 1] = value;

		parameters.put(key, values);
	}
	
	@Override
	/**
	 * We need to override this method so that it takes into account the X-Forwarded-* headers.  Otherwise, the SAML request 'destination' does not
	 * match what this server thinks the URL is, as it is hidden behind proxypass.
	 */
	public StringBuffer getRequestURL() {
		return new StringBuffer(RequestUtil.getRequestURL(wrapped));
	}
}
