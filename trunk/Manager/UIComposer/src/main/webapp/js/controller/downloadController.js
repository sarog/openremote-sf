var DownloadController = function() {
    function DownloadController() {
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

        iteratorTableCell($("#dropable_table"),function(td, i, j) {
            if (td.find(".iphone_btn").length == 1) {
                var btnElement = td.find(".iphone_btn");
                var iphoneBtn = btnElement.data("model");

                var button = new Object();

                button.label = iphoneBtn.oModel.label;
                button.y = i;
                button.x = j;
                button.width = 1;
                button.height = 1;
                button.id = iphoneBtn.id;

                buttonArray.push(button);
                btnModelHash[button.id] = iphoneBtn;
            }
        });

		if (buttonArray.length != 0 ) {
			screen.button = buttonArray;
		}
        
        var screenAttr = new Array();

        screenAttr.push(screen);

        activity.screen = screenAttr;

        var iphoneXml = JSON.stringify({
            activity: activity
        });

        var controllerXml = getControllerxml(btnModelHash);	
		var panelDesc = getPanelDesc();
        var ids;

        $.post("download.htm", {
            iphone: iphoneXml,
            controller: controllerXml,
			panel:panelDesc,
            restUrl: RESTAPIUrl + ".conf",
            ids: assembledSectionIds
        },
        function(result) {
            window.location = result;
        });
    }

    function getControllerxml(btnModelHash) {
        var openremote = new Object();

        openremote.buttons = {
            button: parseButtons(btnModelHash)
        };

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

    function getPanelDesc() {
		var macroBtns = new Array();
		$("#macro .macro_btn_defination").each(function() {
			var model = $(this).find(".macro_btn").data("model");
			var btnModels = model.getSubModels();
			model.buttons = new Array();
			for (var index in btnModels) {
				model.buttons.push(btnModels[index]);
			}
			macroBtns.push(model);            
		});
		var iphoneBtns = new Array();
		iteratorTableCell($("#dropable_table"),function(td, i, j) {
			 if (td.find(".iphone_btn").length == 1) {
	                var btnElement = td.find(".iphone_btn");
	                var model = btnElement.data("model");
	                model.y = i;
	                model.x = j;
	                model.width = 1;
	                model.height = 1;
	                iphoneBtns.push(model);
	           }
		});
		var knxBtns = new Array();
		$("#knx_container").find(".knx_btn").each(function() {
            knxBtns.push($(this).data("model"));
        });
		var x10Btns = new Array();
		 $("#x10_container").find(".x10_btn").each(function() {
	          x10Btns.push($(this).data("model"));
	     });
		
		var panel = {
			iphoneBtns:iphoneBtns,
			knxBtns:knxBtns,
			x10Btns:x10Btns,
			macroBtns:macroBtns,
			maxId:BUTTONID
		};
		
		var data = JSON.stringify({
	     	panel: panel
	     });

	     return data;
    }

    function iteratorTableCell(table,block) {
        $(table).find("tr").each(function(i) {
            $(this).find("td").each(function(j) {
                block($(this), i, j);
            });
        });
    }

    function parseButtons(btnModelHash) {
        var buttons = new Array();
        for (var id in btnModelHash) {
            var b = new Object();
            var iphoneBtn = btnModelHash[id];
            b.id = parseInt(id);
            var event;

            if (iphoneBtn.oModel.className == "Macro") {
                event = new Array();
				var models = iphoneBtn.oModel.getSubModelsRecursively();
				for (var index in models)  {
					event.push(models[index].id);
                }
            } else {
                event = iphoneBtn.oModel.id;
            }
            b.event = event;
            buttons.push(b);
        }
        return buttons;
    }



    function parseInfared() {
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

            if ($.inArray(parseInt(model.sectionId), sectionIds) == -1) {
                sectionIds.push(parseInt(model.sectionId));
            }
        }
        assembledSectionIds = sectionIds.join(",");
        return irEvents;
    }

    function parseKNX() {
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


    function parseX10() {
        var x10Events = new Array();
        $("#x10_container").find(".x10_btn").each(function() {
            var x10Event = new Object();
            var model = $(this).data("model");
            x10Event.id = model.id;
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
        $("a.button").UIHover();
    };
    return DownloadController;
} ();