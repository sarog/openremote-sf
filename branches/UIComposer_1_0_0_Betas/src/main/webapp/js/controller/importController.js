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
var ImportController = function() {
    function ImportController() {
        // constructor
        }

    //private methods
    /**
     * Show upload from
     */
    function showUploadForm() {
        var zipFile = $("#zip_file_input");
        $("#upload_form_container").showModalForm("Import", {
            buttons: {
                'Import': function() {
                    if ($.empty(zipFile.val())) {
                        $("#upload_form_container").updateTips(zipFile, "zip file is required");
                        return;
                    }
                    if (confirm("All your current work will clear, are you sure?")) {

                        ImportController.getCurrentUserPath(function() {
                            $("#upload_form").submit();
                        });
                    }

                }
            },
            confirmButtonName: 'Import',
            open: function() {
                $("#upload_form").ajaxForm({
                    success: uploadSuccess,
                    dataType: 'text',
                    type: 'post'
                });
            }
        });

    }


    /**
     * Invoked after upload success.
     * @param responseText responseText
     * @param statusText statusText
     */
    function uploadSuccess(responseText, statusText) {

        ImportController.cleanUp();
        var data = eval('(' + responseText + ')');
        revertScreens(data.panel.screens);
        revertKnxBtns(data.panel.knxBtns);
        revertX10Btns(data.panel.x10Btns);
        revertMacroBtns(data.panel.macroBtns);

        global.BUTTONID = data.panel.maxId;
        $("#upload_form_container").closeModalForm();
    }


    function revertScreens(screens) {


        for (var index in screens) {
            var o_screen = screens[index];
            var screen = ImportController.buildModel(o_screen);
            screen.buttons = new Array();
            for (var index in o_screen.buttons) {
                var btn = revertIphoneBtn(o_screen.buttons[index]);
                if (btn.icon != "") {
                    btn.icon = global.userDirPath + "/" + getFileNameFromPath(btn.icon);
                }
                screen.buttons.push(btn);
            }

            ScreenViewController.createScreen(screen);

            ScreenView.setLastOptionSelected();
            ScreenView.updateView(global.screens[ScreenView.getSelectedScreenId()]);
        }
    }

    /**
     * Revert the Iphone buttons
     * @param iphoneBtns iphoneBtns object from descriptionFile
     */
    function revertIphoneBtn(btn) {
        var oModel = ImportController.buildModel(btn.oModel);
        var model = ImportController.buildModel(btn);
        model.oModel = oModel;

        if (btn.oModel.className == "Infrared") {
            global.InfraredCollection[btn.oModel.codeId] = oModel;
        }
        return model;

    }



    /**
     * Revert KNX buttons
     * @param knxBtns knxBtns object from description file
     */
    function revertKnxBtns(knxBtns) {
        for (var index in knxBtns) {
            var btn = knxBtns[index];
            var model = ImportController.buildModel(btn);
            TabController.createKNX(model);
        }
    }

    /**
     * Revert X10 buttons	
     * @param x10Btns x10Btns object from description file
     */
    function revertX10Btns(x10Btns) {
        for (var index in x10Btns) {
            var btn = x10Btns[index];
            var model = ImportController.buildModel(btn);
            TabController.createX10(model);
        }
    }

    /**
     * Revert Macro buttons
     * @param macroBtns macroBtns object from description file
     */
    function revertMacroBtns(macroBtns) {
        var macroArray = {};
        for (var index in macroBtns) {
            var btn = macroBtns[index];
            var model = ImportController.buildModel(btn);
            MacroController.createMacroBtn(model);
            macroArray[model.id] = model;
            for (var index in btn.buttons) {
                var sub = btn.buttons[index];
                var subModel;
                if (macroArray[sub.id] === undefined) {
                    subModel = ImportController.buildModel(sub);
                } else {
                    subModel = macroArray[sub.id];
                }

                MacroController.createMacroSubli(subModel, $("#" + model.getElementId()).find("ul"));

                if (subModel.className == "Infrared") {
                    global.InfraredCollection[subModel.codeId] = subModel;
                }
            }
        }
    }

    //static method
    ImportController.init = function() {
        $("#uploadBtn").unbind().bind("click", showUploadForm);
    };

    /**
     * Build Model according to className.
     * @param model
     */
    ImportController.buildModel = function(model) {
        return eval(model.className + '.init(model);');
    };

    /**
     * Clean up the page.
     */
    ImportController.cleanUp = function() {
        $("#macro .macro_btn_defination").remove();
        $("#knx_container .knx_btn").remove();
        $("#x10_container .x10_btn").remove();
        $("#command_container .command_btn").remove();
        $("#iphoneBtn_container .iphone_btn").remove();
        $("#screen_select option").remove();
        global.BUTTONID = 1;
        global.InfraredCollection = {};
        global.screens = {};

    };

    ImportController.getCurrentUserPath = function(callback) {
        $.get("currenUserPath.htm",
        function(data) {
            global.userDirPath = data;
            callback();
        });
    };
    return ImportController;
} ();