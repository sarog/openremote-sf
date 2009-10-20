BUTTONID = 1;
MACROBUTTONID = 1;

HTMLBuilder = function() {
    return {

        KNXBtnBuilder: function(label, groupAddress) {
            var button = HTMLBuilder.blueBtnBuilder(label);
            button.addClass("knx_btn");
            button.attr("eventId", BUTTONID++);
            button.data("groupAddress", groupAddress);
            return button;

        },

        X10BtnBuilder: function(label, address, command) {
            var button = HTMLBuilder.blueBtnBuilder(label);
            button.addClass("x10_btn");
            button.attr("eventId", BUTTONID++);
            button.data("address", address);
            button.data("command", command);
            button.attr("title", label);
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
            var text = $(commandBtn).text();
            var btn = $("<div class='iphone_btn'></div>");
            if ($(commandBtn).hasClass("macro_btn")) {
                btn.addClass("macro_btn");
                btn.attr("macroId", $(commandBtn).attr("macroId"));
            } else {
                btn.attr("eventId", $(commandBtn).attr("eventId"));
            }

            btn.attr("title", text);
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

        macroBtnBuilder: function(name) {
            var template = $("#macro_template .macro_btn_defination").clone();
            $(template).find(".macro_btn").attr("title", name);
            if (name.length > 14) {
                name = name.substr(0, 14) + "...";
            }
            $(template).find(".macro_btn").html(name);
            $(template).find(".macro_btn").attr("macroId", MACROBUTTONID++);
            return $(template);
        },

        macroLiBtnBuilder: function(draggable) {
            var macroCommandLi = $("<li><span></span></li>");
            macroCommandLi.addClass("macro_command");
            macroCommandLi.addClass("ui-state-default");
            macroCommandLi.attr("eventId", draggable.attr("eventId"));
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

        commandBtnBuilder: function(code) {
            var btn = $("<div></div>");
            btn.data("codeId", code.id);
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