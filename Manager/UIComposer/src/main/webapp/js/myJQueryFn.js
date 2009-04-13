jQuery.fn.showModalForm = function(title, buttons, openCallback) {
    var newButtons = {};
    newButtons.Cancel = function() {
        $(this).dialog("close");
    };
    for (var p in buttons) {
        newButtons[p] = buttons[p];
    }

    this.find("input[type='text']").addClass("text");
    this.find("input[type='text']").addClass("ui-widget-content");
    this.find("input[type='text']").addClass("ui-corner-all");

    var dialogElement = this;
    this.dialog({
        bgiframe: true,
        autoOpen: false,
        height: "auto",
        modal: true,
        title: title,
		width:"auto",
        resizable: false,
        buttons: newButtons,
        close: function() {
           dialogElement.find("input").val('').removeClass('ui-state-error');
           dialogElement.find("#validateTips").remove();
        },
        open: openCallback
    });
    this.dialog("open");	
};

jQuery.fn.closeModalForm = function() {
    this.dialog("close");
};

jQuery.empty = function(value) {
    return $.trim(value) == "";
};

jQuery.fn.inputError = function() {
    this.addClass('ui-state-error');
};


jQuery.fn.updateTips = function(element, message) {
    this.find("input").removeClass("ui-state-error");
    element.inputError();
    if (this.find("#validateTips").size() < 1) {
        $("<p id='validateTips'></p>").addClass("ui-state-error-text").prependTo(this);
        var h = parseInt(this.height());
        h = h + 32;
        this.height(h);
    }
    this.find("#validateTips").text("").text(message).effect("highlight", 3000);
};


// add hover function, use jquery ui theme.
jQuery.fn.UIHover = function() {
	$(this).hover(function() {
		$(this).addClass("ui-state-hover");
	},function() {
		$(this).removeClass("ui-state-hover");
	});
};

jQuery.fn.enterKeyPressed = function (func) {
	$(this).keypress(function (e) {
	    if (e.which == 13) {
			func();
		}
	});
};