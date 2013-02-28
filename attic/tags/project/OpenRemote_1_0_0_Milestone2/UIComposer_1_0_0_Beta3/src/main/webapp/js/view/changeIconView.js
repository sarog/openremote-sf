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
					$(this).attr("src");
                   $("#inspect_iphoneBtn_icon").attr("src",$(this).attr("src"));
					$("#change_icon_form").closeModalForm();
                });
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