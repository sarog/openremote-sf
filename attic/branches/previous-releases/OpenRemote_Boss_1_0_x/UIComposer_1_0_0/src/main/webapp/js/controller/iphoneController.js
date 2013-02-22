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


        // If we drag a button which is not iphone button, we need to create iphoneBtn model.
        if (draggable.data("model").className != "IphoneBtn") {
            iphoneBtn = new IphoneBtn();

            iphoneBtn.id = global.BUTTONID++;
            iphoneBtn.oModel = draggable.data("model");
            iphoneBtn.label = draggable.data("model").label;
        }

        var height = draggable.height();
        var width = draggable.width();

        iphoneBtn.x = x;
        iphoneBtn.y = y;

        if (draggable.hasClass(".iphone_btn")) {
            iphoneBtn.updateModel();
        } else {
            iphoneBtn.height = 1;
            iphoneBtn.width = 1;
            var btn = IPhoneController.createIphoneBtn(iphoneBtn);
            if (InspectView.getModel() != null && InspectView.getModel().getElementId !== undefined && iphoneBtn.getElementId() == InspectView.getModel().getElementId()) {
                btn.addClass("highlightInspected");
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
            start: function(event, ui) {
                $(this).draggable('option', 'revert', false);
                $(this).data("model").clearArea();
                $(this).addClass("highlightInspected");
            },
            stop: function(event, ui) {
				if ($(this).hasClass("highlightInspected")) {
					InspectView.hideView();
				}
				$("#tooltip").hide();
				
				
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
            zIndex: 2700
        });
    }

    function resetIphoneBtnBack(iphoneBtnElement) {
        var iphoneBtn = $(iphoneBtnElement).data("model");
		if ($(iphoneBtnElement).hasClass("highlightInspected")) {
			InspectView.hideView();
		}
		iphoneBtn.updateModel();
		
        // var view = $(iphoneBtnElement).data("view");
        // 
        //         iphoneBtn.removeUpdateListener(view);
        //         iphoneBtn.removeDeleteListener(view);
        // 		
        //         view.deleteView();
        //         view = null;
        //         
        // 
        //         $("#dropable_table td.hiLight").removeClass("hiLight");
        //         var btn = IPhoneController.createIphoneBtn(iphoneBtn);

        // if (InspectView.getModel() != null && iphoneBtn.getElementId() == InspectView.getModel().getElementId()) {
        //             btn.addClass("highlightInspected");
        //         }

		$("#dropable_table td.hiLight").removeClass("hiLight");
    }

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

				btns.find("td.middle").height("100%");
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
                iphoneBtn.updateModel();
            },
            helper: false
        });
        //Hack the JQuery Draggable,use can't draggable the button sidelong
        btns.find("div.ui-resizable-handle.ui-resizable-se").remove();
    }

    function canDrop(x, y, iphoneBtn, screen) {
        for (var tmpX = x; tmpX < x + iphoneBtn.width; tmpX++) {
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
            grid: [ScreenView.cellHeight, ScreenView.cellWidth],
            maxHeight: height,
            maxWidth: width
        });
    }


    //static method
    IPhoneController.init = function() {
        makeTableCellDroppable();
    };


    IPhoneController.afterConfirmChangeIcon = function(icon) {
        $("#inspect_iphoneBtn_icon").attr("src", icon);
    };

    /**
     * Create a iphone button on screen panel
     * @satic
     * @param {Object} iphoneBtn iphoneBtn model
     * @returns Created iphone button element
     * @type JQuery DOM
     */
    IPhoneController.createIphoneBtn = function(iphoneBtn) {

        var iphoneBtnView = new IphoneBtnView(iphoneBtn);

        iphoneBtn.fillArea();

        iphoneBtn.oModel.addDeleteListener(iphoneBtnView);

        iphoneBtn.addDeleteListener(iphoneBtnView);
        iphoneBtn.addUpdateListener(iphoneBtnView);


        var btn = iphoneBtnView.getElement();

        btn.data("view", iphoneBtnView);

        makeIphoneBtnDraggable(btn);
        makeIphoneBtnResizable(btn);

        var left = Math.round($("#iphone_backgroud").offset().left + $("#iphone_backgroud").width());
        btn.inspectable({
            after: function() {
                $("#inspect_iphoneBtn_icon").unbind().click(function() {
                    ChangeIconViewController.showChangeIconForm();
                });
            },
            left: left
        });

        btn.tooltip({
            bodyHandler: function() {
                return $(EJSHelper.render("template/_inspectTooltip.ejs", $(this).data("model")));
            },
            showURL: false,
			trace:true,
            delay: 1
        });
        return btn;
    };





    return IPhoneController;
} ();