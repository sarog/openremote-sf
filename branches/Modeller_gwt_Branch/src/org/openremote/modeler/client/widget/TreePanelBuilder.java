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
import java.util.Set;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.ConfigCategoryBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceCommandBeanModelProxy;
import org.openremote.modeler.client.proxy.DeviceMacroBeanModelProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.DeviceBeanModelTable;
import org.openremote.modeler.client.utils.DeviceMacroBeanModelTable;
import org.openremote.modeler.client.utils.DeviceBeanModelTable.DeviceInsertListener;
import org.openremote.modeler.client.utils.DeviceMacroBeanModelTable.DeviceMacroInsertListener;
import org.openremote.modeler.client.widget.buildingmodeler.ControllerConfigTabItem;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.client.widget.uidesigner.ScreenTabItem;
import org.openremote.modeler.domain.CommandDelay;
import org.openremote.modeler.domain.ConfigCategory;
import org.openremote.modeler.domain.Device;
import org.openremote.modeler.domain.DeviceCommand;
import org.openremote.modeler.domain.DeviceCommandRef;
import org.openremote.modeler.domain.DeviceMacro;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.Screen;
import org.openremote.modeler.domain.ScreenRef;
import org.openremote.modeler.domain.Sensor;
import org.openremote.modeler.domain.Slider;
import org.openremote.modeler.domain.Switch;
import org.openremote.modeler.domain.UICommand;
import org.openremote.modeler.domain.component.UIButton;
import org.openremote.modeler.domain.component.UIGrid;
import org.openremote.modeler.domain.component.UIImage;
import org.openremote.modeler.domain.component.UILabel;
import org.openremote.modeler.domain.component.UISlider;
import org.openremote.modeler.domain.component.UISwitch;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
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
   private static TreeStore<BeanModel> deviceTreeStore = null;

   private static TreeStore<BeanModel> deviceAndCmdTreeStore = null;
   /** The macro tree store. */
   private static TreeStore<BeanModel> macroTreeStore = null;
   
   private static TreeStore<BeanModel> widgetTreeStore = null;
   private static TreeStore<BeanModel> panelTreeStore = null;
   private static TreeStore<BeanModel> controllerConfigCategoryTreeStore = null;
   
   /**
    * Builds a device command tree.
    * 
    * @return the a new device command tree
    */
   public static TreePanel<BeanModel> buildDeviceCommandTree() {
//      if (commandTreeStore == null) {
         RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
            @Override
            protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
                DeviceBeanModelProxy.loadDeviceAndCommand((BeanModel) o, new AsyncSuccessCallback<List<BeanModel>>() {
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
        deviceAndCmdTreeStore = new TreeStore<BeanModel>(loadDeviceTreeLoader);
//      }
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(deviceAndCmdTreeStore);

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
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
   
   public static TreePanel<BeanModel> buildDeviceTree() {
      if (deviceTreeStore == null) {
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
               if (beanModel.getBean() instanceof DeviceCommand || beanModel.getBean() instanceof UICommand) {
                   return false;
               }
               return true;
           }
        };
        deviceTreeStore = new TreeStore<BeanModel>(loadDeviceTreeLoader);
      }
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(deviceTreeStore);
      ((DeviceBeanModelTable) BeanModelDataBase.deviceTable)
            .addDeviceInsertListener(new DeviceInsertListener<BeanModel>() {

               @Override
               public void handleInsert(BeanModel beanModel) {
                  if (beanModel != null && beanModel.getBean() instanceof Device) {
                     if (!deviceTreeStore.contains(beanModel)) {
                        deviceTreeStore.add(beanModel, false);
                        tree.getSelectionModel().select(beanModel, true);
                     }
                  }
               }

            });

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
      tree.setHeight("100%");
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof DeviceCommand) {
               return ICON.deviceCmd();
            } else if (thisModel.getBean() instanceof Device) {
               return ICON.device();
            } else if(thisModel.getBean() instanceof Sensor){
               return ICON.sensorIcon();
            } else if(thisModel.getBean() instanceof Switch){
               return ICON.switchIcon();
            } else if(thisModel.getBean() instanceof Slider){
               return ICON.sliderIcon();
            } else if(thisModel.getBean() instanceof UICommand){
               return ICON.deviceCmd();
            } else {
               return ICON.folder();
            }
         }

      });
      return tree;
   }
   
   public static TreePanel<BeanModel> buildCommandTree(final Device device) {
      RpcProxy<List<BeanModel>> loadDeviceRPCProxy = new RpcProxy<List<BeanModel>>() {
         @Override
         protected void load(Object o, final AsyncCallback<List<BeanModel>> listAsyncCallback) {
            DeviceCommandBeanModelProxy.loadDeviceCmdFromDevice(device,
                  new AsyncSuccessCallback<List<DeviceCommand>>() {

                     @Override
                     public void onSuccess(List<DeviceCommand> result) {
                        listAsyncCallback.onSuccess(DeviceCommand.createModels(result));
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
      TreeStore<BeanModel> commandTree = new TreeStore<BeanModel>(loadDeviceTreeLoader);
      final TreePanel<BeanModel> tree = new TreePanel<BeanModel>(commandTree);

      tree.setBorders(false);
      tree.setStateful(true);
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
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
      ((DeviceMacroBeanModelTable)BeanModelDataBase.deviceMacroTable).addDeviceMacroInsertListener(new DeviceMacroInsertListener<BeanModel> (){

         @Override
               public void handleInsert(BeanModel beanModel) {
                  if (beanModel != null && beanModel.getBean() instanceof DeviceMacro) {
                     if (!macroTreeStore.contains(beanModel)) {
                        macroTreeStore.add(beanModel, false);
                        tree.getSelectionModel().select(beanModel, true);
                     }
                  }
               }

        });
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("displayName");
      tree.setStyleAttribute("overflow", "auto");
      
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
      widgetTree.setStyleAttribute("overflow", "auto");
      
      widgetTreeStore.add(new UIGrid().getBeanModel(), true);
      widgetTreeStore.add(new UILabel().getBeanModel(), true);
      widgetTreeStore.add(new UIImage().getBeanModel(), true);
      widgetTreeStore.add(new UIButton().getBeanModel(), true);
      widgetTreeStore.add(new UISwitch().getBeanModel(), true);
      widgetTreeStore.add(new UISlider().getBeanModel(), true);
      
      widgetTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof UIButton) {
               return ICON.buttonIcon();
            } else if (thisModel.getBean() instanceof UISwitch) {
               return ICON.switchIcon();
            } else if (thisModel.getBean() instanceof UILabel) {
               return ICON.labelIcon();
            } else if (thisModel.getBean() instanceof UIImage) {
               return ICON.imageIcon();
            } else if (thisModel.getBean() instanceof UISlider) {
               return ICON.sliderIcon();
            } else if (thisModel.getBean() instanceof UIGrid) {
               return ICON.gridIcon();
            } else {
               return ICON.buttonIcon();
            }
         }
      });
      
      return widgetTree;
   }
   
   public static TreePanel<BeanModel> buildPanelTree(final ScreenTab screenTab) {
      if (panelTreeStore == null) {
         panelTreeStore = new TreeStore<BeanModel>();
      }
      TreePanel<BeanModel> panelTree = new TreePanel<BeanModel>(panelTreeStore) {
         @Override
         public void onBrowserEvent(Event event) {
            super.onBrowserEvent(event);
            if (event.getTypeInt() == Event.ONCLICK) {
               BeanModel beanModel = this.getSelectionModel().getSelectedItem();
               if (beanModel != null && beanModel.getBean() instanceof ScreenRef) {
                  Screen screen = ((ScreenRef) beanModel.getBean()).getScreen();
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
            
         }
      };
      panelTree.setStateful(true);
      panelTree.setBorders(false);
      panelTree.setHeight("100%");
      panelTree.setDisplayProperty("displayName");
      panelTree.setStyleAttribute("overflow", "auto");

      panelTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Panel) {
               return ICON.panelIcon();
            } else if (thisModel.getBean() instanceof GroupRef) {
               return ICON.groupIcon();
            } else if (thisModel.getBean() instanceof ScreenRef) {
               return ICON.screenIcon();
            } else {
               return ICON.panelIcon();
            }
         }
      });
      return panelTree;
   }
   
   public static TreePanel<BeanModel> buildPanelTree(TreeStore<BeanModel> store) {
      TreePanel<BeanModel> panelTree = new TreePanel<BeanModel>(store);
         
      panelTree.setStateful(true);
      panelTree.setBorders(false);
      panelTree.setHeight("100%");
      panelTree.setDisplayProperty("displayName");
      panelTree.setStyleAttribute("overflow", "auto");
      
      panelTree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            if (thisModel.getBean() instanceof Panel) {
               return ICON.panelIcon();
            } else if (thisModel.getBean() instanceof GroupRef) {
               return ICON.groupIcon();
            } else if (thisModel.getBean() instanceof ScreenRef) {
               return ICON.screenIcon();
            } else {
               return ICON.panelIcon();
            }
         }
      });

      return panelTree;
   }
   
   public static TreePanel<BeanModel> buildControllerConfigCategoryPanelTree(final TabPanel configTabPanel){
      if(controllerConfigCategoryTreeStore == null){
         controllerConfigCategoryTreeStore = new TreeStore<BeanModel>();
         ConfigCategoryBeanModelProxy.getAllCategory(new AsyncSuccessCallback<Set<ConfigCategory>>(){

            @Override
            public void onSuccess(Set<ConfigCategory> result) {
               for(ConfigCategory category : result){
                  controllerConfigCategoryTreeStore.add(category.getBeanModel(), false);
               }
            }
         });
      }
      
      TreePanel<BeanModel> tree = new TreePanel<BeanModel>(controllerConfigCategoryTreeStore){
         @Override
         public void onBrowserEvent(Event event) {
            if (event.getTypeInt() == Event.ONCLICK) {
               BeanModel beanModel = this.getSelectionModel().getSelectedItem();
               ConfigCategory  category = beanModel.getBean();
               configTabPanel.removeAll();
               ControllerConfigTabItem configTabItem = new ControllerConfigTabItem(category);
               configTabPanel.add(configTabItem);
            }
            super.onBrowserEvent(event);
         }
      };
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            return ICON.configIcon();
         }
      });
      
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("name");
      return tree;
   }
   
   /*public static TreePanel<BeanModel> buildTemplateTree(){
      final TreeStore<BeanModel> treeStore = new TreeStore<BeanModel>();
      UtilsProxy.getTemplatesListRestUrl(new AsyncSuccessCallback<String> (){
         public void onSuccess(String result){
            ModelType templateType = new ModelType();
            templateType.setRoot("templates.template");
            DataField idField = new DataField("id");
            idField.setType(Long.class);
            templateType.addField(idField);
            templateType.addField("content");
            templateType.addField("name");
            ScriptTagProxy<ListLoadResult<ModelData>> scriptTagProxy = new ScriptTagProxy<ListLoadResult<ModelData>>(result);
            NestedJsonLoadResultReader<ListLoadResult<ModelData>> reader = new NestedJsonLoadResultReader<ListLoadResult<ModelData>>(
                  templateType);
            final BaseListLoader<ListLoadResult<ModelData>> loader = new BaseListLoader<ListLoadResult<ModelData>>(scriptTagProxy, reader);

            ListStore<ModelData> store = new ListStore<ModelData>(loader);
            loader.load();
            for(ModelData data : store.getModels()){
               Template template = new Template();
               template.setOid((Long) data.get("id"));
               template.setContent((String) data.get("content"));
               template.setName((String) data.get("name"));
               treeStore.add(template.getBeanModel(), false);
            }
         }
      });
      TreePanel<BeanModel> tree = new TreePanel<BeanModel> (treeStore);
      tree.setIconProvider(new ModelIconProvider<BeanModel>() {
         public AbstractImagePrototype getIcon(BeanModel thisModel) {
            return ICON.configIcon();
         }
      });
      
      tree.setStateful(true);
      tree.setBorders(false);
      tree.setHeight("100%");
      tree.setDisplayProperty("name");
      return tree;
   }*/
}
