Ext.application({
	"name": "Login",
	"appFolder": "login",
	
	"views": ["LoginPanel"],
	"controllers": ["LoginController", "PasswordFieldController"],

	"launch": function() {
		Ext.create("Login.view.LoginPanel");
	}
});
