/*
require HTMLBuilder.js
require model/Macro.js
*/


var MacroController = function() {
	
	
    function MacroController () {
       //init
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
            'Create': function() {
                if ($.empty($("#macro_name_input").val())) {
                    $("#macro_name_form").updateTips($("#macro_name_input"), "Macro Name is required");
                    return;
                } else {
                    MacroController.createMacroBtn($("#macro_name_input").val());
                    $("#macro_name_form").closeModalForm();
                }
            }
        });
    };

    MacroController.createMacroBtn = function(buttonName) {
        var macro = new Macro();
        macro.id = BUTTONID++;
        macro.label = $.trim(buttonName);
        var macroBtn = HTMLBuilder.macroBtnBuilder(macro);
        $(macroBtn).appendTo($("#macro .item_container"));
        MacroController.prepareMacroSublist(macroBtn);
        MacroController.makeMacroBtnDraggable(macroBtn.find(".blue_btn.macro_btn"));
    };


    MacroController.prepareMacroSublist = function(btn) {
        var buttons;
        if (typeof(btn) == 'undefined') {
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
                    return true;
                }
                return false;
            },
            drop: function(event, ui) {
                if (ui.draggable.hasClass("command_btn")) {                    
					ui.draggable.data("model",Infrared.getInfraredModelWithDraggable(ui.draggable));
                }
                HTMLBuilder.macroLiBtnBuilder(ui.draggable).appendTo($(this));

            }
        });
    };

    MacroController.makeMacroBtnDraggable = function(items) {
        var btns;
        if (typeof(items) == 'undefined') {
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
