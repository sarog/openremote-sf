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
package org.openremote.modeler.client.widget.propertyform;

import org.openremote.modeler.client.event.AbsoluteBoundsEvent;
import org.openremote.modeler.client.listener.AbsoluteBoundsListener;
import org.openremote.modeler.client.model.ORBounds;
import org.openremote.modeler.client.utils.AbsoluteBoundsListenerManager;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.domain.Absolute;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * An object that handles the fields used to edit position and size of widget embedded in AbsoluteLayout.
 * 
 * @author <a href = "mailto:eric@openremote.org">Eric Bariaux</a>
 */
public class AbsoluteLayoutContainerPropertyForm {

  private AbsoluteLayoutContainer componentContainer;
  protected WidgetSelectionUtil widgetSelectionUtil;

  /**
   * @param componentContainer
   * @param widgetSelectionUtil
   */
  public AbsoluteLayoutContainerPropertyForm(AbsoluteLayoutContainer componentContainer, WidgetSelectionUtil widgetSelectionUtil) {
    this.componentContainer = componentContainer;
    this.widgetSelectionUtil = widgetSelectionUtil;
 }
  
  /**
   * Manage the absolute layoutcontainer's position and size accurately.
   */
  public void addAbsolutePositionAndSizeProperties(PropertyForm masterForm) {
     final Absolute absolute = this.componentContainer.getAbsolute();
     final TextField<String> posLeftField = new TextField<String>();
     posLeftField.setName("posLeft");
     posLeftField.setFieldLabel("Left");
     posLeftField.setAllowBlank(false);
     posLeftField.setRegex("^\\d+$");
     posLeftField.getMessages().setRegexText("The left must be a nonnegative integer");
     posLeftField.setValue(absolute.getLeft() + "");
     posLeftField.addListener(Events.Blur, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          componentContainer.setPosition(Integer.parseInt(posLeftField.getValue()), absolute.getTop());
          componentContainer.layout();
        }
     });
     final TextField<String> posTopField = new TextField<String>();
     posTopField.setName("posTop");
     posTopField.setFieldLabel("Top");
     posTopField.setAllowBlank(false);
     posTopField.setRegex("^\\d+$");
     posTopField.getMessages().setRegexText("The top must be a nonnegative integer");
     posTopField.setValue(absolute.getTop() + "");
     posTopField.addListener(Events.Blur, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          componentContainer.setPosition(absolute.getLeft(), Integer.parseInt(posTopField.getValue()));
          componentContainer.layout();
        }
     });
     final TextField<String> widthField = new TextField<String>();
     widthField.setName("width");
     widthField.setFieldLabel("Width");
     widthField.setAllowBlank(false);
     widthField.setRegex("^[1-9][0-9]*$");
     widthField.getMessages().setRegexText("The width must be a positive integer");
     widthField.setValue(absolute.getWidth() + "");
     widthField.addListener(Events.Blur, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          componentContainer.setSize(Integer.parseInt(widthField.getValue()), absolute.getHeight());
          componentContainer.layout();
        }
     });

     final TextField<String> heightField = new TextField<String>();
     heightField.setName("height");
     heightField.setFieldLabel("Height");
     heightField.setAllowBlank(false);
     heightField.setRegex("^[1-9][0-9]*$");
     heightField.getMessages().setRegexText("The height must be a positive integer");
     heightField.setValue(absolute.getHeight() + "");
     heightField.addListener(Events.Blur, new Listener<BaseEvent>() {
        @Override
        public void handleEvent(BaseEvent be) {
          componentContainer.setSize(absolute.getWidth(), Integer.parseInt(heightField.getValue()));
          componentContainer.layout();
        }
     });
     masterForm.insert(posLeftField, 0);
     masterForm.insert(posTopField, 1);
     masterForm.insert(widthField, 2);
     masterForm.insert(heightField, 3);
     AbsoluteBoundsListenerManager.getInstance().addAbsoluteBoundsListener(componentContainer, new AbsoluteBoundsListener() {
        public void handleEvent(AbsoluteBoundsEvent event) {
           ORBounds bounds = event.getBounds();
           posLeftField.setValue(bounds.getLeft() + "");
           posTopField.setValue(bounds.getTop() + "");
           widthField.setValue(bounds.getWidth() + "");
           heightField.setValue(bounds.getHeight() + "");
        }
     });
     masterForm.layout();
  }

}
