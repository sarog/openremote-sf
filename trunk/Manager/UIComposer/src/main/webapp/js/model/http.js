/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2009, OpenRemote Inc.
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
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
var HTTP = function() {
	function HTTP() {
		var self = this;
		Model.call(self);

    //text ui interface display
		self.label = "";
		self.url = "";

    /**
     * Get HTML getElementId
     */
		self.getElementId = function() {
			return "http"+self.id;
		};
		
		self.inspectViewTemplate = "template/_httpInspect.ejs";


	}

  /**
   * Create new instance from flat model (which have no private method).
   * @param model flat model (which have no private method).
   * @returns created new instance.
   */
	HTTP.init = function(model) {
		var http = new HTTP();
		http.id      = model.id     ;
		http.label   = model.label  ;
		http.url     = model.url;
		return http;
	};
    
	return HTTP;
}();