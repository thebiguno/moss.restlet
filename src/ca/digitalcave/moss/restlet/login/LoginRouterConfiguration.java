package ca.digitalcave.moss.restlet.login;

import java.util.Map;
import java.util.ResourceBundle;

import ca.digitalcave.moss.restlet.util.PasswordChecker;

public class LoginRouterConfiguration implements Cloneable {
	/**
	 * Show the 'forgot password' panel on the login screen.
	 */
	public boolean showForgotPassword = true;
	/**
	 * Show the 'login' panel on the login screen.
	 */
	public boolean showLogin = true;
	/**
	 * Show the 'register' panel on the login screen.
	 */
	public boolean showRegister = false;
	
	/**
	 * Show the 'remember me' checkbox on login panel
	 */
	public boolean showRemember = true;
	
	/**
	 * An instance of PasswordChecker; required only if the default 
	 * instance does not match your password policy.
	 */
	public PasswordChecker passwordChecker;
	
	public String activationKeyLabel;
	public String activationKeyLabelKey;
	public String activationKeySentMessage;
	public String activationKeySentMessageKey;
	/**
	 * A list of extra EXT JS 4 controller class names, to be instantiated by Login's application.
	 */
	public String[] applicationControllers;
	/**
	 * A list of extra EXT JS 4 model class names, to be instantiated by Login's application.
	 */
	public String[] applicationModels;
	/**
	 * A list of extra EXT JS 4 view class names, to be instantiated by Login's application.
	 */
	public String[] applicationViews;
	/**
	 * A mapping between application names (e.g. MyApplication) and top level paths (e.g. app).
	 * This is only needed when you integrate components from a different application into the 
	 * login application.
	 */
	public Map<String, String> applicationLoaderPaths;
	public String backButton;
	public String backButtonKey;
	public String createAccountButton;
	public String createAccountButtonKey;
	public String existingKeyButton;
	public String existingKeyButtonKey;
	public ExtraFieldsDirective extraLoginStep1Fields;
	public ExtraFieldsDirective extraLoginStep2Fields;
	public ExtraFieldsDirective extraRegisterStep1Fields;
	public ExtraFieldsDirective extraRegisterStep2Fields;
	public ExtraFieldsDirective extraResetStep1PanelFields;
	public ExtraFieldsDirective extraResetStep2PanelFields;
	public String invalidCredentialsMessage;
	public String invalidCredentialsMessageKey;
	public String forcedPasswordChangeMessage;
	public String forcedPasswordChangeMessageKey;
	public String formTitle;
	public String formTitleKey;
	public String generateKeyButton;
	public String generateKeyButtonKey;
	/**
	 * Specify the resource bundle file for the built in i18n
	 */
	public String i18nBase = "ca.digitalcave.moss.restlet.login.i18n.i18n";
	/**
	 * Specify the custom resource bundle file for custom i18n.  This is 
	 * used when implementing the extraXStepYFields; the custom translator 
	 * is passed into the {@link ExtraFieldsDirective} abstract method.
	 */
	public String i18nBaseCustom;
	public String identifierLabel;
	public String identifierLabelKey;
	public String loginFormId;
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
	public String routerAttachPoint;
	public String unknownErrorMessage;
	public String unknownErrorMessageKey;

	
	//Not included in clone; set automatically in LoginFreemarkerResource, loaded from i18nBase.
	public ResourceBundle translation;
	public ResourceBundle customTranslation;
	
	@Override
	protected LoginRouterConfiguration clone() {
		final LoginRouterConfiguration result = new LoginRouterConfiguration();
		
		result.showForgotPassword = this.showForgotPassword;
		result.showLogin = this.showLogin;
		result.showRegister = this.showRegister;
		result.showRemember = this.showRemember;
				
		result.passwordChecker = this.passwordChecker;
				
		result.activationKeyLabel = this.activationKeyLabel;
		result.activationKeyLabelKey = this.activationKeyLabelKey;
		result.applicationControllers = this.applicationControllers;
		result.applicationModels = this.applicationModels;
		result.applicationViews = this.applicationViews;
		result.applicationLoaderPaths = this.applicationLoaderPaths;
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
		result.i18nBaseCustom = this.i18nBaseCustom; 
		result.identifierLabel = this.identifierLabel;
		result.identifierLabelKey = this.identifierLabelKey;
		result.loginFormId = this.loginFormId;
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
		result.routerAttachPoint = this.routerAttachPoint;
		
		return result;
	}
	
	public ResourceBundle getTranslation() {
		return translation;
	}
	
	public ResourceBundle getCustomTranslation() {
		return customTranslation;
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

	public boolean isShowRemember() {
		return showRemember;
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

	public String[] getApplicationControllers() {
		return applicationControllers;
	}

	public String[] getApplicationModels() {
		return applicationModels;
	}

	public String[] getApplicationViews() {
		return applicationViews;
	}

	public Map<String, String> getApplicationLoaderPaths() {
		return applicationLoaderPaths;
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

	public ExtraFieldsDirective getExtraLoginStep1Fields() {
		return extraLoginStep1Fields;
	}

	public ExtraFieldsDirective getExtraLoginStep2Fields() {
		return extraLoginStep2Fields;
	}

	public ExtraFieldsDirective getExtraRegisterStep1Fields() {
		return extraRegisterStep1Fields;
	}

	public ExtraFieldsDirective getExtraRegisterStep2Fields() {
		return extraRegisterStep2Fields;
	}

	public ExtraFieldsDirective getExtraResetStep1PanelFields() {
		return extraResetStep1PanelFields;
	}

	public ExtraFieldsDirective getExtraResetStep2PanelFields() {
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
	
	public String getI18nBaseCustom() {
		return i18nBaseCustom;
	}

	public String getIdentifierLabel() {
		return identifierLabel;
	}

	public String getIdentifierLabelKey() {
		return identifierLabelKey;
	}

	public String getLoginFormId() {
		return loginFormId;
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

	public String getRouterAttachPoint() {
		return routerAttachPoint;
	}
	
	public String getUnknownErrorMessage() {
		return unknownErrorMessage;
	}

	public String getUnknownErrorMessageKey() {
		return unknownErrorMessageKey;
	}
	
}