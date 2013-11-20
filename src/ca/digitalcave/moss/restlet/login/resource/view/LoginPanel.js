Ext.define('Login.view.LoginPanel', {
	"extend": "Ext.tab.Panel",
	"alias": "widget.login",
	"requires": [
		"Login.view.PasswordField",
		"Login.view.SelfDocumentingField"
	],

	"title": "${formTitle!(translation(formTitleKey!"FORM_TITLE"))?json_string}",
	"renderTo": "form",
	"tabPosition": "bottom",
	"items": [
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
						{ "xtype": "label", "itemId": "message", "text": "\xA0" }
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
						{ "xtype": "label", "text": "${translation(forcedPasswordChangeMessageKey!"FORCED_PASSWORD_CHANGE_MESSAGE")?json_string}" },
						{ "name": "identifier", "itemId": "identifier", "type": "hidden" },
						{ "fieldLabel": "${translation(activationKeyLabelKey!"ACTIVATION_KEY_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraLoginStep2PanelFields!)?json_string}
						{ "xtype": "label", "itemId": "message", "text": "\xA0" }
					],
					"buttons": [
						{ "text": "${translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${translation(createAccountButtonKey!"CREATE_ACCOUNT_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		},
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "${translation(enroleTitleKey!"ENROLE_TITLE")?json_string}",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "enrole",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier", "vtype": "email" },
						${(extraEnroleStep1Fields!)?json_string}
						{ "xtype": "label", "itemId": "message", "text": "\xA0" }
						
					],
					"buttons": [
						{ "text": "${translation(existingKeyButtonKey!"EXISTING_KEY_BUTTON")?json_string}", "itemId": "forward" },
						"->",
						{ "text": "${translation(generateKeyButtonKey!"GENERATE_KEY_BUTTON")?json_string}", "itemId": "enrole" }
					]
				},
				{
					"xtype": "form",
					"itemId": "activate",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(activationKeyLabelKey!"ACTIVATION_KEY_LABEL")?json_string}", "name": "identifier" },
						{ "fieldLabel": "${translation(newPasswordLabelKey!"NEW_PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						${(extraEnroleStep2PanelFields!)?json_string}
						{ "xtype": "label", "itemId": "message", "text": "\xA0" }
					],
					"buttons": [
						{ "text": "${translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${translation(createAccountButtonKey!"CREATE_ACCOUNT_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		},
		{
			"xtype": "panel",
			"defaults": { "border": false, "margin": 10 },
			"title": "${translation(resetTitleKey!"RESET_TITLE")?json_string}",
			"layout": "card",
			"items": [
				{
					"xtype": "form",
					"itemId": "enrole",
					"defaults": { "anchor": "100%", "allowBlank": false, "xtype": "textfield", "enableKeyEvents": true },
					"items": [
						{ "fieldLabel": "${translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier" },
						${(extraResetStep1PanelFields!)?json_string}
						{ "xtype": "label", "itemId": "message", "text": "\xA0" }
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
						{ "xtype": "label", "itemId": "message", "text": "\xA0" }
					],
					"buttons": [
						{ "text": "${translation(backButtonKey!"BACK_BUTTON")?json_string}", "itemId": "back" },
						{ "text": "${translation(resetPasswordButtonKey!"RESET_PASSWORD_BUTTON")?json_string}", "itemId": "activate" }
					]
				}
			]
		}
	]
});
