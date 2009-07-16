package org.openremote.modeler.client;

import org.openremote.modeler.client.view.ApplicationView;

import com.google.gwt.core.client.EntryPoint;

/*
 * * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Modeler implements EntryPoint {

   /**
    * This is the entry point method.
    */
   public void onModuleLoad() {
      init();
   }

   private void init() {
      ApplicationView appView = new ApplicationView();
      appView.initialize();
   }

}
