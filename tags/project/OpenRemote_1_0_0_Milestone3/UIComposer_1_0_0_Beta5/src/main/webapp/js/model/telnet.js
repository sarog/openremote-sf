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
 * You should have received a copy of the GNU General Public License along with this software;
 * if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
 * MA 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 * Client state model for telnet buttons. Includes mandatory attribute 'label' and telnet
 * specific attributes 'ip', 'port' and 'command'.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
var Telnet = function() {
	function Telnet() {
		var self = this;
		Model.call(self);

		self.label  = "";
		self.ip     = "";
    self.port   = "";
    self.command = "";

    /**
     * Get HTML getElementId
     */
		self.getElementId = function() {
			return "telnet"+self.id;
		};

		self.inspectViewTemplate = "template/_telnetInspect.ejs";
	}

  /**
   * Create new instance from flat model (which have no private method).
   *
   * @param model flat model (which have no private method).
   *
   * @returns created new instance.
   */
	Telnet.init = function(model) {
		var telnet = new Telnet();
		telnet.id      = model.id;
		telnet.label   = model.label;
		telnet.ip      = model.ip;
    telnet.port    = model.port;
    telnet.command = model.command;

    return telnet;
	};

	return Telnet;
}();