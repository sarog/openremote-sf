package org.openremote.modeler.client.utils;

import org.openremote.modeler.client.event.WidgetSelectChangeEvent;
import org.openremote.modeler.client.listener.WidgetSelectChangeListener;
import org.openremote.modeler.client.widget.uidesigner.PropertyPanelBuilder;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class SelectedWidgetContainer {

   private static PropertyPanelBuilder selectedWidget;
   private static WidgetSelectChangeListener widgetSelectChangeListener;
   
   public static void setChangeListener(WidgetSelectChangeListener widgetSelectChangeListener) {
      SelectedWidgetContainer.widgetSelectChangeListener = widgetSelectChangeListener;
   }
   
   public static void setSelectWidget(PropertyPanelBuilder selectedWidget) {
      if (SelectedWidgetContainer.selectedWidget != null) {
         ((LayoutContainer)SelectedWidgetContainer.selectedWidget).removeStyleName("button-border");
      }
      if (selectedWidget != null) {
         ((LayoutContainer)selectedWidget).addStyleName("button-border");
      }
      SelectedWidgetContainer.selectedWidget = selectedWidget;
      widgetSelectChangeListener.handleEvent(new WidgetSelectChangeEvent(selectedWidget));
   }
   
   public static PropertyPanelBuilder getSelectWidget() {
      return SelectedWidgetContainer.selectedWidget;
   }
   
}
