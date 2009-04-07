BUTTONID = 1;
// a hash contain all of infrared object, key is code id value is infrared model.
InfraredCollection = {};


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

        iphoneBtnBuilder: function(commandBtn) {
			var model = $(commandBtn).data("model");
           
            var btn = $("<div class='iphone_btn'></div>");
			btn.data("model",model);
            btn.attr("title",model.label);

			var text = model.label;
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

        macroLiBtnBuilder: function(draggable) {
            var macroCommandLi = $("<li><span></span></li>");
            macroCommandLi.addClass("macro_command");
            macroCommandLi.addClass("ui-state-default");
            macroCommandLi.data("model",draggable.data("model"));
            macroCommandLi.find("span").addClass("ui-icon");
            macroCommandLi.find("span").addClass("ui-icon-arrowthick-2-n-s");
            var name = draggable.text();
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