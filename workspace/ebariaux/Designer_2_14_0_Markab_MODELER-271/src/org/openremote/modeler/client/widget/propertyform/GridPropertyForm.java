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

import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainerHandle;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.shared.PropertyChangeEvent;
import org.openremote.modeler.shared.PropertyChangeListener;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * A form to edit grid properties.
 * The grid has row, column, size and position properties.
 */
public class GridPropertyForm extends PropertyForm {
   private UIGrid grid;
   private GridLayoutContainerHandle gridContainer = null;
   
   private PropertyChangeListener leftListener;
   private PropertyChangeListener topListener;
   private PropertyChangeListener widthListener;
   private PropertyChangeListener heightListener;

   public GridPropertyForm(GridLayoutContainerHandle gridContainer, UIGrid grid, WidgetSelectionUtil widgetSelectionUtil) {
      super(widgetSelectionUtil);
      this.grid = grid;
      this.gridContainer = gridContainer;
      initForm();
   }
   
   protected void initForm() {
      this.setFieldWidth(5);
      Screen screen = gridContainer.getScreenCanvas().getScreen();
      FieldSet gridAttrSet = new FieldSet();
      FormLayout layout = new FormLayout();
      layout.setLabelWidth(80);
      layout.setDefaultWidth(80);
      gridAttrSet.setLayout(layout);
      gridAttrSet.setHeading("Grid Attributes");

      /*
       * temp for set the width and height of gird.
       */
      final TextField<String> gridRowCountField = new TextField<String>();
      gridRowCountField.setName("gridRow");
      gridRowCountField.setFieldLabel("Row Count");
      gridRowCountField.setAllowBlank(false);
      gridRowCountField.setRegex("[1-9]");
      gridRowCountField.getMessages().setRegexText("The row must between [1-9]");
      gridRowCountField.setValue("6"); // temp set 6 rows.
      gridRowCountField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            grid.setRowCount(Integer.parseInt(gridRowCountField.getValue()));
         }
      });

      final TextField<String> gridColumnCountField = new TextField<String>();
      gridColumnCountField.setName("gridColumn");
      gridColumnCountField.setFieldLabel("Col Count");
      gridColumnCountField.setAllowBlank(false);
      gridColumnCountField.setRegex("[1-9]");
      gridColumnCountField.getMessages().setRegexText("The column must between [1-9]");
      gridColumnCountField.setValue("4"); // temp set 4 columns.
      gridColumnCountField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            grid.setColumnCount(Integer.parseInt(gridColumnCountField.getValue()));
         }
      });
      final TextField<String> posLeftField = new TextField<String>();
      posLeftField.setName("posLeft");
      posLeftField.setFieldLabel("Left");
      posLeftField.setAllowBlank(false);
      posLeftField.setRegex("^\\d+$");
      posLeftField.getMessages().setRegexText("The left must be a nonnegative integer");
      posLeftField.setValue("0"); // temp set left 0
      posLeftField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            grid.setLeft(Integer.parseInt(posLeftField.getValue()));
         }
      });
      final TextField<String> posTopField = new TextField<String>();
      posTopField.setName("posTop");
      posTopField.setFieldLabel("Top");
      posTopField.setAllowBlank(false);
      posTopField.setRegex("^\\d+$");
      posTopField.getMessages().setRegexText("The top must be a nonnegative integer");
      posTopField.setValue("0"); // temp set top 0
      posTopField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            grid.setTop(Integer.parseInt(posTopField.getValue()));
         }
      });
      final TextField<String> widthField = new TextField<String>();
      widthField.setName("width");
      widthField.setFieldLabel("Width");
      widthField.setAllowBlank(false);
      widthField.setRegex("^[1-9][0-9]*$");
      widthField.getMessages().setRegexText("The width must be a positive integer");
      widthField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            grid.setWidth(Integer.parseInt(widthField.getValue()));
         }
      });

      final TextField<String> heightField = new TextField<String>();
      heightField.setName("height");
      heightField.setFieldLabel("Height");
      heightField.setAllowBlank(false);
      heightField.setRegex("^[1-9][0-9]*$");
      heightField.getMessages().setRegexText("The height must be a positive integer");
      heightField.addListener(Events.Blur, new Listener<BaseEvent>() {
         @Override
         public void handleEvent(BaseEvent be) {
            grid.setHeight(Integer.parseInt(heightField.getValue()));
         }
      });
      if (screen != null) {
         gridRowCountField.setValue(grid.getRowCount() + "");
         gridColumnCountField.setValue(grid.getColumnCount() + "");
         posLeftField.setValue(grid.getLeft() + "");
         posTopField.setValue(grid.getTop() + "");
         widthField.setValue(grid.getWidth() + "");
         heightField.setValue(grid.getHeight() + "");
      }
      gridAttrSet.add(gridRowCountField);
      gridAttrSet.add(gridColumnCountField);
      gridAttrSet.add(posLeftField);
      gridAttrSet.add(posTopField);
      gridAttrSet.add(widthField);
      gridAttrSet.add(heightField);
      
      leftListener = new PropertyChangeListener() {      
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          posLeftField.setValue(evt.getNewValue() + "");
        }
      };
      grid.addPropertyChangeListener("left", leftListener);
      topListener = new PropertyChangeListener() {      
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          posTopField.setValue(evt.getNewValue() + "");
        }
      };
      grid.addPropertyChangeListener("top", topListener);
      widthListener = new PropertyChangeListener() {      
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          widthField.setValue(evt.getNewValue() + "");
        }
      };
      // Size can be changed via the "same size" function, so must listen to keep displayed values up to date
      grid.addPropertyChangeListener("width", widthListener);
      heightListener = new PropertyChangeListener() {      
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          heightField.setValue(evt.getNewValue() + "");
        }
      };
      grid.addPropertyChangeListener("height", heightListener);
      
      // No property change on rows/columns count as can not be changed from any other place than here
      
      add(gridAttrSet);
      layout();
   }
   
  @Override
  public String getPropertyFormTitle() {
    return "Grid properties";
  }
  
  @Override
  protected void onUnload() {
    grid.removePropertyChangeListener("left", leftListener);
    leftListener = null;
    grid.removePropertyChangeListener("top", topListener);
    topListener = null;
    grid.removePropertyChangeListener("width", widthListener);
    widthListener = null;
    grid.removePropertyChangeListener("height", heightListener);
    heightListener = null;
    super.onUnload();
  }

}
