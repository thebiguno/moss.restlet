Ext.define("Login.view.PasswordField", {
	"extend": "Ext.form.FieldContainer",
	"alias": "widget.passwordfield",
	
	"layout": {
		"type": "form"
	},

	"getValue": function(){
		return this.down("textfield[itemId='password']").getValue();
	},
	
	"isValid": function(){
		return this.down("textfield[itemId='password']").isValid() && this.down("textfield[itemId='confirm']").isValid();
	},
	
	"getErrors": function(){
		return this.errors;
	},
	
	"required": true,
	
	"initComponent": function(){
		this.items = [
			{
				"xtype": "container",
				"layout": "hbox",
				"items": [
					{
						"xtype": "textfield",
						"inputType": "password",
						"name": this.name,
						"itemId": "password",
						"options": this,
						"flex": 1,
						"validator": function(value){
							var passwordField = this.up("passwordfield");
							if (passwordField == null) return;
							
							var confirmField = passwordField.down("textfield[itemId=confirm]");
							var confirmPassword = confirmField.getValue();
							if (value.length == 0 && confirmPassword.length == 0 && !passwordField.required) return true;
						
							confirmField.validate();	//Validate that passwords match
							if (this.lastCheck == null) {
								
								this.errors = "${passwordUnvalidated!translation("PASSWORD_UNVALIDATED")?json_string}";
								return this.errors;
							}
							else if (this.lastCheck.passed) {
								this.errors = null;
								return true;
							}
							
							var result = "<b>Problems</b>:<br/>";
							if (this.lastCheck.length === false) result += "${passwordLength!translation("PASSWORD_LENGTH")?json_string}<br/>";
							if (this.lastCheck.strength === false) result += "${passwordStrength!translation("PASSWORD_STRENGTH")?json_string}<br/>";
							if (this.lastCheck.variance === false) result += "${passwordVariance!translation("PASSWORD_VARIANCE")?json_string}<br/>";
							if (this.lastCheck.classes === false) result += "${passwordClasses!translation("PASSWORD_CLASSES")?json_string}<br/>";
							if (this.lastCheck.history === false) result += "${passwordHistory!translation("PASSWORD_HISTORY")?json_string}<br/>";
							if (this.lastCheck.dictionary === false) result += "${passwordDictionary!translation("PASSWORD_DICTIONARY")?json_string}<br/>";
							if (this.lastCheck.pattern === false) result += "${passwordPattern!translation("PASSWORD_PATTERN")?json_string}<br/>";
							if (this.lastCheck.custom === false) result += "${passwordCustom!translation("PASSWORD_CUSTOM")?json_string}<br/>";
							this.errors = result;
							return result;
						},
						"connection": Ext.create('Ext.data.Connection', {
							"autoAbort": true,
							"method": "POST"
						})
					},
					{
						"xtype": "textfield",
						"inputType": "password",
						"itemId": "confirm",
						"flex": 1,
						"margin": "0 0 0 5",
						"submitValue": false,
						"validator": function(value){
							var passwordField = this.up("passwordfield").down("textfield[itemId=password]");
							if (passwordField.getValue() != value) {
								return "${passwordConfirmationMatch!translation("PASSWORD_CONFIRMATION_MATCH")?json_string}";
							} else {
								return true;
							}
						}
					}
				]
			},
			{
				"xtype": "draw",
				"height": 7,
				"itemId": "passwordbar",
				"viewBox": false,
				"items": [
					{
						"type": "rect",
						"width": 0,
						"height": 5,
						"fill": "#000",
						"stroke": "#666",
						"strokeWidth": 1
					}
				]
			}
		];
		
		this.callParent(arguments);
	}
});