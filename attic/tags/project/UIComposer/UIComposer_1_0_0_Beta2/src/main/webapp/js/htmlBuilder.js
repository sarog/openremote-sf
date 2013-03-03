/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF site:
 * http://www.fsf.org.
 */
/**
 * This is a singleton class, use for building html only, means it doesn't insert the html element into page.
 * @author allen.wei@finalist.cn
 */
HTMLBuilder = function() {
    return {
        KNXBtnBuilder: function(knx) {			
            var button = HTMLBuilder.blueBtnBuilder(knx.label);
            button.addClass("knx_btn");
            button.attr("id", knx.elementId());
            button.data("model", knx);
            return button;

        },

        X10BtnBuilder: function(x10) {
            var button = HTMLBuilder.blueBtnBuilder(x10.label);
            button.addClass("x10_btn");
            button.attr("id", x10.elementId());
            button.data("model", x10);
            button.attr("title", x10.label);
            return button;

        },


        blueBtnBuilder: function(text) {
            var button = $("<div></div>");
            button.attr("title", text);
            button.addClass("blue_btn");
            if (text.length > 14) {
                text = text.substr(0, 14) + "...";
            }
            button.text(text);
            return button;
        },

        iphoneBtnBuilder: function(iphoneBtn) {           
            var btn = $("<div class='iphone_btn'></div>");
			btn.data("model",iphoneBtn);
            btn.attr("title",iphoneBtn.oModel.label);

			var text = iphoneBtn.oModel.label;
            if (text.length > 5) {
                btn.html(text.substr(0, 5) + "<br/>...");
            } else {
                btn.text(text);
            }
            return btn;
        },

        iphoneBtnDeleteIconBuilder: function() {
            var deleteIcon = $("<img class='delete_icon' src='image/delete_icon.png'>");
            deleteIcon.click(function() {
                if (confirm("Are you sure to delete this button?")) {
                    $(this).parent(".iphone_btn").remove();
                }
            });
            deleteIcon.hover(function() {
                $(this).toggleClass("canMove");
            },
            function() {
                $(this).toggleClass("canMove");
            });
            return deleteIcon;
        },

        macroBtnBuilder: function(macro) {
            var template = $("#macro_template .macro_btn_defination").clone();
			var btn = $(template).find(".macro_btn");
			
			var name = macro.label;
            btn.attr("title", macro.label);
            if (name.length > 14) {
                name = name.substr(0, 14) + "...";
            }
            btn.html(name);
            btn.attr("id", macro.elementId());
			btn.data("model",macro);
            return $(template);
        },

        macroLiBtnBuilder: function(model) {
            var macroCommandLi = $("<li><span></span></li>");
            macroCommandLi.addClass("macro_command");
            macroCommandLi.addClass("ui-state-default");
            macroCommandLi.data("model",model);
            macroCommandLi.find("span").addClass("ui-icon");
            macroCommandLi.find("span").addClass("ui-icon-arrowthick-2-n-s");
            var name = model.label;
            macroCommandLi.attr("title", name);
            if (name.length > 14) {
                name = name.substr(0, 8) + "...";
            }
            macroCommandLi.find("span").after(name);
            return macroCommandLi;
        },

        commandBtnBuilder: function(code,section_id) {
            var btn = $("<div></div>");
            btn.data("codeId", code.id);
			btn.data("remoteName",code.remoteName);
			btn.data("command", code.name);
		    btn.data("sectionId",section_id);
            btn.addClass("command_btn");
            btn.addClass("blue_btn");
            var name = code.name;
            btn.attr("title", name);
            if (name.length > 14) {
                name = name.substr(0, 14) + "...";
            }
            btn.text(name);
            return btn;
        }
    };
} ();