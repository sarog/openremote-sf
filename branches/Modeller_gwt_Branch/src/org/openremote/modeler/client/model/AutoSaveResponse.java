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

import java.io.Serializable;


/**
 * The Class AutoSaveResponse.
 * 
 * @author handy.wang
 */
@SuppressWarnings("serial")
public class AutoSaveResponse implements Serializable {

   /** The is saved success. */
   private boolean isSavedSuccess;
   
   /**
    * Instantiates a new auto save response.
    */
   public AutoSaveResponse() {
      super();
      this.isSavedSuccess = false;
   }

   /**
    * Checks if is saved success.
    * 
    * @return true, if is saved success
    */
   public boolean isSavedSuccess() {
      return isSavedSuccess;
   }

   /**
    * Sets the saved success.
    * 
    * @param isSavedSuccess the new saved success
    */
   public void setSavedSuccess(boolean isSavedSuccess) {
      this.isSavedSuccess = isSavedSuccess;
   }
}
