ChangeIconView = function() {
    return {
        showFromBeehiveView: function(icons) {
            $(".change_icon_from_container").hide();
            $("#change_icon_from_beehive").show();
			if ($("#change_icon_from_beehive :radio").length == 0 ) {
				EJSHelper.updateView("template/_beehiveIconList.ejs",'change_icon_from_beehive',{icons:icons});
                var lastIcon=null;
                var change_icon_from_beehive = $("#change_icon_from_beehive");
                change_icon_from_beehive.find('img').unbind().click(function(){
                    if (lastIcon) {
                        lastIcon.css("border","1px solid white");
                    }
                    lastIcon = $(this);
                    $(this).css("border","1px solid orange");
                });
                change_icon_from_beehive.find('img:first').click();
                change_icon_from_beehive.find('input:first').attr("checked",true);
			}
        },
        showFromUrlView: function() {
            $(".change_icon_from_container").hide();
            $("#change_icon_from_url").show();
        },
        showFromUploadView: function() {
            $(".change_icon_from_container").hide();
            $("#change_icon_from_upload").show();
        },
		getSelectIconSrc: function(){
			return $("#change_icon_from_beehive input:checked").next("img").attr("src");
		},
		getInputUrl: function(){
			return $("#icon_url_input").val();
		},
		getFileName: function(){
			return $("#icon_file_name_input").val();
		}
		
    };
} ();