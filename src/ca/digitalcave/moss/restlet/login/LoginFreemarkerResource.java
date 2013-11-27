package ca.digitalcave.moss.restlet.login;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import freemarker.cache.ClassTemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

public class LoginFreemarkerResource extends ServerResource {
	protected static Configuration freemarkerConfig = null;

	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.APPLICATION_JAVASCRIPT));
		getVariants().add(new Variant(MediaType.IMAGE_ALL));
	}
	
	public Representation get(Variant variant) throws ResourceException {
		final String path = "resource/" + (getReference().getRemainingPart().replaceAll("\\?.*$", "") + "." + getOriginalRef().getExtensions()).replaceAll("^/", "");
		
		
		if (variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT)){
			final LoginRouterConfiguration configuration = (LoginRouterConfiguration) getRequest().getAttributes().get("configuration");
			if (configuration.routerAttachPoint == null){
				//Find the attachment point of the login router; e.g. "login".
				configuration.routerAttachPoint = getReference().toString().replace(getRootRef().toString(), "").replace(getReference().getRemainingPart(), "").replaceFirst("^/", "");
			}
			final LoginRouterConfiguration dataModel = configuration.clone();
			dataModel.translation = ResourceBundle.getBundle(configuration.i18nBase);
			if (configuration.i18nBaseCustom != null) dataModel.customTranslation = ResourceBundle.getBundle(configuration.i18nBaseCustom);
			final TemplateRepresentation entity = new TemplateRepresentation(path, getFreemarkerConfig(), dataModel, variant.getMediaType());
			if (entity.getTemplate() == null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			entity.setModificationDate(new Date());
			return entity;
		}
		else {
			final InputStream is = this.getClass().getResourceAsStream(path);
			if (is == null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			return new OutputRepresentation(variant.getMediaType()) {
				@Override
				public void write(OutputStream os) throws IOException {
					copyStream(is, os);
				}
			};
		}
	}

	protected Configuration getFreemarkerConfig(){
		if (freemarkerConfig == null){
			Configuration config = new Configuration();
			config.setTemplateLoader(new ClassTemplateLoader(LoginFreemarkerResource.class, ""));
			config.setDefaultEncoding("UTF-8");
			config.setLocalizedLookup(false);
			config.setLocale(Locale.ENGLISH);
			config.setTemplateUpdateDelay(0);
			final BeansWrapper beansWrapper = new BeansWrapper();
			beansWrapper.setSimpleMapWrapper(true);
			config.setObjectWrapper(beansWrapper);
			config.setDateFormat("yyyy'-'MM'-'dd");
			config.setDateTimeFormat("yyyy'-'MM'-'dd' 'HH:mm");
			config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
			freemarkerConfig = config;
		}
		return freemarkerConfig;
	}
	
	private static void copyStream(InputStream is, OutputStream os) throws IOException {
		BufferedInputStream bis = new BufferedInputStream(is);
		BufferedOutputStream bos = new BufferedOutputStream(os);

		byte[] data = new byte[1024];
		int bytesRead;
		while((bytesRead = bis.read(data)) > -1){
			bos.write(data, 0, bytesRead);
		}

		bos.flush();
		bos.close();
	}
}
