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

// TODO Need refactor, every screen have it's own controller and view.
ScreenView = function() {
	function ScreenView (screen) {
	}
	
	ScreenView.screenPanelTemplate = "template/_screenPanel.ejs";	
	
	ScreenView.updateView = function (screen) {
		$("#iphoneBtn_container .iphone_btn").remove();
		new EJS({url:ScreenView.screenPanelTemplate}).update('dropable_table_container',{screen:screen});
		// init btnInArea [x][y]
		btnInArea = new Array();
		for (var i = screen.col - 1; i >= 0; i--){
			btnInArea[i] = new Array();
			for (var j = screen.row - 1; j >= 0; j--){
				btnInArea[i][j] = false;
			};
		};
		
		IPhoneController.init();
		for (var index  in screen.buttons) {
			var button = screen.buttons[index];
			IPhoneController.createIphoneBtn(button);
			button.fillArea();
		}
	};
	
	ScreenView.updateInspeactView = function() {
		
	}
	
	ScreenView.getSelectedScreenId = function() {
		return parseInt($("#screen_select").find("option:selected").val());
	};
	ScreenView.getSelectedScreenText = function() {
		return $("#screen_select").find("option:selected").text();		
	};
	ScreenView.addScreenSelect = function (screen) {
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
    ScreenView.findCell =function (x, y) {
        var tr = $("#dropable_table").find("tr")[y];
        var td = $(tr).find("td")[x];
        return td;
    };
	
	
	return ScreenView;
}();