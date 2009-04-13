var TabController = function() {
    function TabController() {
        //constractor 	
    }

    // private method
    function showCreateKNXDialog() {
      
        $("#create_KNX_dialog").showModalForm("Create KNX", {
            'Create': confirmCreateKNX
        });
		$("#create_KNX_dialog").enterKeyPressed(confirmCreateKNX);
    }

	function confirmCreateKNX() {
		var label = $("#knx_label_input");
	    var groupAddress = $("#knx_group_address_input");
		
        var valid = true;
        if ($.empty(label.val())) {
            valid = false;
            $("#create_KNX_dialog").updateTips(label, "Label is required");
            return;
        }
        if ($.empty(groupAddress.val())) {
            valid = false;
            $("#create_KNX_dialog").updateTips(groupAddress, "Group Address is required");
            return;
        }
        if (valid) {
            var knx = new KNX();
            knx.id = BUTTONID++;
            knx.label = label.val();
            knx.groupAddress = groupAddress.val();

            TabController.createKNX(knx);
            $("#create_KNX_dialog").closeModalForm();
        }
    }

    function showCreateX10Dialog() {
    

        $("#create_x10_dialog").showModalForm("Create X10", {
            'Create': confirmCreateX10
        });
		$("#create_x10_dialog").enterKeyPressed(confirmCreateX10);
    }

	function confirmCreateX10 () {
	    var label = $("#x10_label_input");
        var address = $("#x10_address_input");
        var command = $("#x10_command_input");
        var valid = true;
        if ($.empty(label.val())) {
            valid = false;
            $("#create_x10_dialog").updateTips(label, "Label is required");
            return;
        }
        if ($.empty(address.val())) {
            valid = false;
            $("#create_x10_dialog").updateTips(address, "Address is required");
            return;
        }
        if ($.empty(command.val())) {
            valid = false;
            $("#create_x10_dialog").updateTips(command, "Command is required");
            return;
        }
        if (valid) {
            var x10 = new X10();
            x10.id = BUTTONID++;
            x10.label = label.val();
            x10.address = address.val();
            x10.command = command.val();

            TabController.createX10(x10);
            $("#create_x10_dialog").closeModalForm();
        }
    }


    function selectCommand() {
        $("#command_navigition").dialog({
            bgiframe: true,
            autoOpen: false,
            height: "auto",
            width: 785,
            modal: true,
            title: "Please Select Vendor",
            open: function() {
                fillVendorSelect();
            }
        });
        $("#command_navigition").dialog("open");
    }



    //static method
    TabController.init = function() {
        $("#tabs").tabs();
        $("#create_knx_icon").unbind().bind("click", showCreateKNXDialog);
        $("#create_x10_icon").unbind().bind("click", showCreateX10Dialog);
        $("#select_command_icon").unbind().bind("click", selectCommand);
    };

    TabController.createKNX = function(knx) {
        var btn = HTMLBuilder.KNXBtnBuilder(knx);
        btn.prependTo($("#knx_tab .item_container"));
        makeCommandBtnDraggable(btn);
    };

    TabController.createX10 = function(x10) {
        var btn = HTMLBuilder.X10BtnBuilder(x10);
        btn.prependTo($("#x10_tab .item_container"));
        makeCommandBtnDraggable(btn);
    };

    return TabController;
}();