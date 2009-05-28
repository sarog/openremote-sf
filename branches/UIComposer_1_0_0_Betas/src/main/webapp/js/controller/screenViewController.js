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

ScreenViewController = function() {
    function ScreenViewController() {
		
    }

    /**
     * Show create Screen dialog.
     */
    function showCreateScreenDialog() {
        $("#create_screen_dialog").showModalForm("Create Screen", {
			buttons:{
				 'Create': confirmCreateScreen
			},
			confirmButtonName:'Create',
			width:400
        });
    }

    /**
     * Invoked when user confirm create Screen.
     */
    function confirmCreateScreen() {
        var name = $("#screen_name_input");
        var row = $("#screen_row_input");
        var col = $("#screen_col_input");        
        $("#screen_form").validate({
                invalidHandler:function(form, validator) {
                    $("#create_screen_dialog").errorTips(validator);
                },
                showErrors:function(){},
                rules: {
                    screen_name_input: {
                        required: true,
                        maxlength: 50
                    },
                    screen_row_input: {
                        required:true,
                        digits:true,
                        range: [1,6]
                    },
                    screen_col_input: {
                        required:true,
                        digits:true,
                        range: [1,4]
                    }
                },
                messages:{
                    screen_name_input: {
                        required: "Please input a name",
                        maxlength: "Please input a name no more than 50 charactors"
                    },
                    screen_row_input: {
                        required: "Please input a row",
                        digits: "Please input a digit",
                        range: "Please input a digit between 1 and 6"
                    },
                    screen_col_input: {
                        required: "Please input a column",
                        digits: "Please input a digit",
                        range: "Please input a digit between 1 and 4"
                    }
                }
            });
        if ($("#screen_form").valid()) {
            var screen = new Screen();
            screen.id = global.BUTTONID++;
            screen.name = name.val();
			screen.row = row.val();
			screen.col = col.val();
            ScreenViewController.createScreenAndUpdateView(screen);
            $("#create_screen_dialog").closeModalForm();
        }
    }

    function screenSelectChanged() {
        ScreenView.updateView(global.screens[ScreenView.getSelectedScreenId()]);
    }

    function screenSelectClicked() {
        ScreenViewController.storeCurrentScreen();
    }

    ScreenViewController.init = function() {
        $("#creatScreenBtn").unbind().click(showCreateScreenDialog);
        if ($("#creatScreenBtnOnPanel").length > 0) {
            $("#creatScreenBtnOnPanel").unbind().click(showCreateScreenDialog);
        }
        $("#screen_select").unbind().change(screenSelectChanged);

        $("#screen_select").click(screenSelectClicked);

    };

    ScreenViewController.createScreenAndUpdateView = function(screen) {
        if (ScreenView.getSelectedScreenId() != 0) {
            ScreenViewController.storeCurrentScreen();
        }

        $("#iphoneBtn_container").empty();

        ScreenViewController.createScreen(screen);
        ScreenView.setLastOptionSelected();
        screenSelectChanged();
    };

    ScreenViewController.createScreen = function(screen) {
        ScreenView.addScreenSelect(screen);
        global.screens[screen.id] = screen;

    };

    ScreenViewController.getCurrentScreen = function() {
        return global.screens[ScreenView.getSelectedScreenId()];
    };



    ScreenViewController.storeCurrentScreen = function() {
        //save previous screen
        if (ScreenView.getSelectedScreenId() != 0) {
            var preScreen = DownloadController.parseCurrentScreen(global.screens[ScreenView.getSelectedScreenId()]);
            global.screens[preScreen.id] = preScreen;
        }
    };




    return ScreenViewController;
} ();