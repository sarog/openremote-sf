package org.openremote.controller.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class ControllerApplication extends Application {

   @Override
   public synchronized Restlet createInboundRoot() {

      // Create a router Restlet that routes each call to a new instance of HelloWorldResource.
       Router router = new Router(getContext());

       // Route is what is after the servlet matching, this will be an issue with current configuration if we need more that one Restlet resource
       // Either move all rest handling to Restlet, either create multiple applications 
       router.attach("", CapabilitiesResource.class);

       return router;
   }
   
}
