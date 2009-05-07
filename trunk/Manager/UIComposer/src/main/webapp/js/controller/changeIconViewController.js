ChangeIconViewController = function() {
	return {
		
		showChangeIconForm: function(){
			$("#change_icon_form").showModalForm("Change Button Icon", {
				buttons:{
					'OK': comfirmChangeIcon
				},
	            width:"500px",
				confirmButtonName:'OK'
	        });
			
			ChangeIconViewController.showFromBeehive();
			
			$("#fromBeehive").attr("checked","true");
			$("#fromBeehive").unbind().change(function() {
				$("#change_icon_form").clearError();
				$("#validateTips").remove();
				ChangeIconViewController.showFromBeehive();
			});
			$("#fromUrl").unbind().change(function() {
				$("#change_icon_form").clearError();
				$("#validateTips").remove();
				ChangeIconView.showFromUrlView();
			});
			$("#fromUpload").unbind().change(function() {
				$("#change_icon_form").clearError();
				$("#validateTips").remove();
				ChangeIconView.showFromUploadView();
			});
			
			function comfirmChangeIcon() {
				var checkedInput = $(".change_icon_type:checked");
				var imageUrl = null;
				switch (checkedInput.attr("id")) {
					case "fromBeehive":
						if (checkSelectIcon()) {
							imageUrl = ChangeIconView.getSelectIconSrc();
						}
					break;
					case "fromUrl":
						if (checkInputUrl()) {
							imageUrl = ChangeIconView.getInputUrl();
							
						}
					break;
					case "fromUpload":
						if (checkSelectFile()) {
							$("#upload_image_form").ajaxForm({
				                success: uploadSuccess,
				                dataType: 'text',
				                type: 'post'
				            });
							$("#upload_image_form").submit();
						}
					break;
				} 
				
				if (imageUrl != null) {
					changeInspectImage(imageUrl);
					$("#change_icon_form").closeModalForm();
				}
				
			}
			
			function checkSelectIcon () {
				if ($("#change_icon_from_beehive input:checked").length == 0) {
					$("#change_icon_form").updateTips($("#change_icon_from_beehive input:first"),"Please select a icon.");
					return false;
				} 
				return true;
			}
			function checkInputUrl () {
				if (ChangeIconView.getInputUrl().match("http(s?)://") == null) {
					$("#change_icon_form").updateTips($("#icon_url_input"),"Please input correct url.");
					return false;
				}
				return true;
			}
			
			function checkSelectFile () {
				if (ChangeIconView.getFileName().match("\.(png|gif|jpg)") == null) {
					$("#change_icon_form").updateTips($("#icon_file_name_input"),"Please select an gif, jpg, png type image.");
					return false;
				}
				return true;
			}
			
			function changeInspectImage (src) {
				$("#inspect_iphoneBtn_icon").attr("src",src);
			}
			
			function uploadSuccess(responseText, statusText) {
				changeInspectImage(responseText);
				$("#change_icon_form").closeModalForm();
			}
			
	
			function afterSelectIconFromUpload (argument) {
				// body...
			}
		},
		showFromBeehive: function(name){
			var url = "";
			if (name === undefined) {
				url ="/icons";
			} else {
				url = "/icons/" + name;
			}
			getJSONData(url,
		    function(data) {
				ChangeIconView.showFromBeehiveView(data.icons);
			});
		}
	};
}();