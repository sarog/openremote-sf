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
    function showCreateDialog() {
        $("#macro_name_form").showModalForm("Create Macro", {
            buttons: {
                'Create': confirmCreateMacro
            },
            confirmButtonName: 'Create',
			width:380
        });
    }
    /**
	     * Invoke after confirm create macro.
	     */
    function confirmCreateMacro() {
        $("#macro_form").validate({
                invalidHandler:function(form, validator) {
                    $("#macro_name_form").errorTips(validator);
                },
                showErrors:function(){},
                rules: {
                    macro_name_input: {
                        required: true,
                        maxlength: 50
                    }
                },
                messages:{
                    macro_name_input: {
                        required: "Please input a macro name",
                        maxlength: "Please input a macro name no more than 50 charactors"
                    }
                }
        });
        if($("#macro_form").valid()){
            var buttonName = $("#macro_name_input").val();
            var macro = new Macro();
            macro.id = global.BUTTONID++;
            macro.label = $.trim(buttonName);


            MacroController.createMacroBtn(macro);
            $("#macro_name_form").closeModalForm();
        }
    }

    return {

        //static method
        init: function() {
            // makeMacroPanelDraggabe();
            $("#create_macro_btn").unbind().click(showCreateDialog);
        },

        /**
     * Create macro button and add it to page.
     * @param macro macro model
     */
        createMacroBtn: function(macro) {
            var macroView = new MacroView(macro);

            macro.addUpdateListener(macroView);
            macro.addDeleteListener(macroView);
            MacroController.prepareMacroSublist(macroView.getSubList());
            makeBtnDraggable(macroView.getElement());

            macroView.getMacroBtn().inspectable({
                model: macroView.getModel()
            });

        },

        /**
     * Prepare the macro button.Make Macro sub list sortable and droppable.
     * @param btn macro button whose sub list you want to prepare
     */
        prepareMacroSublist: function(ul) {
            MacroController.makeMacroSublistSortable(ul);
            MacroController.makeMacroSubListDroppable(ul);
        },

        /**
     * Make macro sub list sortable.
     * @param items macro button whose sub list you want to make it sortable.
     */
        makeMacroSublistSortable: function(items) {
            items.sortable({
                placeholder: 'ui-state-highlight',
                cursor: "move"
            });
            items.disableSelection();

        },

        /**
     * Make macro sub list droppable.
     * @param items macro button whose sub list you want to make it droppable.
     */
        makeMacroSubListDroppable: function(items) {
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
					var macroSub = new MacroSub($(this).parent().data("model").id,ui.draggable.data("model"));
                    MacroController.createMacroSubli(macroSub);
                }
            });
        },

        /**
     * Create sub li.
     * @param model data model
     * @param container which you want to add li into
     */
        createMacroSubli: function(macroSub) {
			
        	var macroSubView = new MacroSubView(macroSub);

			macroSub.addUpdateListener(macroSubView);
			
			macroSubView.getElement().inspectable({
				check:function() {
					if (!$("#inspect_macroSub_delay").val().toString().isNumber()) {
						if ($(this).data("model").oModel.className == "Macro") {
							return true;
						}
						$("#inspect_body").updateTips($("#inspect_macroSub_delay"),"Please input a number");
						return false;
					}
					return true;
				},
				after: function(){
					if ($(this).data("model").oModel.className == "Macro") {
						$("#inspect_macroSub_delay").attr("disabled","true");
						$("#inspect_macroSub_delay").addClass("ui-state-disabled");
					}
				}
			});
			macroSub.oModel.addDeleteListener(macroSubView);
			
        }

    };

} ();
