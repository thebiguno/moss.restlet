Ext.define("Login.view.AnimatedLabel", {
	"extend": "Ext.form.Label",
	"alias": "widget.animatedlabel",
	"text": "\xA0",	//non breaking space to reserve space for text and not collapse empty label
	
	"setTextAnimated": function(text, duration){
		if (duration == null) duration = 3000;
		this.stopAnimation();
		this.animate({"to": {"opacity": 0},"duration": 0});	//Set label opacity to 0 initially; this only matters on first call for a given label.  TODO How do we set opacity without animation?
		this.setText(text);
		this.animate({
			"to": {
				"opacity": 1
			},
			"duration": 200
		}).animate({
			"to": {
				"opacity": 1
			},
			"duration": duration
		}).animate({
			"to": {
				"opacity": 0
			},
			"duration": 2000
		});
	}
});