package org.openremote.modeler.client;

import java.util.List;

import org.openremote.modeler.client.rpc.MyService;
import org.openremote.modeler.client.rpc.MyServiceAsync;
import org.openremote.modeler.domain.Activity;
import org.openremote.modeler.domain.Screen;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/*
 * * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Modeler implements EntryPoint {

   private Viewport viewport;
   private ContentPanel center;
   
   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {

      viewport = new Viewport();
      viewport.setLayout(new BorderLayout());
      createCenter();

      RootPanel.get("main").add(viewport);
   }

   private void createCenter() {
      center = new ContentPanel();
      center.setHeading("Test");
      center.setScrollMode(Scroll.AUTOX);
      
      final ContentPanel imagePanel = new ContentPanel();
      final MyServiceAsync myServiceAysn = (MyServiceAsync)GWT.create(MyService.class);
      
//      myServiceAysn.addScreen(new AsyncCallback<Void>(){
//
//         public void onFailure(Throwable caught) {
//            MessageBox.info("Info", caught.getMessage(), null);
//            
//         }
//
//         public void onSuccess(Void result) {
//            MessageBox.info("Info", "add success", null);
//           
//         }
//         
//         
//      });
//      
      myServiceAysn.getString(new AsyncCallback<List<Activity>>(){
         public void onFailure(Throwable caught) {
            MessageBox.info("Info", caught.getMessage(), null);
            caught.printStackTrace();
         }

         public void onSuccess(List<Activity> activities) {
            for (Activity activity : activities) {
               HTML html = new HTML();
               html.setText("activity id : "+activity.getOid()+" screens count : "+activity.getScreens().size());
               System.out.println("activity id : "+activity.getOid()+" screens count : "+activity.getScreens().size());
               for (Screen screen : activity.getScreens()) {
                  System.out.println("screen " + screen.getLabel() + " in "+activity.getOid());
               }
               imagePanel.add(html);
            }
            
//            MessageBox.info("Info", "Load success", null);
            MessageBox.alert("Info", "Load success", null);
         }
         
      });
      center.add(imagePanel);
      
      
      

      BorderLayoutData centerBorderLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
      viewport.add(center, centerBorderLayoutData);

   }
}
