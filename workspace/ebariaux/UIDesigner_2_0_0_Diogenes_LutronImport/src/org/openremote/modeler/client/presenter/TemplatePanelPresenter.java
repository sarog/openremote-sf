/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2010, OpenRemote Inc.
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

import java.util.List;

import org.openremote.modeler.client.widget.uidesigner.TemplatePanel;
import org.openremote.modeler.domain.Template;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.google.gwt.event.shared.HandlerManager;

public class TemplatePanelPresenter implements Presenter {

  private HandlerManager eventBus;
  private TemplatePanel view;
  
  public TemplatePanelPresenter(HandlerManager eventBus, TemplatePanel view) {
    super();
    this.eventBus = eventBus;
    this.view = view;
    bind();
  }

  private void bind() {
    this.view.getSelectionService().addListener(new SelectionChangedListener<BeanModel>() {
      @Override
      public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
        List<BeanModel> selection = se.getSelection();
        if (selection.size() == 1) {
          BeanModel selectModel = selection.get(0);
          if (selectModel.getBean() instanceof Template) {
            Template template = selectModel.getBean();
            view.setTemplateInEditing(template);
          }
        }
      }      
    });
  }
}
