Ext.define("Login.controller.PasswordFieldController", {
	"extend": "Ext.app.Controller",

	"init": function() {
		this.control({
			"passwordfield textfield[itemId=password]": {
				"blur": this.checkPassword,
				"focus": this.checkPassword,
				"change": this.checkPassword
			}
		});
	},
	
	"checkPassword": function(field){
		field.lastCheck = null;
		field.connection.request({
			"url": "${routerAttachPoint}/checkpassword",
			"params": {
				"identifier": field.up("passwordfield").identifier ? field.up("passwordfield").identifier : field.up("form").down("component[name=identifier]") ? field.up("form").down("component[name=identifier]").getValue() : "anonymous",
				"secret": field.getValue()
			},
			"success": function(response){
				var result = Ext.decode(response.responseText, true);
				if (result == null) return;
				
				field.lastCheck = result;
				
				var color;
				var value = field.getValue();
				var strength = result.score;

				if (value.length == 0 || strength < 10) color = "#953131";
				else if (strength < 20) color = "#ab5e4a";
				else if (strength < 30) color = "#b17253";
				else if (strength < 40) color = "#b2894f";
				else if (strength < 50) color = "#b18c51";
				else if (strength < 60) color = "#bc9c45";
				else if (strength < 70) color = "#b5b557";
				else if (strength < 80) color = "#8cac4a";
				else if (strength < 90) color = "#74b254";
				else if (strength < 100) color = "#4aa94a";
				else { color = "#26a826"; strength = 100; }
				
				var passwordField = field.up("passwordfield");
				if (passwordField != null){
					var draw = passwordField.down("draw[itemId=passwordbar]");
					var surface = draw.getSurface();
					var sprite = surface.getItems()[0];
					sprite.setAnimation({duration: 500});
					sprite.setAttributes({
						"width": strength * draw.getWidth() / 100,
						"fill": color
					});
					surface.renderFrame();
				}
				
				field.validate();
			}
		});
	}
});
