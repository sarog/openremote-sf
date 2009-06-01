var KNXController = function() {
	return {
         /**
         * Show create knx button dialog.
         */
         showCreateKNXDialog:function() {
            $("#create_KNX_dialog").showModalForm("Create KNX", {
                buttons:{
                    'Create': KNXController.confirmCreate
                },
                confirmButtonName:'Create',
				width:350
            });
        },
	    /**
	     * Invoked when user confirm create knx button.
	     */
		confirmCreate:function() {
	        var label = $("#knx_label_input");
	        var groupAddress = $("#knx_group_address_input");
            
            $("#knx_form").validate({
                invalidHandler:function(form, validator) {
                    $("#create_KNX_dialog").errorTips(validator);
                },
                showErrors:function(){},
                rules: {
                    knx_label_input: {
                        required: true,
                        maxlength: 50
                    },
                    knx_group_address_input: {
                        required:true,
                        maxlength: 50
                    }
                },
                messages:{
                    knx_label_input: {
                        required: "Please input a label",
                        maxlength: "Please input a label no more than 50 charactors"
                    },
                    knx_group_address_input: {
                        required: "Please input a group address",
                        maxlength: "Please input a group address no more than 50 charactors"
                    }
                }
            });
	        if ($("#knx_form").valid()) {
	            var knx = new KNX();
	            knx.id = global.BUTTONID++;
	            knx.label = label.val();
	            knx.groupAddress = groupAddress.val();

	           	var knxView = new KNXView(knx);
	
				knx.addDeleteListener(knxView);
				knx.addUpdateListener(knxView);
				
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
         * Show create x10 button dialog.
         */
        showCreateX10Dialog:function () {
            $("#create_x10_dialog").showModalForm("Create X10", {
                buttons:{
                    'Create': X10Controller.confirmCreate
                },
                confirmButtonName:'Create',
				width:350
            });
        },
		/**
	     * Invoked when user confirm create x10 button.
	     */
	    confirmCreate:function () {
	        var label = $("#x10_label_input");
	        var address = $("#x10_address_input");
	        var command = $("#x10_command_input");
	        
            $("#x10_form").validate({
                invalidHandler:function(form, validator) {
                    $("#create_x10_dialog").errorTips(validator);
                },
                showErrors:function(){},
                rules: {
                    x10_label_input: {
                        required: true,
                        maxlength: 50
                    },
                    x10_address_input: {
                        required:true,
                        maxlength: 50
                    },
                    x10_command_input: {
                        required:true,
                        maxlength: 50
                    }
                },
                messages:{
                    x10_label_input: {
                        required: "Please input a label",
                        maxlength: "Please input a label no more than 50 charactors"
                    },
                    x10_address_input: {
                        required: "Please input a address",
                        maxlength: "Please input a address no more than 50 charactors"
                    },
                    x10_command_input: {
                        required: "Please input a command",
                        maxlength: "Please input a command no more than 50 charactors"
                    }
                }
            });
	        if ($("#x10_form").valid()) {
	            var x10 = new X10();
	            x10.id = global.BUTTONID++;
	            x10.label = label.val();
	            x10.address = address.val();
	            x10.command = command.val();

	            var x10View = new X10View(x10);
	
				x10.addDeleteListener(x10View);
				x10.addUpdateListener(x10View);
				
				var btn = x10View.getElement();
				makeBtnDraggable(btn);
		        btn.inspectable();
		
	            $("#create_x10_dialog").closeModalForm();
	        }
	    }
	};
}();