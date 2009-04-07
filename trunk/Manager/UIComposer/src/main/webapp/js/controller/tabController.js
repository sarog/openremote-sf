var TabController = function() {
    function TabController() {
        //constractor 	
   	}

	// private method 
	function createKNX() {
	    var label = $("#knx_label_input");
	    var groupAddress = $("#knx_group_address_input");

	    $("#create_KNX_dialog").showModalForm("Create KNX", {
	        'Create': function() {
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
					
	                var btn = HTMLBuilder.KNXBtnBuilder(knx);
	                btn.prependTo($("#knx_tab .item_container"));
	                makeCommandBtnDraggable(btn);
	                $("#create_KNX_dialog").closeModalForm();
	            }
	        }
	    });
	}

	function createX10() {
	    var label = $("#x10_label_input");
	    var address = $("#x10_address_input");
	    var command = $("#x10_command_input");

	    $("#create_x10_dialog").showModalForm("Create X10", {
	        'Create': function() {
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
	
	                var btn = HTMLBuilder.X10BtnBuilder(x10);
	                btn.prependTo($("#x10_tab .item_container"));
	                makeCommandBtnDraggable(btn);
	                $("#create_x10_dialog").closeModalForm();
	            }
	        }
	    });
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
		$("#create_knx_icon").unbind().bind("click", createKNX);
	    $("#create_x10_icon").unbind().bind("click", createX10);
	    $("#select_command_icon").unbind().bind("click", selectCommand);
    };
	
    return TabController;
} ();