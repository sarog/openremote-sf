/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2011, OpenRemote Inc.
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

import java.util.List;

import org.openremote.modeler.client.BrandInfo;
import org.openremote.modeler.client.proxy.IrFileParserProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.rpc.ConfigurationRPCService;
import org.openremote.modeler.client.rpc.ConfigurationRPCServiceAsync;
import org.openremote.modeler.client.rpc.IRFileParserRPCService;
import org.openremote.modeler.client.rpc.IRFileParserRPCServiceAsync;
import org.openremote.modeler.client.widget.CommonForm;
import org.openremote.modeler.domain.Device;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

/**
 * IR File Command Import Form.
 * 
 */
public class IRFileImportForm extends CommonForm {
   


   
   /** The configuration service. */
   private IRFileParserRPCServiceAsync iRFileParserService = (IRFileParserRPCServiceAsync) GWT
         .create(IRFileParserRPCService.class);
   
   /** The device. */
   protected Device device = null;
   
   /** The select container. */
   private LayoutContainer selectContainer = new LayoutContainer();
   
   /** The command container. */
   private LayoutContainer commandContainer = new LayoutContainer();
   
   /** The import button. */
   protected Button importButton;


   /** The code grid. */
   protected Grid<ModelData> codeGrid = null;

   protected Component wrapper;

   
   
   /**
    * Instantiates a new iR command import form.
    * 
    * @param wrapper the wrapper
    * @param deviceBeanModel the device bean model
    */
   public IRFileImportForm(final Component wrapper, BeanModel deviceBeanModel) {
      super();
      setHeight(500);
      this.wrapper = wrapper;
      setLayout(new RowLayout(Orientation.VERTICAL));


   }

   /**
    * On submit.
    * 
    * @param wrapper the wrapper
    */
   protected void onSubmit(final Component wrapper) {
      addListener(Events.BeforeSubmit, new Listener<FormEvent>() {
         public void handleEvent(FormEvent be) {
            
      }});
   }
   
   @Override
   protected void addButtons() {
      importButton = new Button("Import");
//      importButton.setEnabled(false);
      importButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

         @Override
         public void componentSelected(ButtonEvent ce) {
            submit();
         }
      });
      addButton(importButton);
   }



public void showDevices() {
	IrFileParserProxy.loadBrands(new AsyncSuccessCallback<List<BrandInfo>>(){

		@Override
		public void onSuccess(List<BrandInfo> brands) {
			//TODO now that we can retrieve brands from server, 
			//add them into a drop-down list, where a selection will trigger the next one
			// with the devices where a selection in it will trigger display of a last drop-down list 
			// or display a list of available codes.
			String test = "";
			for (BrandInfo brandInfo : brands) {
				test += brandInfo.getBrandName()+"\n\r";
			}
			Window.alert(test);
			
		}
		
	});
}

 
 

 

}
