Ext.application({
	"name": "Login",
	"appFolder": "${routerAttachPoint}",
	
	"views": ["LoginPanel" <#list applicationViews! as view>,"${view?json_string}"</#list> ],
	"controllers": ["LoginController", "PasswordFieldController" <#list applicationControllers! as controller>,"${controller?json_string}"</#list>],
	"models": [<#list applicationModels! as model><#if model.first>,</#if>"${model?json_string}"</#list>],
	
	"launch": function() {
		Ext.create("Login.view.LoginPanel");
	}
});
