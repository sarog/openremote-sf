var ImportController = function() {
    function ImportController() {
        // body...
        }



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



    function uploadSuccess(responseText, statusText) {
        ImportController.clear();
        var data = eval('(' + responseText + ')');
        var panel = data.panel;
        revertIphoneBtns(panel.iphoneBtns);
        revertKnxBtns(panel.knxBtns);
        revertX10Btns(panel.x10Btns);
        revertMacroBtns(panel.macroBtns);

        BUTTONID = panel.maxId;
        $("#upload_form_container").closeModalForm();
    }

    function revertIphoneBtns(iphoneBtns) {
        for (var index in iphoneBtns) {
            var btn = iphoneBtns[index];
            var cell = findCell(btn.x, btn.y);
            var oModel = ImportController.buildModel(btn.oModel);
            btn.oModel = oModel;
            var model = ImportController.buildModel(btn);
            IPhoneController.createIphoneBtn(model, cell);
			
			if (btn.oModel.className == "Infrared") {
				InfraredCollection[btn.oModel.codeId] = model;
			}
            
        }
    }

    function findCell(x, y) {
        var tr = $("#dropable_table").find("tr")[y];
        var td = $(tr).find("td")[x];
        return td;
    }


    function revertKnxBtns(knxBtns) {
        for (var index in knxBtns) {
            var btn = knxBtns[index];
            var model = ImportController.buildModel(btn);
            TabController.createKNX(model);
        }
    }


    function revertX10Btns(x10Btns) {
        for (var index in x10Btns) {
            var btn = x10Btns[index];
            var model = ImportController.buildModel(btn);
            TabController.createX10(model);
        }
    }

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
					InfraredCollection[subModel.codeId] = model;
				}
            }
        }
    }

    ImportController.buildModel = function(model) {
        switch (model.className) {
        case 'Infrared':
            return Infrared.init(model);
            break;
        case 'IphoneBtn':
            return IphoneBtn.init(model);
            break;
        case 'KNX':
            return KNX.init(model);
            break;
        case 'Macro':
            return Macro.init(model);
            break;
        case 'X10':
            return X10.init(model);
            break;

        }
    };



    //static method
    ImportController.init = function() {
        $("#uploadBtn").unbind().bind("click", showUploadForm);
    };

    ImportController.clear = function() {
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