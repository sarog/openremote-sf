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
package org.openremote.modeler.client.presenter;

import org.openremote.modeler.client.event.ScreenSelectedEvent;
import org.openremote.modeler.client.event.UIElementSelectedEvent;
import org.openremote.modeler.client.utils.PropertyEditable;
import org.openremote.modeler.client.utils.PropertyEditableFactory;
import org.openremote.modeler.client.widget.uidesigner.ProfilePanel;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.event.shared.HandlerManager;

public class ProfilePanelPresenter {
  
  private HandlerManager eventBus;
  private ProfilePanel view;
  
  public ProfilePanelPresenter(HandlerManager eventBus, ProfilePanel view) {
    super();
    this.eventBus = eventBus;
    this.view = view;
    
    bind();
  }

  private void bind() {
    final TreePanel<BeanModel> panelTree = this.view.getPanelTree();
    
    panelTree.addListener(Events.OnClick, new Listener<TreePanelEvent<ModelData>>() {
      public void handleEvent(TreePanelEvent<ModelData> be) {
        
        BeanModel beanModel = panelTree.getSelectionModel().getSelectedItem();
        if (beanModel != null && beanModel.getBean() instanceof ScreenPairRef) {
          eventBus.fireEvent(new ScreenSelectedEvent((ScreenPairRef) beanModel.getBean()));
        }

        if (beanModel != null) {
          
          // TODO EBR : in next call, should get rid of panelTree param
          // this will allow beanModel.getBean in event
          // and move creation of PropertyEditable to PropertyPanelPresenter
          // then above event is not required, ScreenPanelPresenter can listen on UIElementSelectedEvent
          // and act only if bean in ScreenPairRef
          // Might not be a bad thing that the 2 different events are fired, limit the
          // amount of events the ScreenPanelPresenter will see
          
          PropertyEditable pe = PropertyEditableFactory.getPropertyEditable(beanModel, panelTree);
          
          eventBus.fireEvent(new UIElementSelectedEvent(pe));
        }
      };
    });    
  }
}
