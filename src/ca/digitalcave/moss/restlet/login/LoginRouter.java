package ca.digitalcave.moss.restlet.login;

import java.util.ResourceBundle;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import ca.digitalcave.moss.restlet.util.PasswordChecker;


public class LoginRouter extends Router {
	private final Configuration configuration;
	
	public LoginRouter() {
		this(null, new Configuration());
	}
	
	public LoginRouter(Application application, Configuration configuration) {
		this.configuration = configuration;
		
		//Fill up the defaults
		if (configuration.passwordChecker != null && application != null){
			application.getContext().getAttributes().put(PasswordChecker.class.getName(), configuration.passwordChecker);
		}
		
		this.attach("/checkpassword", PasswordCheckResource.class);
		this.attachDefault(LoginFreemarkerResource.class);
	}
	
	@Override
	protected void doHandle(Restlet next, Request request, Response response) {
		request.getAttributes().put("configuration", configuration);
		
		super.doHandle(next, request, response);
	}
	
	public static class Configuration implements Cloneable {
		public boolean showForgotPassword = true;
		public boolean showLogin = true;
		public boolean showRegister = false;
		
		public PasswordChecker passwordChecker;
		
		public String activationKeyLabel;
		public String activationKeyLabelKey;
		public String activationKeySentMessage;
		public String activationKeySentMessageKey;
		public String backButton;
		public String backButtonKey;
		public String createAccountButton;
		public String createAccountButtonKey;
		public String existingKeyButton;
		public String existingKeyButtonKey;
		public String extraLoginStep1Fields;
		public String extraLoginStep2Fields;
		public String extraRegisterStep1Fields;
		public String extraRegisterStep2Fields;
		public String extraResetStep1PanelFields;
		public String extraResetStep2PanelFields;
		public String invalidCredentialsMessage;
		public String invalidCredentialsMessageKey;
		public String forcedPasswordChangeMessage;
		public String forcedPasswordChangeMessageKey;
		public String formTitle;
		public String formTitleKey;
		public String generateKeyButton;
		public String generateKeyButtonKey;
		public String i18nBase = "ca.digitalcave.moss.restlet.login.i18n.i18n";
		public String identifierLabel;
		public String identifierLabelKey;
		public String loginLabel;
		public String loginLabelKey;
		public String loginTitleKey;
		public String newPasswordLabel;
		public String newPasswordLabelKey;
		public String passwordLabel;
		public String passwordLabelKey;
		public String registerTitle;
		public String registerTitleKey;
		public String resetTitle;
		public String resetTitleKey;
		public String unknownErrorMessage;
		public String unknownErrorMessageKey;

		
		//Not included in clone; set automatically in LoginFreemarkerResource, loaded from i18nBase.
		public ResourceBundle translation;
		
		@Override
		protected Configuration clone() {
			final Configuration result = new Configuration();
			
			result.showForgotPassword = this.showForgotPassword;
			result.showLogin = this.showLogin;
			result.showRegister = this.showRegister;
					
			result.passwordChecker = this.passwordChecker;
					
			result.activationKeyLabel = this.activationKeyLabel;
			result.activationKeyLabelKey = this.activationKeyLabelKey;
			result.backButton = this.backButton;
			result.backButtonKey = this.backButtonKey;
			result.createAccountButton = this.createAccountButton;
			result.createAccountButtonKey = this.createAccountButtonKey;
			result.existingKeyButton = this.existingKeyButton;
			result.existingKeyButtonKey = this.existingKeyButtonKey;
			result.extraLoginStep1Fields = this.extraLoginStep1Fields;
			result.extraLoginStep2Fields = this.extraLoginStep2Fields;
			result.extraRegisterStep1Fields = this.extraRegisterStep1Fields;
			result.extraRegisterStep2Fields = this.extraRegisterStep2Fields;
			result.extraResetStep1PanelFields = this.extraResetStep1PanelFields;
			result.extraResetStep2PanelFields = this.extraResetStep2PanelFields;
			result.formTitle = this.formTitle;
			result.formTitleKey = this.formTitleKey;
			result.generateKeyButton = this.generateKeyButton;
			result.generateKeyButtonKey = this.generateKeyButtonKey;
			result.i18nBase = this.i18nBase;
			result.identifierLabel = this.identifierLabel;
			result.identifierLabelKey = this.identifierLabelKey;
			result.loginLabel = this.loginLabel;
			result.loginLabelKey = this.loginLabelKey;
			result.loginTitleKey = this.loginTitleKey;
			result.newPasswordLabel = this.newPasswordLabel;
			result.newPasswordLabelKey = this.newPasswordLabelKey;
			result.passwordLabel = this.passwordLabel;
			result.passwordLabelKey = this.passwordLabelKey;
			result.registerTitle = this.registerTitle;
			result.registerTitleKey = this.registerTitleKey;
			result.resetTitle = this.resetTitle;
			result.resetTitleKey = this.resetTitleKey;
			
			return result;
		}
		
		public ResourceBundle getTranslation() {
			return translation;
		}

		public boolean isShowForgotPassword() {
			return showForgotPassword;
		}

		public boolean isShowLogin() {
			return showLogin;
		}

		public boolean isShowRegister() {
			return showRegister;
		}

		public PasswordChecker getPasswordChecker() {
			return passwordChecker;
		}

		public String getActivationKeyLabel() {
			return activationKeyLabel;
		}

		public String getActivationKeyLabelKey() {
			return activationKeyLabelKey;
		}

		public String getActivationKeySentMessage() {
			return activationKeySentMessage;
		}

		public String getActivationKeySentMessageKey() {
			return activationKeySentMessageKey;
		}

		public String getBackButton() {
			return backButton;
		}

		public String getBackButtonKey() {
			return backButtonKey;
		}

		public String getCreateAccountButton() {
			return createAccountButton;
		}

		public String getCreateAccountButtonKey() {
			return createAccountButtonKey;
		}

		public String getExistingKeyButton() {
			return existingKeyButton;
		}

		public String getExistingKeyButtonKey() {
			return existingKeyButtonKey;
		}

		public String getExtraLoginStep1Fields() {
			return extraLoginStep1Fields;
		}

		public String getExtraLoginStep2Fields() {
			return extraLoginStep2Fields;
		}

		public String getExtraRegisterStep1Fields() {
			return extraRegisterStep1Fields;
		}

		public String getExtraRegisterStep2Fields() {
			return extraRegisterStep2Fields;
		}

		public String getExtraResetStep1PanelFields() {
			return extraResetStep1PanelFields;
		}

		public String getExtraResetStep2PanelFields() {
			return extraResetStep2PanelFields;
		}

		public String getInvalidCredentialsMessage() {
			return invalidCredentialsMessage;
		}

		public String getInvalidCredentialsMessageKey() {
			return invalidCredentialsMessageKey;
		}

		public String getForcedPasswordChangeMessage() {
			return forcedPasswordChangeMessage;
		}

		public String getForcedPasswordChangeMessageKey() {
			return forcedPasswordChangeMessageKey;
		}

		public String getFormTitle() {
			return formTitle;
		}

		public String getFormTitleKey() {
			return formTitleKey;
		}

		public String getGenerateKeyButton() {
			return generateKeyButton;
		}

		public String getGenerateKeyButtonKey() {
			return generateKeyButtonKey;
		}

		public String getI18nBase() {
			return i18nBase;
		}

		public String getIdentifierLabel() {
			return identifierLabel;
		}

		public String getIdentifierLabelKey() {
			return identifierLabelKey;
		}

		public String getLoginLabel() {
			return loginLabel;
		}

		public String getLoginLabelKey() {
			return loginLabelKey;
		}

		public String getLoginTitleKey() {
			return loginTitleKey;
		}

		public String getNewPasswordLabel() {
			return newPasswordLabel;
		}

		public String getNewPasswordLabelKey() {
			return newPasswordLabelKey;
		}

		public String getPasswordLabel() {
			return passwordLabel;
		}

		public String getPasswordLabelKey() {
			return passwordLabelKey;
		}

		public String getRegisterTitle() {
			return registerTitle;
		}

		public String getRegisterTitleKey() {
			return registerTitleKey;
		}

		public String getResetTitle() {
			return resetTitle;
		}

		public String getResetTitleKey() {
			return resetTitleKey;
		}

		public String getUnknownErrorMessage() {
			return unknownErrorMessage;
		}

		public String getUnknownErrorMessageKey() {
			return unknownErrorMessageKey;
		}
		
	}
}
