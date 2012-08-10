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

import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.shared.PropertyChangeEvent;
import org.openremote.modeler.shared.PropertyChangeListener;

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

  /**
   * @param componentContainer
   * @param widgetSelectionUtil
   */
  public AbsoluteLayoutContainerPropertyForm(AbsoluteLayoutContainer componentContainer) {
    this.componentContainer = componentContainer;
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
          absolute.setLeft(Integer.parseInt(posLeftField.getValue()));
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
          absolute.setTop(Integer.parseInt(posTopField.getValue()));
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
          absolute.setWidth(Integer.parseInt(widthField.getValue()));
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
          absolute.setHeight(Integer.parseInt(heightField.getValue()));
        }
     });
     masterForm.insert(posLeftField, 0);
     masterForm.insert(posTopField, 1);
     masterForm.insert(widthField, 2);
     masterForm.insert(heightField, 3);
     
     absolute.addPropertyChangeListener("left", new PropertyChangeListener() {      
       @Override
       public void propertyChange(PropertyChangeEvent evt) {
         posLeftField.setValue(evt.getNewValue() + "");
       }
     });
     absolute.addPropertyChangeListener("top", new PropertyChangeListener() {      
       @Override
       public void propertyChange(PropertyChangeEvent evt) {
         posTopField.setValue(evt.getNewValue() + "");
       }
     });
     absolute.addPropertyChangeListener("width", new PropertyChangeListener() {      
       @Override
       public void propertyChange(PropertyChangeEvent evt) {
         widthField.setValue(evt.getNewValue() + "");
       }
     });
     absolute.addPropertyChangeListener("height", new PropertyChangeListener() {      
       @Override
       public void propertyChange(PropertyChangeEvent evt) {
         heightField.setValue(evt.getNewValue() + "");
       }
     });

     masterForm.layout();
  }

}
