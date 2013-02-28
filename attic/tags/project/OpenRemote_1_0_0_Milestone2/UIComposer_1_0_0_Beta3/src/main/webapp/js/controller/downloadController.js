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
var DownloadController = function() {
    function DownloadController() {
        // constructor
        }

    //private variable
    //To store selected section ids.
    var assembledSectionIds;
    // Store buttons already added. to keep button id is correct in controller part and iphone part.
    var btnModelHash = {};
    //private method
    /**
     * Parse Page and Send the request for download the current work.
     */
    function download() {
        // first store current screen
        ScreenViewController.storeCurrentScreen();

        var iphoneXml = generateIphoneXml();
        var controllerXml = generateControllerXml(btnModelHash);
        var panelDesc = generatePanelDesc();

        $.post("download.htm", {
            iphone: iphoneXml,
            controller: controllerXml,
            panel: panelDesc,
            restUrl: constant.RESTAPIUrl + "/lirc.conf",
            ids: assembledSectionIds
            //get it in parseInfared() function
        },
        function(result) {
            window.location = result;
        });

    }

    /*-----------  generate Iphone xml ------------------------------*/

    function generateIphoneXml() {
        //TODO It will have multi-activity, so we will refactor it latter.
        var activity = new Object();
        activity.name = "activity1";
        activity.id = 1;

        activity.screen = getStoredScreens();
		
		var iphoneXml = EJSHelper.render("template/_iphoneXML.ejs",{
            activity: activity
        });
       
        return iphoneXml;

    }

    function getStoredScreens() {
        var screenAttr = new Array();

        for (var id in global.screens) {
            screenAttr.push(global.screens[id]);
        }
        return screenAttr;
    }





    /*------------------ generate controller xml ------------------------ */

    /**
     * Compose Controller part JSON.
     * @param btnModelHash parsed iphone buttons
     * @returns Composed JSON string
     */
    function generateControllerXml(btnModelHash) {
        var openremote = new Object();

        openremote.buttons = {
            button: parseButtons(btnModelHash)
        };

        openremote.events = new Object();

        var knxEvents = parseKNX();
        openremote.events.knxEvents = {
            knxEvent: knxEvents
        };

        var x10Events = parseX10();
        openremote.events.x10Events = {
            x10Event: x10Events
        };

        var irEvents = parseInfared();
        openremote.events.irEvents = {
            irEvent: irEvents
        };
		
        var result = EJSHelper.render("template/_controllerXML.ejs",{
            openremote: openremote
        });
        return result;
    }


    /**
     * Parse Controller part iphone buttons.
     * @param btnModelHash parsed iphone buttons
     * @returns button array
     */
    function parseButtons(btnModelHash) {
        var buttons = new Array();
        for (var id in btnModelHash) {
            var b = new Object();
            var iphoneBtn = btnModelHash[id];
            b.id = parseInt(id);
            var event = new Array();

            if (iphoneBtn.oModel.className == "Macro") {
                var models = iphoneBtn.oModel.getSubModelsRecursively();
                for (var index in models) {
                    event.push(models[index].id);
					if (models[index].className == "Infrared") {
						global.InfraredCollection[models[index].codeId] = models[index];
					}
                }
            } else {
                event.push(iphoneBtn.oModel.id);
            }
            b.event = event;
            buttons.push(b);
        }
        return buttons;
    }

    /**
     * Get selected Infrared event and Gets section ids user selected.
     * @returns irEvent array.
     */
    function parseInfared() {
        var sectionIds = new Array();
        var irEvents = new Array();
        for (var codeId in global.InfraredCollection) {
            var model = global.InfraredCollection[codeId];
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

    /**
     * Get all KNX event user added.
     * @returns knxEvent array
     */
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

    /**
     * Get all X10 event user added.
     * @returns x10Event array
     */
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

    /*--------------------     generate irb file     --------------------------------------*/
    /**
     * Generate UI Interface description file.
     */
    function generatePanelDesc() {
        var screens = getStoredScreens();

        var macroBtns = new Array();
        $("#macro .macro_btn_defination").each(function() {
            var model = $(this).data("model");
            var btnModels = model.getSubModels();
            model.buttons = new Array();
            for (var index in btnModels) {
                model.buttons.push(btnModels[index]);
            }
            macroBtns.push(model);
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
            screens: screens,
            knxBtns: knxBtns,
            x10Btns: x10Btns,
            macroBtns: macroBtns,
            maxId: global.BUTTONID
        };

        var data = JSON.stringify({
            panel: panel
        });

        return data;
    }


    function iteratorTableCell(table, block) {
        $(table).find("tr").each(function(i) {
            $(this).find("td").each(function(j) {
                block($(this), i, j);
            });
        });
    }


    //static method
    DownloadController.init = function() {
        $("#saveBtn").unbind().bind("click", download);
        $("a.button").UIHover();
    };

    DownloadController.parseCurrentScreen = function(screen) {
        var buttonArray = new Array();
        $("#iphoneBtn_container .iphone_btn").each(function() {
            var iphoneBtn = $(this).data("model");
            buttonArray.push(iphoneBtn);
            btnModelHash[iphoneBtn.id] = iphoneBtn;
			if (iphoneBtn.oModel.className == "Infrared") {
				global.InfraredCollection[iphoneBtn.oModel.codeId] = iphoneBtn.oModel;
			}
        });

        screen.buttons = buttonArray;
        return screen;
    };
    return DownloadController;
} ();