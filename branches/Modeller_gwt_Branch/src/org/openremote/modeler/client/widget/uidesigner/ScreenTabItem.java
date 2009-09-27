/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.widget.uidesigner;

import java.util.List;
import java.util.Map;

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UIButton;
import org.openremote.modeler.touchpanel.TouchPanelDefinition;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;

/**
 * The Class ScreenTabItem contain a screenPanel.
 */
public class ScreenTabItem extends TabItem {


   /** The screen. */
   private Screen screen;

   /** The icon. */
   private Icons icon = GWT.create(Icons.class);
   
   /** The screen panel. */
   private ScreenPanel screenPanel;
   
   /** The row. */
   private int row;
   
   /** The column. */
   private int column;
   
   /**
    * Instantiates a new screen panel.
    * 
    * @param s
    *           the s
    */
   public ScreenTabItem(Screen s) {
      screen = s;
      setText(screen.getName());
      setRow(screen.getRowCount());
      setColumn(screen.getColumnCount());
      setClosable(true);
      setLayout(new FlowLayout());
      createToolBar();
      createScreenPanel(s);

   }

   /**
    * Creates the tool bar.
    */
   private void createToolBar() {
      ToolBar toolBar = new ToolBar();
      toolBar.add(createRenameBtn());
      toolBar.add(createChangeIconBtn());
      toolBar.add(createDeleteBtn());
      add(toolBar);
   }
   
   /**
    * Creates the rename btn.
    * 
    * @return the button
    */
   private Button createRenameBtn() {
      Button renameBtn = new Button("Rename Button");
      renameBtn.setIcon(icon.edit());
      renameBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final ScreenButton selectedButton = screenPanel.getSelectedButton();
            if (selectedButton != null) {
               final RenameButtonWindow renameButtonWindow = new RenameButtonWindow((UIButton) selectedButton
                     .getData("button"));
               renameButtonWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                  @Override
                  public void afterSubmit(SubmitEvent be) {
                     renameButtonWindow.hide();
                     UIButton button = be.getData();
                     selectedButton.setLabel(button.getLabel());
                     layout();
                     Info.display("Info", "Edit device " + button.getLabel() + " success.");
                  }
               });
            } else {
               MessageBox.info("Warning", "Please select a button.", null);
            }
         }
      });
      return renameBtn;
   }
   
   /**
    * Creates the change icon btn.
    * 
    * @return the button
    */
   private Button createChangeIconBtn() {
      Button changeIconBtn = new Button("Change Icon");
      changeIconBtn.setIcon(icon.changeIcon());
      changeIconBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final ScreenButton selectedButton = screenPanel.getSelectedButton();
            if (selectedButton != null) {
               final ChangeIconWindow changeIconWindow = new ChangeIconWindow(selectedButton);
               changeIconWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                  @Override
                  public void afterSubmit(SubmitEvent be) {
                     String icon = be.getData();
                     selectedButton.setIcon(icon);
                     layout();
                     Info.display("Info", "Change icon success.");
                  }
               });
            } else {
               MessageBox.info("Warning", "Please select a button.", null);
            }
         }

      });
      return changeIconBtn;
   }
   
   /**
    * Creates the delete btn.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            ScreenButton selectButton = screenPanel.getSelectedButton();
            if (selectButton != null) {
               screen.deleteButton((UIButton) selectButton.getData(ScreenButton.DATA_BUTTON));
               screenPanel.delete(selectButton);
               layout();
            } else {
               MessageBox.info("Warning", "Please select a button.", null);
            }
         }
      });
      return deleteBtn;
   }
   
   /**
    * Creates the screen panel.
    * 
    * @param screen the screen
    */
   private void createScreenPanel(Screen screen) {
      Map<String, List<TouchPanelDefinition>> panels = TouchPanels.getInstance();
      TouchPanelDefinition panelDefinition = panels.get("iphone").get(0);
      screenPanel = new ScreenPanel(screen, panelDefinition);
      add(screenPanel);
      
   }

   /**
    * Gets the screen.
    * 
    * @return the screen
    */
   public Screen getScreen() {
      return screen;
   }

   /**
    * Gets the row.
    * 
    * @return the row
    */
   public int getRow() {
      return row;
   }

   /**
    * Sets the row.
    * 
    * @param row the new row
    */
   public void setRow(int row) {
      this.row = row;
   }

   /**
    * Gets the column.
    * 
    * @return the column
    */
   public int getColumn() {
      return column;
   }

   /**
    * Sets the column.
    * 
    * @param column the new column
    */
   public void setColumn(int column) {
      this.column = column;
   }
   
}
