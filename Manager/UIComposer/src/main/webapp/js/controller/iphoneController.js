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
    function IPhoneController() {
        // body...
        }

    //private variable
    //private method
    /**
     * Make the table cell droppable,Only accept 'blue_btn' and 'iphone_btn'.
     */
    function makeTableCellDroppable() {
        $("#dropable_table").droppable({
            accept: function(draggable) {
                if (draggable.hasClass("iphone_element") || draggable.hasClass("iphone_btn")) {
                    return true;
                }
                return false;
            },
            drop: function(event, ui) {
                draggableDroped(ui.draggable, $(this), ui.helper);
            },
			tolerance: 'pointer'
        });
    }

	/**
	 * Invoked when the draggable dropped.
	 * @private
	 * @param {JQuery Object} draggable draggable item
	 * @param {JQuery Object} droppable droppable item
	 * @param {JQuery Object} draggable helper
	 */
	
    function draggableDroped(draggable, droppable, helper) {
        var cell = $("#dropable_table td.hiLight");

        if (cell.length == 0) {
            draggable.draggable('option', 'revert', true);
            return;
        }

        var x = parseInt(cell.attr("x"));
        var y = parseInt(cell.attr("y"));

        var iphoneBtn;
        if (draggable.hasClass(".iphone_btn")) {
            iphoneBtn = draggable.data("model");
        }
		//To avoid the case that this area have already had a button.
        if (ScreenView.btnInArea[x][y]) {
            draggable.draggable('option', 'revert', true);
			// if this is a iphone button, we need to reFill the area.
            if (draggable.hasClass(".iphone_btn")) {
                iphoneBtn.fillArea();
            }
            return;
        }

        var screen = ScreenViewController.getCurrentScreen();

        if (draggable.hasClass(".iphone_btn")) {
			// We are not allow drag a button on top of other button.
            if (!canDrop(x, y, iphoneBtn, screen)) {
                draggable.draggable('option', 'revert', true);
                iphoneBtn.fillArea();
                $("#dropable_table td.hiLight").removeClass("hiLight");
                return;
            }
        }
		
		
        if (draggable.hasClass("command_btn")) {
            draggable.data("model", Infrared.getInfraredModelWithDraggable(draggable));
        }
		
		// If we drag a button which is not iphone button, we need to create iphoneBtn model.
        if (draggable.data("model").className != "IphoneBtn") {
            iphoneBtn = new IphoneBtn();

            iphoneBtn.id = global.BUTTONID++;
            iphoneBtn.oModel = draggable.data("model");
			iphoneBtn.label = draggable.data("model").label;
        }

        var height = draggable.height();
        var width = draggable.width();
		
        if (draggable.hasClass(".iphone_btn")) {
			// Because of JQuery, we can't delete draggable immediately.
            draggable.hide(200,function() {
				draggable.remove();
			});
            iphoneBtn.clearArea();
        } else {
            iphoneBtn.height = 1;
            iphoneBtn.width = 1;

        }
        iphoneBtn.x = x;
        iphoneBtn.y = y;
		//fill the area.
        iphoneBtn.fillArea();

		var btn = IPhoneController.createIphoneBtn(iphoneBtn);       
		
		if (InspectView.getModel()!= null && iphoneBtn.elementId() == InspectView.getModel().elementId()) {
			btn.addClass("highlightInspected");
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
            start: function(event, ui) {
                $(this).draggable('option', 'revert', false);
                $(this).data("model").clearArea();
				$(this).addClass("highlightInspected");
            },
            stop: function(event, ui) {
				$(this).removeClass("highlightInspected");
	
                var top = ui.offset.top;
                var left = ui.offset.left;
				
				// if dragggable draged out of the rang, the only thing we can do is remove it and create a new one at that position
				// this is all because we not use helper in iphone button draggable, so JQuery will not help us move the button back when drag fail.
                if (!$.isCoordinateInArea(top, left, $("#dropable_table"))) {
					resetIphoneBtnBack(this);
                    return;
                } 
                var cell = $("#dropable_table td.hiLight");
                var x = parseInt(cell.attr("x"));
                var y = parseInt(cell.attr("y"));
                var screen = ScreenViewController.getCurrentScreen();
                var iphoneBtn = $(this).data("model");
				
				//If the button area out of the rang, we move the button back.
                if (((x + iphoneBtn.width) > screen.col) || ((y + iphoneBtn.height) > screen.row) || cell.length == 0) {
                    resetIphoneBtnBack(this);
                    return;
                }
				
				//button in this area but cursor not in this area, so dropped event can't invoke, we must handle it here.
				if (!$.isCoordinateInArea(event.pageY, event.pageX, $("#dropable_table"))) {
					iphoneBtn.x = x;
					iphoneBtn.y = y;
					resetIphoneBtnBack(this);
                    return;
				}
                $("#dropable_table td.hiLight").removeClass("hiLight");

				
            },
            drag: function(event, ui) {
                $("#dropable_table td.hiLight").removeClass("hiLight");
                var cell = findTableCellByCoordinate(ui.offset.top, ui.offset.left);
                $(cell).addClass("hiLight");
            },
            cursorAt: {
                left: 25,
                top: 25
            },
			zIndex:2700
        });
    }

	function resetIphoneBtnBack (iphoneBtnElement) {
		var iphoneBtn = $(iphoneBtnElement).data("model");
		var btn = IPhoneController.createIphoneBtn(iphoneBtn);
		
		if (InspectView.getModel() != null && iphoneBtn.elementId() == InspectView.getModel().elementId()) {
			btn.addClass("highlightInspected");
		}
        $(iphoneBtnElement).remove();
        $("#dropable_table td.hiLight").removeClass("hiLight");
	}
	
	/**
	 * Describe what this method does
	 * @private
	 * @param {String|Object|Array|Boolean|Number} paramName Describe this parameter
	 * @returns Describe what it returns
	 * @type String|Object|Array|Boolean|Number
	 */
	
    function makeIphoneBtnResizable(items) {
        var btns;
        if (items === undefined) {
            btns = $(".iphone_btn");
        } else {
            btns = $(items);
        }
        btns.resizable({
            grid: [ScreenView.cellWidth, ScreenView.cellHeight],
            start: function(event, ui) {

                var iphoneBtn = $(this).data("model");


                var screen = ScreenViewController.getCurrentScreen();


                var maxX = findMaxXWhenResize(iphoneBtn, screen);
                var maxY = findMaxYWhenResize(iphoneBtn, screen);

                btns.resizable('option', 'maxHeight', (maxY - iphoneBtn.y + 1) * ScreenView.cellHeight);
                btns.resizable('option', 'maxWidth', (maxX - iphoneBtn.x + 1) * ScreenView.cellWidth);

            },
            stop: function(event, ui) {
                var iphoneBtn = $(this).data("model");
                iphoneBtn.clearArea();


                if ($(this).width() != ui.originalSize.width) {
                    var width = Math.round(($(this).width() - ui.originalSize.width) / ScreenView.cellWidth);
                    iphoneBtn.width = iphoneBtn.width + width;
                }
                if ($(this).height() != ui.originalSize.height) {
                    var height = Math.round(($(this).height() - ui.originalSize.height) / ScreenView.cellHeight);
                    iphoneBtn.height = iphoneBtn.height + height;
                }
				
                iphoneBtn.fillArea();
				makeIphoneBtnDraggable($(this));
            },
			helper: false
        });
    }

    function canDrop(x, y, iphoneBtn, screen) {
        for (var tmpX = x;tmpX < x + iphoneBtn.width; tmpX++) {
            for (var tmpY = y; tmpY < y + iphoneBtn.height; tmpY++) {
				if (tmpX > screen.col - 1 || tmpY > screen.row - 1) {
					return false;
				}
                if (ScreenView.btnInArea[tmpX][tmpY]) {
                    return false;
                }
            }
        }
		
        return true;
    }



    function findMaxXWhenResize(iphoneBtn, screen) {
        for (var maxX = iphoneBtn.x; maxX < screen.col - 1; maxX++) {
            for (var tmpY = iphoneBtn.y; ((tmpY < screen.row) && (tmpY < iphoneBtn.y + iphoneBtn.height)); tmpY++) {
                if ((ScreenView.btnInArea[maxX + 1][tmpY]) && ((maxX + 1) > (iphoneBtn.x + iphoneBtn.width - 1))) {
                    //second condition is exclude button itself
                    return maxX;
                }
            }
        }
        return maxX;
    }


    function findMaxYWhenResize(iphoneBtn, screen) {
        for (var maxY = iphoneBtn.y; maxY < screen.row - 1; maxY++) {
            for (var tmpX = iphoneBtn.x; ((tmpX < screen.col) && (tmpX < iphoneBtn.x + iphoneBtn.width)); tmpX++) {
                if ((ScreenView.btnInArea[tmpX][maxY + 1] == true) && ((maxY + 1) > (iphoneBtn.y + iphoneBtn.height - 1))) {
                    //second condition is exclude button itself
                    return maxY;
                }
            }
        }
        return maxY;
    }


    function setIphoneBtnMaxHeightAndWidth(height, width) {
        var btns;
        if (items === undefined) {
            btns = $(".iphone_btn");
        } else {
            btns = $(items);
        }
        btns.resizable({
            grid:[ScreenView.cellHeight, ScreenView.cellWidth],
            maxHeight: height,
            maxWidth: width
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
    IPhoneController.init = function() {
        makeTableCellDroppable();
    };

     IPhoneController.afterShowInspect = function() {
        $("#inspect_change_icon").unbind().click(function(){
                    
        });
         
     };

    IPhoneController.afterConfirmChangeIcon = function(icon) {
        $("#inspect_iphoneBtn_icon").attr("src",icon);    
    };

    /**
     * Create iphone button and add it into page.
     * @param iphoneBtn iphoneBtn model
     * @param tableCell table cell which you want to add into
     */
    IPhoneController.createIphoneBtn = function(iphoneBtn) {
		iphoneBtn.fillArea();
        var tableCell = ScreenView.findCell(iphoneBtn.x, iphoneBtn.y);
		
		var btn = HTMLBuilder.iphoneBtnBuilder(iphoneBtn);
		
        btn.css("top", $(tableCell).offset().top);
        btn.css("left", $(tableCell).offset().left);
        btn.css("height", $(tableCell).height() * iphoneBtn.height + iphoneBtn.height - 1);
        btn.css("width", $(tableCell).width() * iphoneBtn.width + iphoneBtn.width - 1);
        btn.appendTo($("#iphoneBtn_container"));

        makeIphoneBtnDraggable(btn);
        makeIphoneBtnResizable(btn);

		btn.unbind().click(function() {
			$(".highlightInspected").removeClass("highlightInspected");
			$(this).addClass("highlightInspected");
			InspectViewController.updateView($(this).data("model"));
		});

		//Hack the JQuery Draggable,use can't draggable the button sidelong   
		btn.find("div.ui-resizable-handle.ui-resizable-se").remove();
		return btn;
    };
	
	IPhoneController.updateIphoneBtn = function (iphoneBtn) {
        // get Label value from inspect window
        var label  = $.trim($("#inspect_iphoneBtn_label").val());
        var icon  = $("#inspect_iphoneBtn_icon").attr("src");
		iphoneBtn.label = label;		

        //update view
        var btn = $("#"+iphoneBtn.elementId());
		if (label.length > 5) {
            label = label.substr(0, 5) + "<br/>...";
        }

		if (icon != constant.DEFAULT_IPHONE_BTN_ICON) {
			iphoneBtn.icon = icon;
			btn.find("table").removeClass("iPhone_btn_cont");
			btn.find("table .middle").html("<img src="+icon+">");
		}
		
		btn.find("span").html(label);
		btn.attr("title",iphoneBtn.label);
		
        //re-set model.
		btn.data("model",iphoneBtn);
	};	
	
	IPhoneController.afterShowInspect = function() {
		$("#inspect_change_icon").unbind().click(function() {
			ChangeIconViewController.showChangeIconForm();
		});
	};
	

    return IPhoneController;
} ();