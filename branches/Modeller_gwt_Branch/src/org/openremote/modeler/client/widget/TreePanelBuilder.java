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
package org.openremote.modeler.client.widget;

import java.util.List;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.model.TreeFolderBean;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.client.widget.uidesigner.ScreenTabItem;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UISwitch;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * The Class is used for create tree.
 */
public class TreePanelBuilder {

   /**
    * Not be instantiated.
    */
   private TreePanelBuilder() {
   }

   /** The Constant icon. */
   private static final Icons ICON = GWT.create(Icons.class);

   /** The device command treestore. */
   private static TreeStore<BeanModel> deviceCommandTreestore = null;

   /** The macro tree store. */
   private static TreeStore<BeanModel> macroTreeStore = null;
   
   /** The screen tree store. */
   private static TreeStore<BeanModel> screenTreeStore = null;
   
   /** The group tree store. */
   private static TreeStore<BeanModel> groupTreeStore = null;
   private static TreeStore<BeanModel> widgetTreeStore = null;
   private static TreeStore<BeanModel> panelTreeStore = null;
   
   
   /**
    * Builds a device command tree.
    * 
    * @return the a new device command tree
    */
   public static TreePanel<BeanModel> buildDeviceCommandTree() {
      if (deviceCommandTreestore == null) {
         RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
            @Override
            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
                DeviceBeanModelProxy.loadDevice((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {
                    public void onSuccess(List<BeanModel> result) {
                        listAsyncCallback.onSuccess(result);
                    }
                });
            }
        };
        TreeLoader<BeanModel> loadDeviceTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceRPCProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
                if (beanModel.getBean() instanceof Device) {
                    return true;
                }
                return false;
            }
        };
        deviceCommandTreestore = new TreeStore<BeanModel>(loadDeviceTreeLoader);
      }
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(deviceCommandTreestore);

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof DeviceCommand) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof Device) {
               return ICON.device();
            } else {
               return ICON.folder();
            }
         }

      });
      return tree;
   }
   
   /**
    * Builds a new macro tree.
    * 
    * @return a new macro tree
    */
   public static TreePanel<BeanModel> buildMacroTree() {
      if (macroTreeStore == null) {
         RpcProxy<List<BeanModel>> loadDeviceMacroRPCProxy = new RpcProxy<List<BeanModel>>() {

            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
               DeviceMacroBeanModelProxy.loadDeviceMaro((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {

                  public void onSuccess(List<BeanModel> result) {
                     listAsyncCallback.onSuccess(result);
                  }
               });
            }
         };
         BaseTreeLoader<BeanModel> loadDeviceMacroTreeLoader = new BaseTreeLoader<BeanModel>(loadDeviceMacroRPCProxy) {
            @Override
            public boolean hasChildren(BeanModel beanModel) {
               if (beanModel.getBean() instanceof DeviceMacro) {
                  return true;
               }
               return false;
            }
         };
         macroTreeStore = new TreeStore<BeanModel>(loadDeviceMacroTreeLoader);
      }

      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(macroTreeStore);
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("displayName");
      
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {

            if (thisModel.getBean() instanceof DeviceMacro) {
               return ICON.macroIcon();
            } else if (thisModel.getBean() instanceof DeviceCommandRef) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof CommandDelay) {
               return ICON.delayIcon();
            } else {
               return ICON.macroIcon();
            }
         }
      });
      return tree;
   }
   
   /**
    * Builds the screen tree.
    * 
    * @return the tree panel< bean model>
    */
   public static TreePanel<BeanModel> buildScreenTree(final ScreenTab screenTab) {
      if (screenTreeStore == null) {
         screenTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> screenTree = new TreePanel<BeanModel>(screenTreeStore) {
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONDBLCLICK) {
               BeanModel beanModel = this.getSelectionModel().getSelectedItem();
               if (beanModel.getBean() instanceof Screen) {
                  Screen screen = beanModel.getBean();
                  ScreenTabItem screenTabItem = null;
                  for (TabItem tabPanel : screenTab.getItems()) {
                     screenTabItem = (ScreenTabItem) tabPanel;
                     if (screen == screenTabItem.getScreen()) {
                        screenTab.setSelection(screenTabItem);
                        return;
                     } else {
                        screenTabItem = null;
                     }
                  }
                  if (screenTabItem == null) {
                     screenTabItem = new ScreenTabItem(screen);
                     screenTab.add(screenTabItem);
                     screenTab.setSelection(screenTabItem);
                  }
               }
            }
            
            super.onBrowserEvent(event);
         }
         
      
      };
      screenTree.setStateful(true);
      screenTree.setBorders(false);
      screenTree.setHeight("100%");      
      screenTree.setDisplayProperty("displayName");
      
      screenTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            return ICON.screenIcon();
         }
      });
      
      return screenTree;
   }
   
   /**
    * Builds the group tree.
    * 
    * @return the tree panel< bean model>
    */
   public static TreePanel<BeanModel> buildGroupTree() {
      if (groupTreeStore == null) {
         groupTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> groupTree = new TreePanel<BeanModel>(groupTreeStore);
      groupTree.setStateful(true);
      groupTree.setBorders(false);
      groupTree.setHeight("100%");
      groupTree.setDisplayProperty("displayName");
      TreeFolderBean folderBean = new TreeFolderBean();
      folderBean.setDisplayName("groups");
      groupTreeStore.add(folderBean.getBeanModel(), true);

      groupTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Group) {
               return ICON.activityIcon();
            } else if (thisModel.getBean() instanceof ScreenRef) {
               return ICON.screenIcon();
            } else {
               return ICON.activityIcon();
            }
         }
      });

      return groupTree;
   }
   
   /**
    * Builds the widget tree, it contain all kind's of component.
    * 
    * @return the tree panel< bean model>
    */
   public static TreePanel<BeanModel> buildWidgetTree() {
      if (widgetTreeStore == null) {
         widgetTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> widgetTree = new TreePanel<BeanModel>(widgetTreeStore);
      widgetTree.setStateful(true);
      widgetTree.setBorders(false);
      widgetTree.setHeight("100%");
      widgetTree.setDisplayProperty("name");
//      TreeFolderBean folderBean = new TreeFolderBean();
//      folderBean.setDisplayName("groups");
      widgetTreeStore.add(new UIButton().getBeanModel(), true);
      widgetTreeStore.add(new UISwitch().getBeanModel(), true);
      widgetTreeStore.add(new UIGrid().getBeanModel(), true);
      
      widgetTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof UIButton) {
               return ICON.buttonIcon();
            } else if (thisModel.getBean() instanceof UISwitch) {
               return ICON.switchIcon();
            } else if (thisModel.getBean() instanceof UIGrid) {
               return ICON.gridIcon();
            } else{
               return ICON.buttonIcon();
            }
         }
      });
      
      return widgetTree;
   }
   
   public static TreePanel<BeanModel> buildPanelTree() {
      if (panelTreeStore == null) {
         panelTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> panelTree = new TreePanel<BeanModel>(panelTreeStore);
      panelTree.setStateful(true);
      panelTree.setBorders(false);
      panelTree.setHeight("100%");
      panelTree.setDisplayProperty("displayName");
//      TreeFolderBean folderBean = new TreeFolderBean();
//      folderBean.setDisplayName("groups");
//      groupTreeStore.add(folderBean.getBeanModel(), true);

      panelTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Panel) {
               return ICON.panelIcon();
            } else if (thisModel.getBean() instanceof GroupRef) {
               return ICON.activityIcon();
            } else {
               return ICON.panelIcon();
            }
         }
      });

      return panelTree;
   }
   
}
