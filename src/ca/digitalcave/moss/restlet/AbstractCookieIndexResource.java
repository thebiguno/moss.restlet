package ca.digitalcave.moss.restlet;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
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


public abstract class AbstractCookieIndexResource extends ServerResource {

	@Override
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		getVariants().add(new Variant(MediaType.APPLICATION_JSON));
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final ChallengeResponse cr = getRequest().getChallengeResponse();
		final String action = cr.getParameters().getFirstValue("action");
		
		//Delay a random amount of time, between 0 and 1000 millis, so that user enumeration attacks relying on post
		// response time are more difficult
		try {long delay = (long) (Math.random() * 1000); System.out.println(delay); Thread.sleep(delay);} catch (Throwable e){}
		
		final HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		
		if ("login".equals(action)) {
			final User user = (User) getClientInfo().getUser();
			if (user == null) {
				result.put("success", false);
				result.put("msg", "Invalid Credentials");
			}

			// TODO forced password change would set an activation key onto the account and return that in the response
		} else if (isAllowImpersonate() && "impersonate".equals(action)) {
			if (cr.getParameters().getFirstValue("authenticator") == null) {
				result.put("success", false);
				result.put("msg", "Not Permitted");
			}
		} else if (isAllowEnrole() && "enrole".equals(action)) {
			final User user = new User();
			user.setIdentifier(cr.getIdentifier());
			user.setEmail(cr.getParameters().getFirstValue("email"));
			user.setFirstName(cr.getParameters().getFirstValue("firstName"));
			user.setLastName(cr.getParameters().getFirstValue("lastName"));
			final String activationKey = UUID.randomUUID().toString();
			insertUser(user, activationKey);
			sendActivationKey(user.getEmail(), activationKey);
		} else if (isAllowReset() && "reset".equals(action)) {
			final String activationKey = UUID.randomUUID().toString();
			updateActivationKey(cr.getIdentifier(), activationKey);
			//Send the email in a different thread so that the time taken to send the email does not help identify valid accounts
			new Thread(new Runnable() { public void run() { if (getClientInfo().getUser() != null) sendActivationKey(getClientInfo().getUser().getEmail(), activationKey); } }).start();
		} else if ("activate".equals(action)) {
			final String password = new String(cr.getSecret());
			// TODO policies could be enforced here such as strength, dictionary words or password history
			final String hash = getHash(password);
			updateSecret(cr.getIdentifier(), cr.getParameters().getFirstValue("activationKey"), hash);
		} else {
			result.put("success", false);
			result.put("msg", "Unknown action " + action);
		}

		return new WriterRepresentation(MediaType.APPLICATION_JSON) {
			@Override
			public void write(Writer w) throws IOException {
				final JsonGenerator g = getJsonFactory().createJsonGenerator(w);
				g.writeStartObject();
				g.writeBooleanField("success", (Boolean) result.get("success"));
				if (result.containsKey("msg")) g.writeStringField("msg", (String) result.get("msg"));
				if (result.containsKey("key")) g.writeStringField("key", (String) result.get("key"));
				g.writeObjectFieldStart("errors");
				if (result.containsKey("secret")) g.writeStringField("secret", (String) result.get("secret"));
				g.writeEndObject();
				g.writeEndObject();
				g.flush();
			}
		};
	}
	
	@Override
	protected Representation delete(Variant variant) throws ResourceException {
		//Delete should always work; the request is intercepted by the CookieAuthenticator, and the 
		// login token is deleted.
		return new StringRepresentation("{success:true}");
	}
	
	/**
	 * Implement this to persist the provided user and activation key
	 * @param account
	 * @throws ResourceException
	 */
	protected abstract void insertUser(User user, String activationKey) throws ResourceException;
	
	/**
	 * Implement this to set an activation key for the provided identifier
	 * @param identifier
	 * @param activationKey
	 * @throws ResourceException
	 */
	protected abstract void updateActivationKey(String identifier, String activationKey) throws ResourceException;
	
	/**
	 * Implement this to set the hashed secret for the provided identifier.  This method MUST verify that the activation key
	 * is valid for the given identifier.
	 * @param identifier
	 * @param activationKey
	 * @param hashedSecret
	 * @throws ResourceException
	 */
	protected abstract void updateSecret(String identifier, String activationKey, String hashedSecret) throws ResourceException;
	
	/**
	 * Override this to use the JsonFactory provided in the main application.  The default implementation
	 * includes a singleton factory.
	 * @return
	 */
	protected JsonFactory getJsonFactory(){
		if (jsonFactory == null){
			jsonFactory = new JsonFactory();
		}
		return jsonFactory;
	}
	private static JsonFactory jsonFactory;
	
	/**
	 * Default implementation of getConfig(), returning an empty properties file.  Default values will be used for
	 * all properties required.
	 */
	protected Properties getConfig() {
		return new Properties();
	}
	
	/**
	 * Override this method to allow impersonation; default is false.
	 * @return
	 */
	protected boolean isAllowImpersonate(){
		return false;
	}

	/**
	 * Override this method to allow enrollment; default is false.
	 * @return
	 */
	protected boolean isAllowEnrole(){
		return false;
	}
	
	/**
	 * Override this method to allow password reset; default is false.
	 * @return
	 */
	protected boolean isAllowReset(){
		return false;
	}

	/**
	 * Hashes the given password using the default hash algorithm and parameters in moss crypto's Hash 
	 * object.  Override this method to use a different hash algorithm.
	 * @param password
	 * @return
	 */
	protected String getHash(String password){
		return new MossHash().generate(password);
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
}
