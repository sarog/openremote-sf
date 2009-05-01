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
var TabController = function() {
    function TabController() {
        //constractor 	
    }

    // private method
    /**
     * Show create knx button dialog.
     */
    function showCreateKNXDialog() {
      
        $("#create_KNX_dialog").showModalForm("Create KNX", {
            'Create': confirmCreateKNX
        });
		$("#create_KNX_dialog").enterKeyPressed(confirmCreateKNX);
    }

    /**
     * Invoked when user confirm create knx button.
     */
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

    /**
     * Show create x10 button dialog.
     */
    function showCreateX10Dialog() {
        $("#create_x10_dialog").showModalForm("Create X10", {
            'Create': confirmCreateX10
        });
		$("#create_x10_dialog").enterKeyPressed(confirmCreateX10);
    }

    /**
     * Invoked when user confirm create x10 button.
     */
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

    /**
     * Show select vendor model dialog.
     */
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

    /**
     * Create kxn button and add it into page.
     * @param knx knx model
     */
    TabController.createKNX = function(knx) {
        var btn = HTMLBuilder.KNXBtnBuilder(knx);
        btn.prependTo($("#knx_tab .item_container"));
        makeBtnDraggable(btn);
    };

    /**
     * Create x10 button and add it into page.
     * @param x10 x10 model
     */
    TabController.createX10 = function(x10) {
        var btn = HTMLBuilder.X10BtnBuilder(x10);
        btn.prependTo($("#x10_tab .item_container"));
        makeBtnDraggable(btn);
    };

    return TabController;
}();