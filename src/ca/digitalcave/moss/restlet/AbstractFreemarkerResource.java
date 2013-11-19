package ca.digitalcave.moss.restlet;

import java.util.Date;
import java.util.HashMap;

import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import freemarker.template.Configuration;

public abstract class AbstractFreemarkerResource extends ServerResource {

	@Override
	/**
	 * Sets up the mime types.  By default the variants for Text/HTML, Application/Javascript,
	 * and Image/All are added.  If you filter on different media types in isTransform(), 
	 * you may need to modify the order of these.
	 */
	protected void doInit() throws ResourceException {
		getVariants().add(new Variant(MediaType.TEXT_HTML));
		getVariants().add(new Variant(MediaType.TEXT_CSS));
		getVariants().add(new Variant(MediaType.APPLICATION_JAVASCRIPT));
		getVariants().add(new Variant(MediaType.IMAGE_ALL));
	}
	
	public Representation get(Variant variant) throws ResourceException {
		final String path = new Reference(getRootRef(), getOriginalRef()).getRemainingPart(true, false);
				
		if (path.startsWith("WEB-INF")) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
		
		if (isTransform(variant)) {
			final TemplateRepresentation entity = new TemplateRepresentation(path, getFreemarkerConfig(), getDataModel(), variant.getMediaType());
			if (entity.getTemplate() == null) throw new ResourceException(Status.CLIENT_ERROR_NOT_FOUND);
			entity.setModificationDate(new Date());
			return entity;
		} else {
			final Request request = new Request(Method.GET, new Reference("war://" + path));
			request.getConditions().setUnmodifiedSince(getRequest().getConditions().getUnmodifiedSince());
			getContext().getClientDispatcher().handle(request, getResponse());
			return getResponseEntity();
		}
	}

	/**
	 * This method must return your Freemarker configuration object.  For instance, 
	 * return ((HelpdeskApplication) getApplication()).getFreemarkerConfiguration();
	 * @return
	 */
	abstract protected Configuration getFreemarkerConfig();
	
	/**
	 * This method determines whether this resource is supposed to transform the file through Freemarker, or
	 * just return it without any changes.  Returns true to transform, false otherwise.
	 * 
	 * The default implementation is to return true if the variant is either Text/HTML or Application/Javascript.
	 * @param variant
	 * @return
	 */
	protected boolean isTransform(Variant variant){
		return variant.getMediaType().equals(MediaType.TEXT_HTML) || variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT);
	}
	
	/**
	 * Returns the data model to inject into the freemarker template.  
	 * 
	 * By default, this method injects the Restlet User object under key "user", and the HTTP request 
	 * attributes under key "requestAttributes".  Override this method if you need more information in your data model.
	 * @return
	 */
	protected Object getDataModel(){
		final HashMap<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("user", getClientInfo().getUser());
		dataModel.put("requestAttributes", getRequestAttributes());
		return dataModel;
	}
}
