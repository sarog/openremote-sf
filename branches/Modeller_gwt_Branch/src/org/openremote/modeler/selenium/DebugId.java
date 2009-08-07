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
package org.openremote.modeler.selenium;

/**
 * Used to collect all the debug identifiers of Selenium test on widget, so that if one is changed, its test can detect
 * it. This can also keep unique for all identifiers.
 * 
 * @author Dan 2009-8-6
 */
public class DebugId {
   
   /**
    * Not be instantiated.
    */
   private DebugId() {
   }
   
   /* DevicePanel */
   /** The Constant DEVICE_TREE_CONTAINER. */
   public static final String DEVICE_TREE_CONTAINER = "deviceTreeContainer";
   
   /** The Constant DEVICE_NEW_BTN. */
   public static final String DEVICE_NEW_BTN = "DeviceNewBtn";
   
   /** The Constant NEW_DEVICE_MENU_ITEM. */
   public static final String NEW_DEVICE_MENU_ITEM = "newDeviceMenuItem";
   
   /** The Constant DEVICE_EDIT_BTN. */
   public static final String DEVICE_EDIT_BTN = "DeviceEditBtn";
   
   /** The Constant DELETE_DEVICE_BUTTON. */
   public static final String DELETE_DEVICE_BUTTON = "deleteDeviceButton";
   
   /* DeviceWindow */
   /** The Constant NEW_DEVICE_WINDOW. */
   public static final String NEW_DEVICE_WINDOW = "newDeviceWindow";
   
   /** The Constant DEVICE_SUBMIT_BTN. */
   public static final String DEVICE_SUBMIT_BTN = "deviceSubmitBtn";
   
   /** The Constant DEVICE_NAME_FIELD. */
   public static final String DEVICE_NAME_FIELD = "deviceNameField";
   
   /** The Constant DEVICE_VENDOR_FIELD. */
   public static final String DEVICE_VENDOR_FIELD = "deviceVendorField";
   
   /** The Constant DEVICE_MODEL_FIELD. */
   public static final String DEVICE_MODEL_FIELD = "deviceModelField";

}
