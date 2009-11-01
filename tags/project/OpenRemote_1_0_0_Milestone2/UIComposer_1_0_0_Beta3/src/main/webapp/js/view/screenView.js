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

ScreenView = function() {
    function ScreenView(screen) {
        }

    ScreenView.cellHeight = -1;
    ScreenView.cellWidth = -1;
    // "Notice":should clean up when delete infrared button from iphone panel.
    // "Notice":should clean up when delete iphone button.
    ScreenView.btnInArea = [];


    ScreenView.screenPanelTemplate = "template/_screenPanel.ejs";

    ScreenView.updateView = function(screen) {

        ScreenView.cellHeight = Math.round(($("#dropable_table_container").height() + 2) / screen.row);
        ScreenView.cellWidth = Math.round(($("#dropable_table_container").width() + 2) / screen.col);

        $("#iphoneBtn_container .iphone_btn").remove();

		EJSHelper.updateView(ScreenView.screenPanelTemplate,'dropable_table_container',{screen: screen});
		
		// reset draggable style
		makeBtnDraggable($(".iphone_element"));

        // init ScreenView.btnInArea [x][y]
        ScreenView.btnInArea = [];
        for (var i = screen.col - 1; i >= 0; i--) {
            ScreenView.btnInArea[i] = new Array();
            for (var j = screen.row - 1; j >= 0; j--) {
                ScreenView.btnInArea[i][j] = false;
            };
        };

        IPhoneController.init();
        for (var index in screen.buttons) {
            var button = screen.buttons[index];
            IPhoneController.createIphoneBtn(button);
            button.fillArea();
        }
    };

    ScreenView.updateInspeactView = function() {

        };

    ScreenView.getSelectedScreenId = function() {
        return parseInt($("#screen_select").find("option:selected").val());
    };
    ScreenView.getSelectedScreenText = function() {
        return $("#screen_select").find("option:selected").text();
    };
    ScreenView.addScreenSelect = function(screen) {
        if (ScreenView.getSelectedScreenId() == 0) {
            $("#screen_select").find("option:selected").remove();
        }
        var opt = new Option(screen.name, screen.id);
        $("#screen_select")[0].options.add(opt);
    };

    ScreenView.setLastOptionSelected = function() {
        $("#screen_select").find("option:last")[0].selected = true;
    };


    /**
     * Find the table cell according to x and y.
     * @param x
     * @param y
     */
    ScreenView.findCell = function(x, y) {
        var tr = $("#dropable_table").find("tr")[y];
        var td = $(tr).find("td")[x];
        return td;
    };


    return ScreenView;
} ();