/*/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

var EJSHelper = function() {
    return {
        updateView: function(url, containerId, model) {
            try {
                new EJS({
                    url: url
                }).update(containerId, model);
            } catch(e) {
                if (e instanceof Error) {
                    $.showErrorMsg(e.message);
                } else {
                    $.showErrorMsg("Can't Connect to server.");
                }
            }
        },
        render: function(url, model) {
            var html = "";
            try {
                if (model === undefined) {
                    html = new EJS({
                        url: url
                    }).render();
                } else {
                    html = new EJS({
                        url: url
                    }).render(model);
					
                }
            } catch(e) {
                if (e instanceof Error) {
                    $.showErrorMsg(e.message);
                } else {
                    $.showErrorMsg("Can't Connect to server.");
                }
            }

            return html;
        }
    };
} ();