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
package org.openremote.modeler.client.widget;

import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallbackGXT3;
import org.openremote.useraccount.domain.ControllerDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * Dailog for adding a controller to a designer account
 * 
 * @author <a href = "mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class AddControllerWindow extends Window {

  private static AddControllerWindowUiBinder uiBinder = GWT.create(AddControllerWindowUiBinder.class);

  interface AddControllerWindowUiBinder extends UiBinder<Widget, AddControllerWindow> {
  }

  public AddControllerWindow() {
    uiBinder.createAndBindUi(this);
  }

  @UiFactory
  Window itself() {
    return this;
  }

  @UiField
  FormPanel form;

  @UiField
  TextField macAddressField;
    
  @UiHandler("addControllerButton")
  void onAddClick(SelectEvent e) {
    if (form.isValid()) {
      form.mask("Adding controller ...");
      AsyncServiceFactory.getLinkControllerRPCServiceAsync().linkController(macAddressField.getValue(), new AsyncSuccessCallbackGXT3<ControllerDTO>() {
         public void onSuccess(ControllerDTO controllerDTO) {
            form.unmask();
            fireEvent(new ControllerAddedEvent(controllerDTO));
         }
         public void onFailure(Throwable caught) {
            super.onFailure(caught);
            form.unmask();
         }
      });     
    }
  }
  
  @UiHandler("cancelButton")
  void onCancelClick(SelectEvent e) {
    this.hide();
  }
  
  public interface ControllerAddedHandler extends EventHandler {
    void controllerAdded(ControllerDTO controller);
  }
  
  public static class ControllerAddedEvent extends GwtEvent<ControllerAddedHandler> {
    
    public static Type<ControllerAddedHandler> TYPE = new Type<ControllerAddedHandler>();
    
    private ControllerDTO controller;
    
    public ControllerAddedEvent(ControllerDTO controller) {
      super();
      this.controller = controller;
    }

    @Override
    public Type<ControllerAddedHandler> getAssociatedType() {
      return TYPE;
    }

    @Override
    protected void dispatch(ControllerAddedHandler handler) {
      handler.controllerAdded(this.controller);
    }    
  }
}
