Ext.define('Login.view.LoginPanel', {
	"extend": "Ext.tab.Panel",
	"alias": "widget.login",
	"requires": [
		"Login.view.AnimatedLabel",
		"Login.view.PasswordField"
		${applicationRequires!}
	],

	"title": "${formTitle!translation(formTitleKey!"FORM_TITLE")?json_string}",
	"renderTo": "${loginFormId!"loginform"}",
	"tabPosition": "${tabPosition!"bottom"?json_string}",
	"height": "100%",
	"tabBar": {
		<#if tabBarBackgroundInvisible!false>
		"style": {
			"background-image": "none",
			"background-color": "transparent",
			"border": "none"
		},
		</#if>
		"layout": {
			"pack": "${tabPackAlignment!"start"?json_string}"
		}
	},
	"defaults": {
		"xtype": "panel",
		"layout": "card", 
		"defaults": {
			"xtype": "form",
			"border": false, 
			"margin": 10, 
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
			"title": "${loginTitle!translation(loginTitleKey!"LOGIN_TITLE")?json_string}",
			"items": [
				{
					"itemId": "authenticate",
					"items": [
						{ "fieldLabel": "${identifierLabel!translation(identifierLabelKey!"IDENTIFIER_LABEL")?json_string}", "name": "identifier", "listeners": { "afterrender": function(component){ component.focus(); } } },
						{ "fieldLabel": "${passwordLabel!translation(passwordLabelKey!"PASSWORD_LABEL")?json_string}", "inputType": "password", "name": "secret" },
						<#if showRemember!true>
						{ "fieldLabel": "${rememberLabel!translation(rememberLabelKey!"REMEMBER_LABEL")?json_string}", "xtype": "checkbox", "name": "remember" },
						</#if>
						<#if extraLoginStep1Fields??><@extraLoginStep1Fields/></#if>
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
						<#if extraLoginStep2Fields??><@extraLoginStep2Fields/></#if>
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
						<#if extraRegisterStep1Fields??><@extraRegisterStep1Fields/></#if>
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
						{ "fieldLabel": "${newPasswordLabel!translation(newPasswordLabelKey!"PASSWORD_LABEL")?json_string}", "name": "secret", "xtype": "passwordfield" },
						<#if extraRegisterStep2Fields??><@extraRegisterStep2Fields/></#if>
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
						<#if extraResetStep1PanelFields??><@extraResetStep1PanelFields/></#if>
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
						<#if extraResetStep2PanelFields??><@extraResetStep2PanelFields/></#if>
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
