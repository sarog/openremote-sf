/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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

import org.openremote.modeler.exception.BeehiveNotAvailableException;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * Inherited from {@link AsyncCallback}.
 * 
 * Provides a generic way to handle errors from GWT-RPC calls.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public abstract class AsyncSuccessCallbackGXT3<T> implements AsyncCallback<T> {

  /**
   * Because If the beehive is not available, this exception will be thrown many times because of initializing template
   * list, downloading user resources etc.  That means there will be a lot of alert windows show to user. Therefore,
   * we can replace most of alert information with silent "Info". If you want to let user know what the error
   * information is, you can just override this method in its subclass.
   * 
   * @param caught the caught
   * 
   * @see com.google.gwt.user.client.rpc.AsyncCallback#onFailure(java.lang.Throwable)
   */
  public void onFailure(Throwable caught) {
    if (checkTimeout(caught)) {
      return;
    }
    if (caught instanceof BeehiveNotAvailableException) {
      Info.display("ERROR", "Beehive is not available right now! ");
    } else {
      new AlertMessageBox("Error", caught.getMessage()).show();
    }
  }
   
  /**
   * onSuccess.
   * 
   * @param result the result
   * 
   * @see com.google.gwt.user.client.rpc.AsyncCallback#onSuccess(java.lang.Object)
   */
   public abstract void onSuccess(T result);
   
   protected boolean checkTimeout(Throwable caught) {
     if (caught instanceof StatusCodeException) {
       StatusCodeException sce = (StatusCodeException) caught;
       if (sce.getStatusCode() == 401) {
         final ConfirmMessageBox mb = new ConfirmMessageBox("Timeout", "Your session has timeout, please login again.");
         mb.addHideHandler(new HideHandler() {
           public void onHide(HideEvent event) {
             if (mb.getHideButton() == mb.getButtonById(PredefinedButton.YES.name())) {
               Window.open("login.jsp", "_self", "");
             }
          }
        });           
      }
    }
    return false;
  }

}