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
import org.xml.sax.SAXException;

import ca.digitalcave.moss.crypto.Hash;
import ca.digitalcave.moss.restlet.model.Account;


public abstract class AbstractCookieIndexResource extends ServerResource {

	final String mobile = "android|blackberry|iphone|ipod|iemobile|opera mobile|palmos|webos|googlebot-mobile";

	@Override
	protected void doInit() throws ResourceException {
		final Variant variant = new Variant(MediaType.TEXT_HTML);
		getVariants().add(variant);
	}
	
	@Override
	protected Representation post(Representation entity, Variant variant) throws ResourceException {
		final ChallengeResponse cr = getRequest().getChallengeResponse();
		final String action = cr.getParameters().getFirstValue("action");
		
		final HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("success", true);
		
		if ("login".equals(action)) {
			final Account account = (Account) getClientInfo().getUser();
			if (account == null) {
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
			final Account account = new Account();
			account.setIdentifier(cr.getIdentifier());
			account.setEmail(cr.getParameters().getFirstValue("email"));
			account.setFirstName(cr.getParameters().getFirstValue("firstName"));
			account.setLastName(cr.getParameters().getFirstValue("lastName"));
			account.setActivationKey(UUID.randomUUID().toString());
			insertAccount(account);
			sendEmail(account.getEmail(), account.getActivationKey());
		} else if (isAllowReset() && "reset".equals(action)) {
			final String activationKey = UUID.randomUUID().toString();
			updateActivationKey(cr.getIdentifier(), activationKey);
			sendEmail(cr.getParameters().getFirstValue("email"), activationKey);
		} else if ("activate".equals(action)) {
			final String password = new String(cr.getSecret());
			// TODO policies could be enforced here such as strength, dictionary words or password history
			final String hash = new Hash().generate(password);
			updateSecret(cr.getIdentifier(), hash);
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
	 * Implement this to persist the provided account to the database
	 * @param account
	 * @throws ResourceException
	 */
	protected abstract void insertAccount(Account account) throws ResourceException;
	
	/**
	 * Implement this to set an activation key for the provided identifier
	 * @param identifier
	 * @param activationKey
	 * @throws ResourceException
	 */
	protected abstract void updateActivationKey(String identifier, String activationKey) throws ResourceException;
	
	/**
	 * Implement this to set the hashed secret for the provided identifier
	 * @param identifier
	 * @param hashedSecret
	 * @throws ResourceException
	 */
	protected abstract void updateSecret(String identifier, String hashedSecret) throws ResourceException;
	
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
	 * @param email
	 * @param activationKey
	 */
	private void sendEmail(final String email, final String activationKey) {
//		System.out.println(activationKey);
//		if (true) return;
//		
		final Properties config = getConfig();
		final SaxRepresentation entity = new SaxRepresentation() {
			@Override
			public void write(XmlWriter w) throws IOException {
				try {
					w.startDocument();
					w.startElement("email");
					w.startElement("head");
					w.dataElement("subject", "Account activation");
					w.dataElement("from", config.getProperty("mail.smtp.from", "Local System"));
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
		final String url = "smtp://" + config.getProperty("mail.smtp.host", "localhost") + ":" + config.getProperty("mail.smtp.port", "25");
		final Request request = new Request(Method.POST, url);
		request.setEntity(entity);
		if ("true".equals(config.getProperty("mail.smtp.auth", "false"))) {
			final ChallengeResponse cr = new ChallengeResponse(ChallengeScheme.SMTP_PLAIN, config.getProperty("mail.smtp.username"), config.getProperty("mail.smtp.password"));
			request.setChallengeResponse(cr);
		}
		
		final Client client = new Client(getContext().createChildContext(), Protocol.SMTP);
		client.getContext().getParameters().set("startTls", getConfig().getProperty("mail.smtp.starttls.enable", "false"));
		client.handle(request);
	}
}
