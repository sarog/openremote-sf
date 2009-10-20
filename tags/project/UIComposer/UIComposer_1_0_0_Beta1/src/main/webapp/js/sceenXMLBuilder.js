function postData() {
    sendButtons();
}

function sendButtons() {
    var buttonIdNum = 1;
    var activity = new Object();
    activity.name = "activity1";
	activity.id = 1;

    var screen = new Object();
    screen.name = "screen1";
    screen.row = 6;
    screen.col = 4;
	screen.id = 1;

    //for controller part
    var c_buttons;
    if ($("#dropable_table td .iphone_btn").length != 0) {
        var buttonArray = new Array();
        //for controller part
        var c_buttonArray = new Array();

        $("#dropable_table tr").each(function(i) {
            $(this).find("td").each(function(j) {
                if ($(this).find(".iphone_btn").length == 1) {
                    var btn = $(this).find(".iphone_btn");
                    var button = new Object();
                    //for controller part
                    var c_button = new Object();

                    button.label = btn.attr("title");
                    button.y = i;
                    button.x = j;
                    button.width = 1;
                    button.height = 1;
                    button.id = parseInt(buttonIdNum);
                    c_button.id = parseInt(buttonIdNum++);

                    //for controller part
                    if (btn.hasClass("macro_btn")) {
                        c_button.event = new Array();
                        $("#macro .macro_btn_defination").find(".macro_btn[macroId='" + btn.attr("macroId") + "']").parent().find("li").each(function() {
                            c_button.event.push(parseInt($(this).attr("eventId")));
                        });
                    } else {
                        c_button.event = parseInt(btn.attr("eventId"));
                    }
                    c_buttonArray.push(c_button);

                    buttonArray.push(button);
                }
            });
        });
        c_buttons = {
            button: c_buttonArray
        };
    } else {
        c_buttons = [""];
    }

    screen.button = buttonArray;

    var screenAttr = new Array();

    screenAttr.push(screen);

    activity.screen = screenAttr;


    var data = JSON.stringify({
        activity: activity
    });


    $.post("saveScreen.htm", {
        data: data
    },
    function(result) {
        $("#iphone_result .json").html("");
        $("#iphone_result .xml").html("");
        $("#iphone_result .json").html(data);
        $("#iphone_result .xml").text(result);
        $("#iphone_result").show("slow");
    });

    sendEvents(c_buttons);
}

function sendEvents(c_buttons) {

    var openremote = new Object();

    openremote.buttons = c_buttons;

    openremote.events = new Object();


    if ($("#knx_container").find(".knx_btn").length != 0) {
        var knxEvents = new Array();
        $("#knx_container").find(".knx_btn").each(function() {
            var knxEvent = new Object();
            knxEvent.id = $(this).attr("eventId");
            knxEvent.label = $(this).attr("title");
            knxEvent.groupAddress = $(this).data("groupAddress");
            knxEvents.push(knxEvent);
        });
        openremote.events.knxEvents = {
            knxEvent: knxEvents
        };
    } else {
        openremote.events.knxEvents = [""];
    }



    if ($("#x10_container").find(".x10_btn").length != 0) {
        var x10Events = new Array();
        $("#x10_container").find(".x10_btn").each(function() {
            var x10Event = new Object();
            x10Event.id = $(this).attr("eventId");
            x10Event.address = $(this).data("address");
            x10Event.command = $(this).data("command");
            x10Event.label = $(this).attr("title");
            x10Events.push(x10Event);
        });
        openremote.events.x10Events = {
            x10Event: x10Events
        };
    } else {
        openremote.events.x10Events = [""];
    }

    if ($("#command_container .command_btn[eventId]").length != 0) {
        var irEvents = new Array();
        $("#command_container .command_btn[eventId]").each(function() {
            var eventId = $(this).attr("eventId");
            var irEvent = new Object();
            irEvent.id = eventId;
            irEvent.name = "MP8640";
            irEvent.label = $(this).attr("title");
            irEvent.command = $(this).attr("title");
            irEvents.push(irEvent);
        });
        openremote.events.irEvents = {
            irEvent: irEvents
        };
    } else {
        openremote.events.irEvents = [""];
    }



    var data = JSON.stringify({
        openremote: openremote
    });


    $.post("saveEvents.htm", {
        data: data
    },
    function(result) {
        $("#controller_result .json").html("");
        $("#controller_result .xml").html("");
        $("#controller_result .json").html(data);
        $("#controller_result .xml").text(result);
        $("#controller_result").show("slow");
    });
}