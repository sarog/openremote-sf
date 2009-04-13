var IPhoneController = function() {
	function IPhoneController () {
		// body...
	}
	//private method
	function makeTableCellDrappable() {
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
	
	
	function selectIphoneBtn(btn) {
	    $(".iphone_btn").removeClass("selected");
	    $(".iphone_btn .delete_icon").remove();
	    btn.addClass("selected");
	}
	
	//static method
	IPhoneController.init = function (){
		makeTableCellDrappable();
	};
	
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