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
            'Import': function() {
                if ($.empty(zipFile.val())) {
                    $("#upload_form_container").updateTips(zipFile, "zip file is required");
                    return;
                }
                if (confirm("All your current work will clear, are you sure?")) {
                    $("#upload_form").submit();

                }

            }
        },
        function() {
            $("#upload_form").ajaxForm({
                success: uploadSuccess,
                dataType: 'text',
                type: 'post'
            });
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
        var panel = data.panel;
        revertIphoneBtns(panel.iphoneBtns);
        revertKnxBtns(panel.knxBtns);
        revertX10Btns(panel.x10Btns);
        revertMacroBtns(panel.macroBtns);

        BUTTONID = panel.maxId;
        $("#upload_form_container").closeModalForm();
    }

    /**
     * Revert the Iphone buttons
     * @param iphoneBtns iphoneBtns object from descriptionFile
     */
    function revertIphoneBtns(iphoneBtns) {
        for (var index in iphoneBtns) {
            var btn = iphoneBtns[index];
            var cell = findCell(btn.x, btn.y);
            var oModel = ImportController.buildModel(btn.oModel);
            btn.oModel = oModel;
            var model = ImportController.buildModel(btn);
            IPhoneController.createIphoneBtn(model, cell);
			
			if (btn.oModel.className == "Infrared") {
				InfraredCollection[btn.oModel.codeId] = oModel;
			}
            
        }
    }

    /**
     * Find the table cell according to x and y.
     * @param x
     * @param y
     */
    function findCell(x, y) {
        var tr = $("#dropable_table").find("tr")[y];
        var td = $(tr).find("td")[x];
        return td;
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
				
                MacroController.createMacroSubli(subModel, $("#" + model.elementId()).next("ul"));
                                               
				if (subModel.className == "Infrared") {
					InfraredCollection[subModel.codeId] = subModel;
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
		return eval(model.className+'.init(model);');
    };

    /**
     * Clean up the page.
     */
    ImportController.cleanUp = function() {
        $("#macro .macro_btn_defination").remove();
        $("#knx_container .knx_btn").remove();
        $("#x10_container .x10_btn").remove();
        $("#command_container .command_btn").remove();
        $("#dropable_table .iphone_btn").remove();
        BUTTONID = 1;
        InfraredCollection = {};
    };
    return ImportController;
} ();