/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.modeler.client.view;

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
import org.openremote.modeler.auth.Authority;
import org.openremote.modeler.client.rpc.AuthorityRPCService;
import org.openremote.modeler.client.rpc.AuthorityRPCServiceAsync;
import org.openremote.modeler.client.utils.Protocols;

import java.util.List;


/**
 * The Class ApplicationView.
 * 
 * @author Tomsky,Allen
 */
public class ApplicationView implements View {
   
   /** The viewport. */
   private Viewport viewport;
   
   /**
    * @see org.openremote.modeler.client.view.View#initialize()
    */
   public void initialize() {
      Protocols.getInstance(); // get protocol definition from xml files 
      viewport = new Viewport();
      viewport.setLayout(new BorderLayout());
      final AuthorityRPCServiceAsync auth = (AuthorityRPCServiceAsync) GWT.create(AuthorityRPCService.class);
      auth.getAuthority(new AsyncCallback<Authority>() {
         public void onFailure(Throwable caught) {
            MessageBox.info("Info", caught.getMessage(), null);
            caught.printStackTrace();
         }
         public void onSuccess(Authority authority) {
            if (authority != null) {
               createNorth(authority);
               createCenter(authority);
               createSouth();
               show();
            } else {
               MessageBox.info("Info", "you haven't login", null);
            }
         }
         
      });
//      createNorth();
//      createCenter();
//      createSouth();
   }
   
   /**
    * Show.
    */
   private void show() {
      RootPanel.get().add(viewport);
   }
   
   
   /**
    * Creates the north.
    * 
    * @param authority the authority
    */
   private void createNorth(Authority authority) {
      HorizontalPanel headerPanel = new HorizontalPanel();
      Anchor logout = new Anchor("logout " + authority.getUsername(), "j_security_logout");
      headerPanel.add(logout);
      logout.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      headerPanel.setCellHorizontalAlignment(logout, HorizontalPanel.ALIGN_RIGHT);
      headerPanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.NORTH, 25);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(headerPanel, data);
   }

   /**
    * Creates the center.
    * 
    * @param authority
    *           the authority
    */
   private void createCenter(Authority authority) {
      List<String> roles = authority.getRoles();
      TabPanel builderPanel = new TabPanel();
      if (roles.contains("ROLE_MODELER")) {
         BuildingModelerView buildingModelerItem = new BuildingModelerView();
         buildingModelerItem.initialize();
         builderPanel.add(buildingModelerItem);
      }
      if (roles.contains("ROLE_DESIGNER")) {
         UIDesignerView uiDesignerItem = new UIDesignerView();
         uiDesignerItem.initialize();
         builderPanel.add(uiDesignerItem);
         builderPanel.setSelection(uiDesignerItem); // Temp to show uiDesigner. It will remove after development.
      }
      builderPanel.setAutoSelect(true);
      BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.CENTER);
      data.setMargins(new Margins(0, 5, 0, 5));
      viewport.add(builderPanel, data);

   }
   
   /**
    * Creates the south.
    */
   private void createSouth() {
      // Status status = new Status();
      // BorderLayoutData data = new BorderLayoutData(Style.LayoutRegion.SOUTH, 20);
      // data.setMargins(new Margins(0,5,0,5));
      // viewport.add(status, data);
   }
}
