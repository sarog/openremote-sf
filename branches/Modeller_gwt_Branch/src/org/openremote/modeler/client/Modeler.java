package org.openremote.modeler.client;

import java.util.Map;

import org.openremote.modeler.client.rpc.ProtocolService;
import org.openremote.modeler.client.rpc.ProtocolServiceAsync;
import org.openremote.modeler.client.view.ApplicationView;
import org.openremote.modeler.client.widget.ProtocolForm;
import org.openremote.modeler.protocol.ProtocolDefinition;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/*
 * * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Modeler implements EntryPoint {

   private final VerticalPanel vp = new VerticalPanel();

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      createCenter();
//      init();
   }
   
   private void init() {
      ApplicationView appView = new ApplicationView();
      appView.initialize();
      appView.show();
  }
   private void createCenter() {

      final ProtocolServiceAsync protocolService = (ProtocolServiceAsync) GWT.create(ProtocolService.class);

      protocolService.getProtocolContainer(new AsyncCallback<Map<String, ProtocolDefinition>>() {
         public void onFailure(Throwable caught) {
            caught.printStackTrace();
            MessageBox.info("Info", caught.getMessage(), null);
         }

         public void onSuccess(Map<String, ProtocolDefinition> protocols) {
            for (int i = 0; i < protocols.keySet().size(); i++) {

               final ProtocolDefinition definition = protocols.get(protocols.keySet().toArray()[i]);
               ProtocolForm protocolForm = new ProtocolForm(definition);
               protocolForm.addSubmitListener(new Listener<AppEvent<Map<String, String>>>() {

                  public void handleEvent(AppEvent<Map<String, String>> be) {
                     StringBuffer buffer = new StringBuffer();
                     for (String key : be.data.keySet()) {
                        buffer.append(key + ";");
                        buffer.append(be.data.get(key));
                        buffer.append("<br />");
                     }
                     MessageBox.info(definition.getName(), buffer.toString(), null);
                  }
               });

               vp.add(protocolForm);

            }
            RootPanel.get("main").add(vp);
         }

      });

   }

}
