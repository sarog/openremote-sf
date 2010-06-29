package org.openremote.web.console.client.view;

import org.openremote.web.console.domain.AbsoluteLayoutContainer;
import org.openremote.web.console.domain.Button;
import org.openremote.web.console.domain.Component;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class AbsolutLayoutContainerView extends LayoutContainer {

   public AbsolutLayoutContainerView(AbsoluteLayoutContainer absolutlayoutContainer) {
      setStyleAttribute("position", "absolute");
      setLayout(new FitLayout());
      setSize(absolutlayoutContainer.getWidth(), absolutlayoutContainer.getHeight());
      setPosition(absolutlayoutContainer.getLeft(), absolutlayoutContainer.getTop());
      Component component = absolutlayoutContainer.getComponent();
      if (component instanceof Button) {
         // temp display button.
         Button uiButton = (Button)component;
         com.extjs.gxt.ui.client.widget.button.Button btn = new com.extjs.gxt.ui.client.widget.button.Button();
         btn.setText(uiButton.getName());
         add(btn);
      }
   }
}
