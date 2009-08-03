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
package org.openremote.modeler.service;


import org.openremote.modeler.domain.DeviceMacro;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface DeviceMacroService.
 */
public interface DeviceMacroService {
   
   /**
    * Load all.
    * 
    * @return the list< device macro>
    */
   public List<DeviceMacro> loadAll();
   

   /**
    * Save device macro.
    * 
    * @param deviceMacro the device macro
    * 
    * @return the device macro
    */
   public DeviceMacro saveDeviceMacro(DeviceMacro deviceMacro);
   
   


   /**
    * Update device macro.
    * 
    * @param deviceMacro the device macro
    * 
    * @return the device macro
    */
   public DeviceMacro updateDeviceMacro(DeviceMacro deviceMacro);
   
   
   /**
    * Delete device macro.
    * 
    * @param id the id
    */
   public void deleteDeviceMacro(long id);


    /**
     * Load by id.
     * 
     * @param id the id
     * 
     * @return the device macro
     */
    public DeviceMacro loadById(long id);
}
