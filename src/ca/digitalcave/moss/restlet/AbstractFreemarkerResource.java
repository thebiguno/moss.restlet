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

	protected Representation get(Variant variant) throws ResourceException {
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

	abstract protected Configuration getFreemarkerConfig();
	
	protected boolean isTransform(Variant variant){
		return variant.getMediaType().equals(MediaType.TEXT_HTML) || variant.getMediaType().equals(MediaType.APPLICATION_JAVASCRIPT);
	}
	
	protected HashMap<String, Object> getDataModel(){
		final HashMap<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put("user", getClientInfo().getUser());
		dataModel.put("requestAttributes", getRequestAttributes());
		return dataModel;
	}
}
