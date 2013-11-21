Ext.define('Login.view.LoginPanel', {
	"extend": "Ext.tab.Panel",
	"alias": "widget.login",
	"requires": [
		"Login.view.AnimatedLabel",
		"Login.view.PasswordField",
		"Login.view.SelfDocumentingField"
	],

	"title": "${formTitle!(translation(formTitleKey!"FORM_TITLE"))?json_string}",
	"renderTo": "form",
	"tabPosition": "bottom",
	"items": [
<#if showLogin!true>
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "${translation(loginTitleKey!"LOGIN_TITLE")?json_string}",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "authenticate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${translation(passwordLabelKey!"PASSWORD_LABEL")?json_string}", "inputType": "password", "name": "secret" },
						${(extraLoginStep1Fields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageLogin1" }
					],
					"buttons": [
						{ "text": "${translation(loginLabelKey!"LOGIN_LABEL")?json_string}", "itemId": "authenticate" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "name": "identifier", "xtype": "hidden" },
						{ "fieldLabel": "${translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraLoginStep2PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageLogin2" }
					],
					"buttons": [
						{ "text": "${translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${translation(createAccountButtonKey!"CHANGE_PASSWORD_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		},
</#if>
<#if showRegister!false>
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "${translation(registerTitleKey!"REGISTER_TITLE")?json_string}",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "register",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier", "vtype": "email" },
						${(extraRegisterStep1Fields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageRegister1" }
						
					],
					"buttons": [
						{ "text": "${translation(existingKeyButtonKey!"EXISTING_KEY_BUTTON")?json_string}", "itemId": "forward" },
						"->",
						{ "text": "${translation(generateKeyButtonKey!"GENERATE_KEY_BUTTON")?json_string}", "itemId": "register" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(activationKeyLabelKey!"ACTIVATION_KEY_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraRegisterStep2PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageRegister2" }
					],
					"buttons": [
						{ "text": "${translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${translation(createAccountButtonKey!"CREATE_ACCOUNT_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		},
</#if>
<#if showForgotPassword!true>
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "${translation(resetTitleKey!"RESET_TITLE")?json_string}",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "register",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier" },
						${(extraResetStep1PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageForgotPassword1" }
					],
					"buttons": [
						{ "text": "${translation(existingKeyButtonKey!"EXISTING_KEY_BUTTON")?json_string}", "itemId": "forward" },
						"->",
						{ "text": "${translation(generateKeyButtonKey!"GENERATE_KEY_BUTTON")?json_string}", "itemId": "reset" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(activationKeyLabelKey!"ACTIVATION_KEY_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraResetStep2PanelFields!)?json_string}
						{ "xtype": "animatedlabel", "itemId": "messageForgotPassword2" }
					],
					"buttons": [
						{ "text": "${translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${translation(resetPasswordButtonKey!"RESET_PASSWORD_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		}
</#if>
	]
});
