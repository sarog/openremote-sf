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
            button.addClass("iphone_element");
            button.attr("id", knx.getElementId());
            button.data("model", knx);
            return button;

        },

        X10BtnBuilder: function(x10) {
            var button = HTMLBuilder.blueBtnBuilder(x10.label);
            button.addClass("x10_btn");
            button.addClass("iphone_element");
            button.attr("id", x10.getElementId());
            button.data("model", x10);
            button.attr("title", x10.label);
            return button;

        },


        blueBtnBuilder: function(text) {
            var button = $("<div></div>");
            button.attr("title", text);
            button.addClass("blue_btn");

            button.interceptStr({
                text: text,
                max: 14,
				setTitle:false
            });
            return button;
        },

        iphoneBtnBuilder: function(iphoneBtn) {
            var text = iphoneBtn.label;
            if (text.length > 5) {
                text = text.substr(0, 5) + "<br/>...";
            }
            var btn = $(EJSHelper.render("template/_iphoneBtn.ejs", {
                label: text
            }));

            if (iphoneBtn.icon != "") {
                btn.find("table").removeClass("iPhone_btn_cont");
                btn.find("table .middle").html("<img src=" + iphoneBtn.icon + ">");
            }
            btn.data("model", iphoneBtn);
            btn.attr("title", iphoneBtn.label);
            btn.attr("id", iphoneBtn.getElementId());
            return btn;
        },

        iphoneBtnHelperBuilder: function(label) {
            // var helper = $(EJSHelper.render("template/_iphoneBtn.ejs", {
            //                label: label
            //            }));
            //            helper.height(ScreenView.cellHeight);
            //            helper.width(ScreenView.cellWidth);
            return HTMLBuilder.blueBtnBuilder(label);;
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
            template.attr("id", macro.getElementId());
            template.data("model", macro);
            return $(template);
        },

        macroLiBtnBuilder: function(model) {
            var macroCommandLi = $("<li><span></span></li>");
            macroCommandLi.addClass("macro_command");
            macroCommandLi.addClass("ui-state-default");
            macroCommandLi.data("model", model);
            macroCommandLi.find("span").addClass("ui-icon");
            macroCommandLi.find("span").addClass("ui-icon-arrowthick-2-n-s");

            macroCommandLi.interceptStr({
				max:8,
				text:model.label,
				setText: function(str){
					$(this).text(str);
				}
			});
            return macroCommandLi;
        },

        infraredBtnBuilder: function(infrared) {
            var btn = $("<div></div>");
            btn.attr("id", infrared.getElementId());
            btn.data("model", infrared);

            btn.addClass("command_btn");
            btn.addClass("blue_btn");
            btn.addClass("iphone_element");

            var name = infrared.label;
            btn.attr("title", name);
            if (name.length > 14) {
                name = name.substr(0, 14) + "...";
            }
            btn.text(name);
            return btn;
        }
    };
} ();