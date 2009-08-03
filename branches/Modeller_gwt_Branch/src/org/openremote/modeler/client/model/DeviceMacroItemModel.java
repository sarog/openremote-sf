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
package org.openremote.modeler.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

// TODO: Auto-generated Javadoc
/**
 * The Class DeviceMacroItemModel.
 */
public class DeviceMacroItemModel extends BaseModelData {

   /** The Constant DATA_FIELD. */
   private static final String DATA_FIELD = "data";
   
   /** The Constant LABEL_FIELD. */
   private static final String LABEL_FIELD = "label";

   /**
    * Instantiates a new device macro item model.
    * 
    * @param label the label
    * @param deviceMacroItem the device macro item
    */
   public DeviceMacroItemModel(String label, Object deviceMacroItem) {
      set(LABEL_FIELD, label);
      set(DATA_FIELD, deviceMacroItem);
   }

   /**
    * Gets the label.
    * 
    * @return the label
    */
   public String getLabel() {
      return get(LABEL_FIELD).toString();
   }

   /**
    * Gets the data.
    * 
    * @return the data
    */
   public Object getData() {
      return  get(DATA_FIELD);
   }
   
   /**
    * Gets the display property.
    * 
    * @return the display property
    */
   public static String getDisplayProperty() {
      return LABEL_FIELD;
   }
   
   /**
    * Gets the data property.
    * 
    * @return the data property
    */
   public static String getDataProperty() {
      return DATA_FIELD;
   }
}
