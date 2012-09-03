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

import java.util.ArrayList;

import org.openremote.devicediscovery.domain.DiscoveredDeviceDTO;
import org.openremote.modeler.client.icon.IconResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

/**
 * This window is for creating devices and commands based on a wizard
 * 
 * @author <a href = "mailto:marcus@openremote.org">Marcus Redeker</a>
 */
public class CreateDeviceWizardWindow extends Window {  
  
  private IconResources icons = GWT.create(IconResources.class);
  
  @UiField(provided = true)
  BorderLayoutData northData = new BorderLayoutData(36);
    
  private static CreateDeviceWizardWindowUiBinder uiBinder = GWT.create(CreateDeviceWizardWindowUiBinder.class);

  interface CreateDeviceWizardWindowUiBinder extends UiBinder<Widget, CreateDeviceWizardWindow> {
  }
  
  @UiFactory
  Window itself() {
    return this;
  }

  public CreateDeviceWizardWindow(ArrayList<DiscoveredDeviceDTO> result) {
    uiBinder.createAndBindUi(this);
    show();
  }

  public CreateDeviceWizardWindow() {
    this(new ArrayList<DiscoveredDeviceDTO>());
  }
  
}