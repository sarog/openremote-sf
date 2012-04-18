package org.openremote.controller.rest;

import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

public class CapabilitiesResource extends ServerResource {

   @Get
   public Representation getCapabilities() {
      System.out.println(">>getCapabilities");
      return new StringRepresentation("test");
   }
}
