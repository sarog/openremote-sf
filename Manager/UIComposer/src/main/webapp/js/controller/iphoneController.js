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
                if (typeof(draggable.attr("eventId")) == "undefined") {
                    draggable.data("model",Infrared.getInfraredModelWithDraggable(draggable));
                }
            }
            var btn = HTMLBuilder.iphoneBtnBuilder(draggable);

            btn.click(function() {
                selectIphoneBtn(btn);
                HTMLBuilder.iphoneBtnDeleteIconBuilder().appendTo(this);
            });
            btn.appendTo(droppable);
            makeIphoneBtnDraggable(btn);
            if (draggable.hasClass(".iphone_btn")) {
                draggable.hide("fast",
                function() {
                    droppable.remove();
                });
            }
        }
	}
	
	function makeIphoneBtnDraggable(items) {
	    var btns;
	    if (typeof(items) == 'undefined') {
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
	
	//static method
	IPhoneController.init = function (){
		makeTableCellDrappable();
	};
	
	
	return IPhoneController;
}();