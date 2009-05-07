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

/**
 * Shows block window, this window have a default "close" button.
 * @public
 * @param {String} title Window title.
 * @param {int|String} options.height (optional) Window height, default value is 'auto'.
 * @param {int|String} options.width (optional) Window width, default value is 'auto'.
 * @param {int|String} options.buttons (optional) an object contain button name and function.
 * @param {Boolean} options.modal (optional) Is this window is modal window, default value is true.
 * @param {Boolean} options.resizable (optional) Is this window can resize, default value is false.
 * @param {Function} options.open (optional) Function will be called, after window have opened.
 * @param {String} options.confirmButtonName (optional) Will call the function which on this button after enter key pressed.
 */

jQuery.fn.showModalForm = function(title, options) {
    var newButtons = {};
    newButtons.Cancel = function() {
        $(this).dialog("close");
    };

    //in order to put default 'close' button at last.
	if (options.buttons !== undefined) {
		for (var p in options.buttons) {
	        newButtons[p] = options.buttons[p];
	    }
	}
    
    this.find("input[type='text']").addClass("text");
    this.find("input[type='text']").addClass("ui-widget-content");
    this.find("input[type='text']").addClass("ui-corner-all");

    var dialogElement = this;
    this.dialog({
        bgiframe: true,
        autoOpen: false,
        height: options.height||'auto',
        modal: options.modal||true,
        title: title,
        width: options.width||'auto',
        resizable: options.resizable||false,
        buttons: newButtons,
        close: function() {
            dialogElement.find("input").val('').removeClass('ui-state-error');
            dialogElement.find("#validateTips").remove();
        },
        open: options.open || function() {}
    });
    // If autoOpen=true, we needn't to call open(), but the dialog can only open once.
    this.dialog("open");
	if (options.confirmButtonName !== undefined) {
		this.enterKeyPressed(newButtons[options.confirmButtonName]);
	}
	
};

/**
 * Convenient mehod to close the Dialog.
 * Usage: $("#dialogElement").closeModalForm().
 */
jQuery.fn.closeModalForm = function() {
    this.dialog("close");
};

/**
 * Judge a string is emptry(Blank).
 * @param test value.
 * @returns true/false.
 */
jQuery.empty = function(value) {
    return $.trim(value + "") == "";
};

jQuery.showErrorMsg = function(msg) {
    $("#error #errorMsg").html(msg);
    $("#error").show();

};

/**
 * Add a style to element, notice user there is a error occured on this element.
 * Use JQuery Theme 'ui-state-error' style.
 */
jQuery.fn.inputError = function() {
    this.addClass('ui-state-error');

};

/**
 * Remove error style 
 * Use JQuery Theme 'ui-state-error' style.
 */
jQuery.fn.clearError = function() {
    this.removeClass('ui-state-error');
};


/**
 * Add some text inside this element. tell user what's wrong with it.
 * @param element which element is wrong. Often pass a input element.
 * @param message the error message
 */
jQuery.fn.updateTips = function(element, message) {
    this.find("input").removeClass("ui-state-error");
    $(element).inputError();
    if (this.find("#validateTips").size() < 1) {
        $("<p id='validateTips'></p>").addClass("ui-state-error-text").prependTo(this);
        var h = parseInt(this.height());
        h = h + 32;
        this.height(h);
    }
    this.find("#validateTips").text("").text(message).effect("highlight", 3000);
};


/**
 * Add hover style
 * Use JQuery theme style 'ui-state-hover'
 */
jQuery.fn.UIHover = function() {
    $(this).hover(function() {
        $(this).addClass("ui-state-hover");
    },
    function() {
        $(this).removeClass("ui-state-hover");
    });
};

/**
 * Invoke a function when enter key pressed.
 * You can also pass some arguments to the function you passed in, just put after the func argument.
 * @param func function which invoked after enter key pressed.
 */
jQuery.fn.enterKeyPressed = function(func) {
    $(this).keypress(function(e) {
        if (e.which == 13) {
            var args = [];
            for (var i = 1; i < arguments.length; i++) {
                args.push(arguments[i]);
            }
            func.apply(this, args);
        }
    });
};
jQuery.fn.isInArea = function(container) {
    var top = $(this).offset().top;
    var left = $(this).offset().left;
    var minTop = $(container).offset().top;
    var maxTop = $(container).offset().top + $(container).outerHeight();
    var minLeft = $(container).offset().left;
    var maxLeft = $(container).offset().left + $(container).outerWidth();

    if (top > maxTop || top < minTop || left > maxLeft || left < minLeft) {
        return false;
    }
    return true;
};

jQuery.isCoordinateInArea = function(top, left, container) {

    var minTop = $(container).offset().top;
    var maxTop = $(container).offset().top + $(container).outerHeight();
    var minLeft = $(container).offset().left;
    var maxLeft = $(container).offset().left + $(container).outerWidth();

    if (top > maxTop || top < minTop || left > maxLeft || left < minLeft) {
        return false;
    }
    return true;
};

/**
 * make button inspectable
 * @static
 * @param {Object} options.model (optional) model need to inpect, default is $(this).data("model").
 * @param {String} options.template  (optional) inspect window template,default is $(this).data("model").inspectViewTemplate.
 * @param {Function} options,before (optional) do some thing before inspect window has showed, like some css stuff.
 */
jQuery.fn.inspectable = function(options) {
	if (options === undefined) {
		options = {};
	}
    if (options.model === undefined) {
        options.model = $(this).data("model");
    }
    if (options.template === undefined) {
        options.template = options.model.inspectViewTemplate;
    }

    $(this).unbind().click(function() {
        if (options.before !== undefined) {
            options.before();
        } else {
            $(".highlightInspected").removeClass("highlightInspected");
            $(this).addClass("highlightInspected");
        }
        InspectViewController.updateView(options);
    });

};
