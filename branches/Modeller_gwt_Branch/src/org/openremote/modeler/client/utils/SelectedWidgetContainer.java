package org.openremote.modeler.client.utils;

import org.openremote.modeler.client.event.WidgetSelectChangeEvent;
import org.openremote.modeler.client.listener.WidgetSelectChangeListener;
import org.openremote.modeler.client.widget.uidesigner.ComponentContainer;

import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class SelectedWidgetContainer {

   private static LayoutContainer selectedWidget;
   private static WidgetSelectChangeListener widgetSelectChangeListener;
   
   public static void setChangeListener(WidgetSelectChangeListener widgetSelectChangeListener) {
      SelectedWidgetContainer.widgetSelectChangeListener = widgetSelectChangeListener;
   }
   
   public static void setSelectWidget(ComponentContainer selectedWidget) {
      if (SelectedWidgetContainer.selectedWidget != null) {
         SelectedWidgetContainer.selectedWidget.removeStyleName("button-border");
      }
      if (selectedWidget != null) {
         selectedWidget.addStyleName("button-border");
      }
      SelectedWidgetContainer.selectedWidget = selectedWidget;
      widgetSelectChangeListener.handleEvent(new WidgetSelectChangeEvent(selectedWidget));
   }
   
   public static LayoutContainer getSelectWidget() {
      return SelectedWidgetContainer.selectedWidget;
   }
   
}
