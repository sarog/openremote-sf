var MacroController = function() {

    function MacroController() {

        };

    //private method
    function makeMacroPanelDraggabe() {
        $("#macro").draggable({
            cursor: 'move',
            handle: ".item_title"
        });
    }


    //static method
    MacroController.init = function() {
        makeMacroPanelDraggabe();
        $("#create_macro_btn").unbind().click(MacroController.showCreateDialog);
    };

    MacroController.showCreateDialog = function() {
        $("#macro_name_form").showModalForm("Create Macro", {
            'Create': confirmCreateMacro
        });
        $("#macro_name_form").enterKeyPressed(confirmCreateMacro);
    };

    function confirmCreateMacro() {
        if ($.empty($("#macro_name_input").val())) {
            $("#macro_name_form").updateTips($("#macro_name_input"), "Macro Name is required");
            return;
        } else {
            var buttonName = $("#macro_name_input").val();
            var macro = new Macro();
            macro.id = BUTTONID++;
            macro.label = $.trim(buttonName);

            MacroController.createMacroBtn(macro);
            $("#macro_name_form").closeModalForm();
        }
    }
    MacroController.createMacroBtn = function(macro) {
        var macroBtn = HTMLBuilder.macroBtnBuilder(macro);
        $(macroBtn).appendTo($("#macro .item_container"));
        MacroController.prepareMacroSublist(macroBtn);
        MacroController.makeMacroBtnDraggable(macroBtn.find(".blue_btn.macro_btn"));
    };


    MacroController.prepareMacroSublist = function(btn) {
        var buttons;
        if (btn === undefined) {
            buttons = $(".macro_btn_defination");
        } else {
            buttons = $(btn);
        }

        MacroController.makeMacroSublistSortable(buttons.find(".macro_detail"));
        MacroController.makeMacroSubListDroppable(buttons);
    };

    MacroController.makeMacroSublistSortable = function(items) {
        items.sortable({
            placeholder: 'ui-state-highlight',
            cursor: "move"
        });
        items.find(".macro_detail").disableSelection();

    };

    MacroController.makeMacroSubListDroppable = function(items) {
        items.find(".macro_detail").droppable({
            hoverClass: 'ui-state-highlight',
            accept: function(draggable) {
                if (draggable.hasClass("blue_btn")) {
                    // Can't drag macro button to its sublist, this may occur recursion error.
                    if (draggable.attr("id") == $(this).prev(".macro_btn").attr("id")) {
                        return false;
                    }

                    if (draggable.data("model") !== undefined && draggable.data("model").className == "Macro") {
                        var draggableModel = draggable.data("model");
                        var macroBtnModel = $(this).prev(".macro_btn").data("model");

                        if (draggableModel.getSubModels().length == 0) {
                            return true;
                        }

                        if ($.inArray(macroBtnModel, draggableModel.getSubModels()) != -1) {
                            return false;
                        }
                    }

                    return true;
                }
                return false;
            },
            drop: function(event, ui) {
                if (ui.draggable.hasClass("command_btn")) {
                    ui.draggable.data("model", Infrared.getInfraredModelWithDraggable(ui.draggable));
                }
                MacroController.createMacroSubli(ui.draggable.data("model"), $(this));
            }
        });
    };
    MacroController.createMacroSubli = function(model, container) {
        HTMLBuilder.macroLiBtnBuilder(model).appendTo(container);
    };
    MacroController.makeMacroBtnDraggable = function(items) {
        var btns;
        if (items === undefined) {
            btns = $(".blue_btn");
        } else {
            btns = items;
        }
        btns.draggable({
            zIndex: 2700,
            cursor: 'move',
            helper: 'clone',
            start: function(event, ui) {
                $(this).draggable('option', 'revert', false);
            }
        });
    };


    return MacroController;
} ();
