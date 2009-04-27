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
/*-----------  some constant   --------------------------*/

// Define rest api url
RESTAPIUrl = "http://openremote.finalist.hk/beehive/rest/lirc";

/*-----------  store global data  --------------------------*/

//in order to let button id keep increasing
BUTTONID = 1;
// a hash contain all of infrared object, key is code id value is infrared model.
// this variable is used for record the infrared button you already dragged.
// "Notice":should clean up when delete infrared button from iphone panel.
InfraredCollection = {};
//Store Screen here 
// key is screen id and value is screen model
// "Notice":should clean up when delete screen.
g_screens = {};

// TODO Need  refactor, every screen have it's own controller and view.
// "Notice":should clean up when delete infrared button from iphone panel.
// "Notice":should clean up when delete iphone button.
btnInArea = new Array();




