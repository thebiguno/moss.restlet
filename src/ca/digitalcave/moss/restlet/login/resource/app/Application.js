Ext.application({
	"name": "Login",
	"appFolder": "login",
	
	"views": ["LoginPanel"],
	"controllers": ["LoginController"],

	"launch": function() {
		Ext.create("Login.view.LoginPanel");
	}
});
