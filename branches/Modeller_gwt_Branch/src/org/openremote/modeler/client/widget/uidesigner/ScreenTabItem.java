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

import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.UIButton;

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
   
   private ScreenPanel screenPanel;
   
   private int row;
   
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
      toolBar.add(createDeleteBtn());
      add(toolBar);
   }
   private Button createRenameBtn(){
      Button renameBtn = new Button("Rename Button");
      renameBtn.setIcon(icon.edit());
      renameBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            final ScreenButton selectedButton = screenPanel.getSelectedButton();
            if(selectedButton != null){
               final RenameButtonWindow renameButtonWindow = new RenameButtonWindow((UIButton)selectedButton.getData("button"));
               renameButtonWindow.addListener(SubmitEvent.Submit, new SubmitListener() {
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
   private Button createDeleteBtn(){
      Button deleteBtn = new Button("Delete");
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            ScreenButton selectButton = screenPanel.getSelectedButton();
            if(selectButton != null){
               screen.deleteButton((UIButton)selectButton.getData(ScreenButton.DATA_BUTTON));
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
    * Creates the screen background.
    */
   private void createScreenPanel(Screen screen) {
      screenPanel = new ScreenPanel(screen);
      add(screenPanel);
      
   }

   public Screen getScreen(){
      return screen;
   }

   public int getRow() {
      return row;
   }

   public void setRow(int row) {
      this.row = row;
   }

   public int getColumn() {
      return column;
   }

   public void setColumn(int column) {
      this.column = column;
   }
   
}
