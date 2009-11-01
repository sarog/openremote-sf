/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

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