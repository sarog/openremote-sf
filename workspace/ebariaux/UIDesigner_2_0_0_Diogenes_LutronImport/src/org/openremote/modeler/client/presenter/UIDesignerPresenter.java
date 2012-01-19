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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.PanelsAndMaxOid;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.view.UIDesignerView;
import org.openremote.modeler.domain.Group;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPair;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class UIDesignerPresenter {

  private HandlerManager eventBus;
  private UIDesignerView view;
  
  private ProfilePanelPresenter profilePanelPresenter;
  private TemplatePanelPresenter templatePanelPresenter;
  private ScreenPanelPresenter screenPanelPresenter;
  private PropertyPanelPresenter propertyPanelPresenter;

  /** The auto_save_interval millisecond. */
  private static final int AUTO_SAVE_INTERVAL_MS = 30000;

  private Timer timer;

  public UIDesignerPresenter(HandlerManager eventBus, UIDesignerView view) {
    super();
    this.eventBus = eventBus;
    this.view = view;
    
    this.profilePanelPresenter = new ProfilePanelPresenter(eventBus, view.getProfilePanel());
    this.templatePanelPresenter = new TemplatePanelPresenter(eventBus, view.getTemplatePanel());
    this.screenPanelPresenter = new ScreenPanelPresenter(eventBus, view.getScreenPanel());
    this.propertyPanelPresenter = new PropertyPanelPresenter(eventBus, view.getPropertyPanel());
    
    AsyncServiceFactory.getUtilsRPCServiceAsync().getAccountPath(new AsyncSuccessCallback<String>() {
      public void onFailure(Throwable caught) {
        Info.display("Error", "failed to get account path.");
        super.checkTimeout(caught);
      }

      public void onSuccess(String result) {
        Cookies.setCookie(Constants.CURRETN_RESOURCE_PATH, result);
      }

    });

    prepareData();
    createAutoSaveTimer();
  }

  /**
   * Creates the auto save timer.
   */
  private void createAutoSaveTimer() {
    timer = new Timer() {
      @Override
      public void run() {
        autoSaveUiDesignerLayout();
      }
    };
    timer.scheduleRepeating(AUTO_SAVE_INTERVAL_MS);
  }

  /**
   * Auto save ui designer layout json.
   */
  public void autoSaveUiDesignerLayout() {
    if (view.getProfilePanel().isInitialized()) {
      UtilsProxy.autoSaveUiDesignerLayout(getAllPanels(), IDUtil.currentID(), new AsyncSuccessCallback<AutoSaveResponse>() {
        @Override
        public void onSuccess(AutoSaveResponse result) {
          if (result != null && result.isUpdated()) {
            Info.display("Info", "UI designer layout saved at " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
          }
          Window.setStatus("Auto-Saving: UI designer layout saved at: " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
        }

        @Override
        public void onFailure(Throwable caught) {
          timer.cancel();
          boolean timeout = super.checkTimeout(caught);
          if (!timeout) {
            Info.display(new InfoConfig("Error", caught.getMessage() + " " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date())));
          }
          Window.setStatus("Failed to save UI designer layout at: " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
        }
      });
      Window.setStatus("Saving ....");
    } else {
      Window.setStatus("Auto-Saving: Unable to save UI designer because panel list has not been initialized. ");
    }
  }

  /**
   * Restore ui designer layout and datas from server.
   */
  public void restore() {
    UtilsProxy.restore(new AsyncSuccessCallback<PanelsAndMaxOid>() {
      @Override
      public void onSuccess(PanelsAndMaxOid panelsAndMaxOid) {
        BeanModelDataBase.panelTable.clear();
        BeanModelDataBase.groupTable.clear();
        BeanModelDataBase.screenTable.clear();
        Collection<Panel> panels = panelsAndMaxOid.getPanels();
        long maxOid = panelsAndMaxOid.getMaxOid();

        for (Panel panel : panels) {
          BeanModelDataBase.panelTable.insert(panel.getBeanModel());
          for (GroupRef groupRef : panel.getGroupRefs()) {
            Group group = groupRef.getGroup();
            BeanModelDataBase.groupTable.insert(group.getBeanModel());
            for (ScreenPairRef screenRef : group.getScreenRefs()) {
              ScreenPair screen = screenRef.getScreen();
              BeanModelDataBase.screenTable.insert(screen.getBeanModel());
            }
          }
        }
        IDUtil.setCurrentID(maxOid);
        view.refreshPanelTree();
      }

      @Override
      public void onFailure(Throwable caught) {
        MessageBox.alert("Error", "UI designer restore failed: " + caught.getMessage(), null);
        super.checkTimeout(caught);
      }
    });
  }

  /**
   * Save ui designer layout, if the template panel is expanded, save its data, else save the profile panel's data.
   */
  public void saveUiDesignerLayout() {
     if (view.getTemplatePanel() != null && view.getTemplatePanel().isExpanded()) {
       view.getTemplatePanel().saveTemplateUpdates();
     } else {
        if (view.getProfilePanel().isInitialized()) {
           UtilsProxy.saveUiDesignerLayout(getAllPanels(), IDUtil.currentID(),
                 new AsyncSuccessCallback<AutoSaveResponse>() {
                    @Override
                    public void onSuccess(AutoSaveResponse result) {
                       if (result != null && result.isUpdated()) {
                          Info.display("Info", "UI designer layout saved at "
                                + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
                       }
                       Window.setStatus("UI designer layout saved at: "
                             + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
                    }
  
                    @Override
                    public void onFailure(Throwable caught) {
                       timer.cancel();
                       boolean timeout = super.checkTimeout(caught);
                       if (!timeout) {
                          Info.display(new InfoConfig("Error", caught.getMessage() + " "
                                + DateTimeFormat.getFormat("HH:mm:ss").format(new Date())));
                       }
                       Window.setStatus("Failed to save UI designer layout at: "
                             + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
  
                    }
  
                 });
           Window.setStatus("Saving ....");
        } else {
           Window.setStatus("Unable to save UI designer because panel list has not been initialized. ");
        }
     }
  }

  /**
   * Load the touchPanels from server.
   */
  private void prepareData() {
    TouchPanels.load();
  }

  /**
   * Gets the all panels from panelTable.
   * 
   * @return the all panels
   */
  public List<Panel> getAllPanels() {
    List<Panel> panelList = new ArrayList<Panel>();
    for (BeanModel panelBeanModel : BeanModelDataBase.panelTable.loadAll()) {
      panelList.add((Panel) panelBeanModel.getBean());
    }
    return panelList;
  }

}
