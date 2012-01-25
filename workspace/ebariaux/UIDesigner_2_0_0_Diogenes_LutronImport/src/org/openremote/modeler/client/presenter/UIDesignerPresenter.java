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
import java.util.Date;
import java.util.List;

import org.openremote.modeler.client.Constants;
import org.openremote.modeler.client.event.UIElementEditedEvent;
import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncServiceFactory;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.IDUtil;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.view.UIDesignerView;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainerHandle;
import org.openremote.modeler.client.widget.uidesigner.UIDesignerToolbar;
import org.openremote.modeler.domain.Absolute;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.component.UIGrid;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class UIDesignerPresenter implements Presenter, UIDesignerToolbar.Presenter {

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
    this.view.getToolbar().setPresenter(this);
    
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
  
  // TODO EBR : see if possible to implement those function without code copy/paste
  // Maybe Visitor (+ Command) pattern
  
  public void onHorizontalLeftAlignButtonClicked() {
    int leftPosition = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        leftPosition = ((AbsoluteLayoutContainer)firstComponent).getAbsolute().getLeft();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        leftPosition = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid().getLeft();
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(leftPosition);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(leftPosition);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onHorizontalCenterAlignButtonClicked() {
    int middlePosition = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute(); 
        middlePosition = absolute.getLeft() + absolute.getWidth() / 2;
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        middlePosition = grid.getLeft() + grid.getWidth() / 2;
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(middlePosition - absolute.getWidth() / 2);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(middlePosition - (grid.getWidth() / 2));
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onHorizontalRightAlignButtonClicked() {
    int rightPosition = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute(); 
        rightPosition = absolute.getLeft() + absolute.getWidth();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        rightPosition = grid.getLeft() + grid.getWidth();
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(rightPosition - absolute.getWidth());
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(rightPosition - grid.getWidth());
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onVerticalTopAlignButtonClicked() {
    int topPosition = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        topPosition = ((AbsoluteLayoutContainer)firstComponent).getAbsolute().getTop();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        topPosition = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid().getTop();
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setTop(topPosition);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setTop(topPosition);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }
  
  public void onVerticalCenterAlignButtonClicked() {
    int middlePosition = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute();
        middlePosition = absolute.getTop() + (absolute.getHeight() / 2);
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        middlePosition = grid.getTop() + (grid.getHeight() / 2);
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setTop(middlePosition - (absolute.getHeight() / 2));
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setTop(middlePosition - (grid.getHeight() / 2));
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }

  public void onVerticalBottomAlignButtonClicked() {
    int bottomPosition = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute();
        bottomPosition = absolute.getTop() + absolute.getHeight();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        bottomPosition = grid.getTop() + grid.getHeight();
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setTop(bottomPosition - absolute.getHeight());
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setTop(bottomPosition - grid.getHeight());
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }

  @Override
  public void onSameSizeButtonClicked() {
    int referenceWidth = 0, referenceHeight = 0;
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      ComponentContainer firstComponent = WidgetSelectionUtil.getSelectedWidgets().get(0);
      if (firstComponent instanceof AbsoluteLayoutContainer) {
        Absolute absolute = ((AbsoluteLayoutContainer)firstComponent).getAbsolute();
        referenceWidth = absolute.getWidth();
        referenceHeight = absolute.getHeight();
      } else if (firstComponent instanceof GridLayoutContainerHandle) {
        UIGrid grid = ((GridLayoutContainerHandle)firstComponent).getGridlayoutContainer().getGrid();
        referenceWidth = grid.getWidth();
        referenceHeight = grid.getHeight();
      }
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setWidth(referenceWidth);
          absolute.setHeight(referenceHeight);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setWidth(referenceWidth);
          grid.setHeight(referenceHeight);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
    }
  }

  @Override
  public void onHorizontalSpreadButtonClicked() {
    
    // TODO Implement

    
    if (WidgetSelectionUtil.getSelectedWidgets().size() > 1) {
      int leftBorder = Integer.MAX_VALUE, rightBorder = 0;
      int numberOfElements = 0;

      // On first iteration, search for left and right margin of the area we need to spread over
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          if (absolute.getLeft() < leftBorder) {
            leftBorder = absolute.getLeft();
          }
          if (absolute.getLeft() + absolute.getWidth() > rightBorder) {
            rightBorder = absolute.getLeft() + absolute.getWidth();
          }
          numberOfElements++;
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          if (grid.getLeft() < leftBorder) {
            leftBorder = grid.getLeft();
          }
          if (grid.getLeft() + grid.getWidth() > rightBorder) {
            rightBorder = grid.getLeft() + grid.getWidth();
          }
          numberOfElements++;
        }
      }
/*
      
      for (ComponentContainer cc : WidgetSelectionUtil.getSelectedWidgets()) {
        if (cc instanceof AbsoluteLayoutContainer) {
          Absolute absolute = ((AbsoluteLayoutContainer)cc).getAbsolute();
          absolute.setLeft(leftPosition);
          eventBus.fireEvent(new UIElementEditedEvent(absolute));
        } else if (cc instanceof GridLayoutContainerHandle) {        
          UIGrid grid = ((GridLayoutContainerHandle)cc).getGridlayoutContainer().getGrid();
          grid.setLeft(leftPosition);
          eventBus.fireEvent(new UIElementEditedEvent(grid));
        }
      }
      */
    }
    
  }

  @Override
  public void onVerticalSpreadButtonClicked() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onHorizontalCenterButtonClicked() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onVerticalCenterButtonClicked() {
    // TODO Auto-generated method stub
    
  }
  
  
  
  
  
}
