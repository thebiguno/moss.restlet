package ca.digitalcave.moss.restlet.login;

import java.util.HashMap;

import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Router;


public class LoginRouter extends Router {
	private final HashMap<String, Object> configuration;
	
	public LoginRouter() {
		this(new HashMap<String, Object>());
	}
	
	public LoginRouter(HashMap<String, Object> configuration) {
		this.configuration = new HashMap<String, Object>();
		
		//Fill up the defaults
		configuration.put("i18nbase", "ca.digitalcave.moss.restlet.login.i18n.i18n");
		
		//Override the defaults with the user-supplied data map
		this.configuration.putAll(configuration);
		
		this.attach("/checkpassword", PasswordCheckResource.class);
		this.attachDefault(LoginFreemarkerResource.class);
	}
	
	@Override
	protected void doHandle(Restlet next, Request request, Response response) {
		request.getAttributes().put("configuration", configuration);
		
		super.doHandle(next, request, response);
	}
}
