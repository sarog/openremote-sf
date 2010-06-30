package org.openremote.web.console.client.widget;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;

public class ScreenIndicator extends LayoutContainer {

   private LayoutContainer indicatorContainer;
   private int currentScreenIndex = -1;
   
   public ScreenIndicator(int screenCount, int screenIndex) {
      setLayout(new CenterLayout());
      if (screenCount > 1) {
         setStyleAttribute("backgroundColor", "gray");
         currentScreenIndex = screenIndex;
         indicatorContainer = new LayoutContainer();
         indicatorContainer.setSize(14 * screenCount, 14);
         for (int i = 0; i < screenCount; i++) {
            LayoutContainer indicator = new LayoutContainer();
            indicator.setSize(14, 14);
            if (i != screenIndex) {
               indicator.setStyleName("indicator-default");
            } else {
               indicator.setStyleName("indicator-current");
            }
            indicatorContainer.add(indicator);
         }
         add(indicatorContainer);
      }
   }
   
   public void updateCurrentPageControl(int screenIndex) {
      if (indicatorContainer != null) {
         indicatorContainer.getItem(currentScreenIndex).setStyleName("indicator-default");
         currentScreenIndex = screenIndex;
         indicatorContainer.getItem(currentScreenIndex).setStyleName("indicator-current");
         layout();
      }
   }
}
