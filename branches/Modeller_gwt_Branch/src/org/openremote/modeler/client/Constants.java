/* OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.client;

/**
 * Puts all the fronted constant here.
 */
public class Constants {

   /**
    * Not be instantiated.
    */
   private Constants() {
   }

   /** The Constant INFRARED_TYPE. For assign protocol type. */
   public static final String INFRARED_TYPE = "Infrared";
   
   /** The Constant KNX_TYPE. */
   public static final String KNX_TYPE = "knx";
   
   /** The Constant X10. */
   public static final String X10_TYPE = "x10";
   
   /** The Constant BUTTON_DND_GROUP. For assign UIDesigner to DND button. */
   public static final String BUTTON_DND_GROUP = "buttonDNDGroup";
   
   /** The Constant CONTROL_DND_GROUP. */
   public static final String CONTROL_DND_GROUP = "controlDNDGroup";
   
   /** The Constant DEVICES. */
   public static final String DEVICES = "devices";
   
   /** The Constant MACROS. */
   public static final String MACROS = "macros";
   
   /** The Constant DEVICES_OID. */
   public static final long DEVICES_OID = -100;
   
   /** The Constant MACROS_OID. */
   public static final long MACROS_OID = -200;

   /** The Constant NULL_PARENT_OID. */
   public static final long NULL_PARENT_OID = -300;
   
   /** The Constant SCREEN_TABLE_OID. */
   public static final long SCREEN_TABLE_OID = -400;
   
   public static final long GROUP_TABLE_OID = -600;
   
   public static final long PREVIEW_SCREENBUTTON_OID = -500;
   
   /** The Constant PANEL_DESC_FILE_EXT. */
   public static final String PANEL_DESC_FILE = "import";
   
   /** Make resizable in east and south direction. */
   public static final String RESIZABLE_HANDLES = "e s";
   
   public static final String CUSTOM_PANEL = "customPanel";
   public static final String DEFAULT_GROUP = "firstGroup";
   public static final String DEFAULT_SCREEN = "firstScreen";
}
