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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.SubmitEvent;
import org.openremote.modeler.client.gxtextends.SelectionServiceExt;
import org.openremote.modeler.client.gxtextends.SourceSelectionChangeListenerExt;
import org.openremote.modeler.client.gxtextends.TreePanelDragSourcePanelTreeExt;
import org.openremote.modeler.client.gxtextends.TreePanelDropTargetPanelTreeExt;
import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.listener.ConfirmDeleteListener;
import org.openremote.modeler.client.listener.EditDelBtnSelectionListener;
import org.openremote.modeler.client.listener.PanelTreeStoreChangeListener;
import org.openremote.modeler.client.listener.SubmitListener;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.BeanModelTable;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.widget.TreePanelBuilder;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.component.UITabbarItem;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

/**
 * The ProfilePanel .
 */
public class ProfilePanel extends ContentPanel {

   private TreePanel<BeanModel> panelTree;
   private Icons icon = GWT.create(Icons.class);
   private SelectionServiceExt<BeanModel> selectionService;
   private ScreenTab screenTab = null;
   /**
    * Instantiates a new profile panel.
    */
   public ProfilePanel(ScreenTab screenTab) {
      this.screenTab = screenTab;
      selectionService = new SelectionServiceExt<BeanModel>();
      setHeading("Panel");
      setIcon(icon.panelIcon());
      setLayout(new FitLayout());
      createMenu();
      createPanelTree(screenTab);
      new TreePanelDragSourcePanelTreeExt(panelTree);
      new TreePanelDropTargetPanelTreeExt(panelTree);
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
      selectionService.addListener(new EditDelBtnSelectionListener(editDelBtns) {
         @Override
         protected boolean isEditableAndDeletable(List<BeanModel> sels) {
            BeanModel selectModel = sels.get(0);
            if (selectModel != null) {
               return true;
            }
            return false;
         }
      });

      setTopComponent(toolBar);
   }

   /**
    * Creates the screen tree.
    */
   private void createPanelTree(ScreenTab screenTab) {
      panelTree = TreePanelBuilder.buildPanelTree(screenTab);
      selectionService.addListener(new SourceSelectionChangeListenerExt(panelTree.getSelectionModel()));
      selectionService.register(panelTree.getSelectionModel());
      LayoutContainer treeContainer = new LayoutContainer() {
         @Override
         protected void onRender(Element parent, int index) {
            super.onRender(parent, index);
//            addTreeStoreEventListener();
            new PanelTreeStoreChangeListener(panelTree);
            add(panelTree);
         }
      };
      initTreeWithAutoSavedPanels();
      treeContainer.setScrollMode(Scroll.AUTO);
      treeContainer.setStyleAttribute("backgroundColor", "white");
      treeContainer.setBorders(false);
      add(treeContainer);

   }

   private void initTreeWithAutoSavedPanels() {
      UtilsProxy.loadPanelsFromSession(new AsyncSuccessCallback<Collection<Panel>>() {
         @Override
         public void onSuccess(Collection<Panel> panels) {
            if (panels.size() > 0) {
               initModelDataBase(panels);
               panelTree.getStore().removeAll();
               for (Panel panel : panels) {
                  BeanModel panelBeanModel = panel.getBeanModel();
                  panelTree.getStore().add(panelBeanModel, false);
                  for (GroupRef groupRef : panel.getGroupRefs()) {
                     panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
                     for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                        panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
                     }
                  }
               }
               panelTree.expandAll();
               BeanModelDataBase.screenTable.addInsertListener(Constants.SCREEN_TABLE_OID, new ChangeListener() {
                  public void modelChanged(ChangeEvent event) {
                     if (event.getType() == BeanModelTable.ADD) {
                        BeanModel beanModel = (BeanModel) event.getItem();
                        if (beanModel.getBean() instanceof Screen) {
                           ScreenTabItem screenTabItem = new ScreenTabItem((Screen) beanModel.getBean());
                           screenTab.add(screenTabItem);
                           screenTab.setSelection(screenTabItem);
                        }
                     }
                  }

               });
            }
         }
         
         private void initModelDataBase(Collection<Panel> panels) {
            BeanModelDataBase.panelTable.clear();
            BeanModelDataBase.groupTable.clear();
            BeanModelDataBase.screenTable.clear();
            Set<Group> groups = new LinkedHashSet<Group>();
            Set<Screen> screens = new LinkedHashSet<Screen>();
            for (Panel panel : panels) {
               List<GroupRef> groupRefs = panel.getGroupRefs();
               for (GroupRef groupRef : groupRefs) {
                  groups.add(groupRef.getGroup());
               }
               BeanModelDataBase.panelTable.insert(panel.getBeanModel());
            }

            for (Group group : groups) {
               List<ScreenRef> screenRefs = group.getScreenRefs();
               for (ScreenRef screenRef : screenRefs) {
                  screens.add(screenRef.getScreen());
                  BeanModelDataBase.screenTable.insert(screenRef.getScreen().getBeanModel());
               }
               BeanModelDataBase.groupTable.insert(group.getBeanModel());
            }
            
         }
      });
      UtilsProxy.loadMaxID(new AsyncSuccessCallback<Long>() {
         @Override
         public void onSuccess(Long maxID) {
            if (maxID > 0) {              // set the layout component's max id after refresh page.
               IDUtil.setCurrentID(maxID.longValue());
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
      newButton.setIcon(icon.add());
      Menu newMenu = new Menu();
      newMenu.add(createNewPanelMenuItem());
      newMenu.add(createCustomPanelMenuItem());
      final MenuItem newGroupMenu = createNewGroupMenuItem();
      newMenu.add(newGroupMenu);
      final MenuItem newScreenMenu = createNewScreenMenuItem();
      newMenu.add(newScreenMenu);
      final MenuItem configTabbarItem = createConfigTabbarMenuItem();
      newMenu.add(configTabbarItem);
      newMenu.addListener(Events.BeforeShow, new Listener<MenuEvent>() {
         @Override
         public void handleEvent(MenuEvent be) {
            boolean enabled = false;
            BeanModel selectedBeanModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null && !(selectedBeanModel.getBean() instanceof ScreenRef)) {
               enabled = true;
            }
            configTabbarItem.setEnabled(enabled);
            if (BeanModelDataBase.panelTable.loadAll().size() > 0) {
               newGroupMenu.setEnabled(true);
            } else {
               newGroupMenu.setEnabled(false);
            }
            if (BeanModelDataBase.groupTable.loadAll().size() > 0) {
               newScreenMenu.setEnabled(true);
            } else {
               newScreenMenu.setEnabled(false);
            }
         }
         
      });
      newButton.setMenu(newMenu);
      return newButton;
   }

   /**
    * Creates the edit btn.
    * 
    * @return the button
    */
   private Button createEditBtn() {
      Button editBtn = new Button("Edit");
      editBtn.setIcon(icon.edit());
      editBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
         @Override
         public void componentSelected(ButtonEvent ce) {
            BeanModel selectedModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedModel != null) {
               if (selectedModel.getBean() instanceof Panel) {
                  editPanel(selectedModel);
               } else if (selectedModel.getBean() instanceof GroupRef) {
                  editGroup(selectedModel);
               } else if (selectedModel.getBean() instanceof ScreenRef) {
                  editScreen(selectedModel);
               }
            }
         }
      });
      return editBtn;
   }

   /**
    * Edits the group.
    * 
    * @param panelBeanModel
    *           the group bean model
    */
   private void editPanel(final BeanModel panelBeanModel) {
      final PanelWindow panelWindow = new PanelWindow(panelBeanModel);
      panelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            panelWindow.hide();
            Panel panel = be.<Panel> getData();
            panelTree.getStore().update(panel.getBeanModel());
            Info.display("Info", "Edit panel " + panel.getName() + " success.");
         }
         
      });
   }
   private void editGroup(final BeanModel groupRefBeanModel) {
      final GroupWizardWindow groupWindow = new GroupWizardWindow(groupRefBeanModel, true);
      groupWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            groupWindow.hide();
            BeanModel groupRefModel = be.getData();
            GroupRef groupRef = groupRefModel.getBean();
            panelTree.getStore().removeAll(groupRefModel);
            for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
               panelTree.getStore().add(groupRefModel, screenRef.getBeanModel(), false);
            }
            panelTree.getStore().update(groupRefModel);
            BeanModelDataBase.groupTable.update(groupRef.getGroup().getBeanModel());
            panelTree.setExpanded(groupRefModel, true);
            panelTree.getSelectionModel().select(groupRefModel, false);
            Info.display("Info", "Add Group " + groupRef.getGroup().getName() + " success.");
         }
      });
   }
   private void editScreen(final BeanModel panelBeanModel) {
      final ScreenWindow screenWizard = new ScreenWindow(screenTab, panelBeanModel, ScreenWindow.Operation.EDIT);
      screenWizard.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
         @Override
         public void afterSubmit(SubmitEvent be) {
            ScreenRef screenRef = be.<ScreenRef> getData();
           /* BeanModel oldGroupBeanModel = screenWizard.getSelectedGroupRefModel();
            BeanModel newGroupBeanModel = panelTree.getStore().getParent(screenWizard.getSelectItem());
            if(!oldGroupBeanModel.equals(newGroupBeanModel)){
               panelTree.getStore().remove(screenRef.getBeanModel());
               panelTree.getStore().add(newGroupBeanModel, screenRef.getBeanModel(),false);
            }*/
            screenWizard.hide();
            panelTree.getStore().update(screenRef.getBeanModel());
            BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
            Info.display("Info", "Edit screen " + screenRef.getScreen().getName() + " success.");
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
      deleteBtn.setIcon(icon.delete());
      deleteBtn.addSelectionListener(new ConfirmDeleteListener<ButtonEvent>() {
         @Override
         public void onDelete(ButtonEvent ce) {
            List<BeanModel> selectedModels = panelTree.getSelectionModel().getSelectedItems();
            for (BeanModel selectedModel : selectedModels) {
               if (selectedModel != null && selectedModel.getBean() instanceof Panel) {
                  Panel panel = selectedModel.getBean();
                  BeanModelDataBase.panelTable.delete(selectedModel);
                  panelTree.getStore().remove(selectedModel);
                  for (GroupRef groupRef : panel.getGroupRefs()) {
                     groupRef.getGroup().releaseRef();
                     if (groupRef.getGroup().getRefCount() == 0) {
                        BeanModelDataBase.groupTable.delete(groupRef.getGroupId());
                     }
                     for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                        screenRef.getScreen().releaseRef();
                        if (screenRef.getScreen().getRefCount() == 0) {
                           BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                        }
                     }
                  }
                  Info.display("Info", "Delete panel " + selectedModel.get("name") + " success.");
               } else if (selectedModel != null && selectedModel.getBean() instanceof GroupRef) {
                  panelTree.getStore().remove(selectedModel);
                  GroupRef groupRef = selectedModel.getBean();
                  groupRef.getPanel().removeGroupRef(groupRef);
                  groupRef.getGroup().releaseRef();
                  if (groupRef.getGroup().getRefCount() == 0) {
                     BeanModelDataBase.groupTable.delete(groupRef.getGroupId());
                  }
                  for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                     screenRef.getScreen().releaseRef();
                     if (screenRef.getScreen().getRefCount() == 0) {
                        BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                     }
                  }
               } else if (selectedModel != null && selectedModel.getBean() instanceof ScreenRef) {
                  ScreenRef screenRef = (ScreenRef) selectedModel.getBean();
                  screenRef.getGroup().removeScreenRef(screenRef);
                  panelTree.getStore().remove(selectedModel);
                  screenRef.getScreen().releaseRef();
                  if (screenRef.getScreen().getRefCount() == 0) {
                     BeanModelDataBase.screenTable.delete(screenRef.getScreenId());
                  }
               }
            }
         }
      });
      return deleteBtn;
   }

   private void afterCreatePanel(Panel panel) {
      BeanModel panelBeanModel = panel.getBeanModel();
      panelTree.getStore().add(panelBeanModel, false);
      for (GroupRef groupRef : panel.getGroupRefs()) {
         panelTree.getStore().add(panelBeanModel, groupRef.getBeanModel(), false);
         for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
            panelTree.getStore().add(groupRef.getBeanModel(), screenRef.getBeanModel(), false);
         }
      }
      panelTree.setExpanded(panelBeanModel, true, true);
      panelTree.getSelectionModel().select(panelBeanModel, false);
      Info.display("Info", "Create Panel " + panel.getName() + " success.");
   }

   private MenuItem createNewPanelMenuItem() {
      MenuItem newPanelItem = new MenuItem("New Panel");
      newPanelItem.setIcon(icon.panelIcon());
      newPanelItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final PanelWindow panelWindow = new PanelWindow();
            panelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  panelWindow.hide();
                  afterCreatePanel(be.<Panel> getData());
               }

            });
            
         }
      });
      return newPanelItem;
   }
   
   private MenuItem createCustomPanelMenuItem() {
      MenuItem customPanelItem = new MenuItem("Custom Panel");
      customPanelItem.setIcon(icon.panelIcon());
      customPanelItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            final CustomPanelWindow customPanelWindow = new CustomPanelWindow();
            customPanelWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  customPanelWindow.hide();
                  afterCreatePanel(be.<Panel> getData());
               }

            });
            
         }
      });
      return customPanelItem;
   }
   private MenuItem createNewGroupMenuItem() {
      MenuItem newGroupItem = new MenuItem("New Group");
      newGroupItem.setIcon(icon.activityIcon());
      newGroupItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            Group group = new Group();
            group.setOid(IDUtil.nextID());
            GroupRef groupRef = new GroupRef(group);
            BeanModel selectedBeanModel = panelTree.getSelectionModel().getSelectedItem();
            if (selectedBeanModel != null && selectedBeanModel.getBean() instanceof Panel) {
               groupRef.setPanel((Panel) selectedBeanModel.getBean());
            }
            final GroupWizardWindow  groupWizardWindow = new GroupWizardWindow(groupRef.getBeanModel(), false);
            groupWizardWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  groupWizardWindow.hide();
                  BeanModel groupRefModel = be.getData();
                  GroupRef groupRef = groupRefModel.getBean();
                  panelTree.getStore().add(groupRef.getPanel().getBeanModel(), groupRefModel, false);
                  for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
                     panelTree.getStore().add(groupRefModel, screenRef.getBeanModel(), false);
                  }
                  BeanModelDataBase.groupTable.insert(groupRef.getGroup().getBeanModel());
                  panelTree.setExpanded(groupRefModel, true);
                  panelTree.getSelectionModel().select(groupRefModel, false);
                  Info.display("Info", "Add Group " + groupRef.getGroup().getName() + " success.");
                  
               }
            });
         }
         
      });
      return newGroupItem;
   }
   
   private MenuItem createNewScreenMenuItem() {
      MenuItem newScreenItem = new MenuItem("New Screen");
      newScreenItem.setIcon(icon.screenIcon());
      newScreenItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         public void componentSelected(MenuEvent ce) {
            BeanModel selectItem = panelTree.getSelectionModel().getSelectedItem();
            final ScreenWindow screenWindow = new ScreenWindow(screenTab, selectItem);
            screenWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
               @Override
               public void afterSubmit(SubmitEvent be) {
                  screenWindow.hide();
                  ScreenRef screenRef = be.<ScreenRef>getData();
                  panelTree.getStore().add(screenWindow.getSelectedGroupRefModel(), screenRef.getBeanModel(), false);
                  panelTree.setExpanded(screenWindow.getSelectedGroupRefModel(), true);
                  panelTree.getSelectionModel().select(screenRef.getBeanModel(), false);
               }

            });

         }
      });
      return newScreenItem;
   }

   private MenuItem createConfigTabbarMenuItem() {
      MenuItem configTabbarItem = new MenuItem("Config tabbar");
      configTabbarItem.setIcon(icon.tabbarConfigIcon());
      configTabbarItem.addSelectionListener(new SelectionListener<MenuEvent>() {
         @Override
         public void componentSelected(MenuEvent ce) {
            final BeanModel selectItem = panelTree.getSelectionModel().getSelectedItem();
            if (selectItem != null) {
               if (selectItem.getBean() instanceof Panel) {
                  final TabbarWindow tabbarWindow = new TabbarWindow(true, ((Panel) selectItem.getBean())
                        .getTabbarItems(), (Panel) selectItem.getBean());
                  tabbarWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                     @Override
                     public void afterSubmit(SubmitEvent be) {
                    	 List<UITabbarItem> tabbarItems = be.<List<UITabbarItem>>getData();
                    	 if (tabbarItems.size() > 0) {
                    		 ((Panel) selectItem.getBean()).setTabbarItems(tabbarItems);
                    		 for (GroupRef groupRef : ((Panel) selectItem.getBean()).getGroupRefs()) {
								for (ScreenRef screenRef : groupRef.getGroup().getScreenRefs()) {
									screenRef.getScreen().setHasTabbar(true);
									BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
								}
							}
                    	 }
                        tabbarWindow.hide();
                     }
                  });
               } else if (selectItem.getBean() instanceof GroupRef) {
                  final Group group = ((GroupRef) selectItem.getBean()).getGroup();
                  final TabbarWindow tabbarWindow = new TabbarWindow(false, group.getTabbarItems(),
                        ((GroupRef) selectItem.getBean()).getPanel());
                  tabbarWindow.addListener(SubmitEvent.SUBMIT, new SubmitListener() {
                     @Override
                     public void afterSubmit(SubmitEvent be) {
                        List<UITabbarItem> tabbarItems = be.<List<UITabbarItem>> getData();
                        if (tabbarItems.size() > 0) {
                           group.setTabbarItems(tabbarItems);
                           for (ScreenRef screenRef : group.getScreenRefs()) {
                              screenRef.getScreen().setHasTabbar(true);
                              BeanModelDataBase.screenTable.update(screenRef.getScreen().getBeanModel());
                           }
                        }

                        tabbarWindow.hide();
                     }
                  });
               }
               panelTree.getStore().update(selectItem);
            }
         }
      });
      return configTabbarItem;
   }
   public TreePanel<BeanModel> getPanelTree() {
      return panelTree;
   }

   public void setPanelTree(TreePanel<BeanModel> panelTree) {
      this.panelTree = panelTree;
   }

   public ScreenTab getScreenTab() {
      return screenTab;
   }

   public void setScreenTab(ScreenTab screenTab) {
      this.screenTab = screenTab;
   }   
   
}
