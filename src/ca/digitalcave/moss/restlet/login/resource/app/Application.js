Ext.application({
	"name": "Login",
	"appFolder": "${routerAttachPoint}",
	
	"views": ["LoginPanel"],
	"controllers": ["LoginController", "PasswordFieldController" ${applicationControllers}],

	"launch": function() {
		Ext.create("Login.view.LoginPanel");
	}
});
