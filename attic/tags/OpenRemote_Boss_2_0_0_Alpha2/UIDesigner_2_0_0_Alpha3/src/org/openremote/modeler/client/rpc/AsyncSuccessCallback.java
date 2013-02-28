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
package org.openremote.modeler.client.rpc;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Inherited from {@link AsyncCallback}. For global error handle.
 * 
 * @param <T> 
 * 
 * @author allen.wei
 */
public abstract class AsyncSuccessCallback<T> implements AsyncCallback<T> {

   /**
    * On failure.
    * 
    * @param caught the caught
    * 
    * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
    */
   public void onFailure(Throwable caught) {
      MessageBox.alert("ERROR", caught.getLocalizedMessage(), null);
   }


   
   
   /**
    * onSuccess.
    * 
    * @param result the result
    * 
    * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
    */
   public abstract void onSuccess(T result);

}
