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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.openremote.modeler.client.model.AutoSaveResponse;
import org.openremote.modeler.client.proxy.BeanModelDataBase;
import org.openremote.modeler.client.proxy.UtilsProxy;
import org.openremote.modeler.client.rpc.AsyncSuccessCallback;
import org.openremote.modeler.client.utils.TouchPanels;
import org.openremote.modeler.client.widget.uidesigner.DevicesAndMacrosPanel;
import org.openremote.modeler.client.widget.uidesigner.GroupPanel;
import org.openremote.modeler.client.widget.uidesigner.ScreenTab;
import org.openremote.modeler.client.widget.uidesigner.ScreenPanel;
import org.openremote.modeler.domain.Activity;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;


/**
 * The Class UIDesignerView.
 */
public class UIDesignerView extends TabItem implements View {

   /** The screen tab. */
   private ScreenTab screenTab = new ScreenTab();
   
   /** The application view. */
   private ApplicationView applicationView;
   
   /** The auto_save_interval millisecond. */
   private static final int AUTO_SAVE_INTERVAL_MS = 30000;
   
   private Timer timer;
   /**
    * Instantiates a new uI designer view.
    */
   public UIDesignerView() {
      super();
      createAutoSaveTimer();
   }
   
   /**
    * Creates the timer.
    */
   private void createAutoSaveTimer() {
      timer = new Timer() {
         @Override
         public void run() {
//            autoSaveUiDesignerLayoutJSON();
         }
        };
        timer.scheduleRepeating(AUTO_SAVE_INTERVAL_MS);
   }
   
   /**
    * Auto save ui designer layout json.
    */
   public void autoSaveUiDesignerLayoutJSON() {
      UtilsProxy.autoSaveUiDesignerLayoutJSON(getAllActivities(), new AsyncSuccessCallback<AutoSaveResponse>() {
         @Override
         public void onSuccess(AutoSaveResponse result) {
            if (result != null && result.isUpdated()) {
               Info.display("Info", "UI designer layout saved at " + DateTimeFormat.getFormat("HH:mm:ss").format(new Date()));
            }
         }
         @Override
         public void onFailure(Throwable caught) {
            timer.cancel();
            MessageBox.alert("ERROR", "Server error, UI designer layout save failed.", null);
         }
         
      });
   }
   
   /**
    * Gets the all activities.
    * 
    * @return the all activities
    */
   protected List<Activity> getAllActivities() {
      List<Activity> activityList = new ArrayList<Activity>();
      for (BeanModel activityBeanModel : BeanModelDataBase.activityTable.loadAll()) {
         activityList.add((Activity) activityBeanModel.getBean());
      }
      return activityList;
   }


   /**
    * Initialize.
    * 
    * @see org.openremote.modeler.client.view.View#initialize()
    */
   public void initialize() {
      setText("UI Designer");

      setLayout(new BorderLayout());
      createWest();
      createCenter();
      createEast();
      prepareData();
   }
   
   
   /**
    * Creates the east.
    */
   private void createEast() {
      BorderLayoutData eastLayout = new BorderLayoutData(LayoutRegion.EAST);
      eastLayout.setSplit(true);
      eastLayout.setCollapsible(true);
      eastLayout.setMargins(new Margins(2));      
      add(new DevicesAndMacrosPanel(), eastLayout);
   }

   /**
    * Creates the west.
    */
   private void createWest() {
      ContentPanel west = new ContentPanel();
      BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 200);
      westData.setSplit(true);
      westData.setCollapsible(true);
      west.setLayout(new AccordionLayout());
      west.setBodyBorder(false);
      west.setHeading("Browser");
//      ActivityPanel activityPanel = new ActivityPanel(screenTab);
//      west.add(activityPanel);
//      applicationView.setActivityPanel(activityPanel);
      
      west.add(new ScreenPanel(screenTab));
      west.add(new GroupPanel());
      westData.setMargins(new Margins(2));
      add(west, westData);
   }

   /**
    * Creates the center.
    */
   private void createCenter() {
      BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
      centerData.setMargins(new Margins(2));
      add(screenTab, centerData);
   }

   /**
    * Sets the application view.
    * 
    * @param applicationView the new application view
    */
   public void setApplicationView(ApplicationView applicationView) {
      this.applicationView = applicationView;
   }

   /**
    * Prepare data.
    */
   private void prepareData() {
      TouchPanels.load();
   }

   /**
    * Gets the screen tab.
    * 
    * @return the screen tab
    */
   public ScreenTab getScreenTab() {
      return screenTab;
   }
}
