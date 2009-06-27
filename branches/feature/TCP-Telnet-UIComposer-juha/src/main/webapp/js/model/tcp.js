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
 * Client state model for TCP/IP buttons. Includes mandatory attribute 'label' and TCP/IP
 * specific attributes 'ip' and 'port'.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
var TCP = function() {

  function TCP() {

    var self = this;
		Model.call(self);

		self.label    = "";
		self.ip       = "";
    self.port     = "";
    self.command  = "";

    /**
     * Get HTML getElementId
     */
		self.getElementId = function() {
			return "tcp"+self.id;
		};

		self.inspectViewTemplate = "template/_tcpInspect.ejs";
	}

  /**
   * Create new instance from flat model (which have no private method).
   *
   * @param model flat model (which have no private method).
   *
   * @returns created new instance.
   */
	TCP.init = function(model) {

    var tcp     = new TCP();
		tcp.id      = model.id;
		tcp.label   = model.label;
		tcp.ip      = model.ip;
    tcp.port    = model.port;
    tcp.command = model.command;

    return tcp;
	};

	return TCP;
}();