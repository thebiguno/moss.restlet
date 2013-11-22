package ca.digitalcave.moss.restlet.login;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.ResourceBundle;

import freemarker.core.Environment;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public abstract class ExtraFieldsDirective implements TemplateDirectiveModel {
	public void execute(Environment env, @SuppressWarnings("rawtypes") Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
		final Writer out = env.getOut();
		writeFields(out, ((ResourceBundleModel) env.getDataModel().get("translation")).getBundle(), env.getDataModel().get("customTranslation") == null ? null : ((ResourceBundleModel) env.getDataModel().get("customTranslation")).getBundle());
	}
	
	public abstract void writeFields(Writer out, ResourceBundle translation, ResourceBundle customTranslation);
}
