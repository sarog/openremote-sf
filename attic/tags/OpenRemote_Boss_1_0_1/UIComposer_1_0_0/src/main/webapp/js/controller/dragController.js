/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
/**
 * Make button draggable.
 *
 * @param items button which you want to draggable
 */
function makeBtnDraggable(items) {
    var btns;
    if (items === undefined) {
        btns = $(".blue_btn");
    } else {
        btns = items;
    }
    var helper = HTMLBuilder.iphoneBtnHelperBuilder("");
    btns.draggable({
        zIndex: 2700,
        cursor: 'move',
        start: function(event, ui) {
            $(this).draggable('option', 'revert', false);
        },
        drag: function(event, ui) {
            $("#dropable_table td.hiLight").removeClass("hiLight");
            var cell = findTableCellByCoordinate(ui.offset.top, ui.offset.left);
            $(cell).addClass("hiLight");
        },
        stop: function(event, ui) {
            $("#dropable_table td.hiLight").removeClass("hiLight");
        },
		helper: function(event){
			var label = "";
			if ($(this).data("model") === undefined) {
				label = event.currentTarget.firstChild.data;
			} else {
				label = $(this).data("model").label;
			}
			
			helper.interceptStr({
                text: label,
                max: 14,
                title: false
            });
			return helper;
		},
		cursorAt: { left: 10,top : 10 } 
    });
}

function findTableCellByCoordinate(top, left) {
    var cell;
    $("#dropable_table td").each(function() {
        if ($.isCoordinateInArea(top+1, left+1, $(this))) {
            cell = $(this);
            return false;
        }
    });
    return cell;
}




