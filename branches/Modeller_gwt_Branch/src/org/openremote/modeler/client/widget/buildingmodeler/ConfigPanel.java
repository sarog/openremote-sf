package org.openremote.modeler.client.widget.buildingmodeler;

import org.openremote.modeler.client.icon.Icons;
import org.openremote.modeler.client.view.BuildingModelerView;
import org.openremote.modeler.client.widget.TreePanelBuilder;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;

public class ConfigPanel extends ContentPanel {
   private BuildingModelerView buildingModelerView = null;
   private Icons icon = GWT.create(Icons.class);
   private TreePanel<BeanModel> configCategory;
   public ConfigPanel(BuildingModelerView buildingModelerView){
      this.buildingModelerView = buildingModelerView;
      setHeading("Config for Controller");
      setIcon(icon.configIcon());
      createCategory();
      setLayout(new FitLayout());
      show();
   }
   
   
   private void createCategory(){
      configCategory = TreePanelBuilder.buildControllerConfigCategoryPanelTree(buildingModelerView.getConfigTabPanel());
      add(configCategory);
   }


   public BuildingModelerView getBuildingModelerView() {
      return buildingModelerView;
   }


   public void setBuildingModelerView(BuildingModelerView buildingModelerView) {
      this.buildingModelerView = buildingModelerView;
   }
   
   
}
