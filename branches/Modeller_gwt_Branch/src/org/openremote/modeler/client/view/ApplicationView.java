/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.modeler.client.view;

import java.util.List;

import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.rpc.AuthorityService;
import org.openremote.modeler.client.rpc.AuthorityServiceAsync;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * The Class ApplicationView.
 * 
 * @author Tomsky,Allen
 */
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
               createNorth(authority);
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
   
   
   private void createNorth(Authority authority){
      HorizontalPanel headerPanel = new HorizontalPanel();
      Anchor logout = new Anchor("logout "+authority.getUsername(),"j_security_logout");
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
//      Status status = new Status();
//      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 20);
//      data.setMargins(new Margins(0,5,0,5));
//      viewport.add(status, data);
   }
}
