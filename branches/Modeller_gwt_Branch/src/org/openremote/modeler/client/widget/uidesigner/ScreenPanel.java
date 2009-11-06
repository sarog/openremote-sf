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

import java.util.ArrayList;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.ScreenBeanModelProxy;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.UIScreen;
import org.openremote.modeler.selenium.DebugId;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * The Class UIScreenPanel.
 */
public class ScreenPanel extends ContentPanel {

   /** The screen tree. */
   private TreePanel<BeanModel> screenTree;
   
   /** The icon. */
   private Icons icon = GWT.create(Icons.class);
   
   /** The selection service. */
   private SelectionServiceExt<BeanModel> selectionService;
   
   /**
    * Instantiates a new uI screen panel.
    */
   public ScreenPanel(ScreenTab screenTab) {
      selectionService = new SelectionServiceExt<BeanModel>();
      setHeading("Screen");
      setIcon(icon.screenIcon());
      setLayout(new FitLayout());
      createMenu();
      createScreenTree(screenTab);
   }
   
   /**
    * Creates the menu.
    */
   private void createMenu() {
      ToolBar toolBar = new ToolBar();
      List<Button> editDelBtns = new ArrayList<Button>();
      toolBar.add(createNewBtn());
      
      Button editBtn = createEditBtn();
      editBtn.setEnabled(false);
      Button deleteBtn = createDeleteBtn();
      deleteBtn.setEnabled(false);
      
      toolBar.add(editBtn);
      toolBar.add(deleteBtn);
      editDelBtns.add(editBtn);
      editDelBtns.add(deleteBtn);
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns));
      setTopComponent(toolBar);
   }
   
   /**
    * Creates the screen tree.
    */
   private void createScreenTree(ScreenTab screenTab) {
      screenTree = TreePanelBuilder.buildScreenTree(screenTab);
      selectionService.addListener(new SourceSelectionChangeListenerExt(screenTree.getSelectionModel()));
      selectionService.register(screenTree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
            add(screenTree);
         }
      };
      initTreeWithAutoSavedScreens(screenTab);
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);
   }
   
   private void initTreeWithAutoSavedScreens(final ScreenTab screenTab) {
      UtilsProxy.loadMaxID(new AsyncSuccessCallback<Long>(){
         @Override
         public void onSuccess(Long maxID) {
            if (maxID > 0) {              // set the layout component's max id after refresh page.
               IDUtil.setCurrentID(maxID.longValue());
            }
         }
         
      });
      UtilsProxy.loadScreensFromSession(new AsyncSuccessCallback<List<UIScreen>>(){
         @Override
         public void onSuccess(List<UIScreen> screens) {
            if(screens.size() > 0) {
               screenTree.getStore().removeAll();
               BeanModelDataBase.screenTable.clear();
               for (UIScreen screen : screens) {
                  BeanModel screenBeanModel = screen.getBeanModel();
                  screenTree.getStore().add(screenBeanModel, false);
                  BeanModelDataBase.screenTable.insert(screenBeanModel);
               }
               BeanModelDataBase.screenTable.addInsertListener(Constants.SCREEN_TABLE_OID, new ChangeListener() {
                  public void modelChanged(ChangeEvent event) {
                     if (event.getType() == BeanModelTable.ADD) {
                        BeanModel beanModel = (BeanModel) event.getItem();
                        if (beanModel.getBean() instanceof UIScreen) {
                           ScreenTabItem screenTabItem = new ScreenTabItem((UIScreen) beanModel.getBean());
                           screenTab.add(screenTabItem);
                           screenTab.setSelection(screenTabItem);
                        }
                     }
                  }

               });
            }
         }
         
      });
   }
   
   /**
    * Creates the new btn.
    * 
    * @return the button
    */
   private Button createNewBtn() {
      Button newButton = new Button("New");
      newButton.ensureDebugId(DebugId.SCREEN_NEW_BTN);
      newButton.setIcon(icon.add());
      newButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            createScreen();
         }
         
      });
      return newButton;
   }
   
   /**
    * Creates the screen.
    */
   private void createScreen() {
      final ScreenWindow screenWindow = new ScreenWindow();
      screenWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){
         @Override
         public void afterSubmit(SubmitEvent be) {
            screenWindow.hide();
            BeanModel screenModel = be.getData();
            screenTree.getStore().add(screenModel, true);
            screenTree.getSelectionModel().select(screenModel, false);
            Info.display("Info", "Add screen " + screenModel.get("name") + " success.");
         }
         
      });
   }
   
   /**
    * Creates the edit btn.
    * 
    * @return the button
    */
   private Button createEditBtn() {
      Button editBtn = new Button("Edit");
      editBtn.ensureDebugId(DebugId.SCREEN_EDIT_BTN);
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel screenModel = screenTree.getSelectionModel().getSelectedItem();
            if (screenModel != null && (screenModel.getBean() instanceof UIScreen)) {
               editScreen(screenModel);
            }
         }
      });
      return editBtn;
   }
   
   /**
    * Edits the screen.
    * 
    * @param selectedModel the selected model
    */
   private void editScreen(BeanModel selectedModel) {
      final ScreenWindow screenWindow = new ScreenWindow((UIScreen) selectedModel.getBean());
      screenWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener(){
         @Override
         public void afterSubmit(SubmitEvent be) {
            screenWindow.hide();
            BeanModel screenModel = be.getData();
            screenTree.getStore().update(screenModel);
            Info.display("Info", "Edit screen " + screenModel.get("name") + " success.");
         }
         
      });
   }
   
   /**
    * Creates the delete btn.
    * 
    * @return the button
    */
   private Button createDeleteBtn() {
      Button deleteBtn = new Button("Delete");
      deleteBtn.ensureDebugId(DebugId.SCREEN_DELETE_BTN);
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = screenTree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null) {
                  ScreenBeanModelProxy.deleteScreen(selectedModel);
                  screenTree.getStore().remove(selectedModel);
                  Info.display("Info", "Delete screen " + selectedModel.get("name") + " success.");
               } 
            }
         }
      });
      return deleteBtn;
   }
   
}
