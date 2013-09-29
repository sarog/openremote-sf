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
package org.openremote.modeler.client.widget.buildingmodeler;


import net.customware.gwt.dispatch.client.DispatchAsync;

import org.openremote.modeler.client.ModelerGinjector;
import org.openremote.modeler.client.event.DeviceUpdatedEvent;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.shared.omnilink.CreateOmnilinkDeviceAction;
import org.openremote.modeler.shared.omnilink.CreateOmnilinkDeviceResult;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Wizard form for {@link DeviceInfoForm}, this is a part of {@link DeviceWizardWindow}.
 * 
 * @author Dan 2009-8-21
 */
public class OmnilinkInfoWizardForm extends CommonForm {

  /** The Constant DEVICE_NAME. */
  public static final String HOST = "host";
  
  /** The Constant DEVICE_VENDOR. */
  public static final String PORT = "port";
  
  /** The Constant DEVICE_MODEL. */
  public static final String KEY1 = "key1";
  
  /** The Constant CONTROLLERS. */
  public static final String KEY2 = "key2";
  
  /** The wrapper. */
  final protected Component wrapper;
  
  TextField<String> hostField;
  TextField<String> portField;
  TextField<String> key1Field;
  TextField<String> key2Field;
  
   /**
    * Instantiates a new device info wizard form.
    * 
    * @param wrapper
    *           the wrapper
    * @param deviceBeanModel
    *           the device bean model
    */
   public OmnilinkInfoWizardForm(Component parent) {
     super();
     this.wrapper = parent;
     createFields();
     addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
        public void handleEvent(FormEvent be) {
          ModelerGinjector injector = GWT.create(ModelerGinjector.class);
          DispatchAsync dispatcher = injector.getDispatchAsync();

          CreateOmnilinkDeviceAction action = new CreateOmnilinkDeviceAction(hostField.getValue(), Integer.parseInt(portField.getValue()),key1Field.getValue(),key2Field.getValue());
          
          dispatcher.execute(action, new AsyncCallback<CreateOmnilinkDeviceResult>() {

            @Override
            public void onFailure(Throwable caught) {
              Info.display("ERROR", caught.getMessage());
              MessageBox.alert("ERROR", caught.getMessage(), null);
              caught.printStackTrace();
              hide();
              
              // TODO: better error reporting
            }

            @Override
            public void onSuccess(CreateOmnilinkDeviceResult result) {
              
              // TODO: might have an error message in result, handle it
            	wrapper.fireEvent(new DeviceUpdatedEvent(null));
              //wrapper.fireEvent(SubmitEvent.SUBMIT, new SubmitEvent(result.getDevices()));
              hide();
            }            
          });
        }          
     });
   }

  /**
    * Creates the fields.
    */
   private void createFields() {
      hostField = new TextField<String>();
      hostField.setName(HOST);
      hostField.setFieldLabel("Host Name or IP");
      hostField.setAllowBlank(false);
      
      portField = new TextField<String>();
      portField.setName(PORT);
      portField.setFieldLabel("Port (4369)");
      portField.setAllowBlank(false);
      portField.setValue("4369");
      
      key1Field = new TextField<String>();
      key1Field.setName(KEY1);
      key1Field.setFieldLabel("Key 1 Hex (0-9,A-F)");
      key1Field.setAllowBlank(false);
      
      key2Field = new TextField<String>();
      key2Field.setName(KEY2);
      key2Field.setFieldLabel("Key 1 Hex (0-9,A-F)");
      key2Field.setAllowBlank(false);
    

     
      add(hostField);
      add(portField);
      add(key1Field);
      add(key2Field);
   }
   
   

   /* (non-Javadoc)
    * @see org.openremote.modeler.client.widget.CommonForm#isNoButton()
    */
   @Override
   public boolean isNoButton() {
      return true;
   }

   /* (non-Javadoc)
    * @see com.extjs.gxt.ui.client.widget.Component#show()
    */
   @Override
   public void show() {
      super.show();
      ((Window) wrapper).setSize(360, 250);
   }
   
   
   

}
