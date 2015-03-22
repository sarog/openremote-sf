package org.openremote.controller.rest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.restlet.data.MediaType;
import org.restlet.ext.xml.DomRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CapabilitiesResource extends ServerResource {

   @Get("xml")
   public Representation getCapabilities() {
      try {

         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
           DocumentBuilder builder = factory.newDocumentBuilder();
           Document document = builder.newDocument(); 
           Element root = document.createElement("openremote");
           root.setAttribute("xmlns", "http://www.openremote.org");
           root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
           root.setAttribute("xmlns:schemaLocation", "http://www.openremote.org/capabilities.xsd");
           document.appendChild(root);
           Element el = document.createElement("rest-api-versions");
           root.appendChild(el);
           el.appendChild(createVersionNode(document, "2.0"));
           el.appendChild(createVersionNode(document, "2.1"));
           return new DomRepresentation(MediaType.APPLICATION_XML, document);
      } catch (ParserConfigurationException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
      
   }
   
   private Element createVersionNode(Document document, String version) {
      Element v = document.createElement("version");
      v.appendChild(document.createTextNode(version));
      return v;
   }
}
