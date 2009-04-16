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

function fillVendorSelect() {
    var select = $("#vendor_select");
    loadingSelect(select);
    getJSONData("",
    function(data) {
        loadingSelectDone(select);
        var vendors = $.makeArray(data.vendors.vendor);
        $(vendors).each(function() {
            var opt = new Option(this.name, this.id);
            select[0].options.add(opt);
        });
    });
    select.change(function() {
        var vendor_name = select.find("option:selected").text();
        afterSelectVendor(vendor_name);
    });
}

function afterSelectVendor(vendor_name) {
    $("#model_select option").remove();
    fillModelSelect(vendor_name);
}

function fillModelSelect(vendor_name) {
    $("#model_select_container").show();
    var select = $("#model_select");
    loadingSelect(select);
    getJSONData("/" + vendor_name,
    function(data) {
        loadingSelectDone(select);
        var models = $.makeArray(data.models.model);
        $(models).each(function() {
            var opt = new Option(this.name, this.id);
            select[0].options.add(opt);
        });

    });
    select.change(function() {
        var model_name = select.find("option:selected").text();
        afterSelectModel(vendor_name, model_name);
    });
}

function afterSelectModel(vendor_name, model_name) {
    $("#section_select option").remove();
    getJSONData("/" + vendor_name + "/" + model_name,
    function(data) {
        var sections = $.makeArray(data.sections.section);
        if (sections.length == 1) {
            showCommandBtns(vendor_name, model_name, sections[0].id);
        } else if (sections.length > 1) {
            fillSectionSelect(vendor_name, model_name, sections);
        }
    });
}

function fillSectionSelect(vendor_name, model_name, sections) {
    $("#section_select_container").show();
    $(sections).each(function() {
        var opt = new Option(this.name, this.id);
        $("#section_select")[0].options.add(opt);
    });
    $("#section_select").click(function() {
        var section_id = $("#section_select option:selected").val();
        showCommandBtns(vendor_name, model_name, section_id);
    });
}

function showCommandBtns(vendor_name, model_name, section_id) {
	
    getJSONData("/" + vendor_name + "/" + model_name + "/" + section_id + "/codes",
    function(data) {
        $("#command_container").html("");
        var codes = $.makeArray(data.codes.code);
        $(codes).each(function() {
            HTMLBuilder.commandBtnBuilder(this,section_id).appendTo($("#command_container"));
        });
        $("<div class='clear'></div>").appendTo($("#command_container"));
        makeBtnDraggable();
        $("#command_navigition").dialog("close");
		$("#lircUrl").val(RESTAPIUrl+"/" + vendor_name + "/" + model_name + "/"+"lirc.conf");
    });

    $("#command_navigition option").remove();

}

/**
 * Show loading status to certain select.
 * @param select  
 */
function loadingSelect(select) {
    $(select).find("option").remove();
    var opt = new Option("loading...", 0);
    select[0].options.add(opt);
}

/**
 * Remove the loading status from select.
 * @param select
 */
function loadingSelectDone(select) {
    select[0].options[0] = null;
}

/**
 * Load JSON data from a remote url,using JSONP
 * @param path url
 * @param callback callback function with argument data which contain data server response.
 */
function getJSONData(path, callback) {
    $.ajax({
        type: "GET",
        url: RESTAPIUrl + path,
        dataType: "jsonp",
        cache: false,
        success: function(data) {
            callback(data);
            $.unblockUI();
        }
    });
}