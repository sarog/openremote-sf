/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3.0 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/*
 * TODO
 *
 * @author <a href="mailto:">Allen Wei</a>
 * @author <a href="mailto:">Tomsky Wang</a>
 */
var TabController = function() {
    function TabController() {
        //constractor 	
    }

    /**
     * Show select vendor model dialog.
     */
    function selectCommand() {
        $("#command_navigation").dialog({
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
        $("#command_navigation").dialog("open");
    }


    // Static methods -----------------------------------------------------------------------------

    TabController.init = function() {
        $("#tabs").tabs();
        $("#create_knx_icon").unbind().bind("click", KNXController.showCreateKNXDialog);
        $("#create_x10_icon").unbind().bind("click", X10Controller.showCreateX10Dialog);
        $("#create_http_icon").unbind().bind("click", HTTPController.showCreateHTTPDialog);
        $("#create_tcp_icon").unbind().bind("click", TCPController.showCreateTCPDialog);
        $("#create_telnet_icon").unbind().bind("click", TelnetController.showCreateTelnetDialog);
        $("#select_command_icon").unbind().bind("click", selectCommand);
    };

    return TabController;
}();