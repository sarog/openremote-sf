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
 * Show block window, this window have a default "close" button.
 * @param title Dialog title.
 * @param buttons a hash contain button name and button function. eg:{'create':someFunction}.
 * @param openCallback what's going on when the diglog opened.
 */

jQuery.fn.showModalForm = function(title, buttons, openCallback) {
    var newButtons = {};
    newButtons.Cancel = function() {
        $(this).dialog("close");
    };
	
	//in order to put default 'close' button at last.
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
	// If autoOpen=true, we needn't to call open(), but the dialog can only open once.
    this.dialog("open");	
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
    return $.trim(value+"") == "";
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
	},function() {
		$(this).removeClass("ui-state-hover");
	});
};

/**
 * Invoke a function when enter key pressed.
 * You can also pass some arguments to the function you passed in, just put after the func argument.
 * @param func function which invoked after enter key pressed.
 */
jQuery.fn.enterKeyPressed = function (func) {
	$(this).keypress(function (e) {
	    if (e.which == 13) {
			var args = [];
			for (var i=1; i < arguments.length; i++) {
				args.push(arguments[i]);
			}
			func.apply(this,args);
		}
	});
};