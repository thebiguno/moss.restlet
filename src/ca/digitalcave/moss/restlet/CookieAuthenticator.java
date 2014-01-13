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
	private volatile int maxCookieAge = -1;
	private volatile int maxExpiryAge = Integer.MAX_VALUE;
	private volatile boolean secure = false;
	private volatile boolean allowRemember = true;

	public CookieAuthenticator(Context context, boolean optional, Key key) {
		this(context, optional, key, "_auth", "/index");
	}
	/**
	 * <p>This authenticator supports the following features:</p>
	 * <ul><li>XHR-based interface to a single end-point for all authentication operations</li>
	 *     <li>Redirects are not used since they result in mixed content warnings when using Apache mod_proxy and HTTPS</li>
	 *     <li>Both authentication identity and authorization identity (i.e. impersonation of another user)</li>
	 *     <li>Automatic login after enrollment and both forced and elective password reset</li>
	 *     <li>Time limited authentication window that moves with each valid request (defaults to max integer which is 68 years)</li>
	 *     <li>Automatic delay after invalid authentication (defaults to 1500 ms)</li>
	 *     <li>Does not permit user enumeration attacks for account creation and password reset requests</li>
	 *     <li>Can be combined with other authenticators</li></ul>
	 * <p>Uses following additional parameters in the challenge response:</p>
	 * <dl><dt>action</dt><dd>one of login, logout, impersonate, register, reset, activate</dd>
	 *     <dt>expires</dt><dd>the date/time after which the token will no longer be honored</dd>
	 *     <dt>remember</dt><dd>indicates that the user wishes to retain the cookie</dd>
	 *     <dt>authenticator</dt><dd>the authentication identity whose password will be verified</dd>
	 *     <dt>email</dt><dd>the email address of the user requesting a new account or requesting a password reset</dd>
	 *     <dt>firstName</dt><dd>the first name of the user requesting a new account</dd>
	 *     <dt>lastName</dt><dd>the last name of the user requesting a new account</dd>
	 * <p>The following information is written into the cookie:</p>
	 * <ul><li>Authenticator Identifier</li>
	 *     <li>Authenticator Secret</li>
	 *     <li>Authorizer Identifier</li>
	 *     <li>Issue Date/Time</li>
	 *     <li>Expire Date/Time</li></ul>
	 * <p>The following basic work-flows are supported:</p>
	 * <ul><li>Logged Out > Login [ > Activation ] > Logged In</li>
	 *     <li>Logged Out > Register > Activation > Logged In</li>
	 *     <li>Logged Out > Reset > Activation > Logged In</li>
	 *     <li>Logged In > Impersonate > Logged In</li>
	 *     <li>Logged In > Logout > Logged Out</li></ul>
	 * <p>The following form posts are understood by this authenticator.</p>
	 * 
	 * <ul>
	 *     <li>POST identifier=identifier, secret=secret [, action=login][, impersonate=identifier]</li>
	 *     <li>POST action=logout</li>
	 *     <li>DELETE</li>
	 *     <li>POST action=reset, identifier=identifier</li>
	 *     <li>POST action=register, identifier=identifier, email=email [, firstName=first_name] [, lastName=last_name]</li>
	 *     <li>POST action=impersonate, identifier=identifier</li>
	 *     <li>POST action=activate, identifier=activationKey and secret=newPassword</li>
	 * </ul>
	 * <p>If a verifier accepts alternate identifiers such as email address for login it MUST change the identifier in the challenge response to the canonical identifier.</p>
	 * <p>The verifier MUST replace the identifier from activation key to the canonical identifier during activation.</p>
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
	}
	
	@Override
	protected boolean authenticate(Request request, Response response) {
		final boolean intercepting = isIntercepting(request, response);
		final boolean result;
		final boolean authenticated;
		final ChallengeResponse cr;
		if (intercepting) {
			intercept(request, response);
			cr = request.getChallengeResponse();
			authenticated = super.authenticate(request, response);
			result = true;	//You always want to return to the index, even if credentials are bad.
		} else {
			final Cookie cookie = request.getCookies().getFirst(cookieName);
			if (cookie != null) {
				cr = parse(cookie.getValue(), true);
				request.setChallengeResponse(cr);
			}
			else {
				cr = null;
			}
			authenticated = super.authenticate(request, response);
			result = authenticated;
		}
		
		if (!authenticated && (!isOptional() || intercepting) && delay > 0 && cr != null) {
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
		return result;
	}

	@Override
	protected int authenticated(Request request, Response response) {
		final ChallengeResponse cr = request.getChallengeResponse();
		if (cr != null){
			final String value = format(cr);
			if (value != null && !value.equals(cr.getRawValue())){
				//Set the cookie if it is not already set
				try {
					final CookieSetting credentialsCookie = getCredentialsCookie(request, response);
					credentialsCookie.setValue(new Crypto().encrypt(key, value));
					if (isAllowRemember()) {
						boolean remember = false;
						try { remember = Boolean.parseBoolean(cr.getParameters().getFirstValue("remember")); } catch (Exception e) {}
						credentialsCookie.setMaxAge(remember ? Integer.MAX_VALUE : -1);
					} else {
						credentialsCookie.setMaxAge(maxCookieAge);
					}
					credentialsCookie.setSecure(secure);
					credentialsCookie.setAccessRestricted(true);
				} catch (CryptoException e) {
					getLogger().log(Level.WARNING, "Unable to set cookie", e);
					return STOP;
				}
			}
		}
		
		return super.authenticated(request, response);
	}

	public ChallengeResponse parse(String encrypted, boolean checkExpiry) {
		try {
			final String decrypted = Crypto.decrypt(key, encrypted);
			final Form p = new Form(decrypted);
			
			long expires = Long.parseLong(p.getFirstValue("expires"));
			if (checkExpiry && expires < System.currentTimeMillis()) {
				return null;
			}

			final ChallengeResponse result = new ChallengeResponse(getScheme());
			result.setRawValue(decrypted);
			result.setTimeIssued(Long.parseLong(p.getFirstValue("issued")));
			result.setIdentifier(p.getFirstValue("identifier"));
			result.setSecret(p.getFirstValue("secret"));
			result.getParameters().set("expires", Long.toString(expires));
			if (p.getFirstValue("authenticator") != null) {
				result.getParameters().set("authenticator", p.getFirstValue("authenticator"));
			}
			if (isAllowRemember()) {
				result.getParameters().set("remember", p.getFirstValue("remember"));
			}
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
			expires = System.currentTimeMillis() + ((long) maxExpiryAge * 1000);
		}
		
		final Form p = new Form();
		p.set("issued", Long.toString(issued));
		p.set("expires", Long.toString(expires));
		p.set("identifier", cr.getIdentifier());
		if (isAllowRemember()) {
			boolean remember = false;
			try { remember = Boolean.parseBoolean(cr.getParameters().getFirstValue("remember")); } catch (Throwable e) {}
			p.set("remember", Boolean.toString(remember));
		}
		if (cr.getSecret() != null) p.set("secret", new String(cr.getSecret()));
		final String authenticator = cr.getParameters().getFirstValue("authenticator");
		if (authenticator != null) p.set("authenticator", authenticator);
		
//		System.out.println(p.getQueryString());
		
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
			form.set("action", "logout");
		} else if (request.getMethod() == Method.POST) {
			form = new Form(request.getEntity());
			if (form.getFirstValue("action") == null) form.set("action", "login");
		} else {
			return true;
		}
		
		final Action action = Action.find(form.getFirstValue("action"));
	
		ChallengeResponse cr = null;
		if (action == Action.LOGIN) {
			if (form.getFirstValue("identifier") == null) {
				// re-authenticating expired credentials
				cr = parse(request.getCookies().getFirst(cookieName).getValue(), false);
				if (cr == null) {
					response.setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
					return false;
				}
				cr.getParameters().removeAll("expires");
				cr.setSecret(form.getFirstValue("secret"));
			} else if (form.getFirstValue("impersonate") == null) {
				// normal login 
				cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			} else {
				// attempting to sudo right away
				cr = new ChallengeResponse(getScheme(), form.getFirstValue("impersonate"), form.getFirstValue("secret"));
				cr.getParameters().set("authenticator", form.getFirstValue("identifier"));
			}
			if (isAllowRemember()) {
				cr.getParameters().add("remember", form.getFirstValue("remember") != null ? "true" : "false");
			}
		} else if (action == Action.LOGOUT) {
			try {
				cr = parse(request.getCookies().getFirst(cookieName).getValue(), true);
				
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
					cr.getParameters().removeAll("authenticator");
				}
			} catch (Throwable t) {
				; // no cookie, already not logged in
			}
		} else if (action == Action.RESET) {
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			request.getAttributes().put("form", form);
		} else if (action == Action.REGISTER) {
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			cr.getParameters().add("email", form.getFirstValue("email"));
			cr.getParameters().add("firstName", form.getFirstValue("firstName"));
			cr.getParameters().add("lastName", form.getFirstValue("lastName"));
			request.getAttributes().put("form", form);
		} else if (action == Action.ACTIVATE) {
			cr = new ChallengeResponse(getScheme(), form.getFirstValue("identifier"), form.getFirstValue("secret"));
			cr.getParameters().add("activationKey", form.getFirstValue("identifier"));
			cr.getParameters().add("remember", form.getFirstValue("remember"));
		} else if (action == Action.IMPERSONATE) {
			cr = parse(request.getCookies().getFirst(cookieName).getValue(), true);
			cr.getParameters().add("authenticator", cr.getIdentifier());
			cr.setIdentifier(form.getFirstValue("impersonate"));
			request.setChallengeResponse(cr);
		}
		if (cr != null) {
			cr.getParameters().add("action", form.getFirstValue("action"));
		}
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
	/**
	 * Sets the maximum number of seconds that the browser should retain the cookie.
	 * Use -1 to discard the cookie at the end of the session.
	 * This option is only used when the allowRemember option is false.
	 */
	public void setMaxCookieAge(int maxCookieAge) {
		this.maxCookieAge = maxCookieAge;
	}
	
	public int getMaxExpiryAge() {
		return maxExpiryAge;
	}
	/**
	 * Sets the maximum number of seconds that the server should honor the cookie.
	 * Use this option to simulate a session timeout.
	 * Defaults to MAX_INT.
	 */
	public void setMaxExpiryAge(int maxExpiryAge) {
		this.maxExpiryAge = maxExpiryAge;
	}
	
	public int getDelay() {
		return delay;
	}
	/**
	 * Sets the number of milliseconds to delay after authentication failure.
	 * Defaults to 1500.
	 * Use this option to limit brute force attacks.
	 */
	public void setDelay(int delay) {
		this.delay = delay;
	}
	
	public boolean isSecure() {
		return secure;
	}
	/**
	 * Indicates if the cookie should only be transmitted from the browser to the server by secure means (https). 
	 * Default is false.
	 */
	public void setSecure(boolean secure) {
		this.secure = secure;
	}
	
	public boolean isAllowRemember() {
		return allowRemember;
	}
	/**
	 * Indicates if the user has an option to remember the cookie for the maximum age.
	 * If this option is true and the user opts to have their cookie remembered then the cookie age is set to MAX_INT, otherwise it is set to -1 (discard at end of session).
	 * If this option is false the cookie age is set according to the maxCookieAge option.
	 * Default is true.
	 */
	public void setAllowRemember(boolean allowRemember) {
		this.allowRemember = allowRemember;
	}
	
	public enum Action {
		LOGIN, LOGOUT, IMPERSONATE, REGISTER, RESET, ACTIVATE;
	
		public static Action find(String name) {
			if ("login".equalsIgnoreCase(name)) {
				return LOGIN;
			} else if ("logout".equalsIgnoreCase(name)) {
				return LOGOUT;
			} else if ("impersonate".equalsIgnoreCase(name)) {
				return IMPERSONATE;
			} else if ("enrole".equalsIgnoreCase(name) || "register".equalsIgnoreCase(name)) {
				return REGISTER;
			} else if ("reset".equalsIgnoreCase(name)) {
				return RESET;
			} else if ("activate".equals(name)) {
				return ACTIVATE;
			} else {
				return LOGIN;
			}
		}
	}

	public static String getAuthenticator(ChallengeResponse cr) {
		if (cr.getParameters().getFirstValue("authenticator") != null) {
			return cr.getParameters().getFirstValue("authenticator");
		} else {
			return cr.getIdentifier();
		}
	}
	public static void setPasswordExpired(Request request) {
		request.getChallengeResponse().getParameters().add("passwordExpired", "true");
	}
}
