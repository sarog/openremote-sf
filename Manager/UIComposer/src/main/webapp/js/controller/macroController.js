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
var MacroController = function() {

    function MacroController() {

    };

    //private method
    /**
     * Make macro window draggable
     */
    function makeMacroPanelDraggabe() {
        $("#macro").draggable({
            cursor: 'move',
            handle: ".item_title"
        });
    }

    /**
     * Show create macro dialog.
     */
     function showCreateDialog () {
        $("#macro_name_form").showModalForm("Create Macro", {
            'Create': confirmCreateMacro
        });
        $("#macro_name_form").enterKeyPressed(confirmCreateMacro);
    }

    /**
     * Invoke after confirm create macro.
     */
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
    
    //static method
    MacroController.init = function() {
        // makeMacroPanelDraggabe();
        $("#create_macro_btn").unbind().click(showCreateDialog);
    };

    /**
     * Create macro button and add it to page.
     * @param macro macro model
     */
    MacroController.createMacroBtn = function(macro) {
        var macroBtn = HTMLBuilder.macroBtnBuilder(macro);
        $(macroBtn).prependTo($("#macro .item_container"));
        MacroController.prepareMacroSublist(macroBtn);
		
		var btn = macroBtn.find(".macro_btn");
		btn.unbind().click(function() {
			$(".highlightInspected").removeClass("highlightInspected");
			$(this).addClass("highlightInspected");
			var model = $(this).parent(".macro_btn_defination").data("model");
			InspectViewController.updateView(model);
		});
        makeBtnDraggable(macroBtn);
    };

    /**
     * Prepare the macro button.Make Macro sub list sortable and droppable.
     * @param btn macro button whose sub list you want to prepare
     */
    MacroController.prepareMacroSublist = function(btn) {
        var buttons;
        if (btn === undefined) {
            buttons = $(".macro_btn_defination");
        } else {
            buttons = $(btn);
        }

        MacroController.makeMacroSublistSortable(buttons.find(".macro_detail"));
        MacroController.makeMacroSubListDroppable(buttons.find(".macro_detail"));
    };

    /**
     * Make macro sub list sortable.
     * @param items macro button whose sub list you want to make it sortable.
     */
    MacroController.makeMacroSublistSortable = function(items) {
        items.sortable({
            placeholder: 'ui-state-highlight',
            cursor: "move"
        });
        items.find(".macro_detail").disableSelection();

    };

    /**
     * Make macro sub list droppable.
     * @param items macro button whose sub list you want to make it droppable.
     */
    MacroController.makeMacroSubListDroppable = function(items) {
        items.droppable({
            hoverClass: 'ui-state-highlight',
            accept: function(draggable) {
                if (draggable.hasClass("iphone_element")) {
                    // Can't drag macro button to its sublist, this may occur recursion error.
                    if (draggable.attr("id") == $(this).parent().attr("id")) {
                        return false;
                    }
					
                    //Prevent circulative macro
					//TODO 
                    if (draggable.data("model") !== undefined && draggable.data("model").className == "Macro") {
                        var draggableModel = draggable.data("model");
                        var macroBtnModel = $(this).parent().data("model");

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

    /**
     * Create sub li.
     * @param model data model
     * @param container which you want to add li into
     */
    MacroController.createMacroSubli = function(model, container) {
       	var subli = HTMLBuilder.macroLiBtnBuilder(model);
		subli.appendTo(container);
		
		// subli.unbind().click(function() {
		// 		$(".highlightInspected").removeClass("highlightInspected");
		// 		$(this).addClass("highlightInspected");
		// 		var model = $(this).data("model");
		// 		InspectViewController.updateView(model);
		// 	});

    };

	MacroController.updateMacro = function (macro) {
        // get Label value from inspect window
        var label  = $.trim($("#inspect_macro_label").val());
		macro.label = label;

        //update view
        var btn = $("#"+macro.elementId());
		btn.attr("title", macro.label);
        if (label.length > 14) {
            label = label.substr(0, 14) + "...";
        }
        btn.html(label);
	
        //re-set model.
		btn.data("model",macro);
	};


    return MacroController;
} ();
