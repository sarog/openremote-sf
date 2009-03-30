RESTAPIUrl = "http://openremote.finalist.hk/beehive/rest/lirc";

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
            HTMLBuilder.commandBtnBuilder(this).appendTo($("#command_container"));
        });
        $("<div class='clear'></div>").appendTo($("#command_container"));
        makeCommandBtnDraggable();
        $("#command_navigition").dialog("close");
    });

    $("#command_navigition option").remove();

}

function loadingSelect(select) {
    $(select).find("option").remove();
    var opt = new Option("loading...", 0);
    select[0].options.add(opt);
}

function loadingSelectDone(select) {
    select[0].options[0] = null;
}

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