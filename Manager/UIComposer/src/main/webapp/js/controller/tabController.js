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
            buttons:{
				'Create': KNXController.confirmCreate
			},
			confirmButtonName:'Create'
        });
    }

    /**
     * Show create x10 button dialog.
     */
    function showCreateX10Dialog() {
        $("#create_x10_dialog").showModalForm("Create X10", {
			buttons:{
				'Create': X10Controller.confirmCreate
			},
			confirmButtonName:'Create'
        });
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

    TabController.updateKnx = function (knx) {
        var label = $.trim($("#inspect_knx_label").val());
        var groupAddress = $.trim($("#inspect_knx_groupAddress").val());
        knx.label = label;
        knx.groupAddress = groupAddress;

        var btn = $("#"+knx.getElementId());
		if (label.length > 5) {
            label = label.substr(0, 5) + "...";
        }
		btn.attr("title",knx.label);
        btn.text(label);
        btn.data("model",knx);
    };

	TabController.updateX10 = function (x10) {
        var label = $.trim($("#inspect_x10_label").val());
        var address = $.trim($("#inspect_x10_address").val());
		var command = $.trim($("#inspect_x10_command").val());
        x10.label = label;
        x10.address = address;
        x10.command = command;

        var btn = $("#"+x10.getElementId());
		if (label.length > 5) {
            label = label.substr(0, 5) + "...";
        }
		btn.attr("title",x10.label);
        btn.text(label);
        btn.data("model",x10);
    };

    return TabController;
}();