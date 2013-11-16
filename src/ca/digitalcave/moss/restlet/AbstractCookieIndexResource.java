package ca.digitalcave.moss.restlet;

import java.io.IOException;
import java.io.Writer;
import java.util.Properties;
import java.util.UUID;

import org.restlet.Client;
import org.restlet.Request;
import org.restlet.data.ChallengeResponse;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.ext.xml.SaxRepresentation;
import org.restlet.ext.xml.XmlWriter;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.representation.WriterRepresentation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;
import org.xml.sax.SAXException;

import ca.digitalcave.moss.crypto.MossHash;
import ca.digitalcave.moss.restlet.CookieAuthenticator.Action;


public abstract class AbstractCookieIndexResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final ChallengeResponse cr = getRequest().getChallengeResponse();
		
		// Delay a random amount of time, between 0 and 500 millis, so that user enumeration attacks relying on post
		// response time are more difficult
		try { Thread.sleep((long) (Math.random() * 500));} catch (Throwable e){}
		
		final Action action = Action.find(cr.getParameters().getFirstValue("action"));
		boolean success = true;
		String loginActivationKey = null;
		
		if (action == Action.LOGIN) {
			final User user = (User) getClientInfo().getUser();
			if (user == null) {
				success = false;
			}
			if (isPasswordExpired()) {
				loginActivationKey = UUID.randomUUID().toString();
				updateActivationKey(user.getIdentifier(), loginActivationKey);
			}
		} else if (action == Action.IMPERSONATE) {
			if (isAllowImpersonate() == false || cr.getParameters().getFirstValue("authenticator") == null) {
				success = false;
			}
		} else if (isAllowEnrole() && action == Action.REGISTER) {
			final User user = new User();
			user.setIdentifier(cr.getIdentifier());
			user.setEmail(cr.getParameters().getFirstValue("email"));
			user.setFirstName(cr.getParameters().getFirstValue("firstName"));
			user.setLastName(cr.getParameters().getFirstValue("lastName"));
			final String activationKey = UUID.randomUUID().toString();
			if (insertUser(user, activationKey)) {
				sendActivationKey(user.getEmail(), activationKey);
			}
			// success is always true to prevent user enumeration attacks
		} else if (isAllowReset() && action == Action.RESET) {
			final String activationKey = UUID.randomUUID().toString();
			updateActivationKey(cr.getIdentifier(), activationKey);
			if (getClientInfo().getUser() != null) {
				sendActivationKey(getClientInfo().getUser().getEmail(), activationKey);
			}
			// success is always true to prevent user enumeration attacks
		} else if (action == Action.ACTIVATE) {
			final String password = new String(cr.getSecret());
			success = isValidPassword(password);
			if (success) {
				updateSecret(cr.getIdentifier(), cr.getParameters().getFirstValue("activationKey"), password);
				// success is always true to prevent user enumeration attacks
			}
		}

		return getPostRepresentation(action, success, loginActivationKey);
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		// Delete should always work; 
		// the request is intercepted by the CookieAuthenticator, and the login token is deleted.
		return new StringRepresentation("{success:true}");
	}
	
	/**
	 * Implement this to persist the provided user and activation key.
	 * @return true if the user was successfully added.
	 */
	protected abstract boolean insertUser(User user, String activationKey);
	
	/**
	 * Implement this to set an activation key for the provided identifier.
	 */
	protected abstract void updateActivationKey(String identifier, String activationKey);
	
	/**
	 * Implement this to set the hashed secret for the provided identifier.
	 * This method MUST verify that the activation key is valid for the given identifier to prevent a password reset attack.
	 */
	protected abstract void updateSecret(String identifier, String activationKey, String hash);
	
	protected String getHash(String secret) {
		return new MossHash().generate(secret);
	}
	
	/**
	 * Default implementation of getConfig(), returning an empty properties file.  
	 * Default values will be used for all properties required.
	 */
	protected Properties getConfig() {
		return new Properties();
	}
	
	/**
	 * Override this method to force password reset.
	 * Default implementation returns false.
	 */
	protected boolean isPasswordExpired() {
		return false;
	}
	
	/**
	 * Override this method to allow impersonation.
	 * Default implementation returns false;
	 */
	protected boolean isAllowImpersonate(){
		return false;
	}

	/**
	 * Override this method to allow registration.
	 * Default implementation returns false.
	 */
	protected boolean isAllowEnrole(){
		return false;
	}
	
	/**
	 * Override this method to allow password reset; default is false.
	 */
	protected boolean isAllowReset(){
		return false;
	}
	
	/**
	 * Override this method to enforce password policies such as strength, dictionary words or password history.
	 * Default implementation returns true.
	 */
	private boolean isValidPassword(String password) {
		return true;
	}
	
	/**
	 * Returns the response entity for the post.
	 * Override this to change the format of the response.
	 * The default implementation is best suited for ExtJS and Sensha Touch.
	 */
	protected Representation getPostRepresentation(final Action action, final boolean success, final String activationKey) {
		return new WriterRepresentation(MediaType.APPLICATION_JSON) {
			@Override
			public void write(Writer w) throws IOException {
				w.write("{");
				w.write("\"success\":");
				w.write(Boolean.toString(success));
				if (activationKey != null) {
					w.write("\"key\":");
					w.write(activationKey);
					w.write("\"");
				}
				w.write("}");
				w.flush();
			}
		};
	}
	
	/**
	 * Returns a representation which provides the email XML file, in a structure as follows:
	 * 
	 * 	<?xml version="1.0" encoding="ISO-8859-1" ?>
	 * 	<email>
	 * 		<head>
	 * 			<subject>Account activation</subject>
	 * 			<from>support@restlet.org</from>
	 * 			<to>user@domain.com</to>
	 * 			<cc>log@restlet.org</cc>
	 * 		</head>
	 * 		<body><![CDATA[Your account was successfully created!]]></body>
	 * 	</email>
	 * 
	 * @param email
	 * @param activationKey
	 * @return
	 */
	protected Representation getEmailRepresentation(final String email, final String activationKey){
		final SaxRepresentation entity = new SaxRepresentation() {
			@Override
			public void write(XmlWriter w) throws IOException {
				try {
					w.startDocument();
					w.startElement("email");
					w.startElement("head");
					w.dataElement("subject", getConfig().getProperty("mail.subject", "Account activation"));
					w.dataElement("from", getConfig().getProperty("mail.smtp.from", "user@localhost"));
					w.dataElement("to", email);
					w.endElement("head");
					w.startElement("body");
					w.characters("Here is the activation key you requested: ");
					w.characters(activationKey);
					w.characters("\nIf you did not request this activation key please ignore this email.\n");
					w.endElement("body");
					w.endElement("email");
					w.endDocument();
				} catch (SAXException e) {
					throw new IOException(e);
				}
			}
		};
		entity.setCharacterSet(CharacterSet.ISO_8859_1);
		return entity;
	}
	
	/**
	 * Sends the email.  This uses the configuration parameters set in getConfig().  In particular, the following 
	 * parameters are respected, included here along with their defaults (which would be used if getConfig() is not
	 * overridden, or if the properties returned did not include these properties):
	 * 		mail.smtp.from, "Local System",
	 * 		mail.smtp.host, "localhost",
	 * 		mail.smtp.port, "25",
	 * 		mail.smtp.auth, "false",
	 * 		mail.smtp.username, null,
	 * 		mail.smtp.password, null,
	 * 		mail.smtp.starttls.enable, "false"
	 * 
	 * Override this method to use a completely different method for sending mail.
	 * @param email
	 * @param activationKey
	 */
	protected void sendActivationKey(final String email, final String activationKey) {
		final Runnable emailRunnable = new Runnable() { 
			public void run() { 
				final Properties config = getConfig();
		
				final String url = "smtp://" + config.getProperty("mail.smtp.host", "localhost") + ":" + config.getProperty("mail.smtp.port", "25");
				final Request request = new Request(Method.POST, url);
				request.setEntity(getEmailRepresentation(email, activationKey));
				if ("true".equals(config.getProperty("mail.smtp.auth", "false"))) {
					final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.SMTP_PLAIN, config.getProperty("mail.smtp.username"), config.getProperty("mail.smtp.password"));
					request.setChallengeResponse(cr);
				}
				
				final Client client = new Client(getContext().createChildContext(), Protocol.SMTP);
				client.getContext().getParameters().set("startTls", getConfig().getProperty("mail.smtp.starttls.enable", "false"));
				client.handle(request);
			}
		};
		final Thread emailThread = new Thread(emailRunnable, "Email");
		emailThread.setDaemon(false);
		emailThread.start();

	}
}
