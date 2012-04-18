package org.openremote.controller.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ControllerApplication extends Application {

   @Override
   public synchronized Restlet createInboundRoot() {

      System.out.println("Defining routes");
      // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
       Router router = new Router(getContext());

       // Defines only one route
       router.attach("/capabilities", CapabilitiesResource.class);

       return router;
   }
   
}
