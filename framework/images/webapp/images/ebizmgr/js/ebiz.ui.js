
function initUI(_box){
	var $p = $(_box || document);
	
	// navTab
	$("a[target=navTab]", $p).each(function(){
		$(this).click(function(event){
			var $this = $(this);
			var title = $this.attr("title") || $this.text();
			var tabid = $this.attr("rel") || "_blank";
			var fresh = eval($this.attr("fresh") || "true");
			var external = eval($this.attr("external") || "false");
			var url = unescape($this.attr("href"));

			window.parent.ebizOpenTab(tabid, url,{title:title, fresh:fresh, external:external});

			event.preventDefault();
		});
	});

}