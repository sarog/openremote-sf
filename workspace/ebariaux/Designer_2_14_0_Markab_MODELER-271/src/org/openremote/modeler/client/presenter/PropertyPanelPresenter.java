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

import java.util.List;

import org.openremote.modeler.client.event.UIElementEditedEvent;
import org.openremote.modeler.client.event.UIElementEditedEventHandler;
import org.openremote.modeler.client.event.UIElementSelectedEvent;
import org.openremote.modeler.client.event.UIElementSelectedEventHandler;
import org.openremote.modeler.client.event.WidgetSelectedEvent;
import org.openremote.modeler.client.event.WidgetSelectedEventHandler;
import org.openremote.modeler.client.utils.WidgetSelectionUtil;
import org.openremote.modeler.client.widget.component.ScreenButton;
import org.openremote.modeler.client.widget.component.ScreenComponent;
import org.openremote.modeler.client.widget.component.ScreenImage;
import org.openremote.modeler.client.widget.component.ScreenLabel;
import org.openremote.modeler.client.widget.component.ScreenSlider;
import org.openremote.modeler.client.widget.component.ScreenSwitch;
import org.openremote.modeler.client.widget.component.ScreenTabbar;
import org.openremote.modeler.client.widget.component.ScreenTabbarItem;
import org.openremote.modeler.client.widget.component.ScreenWebView;
import org.openremote.modeler.client.widget.propertyform.AbsoluteLayoutContainerPropertyForm;
import org.openremote.modeler.client.widget.propertyform.ButtonPropertyForm;
import org.openremote.modeler.client.widget.propertyform.GridPropertyForm;
import org.openremote.modeler.client.widget.propertyform.GroupPropertyEditForm;
import org.openremote.modeler.client.widget.propertyform.ImagePropertyForm;
import org.openremote.modeler.client.widget.propertyform.LabelPropertyForm;
import org.openremote.modeler.client.widget.propertyform.PanelPropertyEditForm;
import org.openremote.modeler.client.widget.propertyform.PropertyForm;
import org.openremote.modeler.client.widget.propertyform.ScreenPropertyEditForm;
import org.openremote.modeler.client.widget.propertyform.ScreenPropertyForm;
import org.openremote.modeler.client.widget.propertyform.SliderPropertyForm;
import org.openremote.modeler.client.widget.propertyform.SwitchPropertyForm;
import org.openremote.modeler.client.widget.propertyform.TabbarItemPropertyForm;
import org.openremote.modeler.client.widget.propertyform.TabbarPropertyForm;
import org.openremote.modeler.client.widget.propertyform.WebViewPropertyForm;
import org.openremote.modeler.client.widget.uidesigner.AbsoluteLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;
import org.openremote.modeler.client.widget.uidesigner.GridCellContainer;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainer;
import org.openremote.modeler.client.widget.uidesigner.GridLayoutContainerHandle;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenCanvas;
import org.openremote.modeler.domain.GroupRef;
import org.openremote.modeler.domain.Panel;
import org.openremote.modeler.domain.ScreenPairRef;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.info.Info;

public class PropertyPanelPresenter implements Presenter {

  private EventBus eventBus;
  private WidgetSelectionUtil widgetSelectionUtil;
  private PropertyPanel view;
  private ComponentContainer currentWidget;
  
  public PropertyPanelPresenter(EventBus eventBus, WidgetSelectionUtil widgetSelectionUtil, PropertyPanel view) {
    super();
    this.eventBus = eventBus;
    this.widgetSelectionUtil = widgetSelectionUtil;
    this.view = view;
    bind();
  }
  
  private void bind() {
    eventBus.addHandler(UIElementSelectedEvent.TYPE, new UIElementSelectedEventHandler() {
      @Override
      public void onElementSelected(UIElementSelectedEvent event) {
        PropertyPanelPresenter.this.view.setPropertyForm(getPropertyForm(event.getElement()));
        currentWidget = null;
      }
    });
    
    eventBus.addHandler(UIElementEditedEvent.TYPE, new UIElementEditedEventHandler() {      
      @Override
      public void onElementEdited(UIElementEditedEvent event) {
        setPropertyForm(widgetSelectionUtil.getSelectedWidgets());
        // TODO EBR - this is just a quick fix, need to review
      }
    });
    
    eventBus.addHandler(WidgetSelectedEvent.TYPE, new WidgetSelectedEventHandler() {
      @Override
      public void onSelectionChanged(WidgetSelectedEvent event) {
        setPropertyForm(event.getSelectedWidgets());
      }
    });
  }
  
  private void setPropertyForm(List<ComponentContainer> components) {
    if (components.isEmpty()) {
      PropertyPanelPresenter.this.view.setPropertyForm(null);
      PropertyPanelPresenter.this.view.setHeading("Properties");
      currentWidget = null;
      return;
   }
    
   if (components.size() > 1) {
     PropertyPanelPresenter.this.view.setPropertyForm(null);
     PropertyPanelPresenter.this.view.setHeading("Multiple selection");
     currentWidget = null;
     return;
   }
   
   ComponentContainer component = components.get(0);
   if (!component.equals(currentWidget)) {
      if (component instanceof GridLayoutContainerHandle) {
        currentWidget = null;
      } else {
        currentWidget =  component;
      }

      PropertyForm form = getPropertyForm(component);
      PropertyPanelPresenter.this.view.setPropertyForm(form);
      if (component instanceof AbsoluteLayoutContainer) {
        new AbsoluteLayoutContainerPropertyForm((AbsoluteLayoutContainer)component).addAbsolutePositionAndSizeProperties(form);
      }
    }
  }
  
  private PropertyForm getPropertyForm(Object o) {
    if (o instanceof ScreenLabel) {
      ScreenLabel screenLabel = (ScreenLabel)o;
      return new LabelPropertyForm(screenLabel, screenLabel.getUiLabel(), widgetSelectionUtil);
    }
    if (o instanceof ScreenSlider) {
      ScreenSlider screenSlider = (ScreenSlider)o;
      return new SliderPropertyForm(screenSlider, widgetSelectionUtil);
    }
    if (o instanceof ScreenSwitch) {
      ScreenSwitch screenSwitch = (ScreenSwitch)o;
      return new SwitchPropertyForm(screenSwitch, screenSwitch.getUiSwitch(), widgetSelectionUtil);
    }
    if (o instanceof ScreenButton) {
      ScreenButton screenButton = (ScreenButton)o;
      return new ButtonPropertyForm(screenButton, screenButton.getUiButton(), widgetSelectionUtil);
    }
    if (o instanceof ScreenImage) {
      ScreenImage screenImage = (ScreenImage)o;
      return new ImagePropertyForm(screenImage, screenImage.getUiImage(), widgetSelectionUtil);
    }
    if (o instanceof ScreenTabbar) {
      ScreenTabbar screenTabbar = (ScreenTabbar)o;
      return new TabbarPropertyForm(screenTabbar, screenTabbar.getUiTabbar(), widgetSelectionUtil);
    }
    if (o instanceof ScreenWebView) {
      ScreenWebView screenWebView = (ScreenWebView)o;
      return new WebViewPropertyForm(screenWebView, screenWebView.getUIWebView(), widgetSelectionUtil);
    }
    if (o instanceof ScreenTabbarItem) {
      ScreenTabbarItem screenTabbarItem = (ScreenTabbarItem)o;
      return new TabbarItemPropertyForm(screenTabbarItem, widgetSelectionUtil);
    }
    if (o instanceof ScreenCanvas) {
      ScreenCanvas screenCanvas = (ScreenCanvas)o;
      return new ScreenPropertyForm(screenCanvas, widgetSelectionUtil);
    }
    if (o instanceof GridLayoutContainerHandle) {
      GridLayoutContainerHandle gridLayoutContainerHandle = (GridLayoutContainerHandle)o;
      return new GridPropertyForm(gridLayoutContainerHandle, widgetSelectionUtil);
    }
    if (o instanceof GridCellContainer) {
      GridCellContainer gridCellContainer = (GridCellContainer)o;
      return getPropertyForm(gridCellContainer.getScreenComponent());
    }    
    if (o instanceof AbsoluteLayoutContainer) {
      AbsoluteLayoutContainer absoluteLayoutContainer = (AbsoluteLayoutContainer)o;
      return getPropertyForm(absoluteLayoutContainer.getScreenComponent());
    }
    
    if (o instanceof ScreenComponent || o instanceof GridLayoutContainer || o instanceof ComponentContainer) {
      AlertMessageBox alert = new AlertMessageBox("Error", "Such a component should never display a property form, please report this error on the OpenRemote forums.");
      alert.show();
      return null;
    }
    
    if (o instanceof BeanModel) {
      if (((BeanModel)o).getBean() instanceof GroupRef) {
        GroupRef groupRef = ((BeanModel)o).getBean();
        return new GroupPropertyEditForm(groupRef, eventBus);
      }
      if (((BeanModel)o).getBean() instanceof Panel) {
        Panel panel = ((BeanModel)o).getBean();
        return new PanelPropertyEditForm(panel, eventBus);
      }
      if (((BeanModel)o).getBean() instanceof ScreenPairRef) {
        ScreenPairRef screenPairRef = ((BeanModel)o).getBean();
        return new ScreenPropertyEditForm(screenPairRef, eventBus);
      }
    }
    return null;
  }
  
}
