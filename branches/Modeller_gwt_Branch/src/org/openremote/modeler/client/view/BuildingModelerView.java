package org.openremote.modeler.client.view;

import org.openremote.modeler.client.widget.DevicePanel;
import org.openremote.modeler.client.widget.MacroPanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class BuildingModelerView extends TabItem implements View {

   public void initialize() {
      setText("Building Modeler");
      setLayout(new BorderLayout());
//      createNorth();
      createWest();
      createCenter();
   }
   
   private void createNorth(){
      ToolBar north = new ToolBar();
      BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200);
      northData.setCollapsible(false);
      northData.setMargins(new Margins(0, 2, 0, 2));
      add(north, northData);
   }
   
   private void createWest(){
      ContentPanel west = new ContentPanel();
      BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST,200);
      westData.setSplit(true);
      westData.setCollapsible(true);
      west.setLayout(new AccordionLayout());
      west.setBodyBorder(false);
      west.setHeading("Explorer");
      west.add(new DevicePanel());
      west.add(new MacroPanel());
  
      westData.setMargins(new Margins(2));
      add(west,westData);
   }
   
   private void createCenter(){
      ContentPanel center = new ContentPanel();
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
      centerData.setMargins(new Margins(2));

      add(center,centerData);
   }
}
