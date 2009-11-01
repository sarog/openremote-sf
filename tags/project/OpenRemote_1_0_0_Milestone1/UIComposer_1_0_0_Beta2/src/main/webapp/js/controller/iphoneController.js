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
var IPhoneController = function() {
	function IPhoneController () {
		// body...
	}
	//private method
    /**
     * Make the table cell droppable,Only accept 'blue_btn' and 'iphone_btn'.
     */
	function makeTableCellDroppable() {
	    $("#dropable_table td").droppable({
	        hoverClass: 'hiLight',
	        accept: function(draggable) {
	            if (draggable.hasClass("blue_btn") || draggable.hasClass("iphone_btn")) {
	                return true;
	            }
	            return false;
	        },
	        drop: function(event, ui) {
	            draggableDroped(ui.draggable,$(this));
	        }
	    });
	}

    /**
     * Invoked when the draggable dropped.
     * @param draggable draggable item
     * @param droppable droppable item
     */
	function draggableDroped (draggable,droppable) {
		if ($(this).find(".iphone_btn").length > 0) {
            draggable.draggable('option', 'revert', true);
        } else {
            if (draggable.hasClass("command_btn")) {
                if (draggable.attr("eventId") === undefined) {
                    draggable.data("model",Infrared.getInfraredModelWithDraggable(draggable));
                }
            }
			var iphoneBtn;
			if (draggable.data("model").className != "IphoneBtn")  {
				iphoneBtn = new IphoneBtn();
				iphoneBtn.id = BUTTONID++;
				iphoneBtn.oModel = draggable.data("model");	
			} else {
				iphoneBtn = draggable.data("model");
			}
			 
			IPhoneController.createIphoneBtn(iphoneBtn,droppable);
            
            if (draggable.hasClass(".iphone_btn")) {
                draggable.hide("fast",
                function() {
                    draggable.remove();
                });
            }
        }
	}


    /**
     * Make iphone panel button draggable.
     * @param items buttons which you want to make it draggable
     */
	function makeIphoneBtnDraggable(items) {
	    var btns;
	    if (items === undefined) {
	        btns = $(".iphone_btn");
	    } else {
	        btns = $(items);
	    }

	    btns.draggable({
	        cursor: 'hand',
	        opacity: 0.75,
	        start: function(event, ui) {
	            $(this).draggable('option', 'revert', false);
	        },
	        containment: $("#dropable_table")
	    });
	}

    /**
     * Invoked when the button have selected.
     * @param btn button which user selected
     */
	function selectIphoneBtn(btn) {
	    $(".iphone_btn").removeClass("selected");
	    $(".iphone_btn .delete_icon").remove();
	    btn.addClass("selected");
	}
	
	//static method
	IPhoneController.init = function (){
		makeTableCellDroppable();
	};

    /**
     * Create iphone button and add it into page.
     * @param iphoneBtn iphoneBtn model
     * @param tableCell table cell which you want to add into
     */
	IPhoneController.createIphoneBtn = function (iphoneBtn,tableCell) {
		var btn = HTMLBuilder.iphoneBtnBuilder(iphoneBtn);
        btn.click(function() {
        	selectIphoneBtn(btn);
			HTMLBuilder.iphoneBtnDeleteIconBuilder().appendTo(this);
        });
        btn.appendTo($(tableCell));
        makeIphoneBtnDraggable(btn);
	};
	
	return IPhoneController;
}();