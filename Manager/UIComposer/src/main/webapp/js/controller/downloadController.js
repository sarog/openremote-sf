var DownloadController = function() {
	function DownloadController () {
		// body...
	}
	
	var assembledSectionIds;
	//private method
	function download() {	
		
		var btnModelHash = {};
		
	    var activity = new Object();
	    activity.name = "activity1";
		activity.id = 1;

	    var screen = new Object();
	    screen.name = "screen1";
	    screen.row = 6;
	    screen.col = 4;
		screen.id = 1;

        var buttonArray = new Array();

        $("#dropable_table tr").each(function(i) {
            $(this).find("td").each(function(j) {
                if ($(this).find(".iphone_btn").length == 1) {
                    var btnElement = $(this).find(".iphone_btn");
					var model = btnElement.data("model");
					
                    var button = new Object();

                    button.label = model.label;
                    button.y = i;
                    button.x = j;
                    button.width = 1;
                    button.height = 1;
                    button.id = BUTTONID++;
						
                    buttonArray.push(button);
					btnModelHash[button.id] = model;
                }
            });
        });
	   
	    screen.button = buttonArray;

	    var screenAttr = new Array();

	    screenAttr.push(screen);

	    activity.screen = screenAttr;


	    var iphoneXml = JSON.stringify({
	        activity: activity
	    });

		var controllerXml = getControllerxml(btnModelHash);
		var ids;
		
	    $.post("download.htm", {
	        iphone: iphoneXml,
			controller: controllerXml,
			restUrl:RESTAPIUrl + ".conf",
			ids:assembledSectionIds
		},
	    function(result) {
	       window.location = result;
	    });
	}

	function getControllerxml(btnModelHash) {	   
		var openremote = new Object(); 
	
		openremote.buttons = {button:parseButtons(btnModelHash)};
		
	    openremote.events = new Object();

		var knxEvents = parseKNX();
	    if (knxEvents.length != 0) {
	        openremote.events.knxEvents = {
	            knxEvent: knxEvents
	        };
	    } 

		var x10Events = parseX10();
	    if (x10Events.length != 0) {
	        openremote.events.x10Events = {
	            x10Event: x10Events
	        };
	    }
	
		var irEvents = parseInfared();
	    if (irEvents.length != 0) {
	        openremote.events.irEvents = {
	            irEvent: irEvents
	        };
	    } 

	    var data = JSON.stringify({
	        openremote: openremote
	    });

	  	return data;
	}
	
	function parseButtons (btnModelHash) {
		var buttons = new Array();
		for (var id in btnModelHash) {
			var b = new Object();
			var model = btnModelHash[id];
			b.id = parseInt(id);
			var event;
			
			if (model instanceof Macro) {
				event = new Array();
				$("#"+model.elementId()).next("ul").find("li").each(function() {
					event.push(parseInt($(this).data("model").id));
				});
			} else {
				event = model.id;
			} 
			b.event = event;
			buttons.push(b);
		}
		return buttons;
	}
	
	function parseInfared () {
		var sectionIds = new Array();
		var irEvents = new Array();
		for (var codeId in InfraredCollection) {
			var model = InfraredCollection[codeId];
			var irEvent = new Object();
            irEvent.id = model.id;
            irEvent.name = model.name;
            irEvent.command = model.command;
			irEvent.sectionId = model.sectionId;
            irEvents.push(irEvent);

			if($.inArray(parseInt(model.sectionId),sectionIds) == -1) {
				sectionIds.push(parseInt(model.sectionId));
			}
		}
		assembledSectionIds = sectionIds.join(",");
		return irEvents;
	}
	
	function parseKNX () {
	    var knxEvents = new Array();
        $("#knx_container").find(".knx_btn").each(function() {
            var knxEvent = new Object();
			var model = $(this).data("model");
            knxEvent.id = model.id;
            knxEvent.label = model.label;
            knxEvent.groupAddress = model.groupAddress;
            knxEvents.push(knxEvent);
        });
        return knxEvents;
	}
	
	
	function parseX10 () {
        var x10Events = new Array();
        $("#x10_container").find(".x10_btn").each(function() {
            var x10Event = new Object();
			var model = $(this).data("model");
            x10Event.id =model.id;
            x10Event.address = model.address;
            x10Event.command = model.command;
            x10Event.label = model.label;
            x10Events.push(x10Event);
        });
   		return x10Events;
	}
	
	//static method
	DownloadController.init = function() {
		$("#saveBtn").unbind().bind("click", download);
		$("#saveBtn").hover(function() {
			$(this).addClass("ui-state-hover");
		},function() {
			$(this).removeClass("ui-state-hover");
		});
	};
	return DownloadController;
}();