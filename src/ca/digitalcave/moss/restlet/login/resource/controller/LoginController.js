Ext.define("Login.controller.LoginController", {
	"extend": "Ext.app.Controller",

	"init": function() {
		this.control({
			"login button[itemId=back]": {
				"click": function(button) {
					button.up('form').up('panel').getLayout().prev();
				}
			},
			"login button[itemId=forward]": {
				"click": function(button) {
					button.up('form').up('panel').getLayout().next();
				}
			},
			"login button[itemId=authenticate]": { "click": this.authenticate },
			"login form[itemId=authenticate] textfield": { "keypress": this.authenticate },
			"login button[itemId=register]": { "click": this.register },
			"login form[itemId=register] textfield": { "keypress": this.register },
			"login button[itemId=reset]": { "click": this.reset },
			"login form[itemId=reset] textfield": { "keypress": this.reset },
			"login button[itemId=activate]": { "click": this.activate },
			"login form[itemId=activate] textfield": { "keypress": this.activate }
		});
	},

	"authenticate": function(cmp, e) {
		if (!(e.getKey()) || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "login" },
				"success": function() {
					window.location.reload();
				},
				"failure": function(form, action) {
					var key = action.result ? action.result.key : undefined;
					if (key) {
						var card = cmp.up('form').up('panel').getLayout().next();
						card.down('hiddenfield[name=identifier]').setValue(key);
						cmp.up('form').up('panel').down('animatedlabel[itemId=messageLogin2]').setTextAnimated("${forcedPasswordChangeMessage!translation(forcedPasswordChangeMessageKey!"FORCED_PASSWORD_CHANGE_MESSAGE")?json_string}", 30000);
					} else {
						cmp.up('form').down('animatedlabel[itemId=messageLogin1]').setTextAnimated("${invalidCredentialsMessage!translation(invalidCredentialsMessageKey!"INVALID_CREDENTIALS_MESSAGE")?json_string}");
					}
				}
			});
		}
	},
	
	"register": function(cmp, e) {
		if (!(e.getKey()) || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "register" },
				"success": function() {
					cmp.up('form').up('panel').getLayout().next();
					cmp.up('form').up('panel').down('animatedlabel[itemId=messageRegister2]').setTextAnimated("${activationKeySentMessage!translation(activationKeySentMessageKey!"ACTIVATION_KEY_SENT")?json_string}", 30000);
				},
				"failure": function(form, action) {
					var response = Ext.decode(action.response.responseText, true);
					var message = response && response.msg ? response.msg : "${unknownErrorMessage!translation(unknownErrorMessageKey!"UNKNOWN_ERROR_MESSAGE")?json_string}";
					cmp.up('form').down('animatedlabel[itemId=messageRegister1]').setTextAnimated(message);
				}
			});
		}
	},
	
	"reset": function(cmp, e) {
		if (!(e.getKey()) || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "reset" },
				"success": function() {
					cmp.up('form').up('panel').getLayout().next();
					cmp.up('form').up('panel').down('animatedlabel[itemId=messageForgotPassword2]').setTextAnimated("${activationKeySentMessage!translation(activationKeySentMessageKey!"ACTIVATION_KEY_SENT")?json_string}", 30000);
				},
				"failure": function(form, action) {
					var response = Ext.decode(action.response.responseText, true);
					var message = response && response.msg ? response.msg : "${unknownErrorMessage!translation(unknownErrorMessageKey!"UNKNOWN_ERROR_MESSAGE")?json_string}";
					cmp.up('form').down('animatedlabel[itemId=messageForgotPassword1]').setTextAnimated(message);
				}
			});
		}
	},
	
	"activate": function(cmp, e) {
		if (!(e.getKey()) || e.getKey() == e.ENTER) {
			var form = cmp.up('form').getForm();
			if (form.isValid() == false) return;
			form.submit({
				"url": "index",
				"params": { "action": "activate" },
				"success": function() {
					window.location.reload();
				},
				"failure": function(form, action) {
					cmp.up('form').down('animatedlabel[itemId=messageForgotPassword2]').setTextAnimated("${unknownErrorMessage!translation(unknownErrorMessageKey!"UNKNOWN_ERROR_MESSAGE")?json_string}");
				}
			});
		}
	}
});
