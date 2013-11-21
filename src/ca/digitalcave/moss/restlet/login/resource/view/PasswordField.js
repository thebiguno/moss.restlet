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
						"enableKeyEvents": true,
						"itemId": "password",
						"options": this,
						"flex": 1,
						"validator": function(value){
							this.ownerCt.getComponent(1).validate();	//Validate that passwords match
							if (this.lastCheck == null) return "${passwordUnvalidated!translation("PASSWORD_UNVALIDATED")?json_string}";	//TODO
							else if (this.lastCheck.passed) return true;
							else if (!this.lastCheck.length) return "${passwordLength!translation("PASSWORD_LENGTH")?json_string}";
							else if (!this.lastCheck.strength) return "${passwordStrength!translation("PASSWORD_STRENGTH")?json_string}";
							else if (!this.lastCheck.variance) return "${passwordVariance!translation("PASSWORD_VARIANCE")?json_string}";
							else if (!this.lastCheck.classes) return "${passwordClasses!translation("PASSWORD_CLASSES")?json_string}";
							else if (!this.lastCheck.dictionary) return "${passwordDictionary!translation("PASSWORD_DICTIONARY")?json_string}";
							else if (!this.lastCheck.pattern) return "${passwordPattern!translation("PASSWORD_PATTERN")?json_string}";
							else if (!this.lastCheck.custom) return "${passwordCustom!translation("PASSWORD_CUSTOM")?json_string}";
							else return "${translation("UNKNOWN_ERROR_MESSAGE")?json_string}"
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
							if (this.ownerCt.getComponent(0).getValue() != value) {
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