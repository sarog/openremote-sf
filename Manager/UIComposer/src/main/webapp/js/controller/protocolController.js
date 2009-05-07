var KNXController = function() {
	return {
	    /**
	     * Invoked when user confirm create knx button.
	     */
		confirmCreate:function() {
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
	            knx.id = global.BUTTONID++;
	            knx.label = label.val();
	            knx.groupAddress = groupAddress.val();

	           	var knxView = new KNXView(knx);
				var btn = knxView.getElement();
				makeBtnDraggable(btn);
		        btn.inspectable();
		
	            $("#create_KNX_dialog").closeModalForm();
	        }
	    }
		
	};
}();

var X10Controller = function() {
	return {
		/**
	     * Invoked when user confirm create x10 button.
	     */
	    confirmCreate:function () {
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
	            x10.id = global.BUTTONID++;
	            x10.label = label.val();
	            x10.address = address.val();
	            x10.command = command.val();

	            var x10View = new X10View(x10);
				var btn = x10View.getElement();
				makeBtnDraggable(btn);
		        btn.inspectable();
		
	            $("#create_x10_dialog").closeModalForm();
	        }
	    }
	};
}();