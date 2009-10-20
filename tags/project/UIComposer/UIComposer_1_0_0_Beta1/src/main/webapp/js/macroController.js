function createMacro() {
    $("#macro_name_form").showModalForm("Create Macro", {
        'Create': function() {
            if ($.empty($("#macro_name_input").val())) {
                $("#macro_name_form").updateTips($("#macro_name_input"), "Macro Name is required");
                return;
            } else {
                macroBtn = HTMLBuilder.macroBtnBuilder($.trim($("#macro_name_input").val()));
                $(macroBtn).appendTo($("#macro .item_container"));
                prepareMacroBtn(macroBtn);
                $("#macro_name_form").closeModalForm();
            }
        }
    });


}

function prepareMacroBtn(btn) {
    var buttons;
    if (typeof(btn) == 'undefined') {
        buttons = $(".macro_btn_defination");
    } else {
        buttons = $(btn);
    }

    buttons.find(".macro_detail").sortable({
        placeholder: 'ui-state-highlight',
        cursor: "move"
    });
    buttons.find(".macro_detail").disableSelection();

    buttons.find(".macro_detail").droppable({
        hoverClass: 'ui-state-highlight',
        accept: function(draggable) {
            if (draggable.hasClass("macro_btn")) {
                return false;
            }
            if (draggable.hasClass("blue_btn")) {
                return true;
            }
            return false;
        },
        drop: function(event, ui) {
            if (ui.draggable.hasClass("command_btn")) {
                if (typeof(ui.draggable.attr("eventId")) == "undefined") {
                    ui.draggable.attr("eventId", BUTTONID++);
                }
            }
            HTMLBuilder.macroLiBtnBuilder(ui.draggable).appendTo($(this));

        }
    });

    makeCommandBtnDraggable(buttons.find(".blue_btn.macro_btn"));
}

