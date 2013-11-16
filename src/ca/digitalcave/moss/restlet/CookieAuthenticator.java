package ca.digitalcave.moss.restlet;

import java.security.Key;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.ChallengeRequest;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Cookie;
import org.restlet.data.CookieSetting;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Status;
import org.restlet.security.ChallengeAuthenticator;

import ca.digitalcave.moss.crypto.Crypto;
import ca.digitalcave.moss.crypto.Crypto.CryptoException;

public class CookieAuthenticator extends ChallengeAuthenticator {

	private final static Map<String, Semaphore> loginAttempts = Collections.synchronizedMap(new WeakHashMap<String, Semaphore>());
	private final String cookieName;
	private final String interceptPath;
	private final Key key;
	private volatile int delay = 1500;
	private volatile int maxCookieAge = Integer.MAX_VALUE;
	private volatile int maxTokenAge = Integer.MAX_VALUE;
	private volatile boolean secure = false;

	public static final String ACTION_LOGIN = "login";
	public static final String ACTION_LOGOUT = "logout";
	public static final String ACTION_IMPERSONATE = "impersonate";
	public static final String ACTION_ENROLE = "enrole";
	public static final String ACTION_RESET = "reset";
	public static final String ACTION_ACTIVATE = "activate";
	
	public CookieAuthenticator(Context context, boolean optional, Key key) {
		this(context, optional, key, "_auth", "/index");
	}
	/**
	 * <p>This authenticator supports the following features:</p>
	 * <ul><li>XHR-based interface to a single end-point for all authentication operations</li>
	 *     <li>Redirects are not used since they result in mixed content warnings when using Apache mod_proxy and HTTPS</li>
	 *     <li>Both authentication identity and authorization identity (i.e. impersonation of another user)</li>
	 *     <li>Automatic login after enrollment and both forced and elective password reset</li>
	 *     <li>Time limited authentication window that moves with each valid request (defaults to max integer which is 24 days)</li>
	 *     <li>Automatic delay after invalid authentication (defaults to 1500 ms)</li>
	 *     <li>Does not permit user enumeration attacks for account creation and password reset requests</li>
	 *     <li>Can be combined with other authenticators</li></ul>
	 * <p>Uses following additional parameters in the challenge response:</p>
	 * <dl><dt>action</dt><dd>one of login, logout, impersonate, enrole, reset, activate</dd>
	 *     <dt>expires</dt><dd>the date/time after which the token will no longer be honored</dd>
	 *     <dt>remember</dt><dd>indicates that the user wishes to retain the cookie</dd>
	 *     <dt>authenticator</dt><dd>the authentication identity whose password will be verified</dd>
	 *     <dt>email</dt><dd>the email address of the user requesting a new account or requesting a password reset</dd>
	 *     <dt>firstName</dt><dd>the first name of the user requesting a new account</dd>
	 *     <dt>lastName</dt><dd>the last name of the user requesting a new account</dd>
	 * <p>The following information is written into the cookie:</p>
	 * <ul><li>Authentication Identifier</li>
	 *     <li>Authentication Secret</li>
	 *     <li>Authorization Identifier</li>
	 *     <li>Issue Date/Time</li>
	 *     <li>Expire Date/Time</li></ul>
	 * <p>The following basic work-flows are supported:</p>
	 * <ul><li>Logged Out > Login [ > Activation ] > Logged In</li>
	 *     <li>Logged Out > Enrole > Activation > Logged In</li>
	 *     <li>Logged Out > Reset > Activation > Logged In</li>
	 *     <li>Logged In > Impersonate > Logged In</li>
	 *     <li>Logged In > Logout > Logged Out</li></ul>
	 * <p>Each action has the following requirements:</p>
	 * <p>Login with optional forced password change</p>
	 * <ol><li>POST identifier=identifier, secret=secret [, action=login][, impersonate=identifier]</i>
	 *     <li>If the request is honored the verifier MUST set the client info user</li>
	 *     <li>If password reset is required the verifier SHOULD return INVALID to prevent access to other resources</li> 
	 *     <li>If password reset is required the resource MUST return an activation key for use by the activation form</li>
	 *     <li>(Login should be called immediately after a successful elective password change to update the cookie)</li></ol>
	 * <p>Logout</p>
	 * <ol><li>POST action=logout OR DELETE</li></ol>
	 * <p>Password reset</p>
	 * <ol><li>POST action=reset, identifier=identifier OR email</li>
	 *     <li>the resource sends an activation key by email</li>
	 * <p>Account creation</p>
	 * <ol><li>POST action=enrole, identifier=identifier, email=email [, firstName=first_name] [, lastName=last_name]</li>
	 *     <li>the resource sends an activation key by email</li>
	 * <p>Account impersonation</p>
	 * <ol><li>POST action=impersonate, identifier=identifier</li>
	 *     <li>If the request is honored the verifier MUST set authenticator=identifier into the challenge response parameters and set identifier=impersonate into the challenge response so that access to other resources appears to originate as the user being impersonated</li></ol>
	 *     <li>The resource MAY vary the response on the presence of the authenticator parameter to provide feedback if required</li></ol>
	 * <p>Account activation (second step for forced password change, password reset, and account creation</p>
	 * <ol><li>POST action=activate, identifier=activationKey and secret=newPassword</li>
	 *     <li>The resource may vary the response on the acceptance of the new secret</li>
	 * <p>It is recommended that the activation key used for account creation, password reset and forced password change be a UUID stored in the account table.  Furthermore it is required that the identifier, activationKey and email address be unique in the account table.</p>
	 * <p>The verifier needs to find a user by identifier, by email, or by activation key.  When found the verifier needs to replace update the identifier in the challenge response so that the cookie will have the correct value.</p>
	 */
	public CookieAuthenticator(Context context, boolean optional, Key key, String cookieName, String interceptPath) {
		super(context, optional, ChallengeScheme.HTTP_COOKIE, null);
		this.key = key;
		this.cookieName = cookieName;
		this.interceptPath = interceptPath;
	}
	
	@Override
	public void challenge(Response response, boolean stale) {
		boolean loggable = response.getRequest().isLoggable() && getLogger().isLoggable(Level.FINE);
		if (loggable) {
			getLogger().log(Level.FINE, "An authentication challenge was requested.");
		}

		response.setStatus(Status.CLIENT_ERROR_UNAUTHORIZED);
		
		final ChallengeRequest cr = new ChallengeRequest(getScheme());
		cr.setStale(stale);
		if (response.getAttributes().get("activationKey") != null) {
			cr.getParameters().set("activationKey", (String) response.getAttributes().get("activationKey"));
		}
		response.getChallengeRequests().add(cr);
	}
	
	@Override
	protected boolean authenticate(Request request, Response response) {
		final boolean authenticated;
		ChallengeResponse cr = null;
		if (isIntercepting(request, response)) {
			intercept(request, response);
			super.authenticate(request, response);
			authenticated = true;
		} else {
			final Cookie cookie = request.getCookies().getFirst(cookieName);
			if (cookie != null) {
				cr = parse(cookie.getValue());
				request.setChallengeResponse(cr);
			}
			authenticated = super.authenticate(request, response);
		}
		
		if (!isOptional() && !authenticated && delay > 0) {
			// delay to reduce the effectiveness of brute force attacks
			final String identifier = cr.getIdentifier();
			
			try {
				if (loginAttempts.get(identifier) == null) {
					loginAttempts.put(identifier, new Semaphore(1));
				}
				final Semaphore semaphore = loginAttempts.get(identifier);
				semaphore.acquire();
				Thread.sleep(delay);
				semaphore.release();
			} catch (InterruptedException e) {
				;
			}
		}
		return authenticated;
	}

	@Override
	protected int authenticated(Request request, Response response) {
		final ChallengeResponse cr = request.getChallengeResponse();
		if (cr == null) return CONTINUE;

		boolean remember = false;
		try { remember = Boolean.parseBoolean(cr.getParameters().getFirstValue("remember")); } catch (Exception e) {}
		final String value = format(cr);
		if (value == null || value.equals(cr.getRawValue())) return CONTINUE;
		
		try {
			final CookieSetting credentialsCookie = getCredentialsCookie(request, response);
			credentialsCookie.setValue(new Crypto().encrypt(key, value));
			credentialsCookie.setMaxAge(remember ? maxCookieAge : -1);
			credentialsCookie.setSecure(secure);
			credentialsCookie.setAccessRestricted(true);
			return super.authenticated(request, response);
		} catch (CryptoException e) {
			getLogger().log(Level.WARNING, "Unable to set cookie", e);
			return STOP;
		}
	}

	public ChallengeResponse parse(String encrypted) {
		try {
			final String decrypted = Crypto.decrypt(key, encrypted);
			final Form p = new Form(decrypted);
			
			long expires = Long.parseLong(p.getFirstValue("expires"));
			if (expires < System.currentTimeMillis()) {
				return null;
			}

			final ChallengeResponse result = new ChallengeResponse(getScheme());
			result.setRawValue(decrypted);
			result.setTimeIssued(Long.parseLong(p.getFirstValue("issued")));
			result.setIdentifier(p.getFirstValue("identifier"));
			result.setSecret(p.getFirstValue("secret"));
			result.getParameters().set("expires", Long.toString(expires));
			result.getParameters().set("authenticator", p.getFirstValue("authenticator"));
			return result;
		} catch (Exception e) {
			getLogger().log(Level.INFO, "Unable to decrypt cookie credentials", e);
			return null;
		}
	}
	
	public String format(ChallengeResponse cr) {
		long issued = cr.getTimeIssued();
		long expires = 0;
		try { expires = Long.parseLong(cr.getParameters().getFirstValue("expires")); } catch (Throwable e) {}
		if (issued + 60000 < System.currentTimeMillis()) {
			issued = System.currentTimeMillis();
			expires = System.currentTimeMillis() + maxTokenAge;
		}
		boolean remember = false;
		try { remember = Boolean.parseBoolean(cr.getParameters().getFirstValue("remember")); } catch (Throwable e) {}
		
		final Form p = new Form();
		p.set("issued", Long.toString(issued));
		p.set("expires", Long.toString(expires));
		p.set("identifier", cr.getIdentifier());
		p.set("remember", Boolean.toString(remember));
		if (cr.getSecret() != null) p.set("secret", new String(cr.getSecret()));
		final String authenticator = cr.getParameters().getFirstValue("authenticator");
		if (authenticator != null) p.set("authenticator", authenticator);

		return p.getQueryString();
	}

	protected boolean isIntercepting(Request request, Response response) {
		return interceptPath != null
			&& interceptPath.equals(request.getResourceRef().getRemainingPart(false, false))
			&& (Method.POST.equals(request.getMethod()) || Method.DELETE.equals(request.getMethod()));
	}
	
	protected boolean intercept(Request request, Response response) {
		final Form form;
		if (request.getMethod() == Method.DELETE) {
			form = new Form();
			form.set("action", ACTION_LOGOUT);
		} else if (request.getMethod() == Method.POST) {
			form = new Form(request.getEntity());
			if (form.getFirstValue("action") == null) form.set("action", ACTION_LOGIN);
		} else {
			return true;
		}
	
		ChallengeResponse cr = null;
		final String action = form.getFirstValue("action");
		if (ACTION_LOGIN.equals(action)) {
			// the user is attempting to log in
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			cr.getParameters().add("impersonate", form.getFirstValue("impersonate"));
			cr.getParameters().add("remember", form.getFirstValue("remember"));
		} else if ("logout".equals(action)) {
			// the user is attempting to log out
			cr = parse(request.getCookies().getFirst(cookieName).getValue());
			
			final String authenticator = cr.getParameters().getFirstValue("authenticator");
			if (authenticator == null) {
				// user is logged in normally, discard their credentials 
				request.setChallengeResponse(null);
				final CookieSetting credentialsCookie = getCredentialsCookie(request, response);
				credentialsCookie.setMaxAge(0);
				return false;
			} else {
				// user is currently impersonating another user so restore them to their original credentials
				cr.setIdentifier(authenticator);
				cr.getParameters().remove("authenticator");
			}
		} else if (ACTION_RESET.equals(action)) {
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			request.getAttributes().put("form", form);
		} else if (ACTION_ENROLE.equals(action)) {
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			cr.getParameters().add("email", form.getFirstValue("email"));
			cr.getParameters().add("firstName", form.getFirstValue("firstName"));
			cr.getParameters().add("lastName", form.getFirstValue("lastName"));
			request.getAttributes().put("form", form);
		} else if (ACTION_ACTIVATE.equals(action)) {
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			cr.getParameters().add("activationKey", form.getFirstValue("identifier"));
		} else if (ACTION_IMPERSONATE.equals(action)) {
			cr = parse(request.getCookies().getFirst(cookieName).getValue());
			cr.getParameters().add("impersonate", form.getFirstValue("impersonate"));
			request.setChallengeResponse(cr);
		}
		cr.getParameters().add("action", action);
		request.setChallengeResponse(cr);
		return true;
	}

	protected CookieSetting getCredentialsCookie(Request request, Response response) {
		CookieSetting cs = response.getCookieSettings().getFirst(cookieName);

		if (cs == null) {
			cs = new CookieSetting(cookieName, null);
			cs.setAccessRestricted(true);

			if (request.getRootRef() != null) {
				String p = request.getRootRef().getPath();
				cs.setPath(p == null ? "/" : p);
			} else {
			}
			response.getCookieSettings().add(cs);
		}

		return cs;
	}
	
	public int getMaxCookieAge() {
		return maxCookieAge;
	}
	public void setMaxCookieAge(int maxCookieAge) {
		this.maxCookieAge = maxCookieAge;
	}
	
	public int getMaxTokenAge() {
		return maxTokenAge;
	}
	public void setMaxTokenAge(int maxTokenAge) {
		this.maxTokenAge = maxTokenAge;
	}
	
	public int getDelay() {
		return delay;
	}
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	public boolean isSecure() {
		return secure;
	}
}
