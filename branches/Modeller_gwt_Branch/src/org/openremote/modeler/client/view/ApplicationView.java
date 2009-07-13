package org.openremote.modeler.client.view;

import java.util.List;

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.rpc.AuthorityService;
import org.openremote.modeler.client.rpc.AuthorityServiceAsync;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class ApplicationView implements View {
   
   private Viewport viewport;
   
   public void initialize() {
      viewport = new Viewport();
      viewport.setLayout(new BorderLayout());
      final AuthorityServiceAsync auth = (AuthorityServiceAsync)GWT.create(AuthorityService.class);
      auth.getAuthoritication(new AsyncCallback<Authority>(){
         public void onFailure(Throwable caught) {
            MessageBox.info("Info", caught.getMessage(), null);
            caught.printStackTrace();
         }
         public void onSuccess(Authority authority) {
            if(authority!=null){
               createNorth();
               createCenter(authority);
               createSouth();
               show();
            }else{
               MessageBox.info("Info", "you haven't login", null);
            }
         }
         
      });
//      createNorth();
//      createCenter();
//      createSouth();
   }
   
   private void show(){
      RootPanel.get().add(viewport);
   }
   
   
   private void createNorth(){
      HorizontalPanel headerPanel = new HorizontalPanel();
      HTML logout = new HTML("<a href='j_security_logout'>Logout</a>");
      headerPanel.add(logout);
      logout.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      headerPanel.setCellHorizontalAlignment(logout, HorizontalPanel.ALIGN_RIGHT);
      headerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0,5,0,5));
      viewport.add(headerPanel, data);
   }
   
   private void createCenter(Authority authority){
      List<String> roles = authority.getRoles();
      TabPanel builderPanel = new TabPanel();
      if(roles.contains("ROLE_MODELER")){
         BuildingModelerView buildingModelerItem = new BuildingModelerView();
         buildingModelerItem.initialize();
         builderPanel.add(buildingModelerItem);
      }
      if(roles.contains("ROLE_DESIGNER")){
         UIDesignerView uiDesignerItem = new UIDesignerView();
         uiDesignerItem.initialize();
         builderPanel.add(uiDesignerItem);
      }
      builderPanel.setAutoSelect(true);
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0,5,0,5));
      viewport.add(builderPanel, data);
      
   }
   private void createCenter(){
      TabPanel builderPanel = new TabPanel();
      BuildingModelerView buildingModelerItem = new BuildingModelerView();
      buildingModelerItem.initialize();
      builderPanel.add(buildingModelerItem);
      
      UIDesignerView uiDesignerItem = new UIDesignerView();
      uiDesignerItem.initialize();
      builderPanel.add(uiDesignerItem);
      builderPanel.setAutoSelect(true);
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(builderPanel, data);
      
   }
   
   private void createSouth(){
      ContentPanel footerPanel = new ContentPanel();
      footerPanel.setHeading("Copyright 2008-2009, OpenRemote Inc");
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 25);
      viewport.add(footerPanel, data);
   }
}
