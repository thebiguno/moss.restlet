Ext.define('Login.view.LoginPanel', {
	"extend": "Ext.tab.Panel",
	"alias": "widget.login",
	"requires": [
		"Login.view.AnimatedLabel",
		"Login.view.PasswordField",
		"Login.view.SelfDocumentingField"
	],

	"title": "${formTitle!translation(formTitleKey!"FORM_TITLE")?json_string}",
	"renderTo": "${loginFormId!"loginform"}",
	"tabPosition": "bottom",
	"height": "100%",
	"defaults": {
		"xtype": "panel",
		"layout": "card", 
		"defaults": {
			"xtype": "form",
			"border": false, 
			"margin": 10, 
			"autoScroll": true, 
			"defaults": { 
				"xtype": "textfield", 
				"anchor": "100%", 
				"allowBlank": false, 
				"enableKeyEvents": true
			}
		}
	},
	"items": [
<#if showLogin!true>
		{
			"title": "${translation(loginTitleKey!"LOGIN_TITLE")?json_string}",
			"items": [
				{
					"itemId": "authenticate",
					"items": [
						{ "fieldLabel": "${identifierLabel!translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${passwordLabel!translation(passwordLabelKey!"PASSWORD_LABEL")?json_string}", "inputType": "password", "name": "secret" },
						${(extraLoginStep1Fields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageLogin1" }
					],
					"buttons": [
						{ "text": "${loginLabel!translation(loginLabelKey!"LOGIN_LABEL")?json_string}", "itemId": "authenticate" }
					]
				},
				{
					"itemId": "activate",
					"items": [
						{ "name": "identifier", "xtype": "hidden" },
						{ "fieldLabel": "${newPasswordLabel!translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraLoginStep2PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageLogin2" }
					],
					"buttons": [
						{ "text": "${backButton!translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${createAccountButton!translation(createAccountButtonKey!"CHANGE_PASSWORD_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		},
</#if>
<#if showRegister!false>
		{
			"title": "${registerTitle!translation(registerTitleKey!"REGISTER_TITLE")?json_string}",
			"items": [
				{
					"itemId": "register",
					"items": [
						{ "fieldLabel": "${identifierLabel!translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier", "vtype": "email" },
						${(extraRegisterStep1Fields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageRegister1" }
						
					],
					"buttons": [
						{ "text": "${existingKeyButton!translation(existingKeyButtonKey!"EXISTING_KEY_BUTTON")?json_string}", "itemId": "forward" },
						"->",
						{ "text": "${generateKeyButton!translation(generateKeyButtonKey!"GENERATE_KEY_BUTTON")?json_string}", "itemId": "register" }
					]
				},
				{
					"itemId": "activate",
					"items": [
						{ "fieldLabel": "${activationKeyLabel!translation(activationKeyLabelKey!"ACTIVATION_KEY_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${newPasswordLabel!translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraRegisterStep2PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageRegister2" }
					],
					"buttons": [
						{ "text": "${backButton!translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${createAccountButton!translation(createAccountButtonKey!"CREATE_ACCOUNT_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		},
</#if>
<#if showForgotPassword!true>
		{
			"title": "${resetTitle!translation(resetTitleKey!"RESET_TITLE")?json_string}",
			"items": [
				{
					"itemId": "reset",
					"items": [
						{ "fieldLabel": "${identifierLabel!translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier" },
						${(extraResetStep1PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageForgotPassword1" }
					],
					"buttons": [
						{ "text": "${existingKeyButton!translation(existingKeyButtonKey!"EXISTING_KEY_BUTTON")?json_string}", "itemId": "forward" },
						"->",
						{ "text": "${generateKeyButton!translation(generateKeyButtonKey!"GENERATE_KEY_BUTTON")?json_string}", "itemId": "reset" }
					]
				},
				{
					"itemId": "activate",
					"items": [
						{ "fieldLabel": "${activationKeyLabel!translation(activationKeyLabelKey!"ACTIVATION_KEY_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${newPasswordLabel!translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraResetStep2PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageForgotPassword2" }
					],
					"buttons": [
						{ "text": "${backButton!translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${resetPasswordButton!translation(resetPasswordButtonKey!"RESET_PASSWORD_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		}
</#if>
	]
});
