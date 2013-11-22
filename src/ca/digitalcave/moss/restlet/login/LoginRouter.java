package ca.digitalcave.moss.restlet.login;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import ca.digitalcave.moss.restlet.util.PasswordChecker;


public class LoginRouter extends Router {
	private final LoginRouterConfiguration configuration;
	
	public LoginRouter() {
		this(null, new LoginRouterConfiguration());
	}
	
	public LoginRouter(Application application, LoginRouterConfiguration configuration) {
		this.configuration = configuration;
		
		//Fill up the defaults
		if (configuration.passwordChecker != null && application != null){
			application.getContext().getAttributes().put(PasswordChecker.class.getName(), configuration.passwordChecker);
		}
		
		this.attach("/checkpassword", PasswordCheckResource.class);
		this.attachDefault(LoginFreemarkerResource.class);
	}
	
	@Override
	protected void doHandle(Restlet next, Request request, Response response) {
		request.getAttributes().put("configuration", configuration);
		
		super.doHandle(next, request, response);
	}
	

}
