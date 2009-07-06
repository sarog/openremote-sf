/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3.0 of the
 *  License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * TODO
 *
 * @author allen.wei@finalist.cn
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
var ImportController = function() {
    function ImportController() {
        // constructor
        }

    // Private methods ----------------------------------------------------------------------------

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
                            $.showLoading();
                            $("#upload_form").submit();
                        });
                    }

                }
            },
            confirmButtonName: 'Import',
            open: function() {
                $("#upload_form").ajaxForm({
                    success: uploadSuccess,
                    dataType: 'json',
                    type: 'post',
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $.hideLoading();
                        $("#upload_form_container").updateTips($("#zip_file_input"), XMLHttpRequest.responseText);
                    },
                    global: false
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
        var data = responseText;

        // Notice: revert order has functional dependencies. Reverting needs to be order
        // so that protocols are first, macros come after protocol buttons and screens are last

        revertKnxBtns(data.panel.knxBtns);
        revertX10Btns(data.panel.x10Btns);
        revertHTTPBtns(data.panel.httpBtns);          
        revertTCPBtns(data.panel.tcpBtns);
        revertTelnetBtns(data.panel.telnetBtns);
        revertMacroBtns(data.panel.macroBtns);
        revertMacroSubBtns(data.panel.macroBtns);
        revertScreens(data.panel.screens);

        global.BUTTONID = data.panel.maxId;
        $("#upload_form_container").closeModalForm();
        $.hideLoading();
    }


    /**
     * Revert KNX buttons
     * @param knxBtns knxBtns object from description file
     */
    function revertKnxBtns(knxBtns) {
        for (var index in knxBtns) {
            var btn = knxBtns[index];
            var model = ImportController.buildModel(btn);
            KNXController.createKNX(model);
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
            X10Controller.createX10(model);
        }
    }

    /**
     * Recreate HTTP buttons
     *
     * @param httpBtns  TODO
     */
    function revertHTTPBtns(httpBtns) {
        for (var index in httpBtns) {
            var btn = httpBtns[index];
            var model = ImportController.buildModel(btn);
            HTTPController.createHTTP(model);
        }
    }

    /**
     * Recreate TCP buttons
     *
     * @param tcpBtns  TODO
     */
    function revertTCPBtns(tcpBtns) {
        for (var index in tcpBtns) {
            var btn = tcpBtns[index];
            var model = ImportController.buildModel(btn);
            TCPController.createTCP(model);
        }
    }

    /**
     * Recreate Telnet buttons
     *
     * @param telnetBtns  TODO
     */
    function revertTelnetBtns(telnetBtns) {
        for (var index in telnetBtns) {
            var btn = telnetBtns[index];
            var model = ImportController.buildModel(btn);
            TelnetController.createTelnet(model);
        }
    }

    /**
     * Revert Macro buttons
     * @param macroBtns macroBtns object from description file
     */
    function revertMacroBtns(macroBtns) {
        for (var index in macroBtns) {
            var btn = macroBtns[index];
            var model = ImportController.buildModel(btn);
            MacroController.createMacroBtn(model);
        }
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
        var model = ImportController.buildModel(btn);
        model.oModel = findOrBuildModel(btn.oModel);

        if (btn.oModel.className == "Infrared") {
            global.InfraredCollection[btn.oModel.codeId] = model.oModel;
        }
        return model;

    }



    function revertMacroSubBtns(macroBtns) {
        for (var index in macroBtns) {
            var btn = macroBtns[index];
            for (var index in btn.buttons) {
                var sub = btn.buttons[index];
                var macroSub = ImportController.buildModel(sub);
                macroSub.oModel = findOrBuildModel(sub.oModel);
                MacroController.createMacroSubli(macroSub);
            }
        }
    }

    function findOrBuildModel(model) {
		var temp_model = ImportController.buildModel(model);
        var oModel = null;
        if (temp_model.getElementId !== undefined && $("#" + temp_model.getElementId()).length > 0) {
            oModel = $("#" + temp_model.getElementId()).data("model");
        } else {
            oModel = temp_model;
        }
		return oModel;
    }


    // Static methods -----------------------------------------------------------------------------

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
        $("#http_container .http_btn").remove();      
        $("#tcp_container .tcp_btn").remove();
        $("#telnet_container .telnet_btn").remove();      
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