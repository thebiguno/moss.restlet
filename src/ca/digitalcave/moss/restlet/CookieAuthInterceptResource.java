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


public abstract class CookieAuthInterceptResource extends ServerResource {

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
		
		final Action action = Action.find(cr.getParameters().getFirstValue("action","login"));
		boolean success = true;
		String loginActivationKey = null;
		
		if (action == Action.LOGIN) {
			if (cr.getParameters().getFirstValue("passwordExpired") != null) {
				loginActivationKey = UUID.randomUUID().toString();
				updateActivationKey(CookieAuthenticator.getAuthenticator(cr), loginActivationKey);
			}
			
			final User user = (User) getClientInfo().getUser();
			if (user == null) {
				success = false;
			}
		} else if (action == Action.IMPERSONATE) {
			if (isAllowImpersonate() == false || cr.getParameters().getFirstValue("authenticator") == null) {
				success = false;
			}
		} else if (isAllowRegister() && action == Action.REGISTER) {
			final String activationKey = UUID.randomUUID().toString();
			final String contactKey = insertUser(getRegistration(), activationKey);
			if (contactKey != null) {
				sendActivationKey(contactKey, activationKey);
			}
		} else if (isAllowReset() && action == Action.RESET) {
			final String activationKey = UUID.randomUUID().toString();
			final String contactKey = updateActivationKey(cr.getIdentifier(), activationKey);
			if (contactKey != null) {
				sendActivationKey(contactKey, activationKey);
			}
		} else if (action == Action.ACTIVATE) {
			final String password = new String(cr.getSecret());
			success = isValidPassword(password);
			if (success) {
				updateSecret(cr.getParameters().getFirstValue("activationKey"), getHash(password));
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
	
	protected User getRegistration() {
		final ChallengeResponse cr = getChallengeResponse();
		final User user = new User();
		user.setIdentifier(cr.getIdentifier());
		user.setEmail(cr.getParameters().getFirstValue("email"));
		user.setFirstName(cr.getParameters().getFirstValue("firstName"));
		user.setLastName(cr.getParameters().getFirstValue("lastName"));
		return user;
	}
	
	/**
	 * Implement this to persist the provided user and activation key.
	 * @return the user's contact key (e.g. email address) or null if the request failed.
	 */
	protected abstract String insertUser(User user, String activationKey);
	
	/**
	 * Implement this to set an activation key for the provided identifier.  The return value of this
	 * method MUST return the contact key for the required contact method.  For instance, email address,
	 * SMS phone number, carrier pigeon routing information, etc.  Return null if the identifier is not valid
	 * to cancel the sending of the activation key.
	 */
	protected abstract String updateActivationKey(String identifier, String activationKey);
	
	/**
	 * Implement this to set the hashed secret for the provided activation key.
	 */
	protected abstract void updateSecret(String activationKey, String hash);
	
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
	protected boolean isAllowRegister(){
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
					w.write(",\"key\":\"");
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
	protected Representation getEmailRepresentation(final String fromEmail, final String toEmail, final String subject, final String activationKey){
		final SaxRepresentation entity = new SaxRepresentation() {
			@Override
			public void write(XmlWriter w) throws IOException {
				try {
					w.startDocument();
					w.startElement("email");
					w.startElement("head");
					w.dataElement("subject", subject);
					w.dataElement("from", fromEmail);
					w.dataElement("to", toEmail);
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
	protected void sendActivationKey(final String toEmail, final String activationKey) {
		final Properties config = getConfig();
		final Runnable emailRunnable = new Runnable() { 
			public void run() { 
				final String url = "smtp://" + config.getProperty("mail.smtp.host", "localhost") + ":" + config.getProperty("mail.smtp.port", "25");
				final Request request = new Request(Method.POST, url);
				final String subject = config.getProperty("mail.subject", "Account activation");
				final String fromEmail = config.getProperty("mail.smtp.from", "user@localhost");
				request.setEntity(getEmailRepresentation(fromEmail, toEmail, subject, activationKey));
				if ("true".equals(config.getProperty("mail.smtp.auth", "false"))) {
					final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.SMTP_PLAIN, config.getProperty("mail.smtp.username"), config.getProperty("mail.smtp.password"));
					request.setChallengeResponse(cr);
				}
				
				final Client client = new Client(getContext().createChildContext(), Protocol.SMTP);
				client.getContext().getParameters().set("startTls", config.getProperty("mail.smtp.starttls.enable", "false"));
				client.handle(request);
			}
		};
		final Thread emailThread = new Thread(emailRunnable, "Email");
		emailThread.setDaemon(false);
		emailThread.start();
	}
}
