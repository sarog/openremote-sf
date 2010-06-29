package org.openremote.web.console.client.view;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationView {

   private Viewport viewport;
   
   public ApplicationView() {
      viewport = new Viewport();
      viewport.setLayout(new BorderLayout());
      createToolBar();
      createScreenView();
      RootPanel.get().add(viewport);
   }
   
   private void createToolBar() {
      ToolBar toolBar = new ToolBar();
      // TODO: add global tabbar items.
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(toolBar, data);
   }
   
   private void createScreenView() {
      GroupView groupView = new GroupView();
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(groupView, data);
   }
}
