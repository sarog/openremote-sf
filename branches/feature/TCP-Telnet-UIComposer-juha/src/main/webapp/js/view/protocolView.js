/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation; either version 3.0 of the
 * License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License along with this software; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
var KNXView = function() {
    function KNXView(knx) {
        var self = this;
        var _model = knx;

        self.getModel = function() {
            return _model;
        };

        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var btn = HTMLBuilder.KNXBtnBuilder(self.getModel());
            var info = $("#knx_tab p");
            if (info.size() != 0) {
                info.remove();
            }
            btn.prependTo($("#knx_tab .item_container"));
        };
        init();

        self.deleteView = function() {
            self.getElement().remove();
        };

        self.updateView = function() {
			var knx = self.getModel();

            var btn = $("#" + knx.getElementId());
			btn.interceptStr({
				text:knx.label,
				max:14
			});
        };
    }

    return KNXView;
} ();

var X10View = function() {
    function X10View(x10) {
        var self = this;
        var _model = x10;

        self.getModel = function() {
            return _model;
        };

        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var btn = HTMLBuilder.X10BtnBuilder(self.getModel());
            var info = $("#x10_tab p");
            if (info.size() != 0) {
                info.remove();
            }
            btn.prependTo($("#x10_tab .item_container"));
        };
        init();

        self.deleteView = function() {
            self.getElement().remove();
        };

		self.updateView = function () {
			var x10 = self.getModel();
	        var btn = $("#"+x10.getElementId());
			btn.interceptStr({
				text:x10.label,
				max:14
			});
	    };
    }

    return X10View;
} ();


var HTTPView = function() {
    function HTTPView(http) {
        var self = this;
        var _model = http;

        self.getModel = function() {
            return _model;
        };

        self.getElement = function() {
            return $("#" + self.getModel().getElementId());
        };

        var init = function() {
            var btn = HTMLBuilder.HTTPBtnBuilder(self.getModel());
            var info = $("#http_tab p");
            if (info.size() != 0) {
                info.remove();
            }
            btn.prependTo($("#http_tab .item_container"));
        };
        init();

        self.deleteView = function() {
            self.getElement().remove();
        };

		self.updateView = function () {
			var http = self.getModel();
	        var btn = $("#"+http.getElementId());
			btn.interceptStr({
				text:http.label,
				max:14
			});
	    };
    }

    return HTTPView;
} ();

