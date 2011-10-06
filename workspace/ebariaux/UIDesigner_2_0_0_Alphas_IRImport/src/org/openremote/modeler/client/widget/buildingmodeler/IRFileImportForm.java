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

import org.openremote.modeler.client.rpc.ConfigurationRPCService;
import org.openremote.modeler.client.rpc.ConfigurationRPCServiceAsync;
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

/**
 * IR File Command Import Form.
 * 
 */
public class IRFileImportForm extends CommonForm {
   


   
   /** The configuration service. */
   private ConfigurationRPCServiceAsync configurationService = (ConfigurationRPCServiceAsync) GWT
         .create(ConfigurationRPCService.class);
   
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



 
 

 

}
