package org.openremote.modeler.lutron;

import java.io.InputStream;
import java.util.Iterator;

import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.openremote.modeler.lutron.importmodel.Area;
import org.openremote.modeler.lutron.importmodel.Output;
import org.openremote.modeler.lutron.importmodel.Project;
import org.openremote.modeler.lutron.importmodel.Room;

public class LutronHomeworksImporter {

  @SuppressWarnings("unchecked")
  public static Project importXMLConfiguration(InputStream configurationStream) {
    Document protocolDoc = null;
    SAXReader reader = new SAXReader();
    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setValidating(true);
    factory.setNamespaceAware(true);
    
    Project project = null;
    
    try {
      protocolDoc = reader.read(configurationStream);

      Element projectElement = protocolDoc.getRootElement();
      
      project = new Project(projectElement.elementText("ProjectName"));
      Iterator<Element> areaIterator = projectElement.elementIterator("Area");
      while (areaIterator.hasNext()) {
        Element areaElement = areaIterator.next();
        System.out.println("Area " + areaElement.elementText("Name"));
        Area area = new Area(areaElement.elementText("Name"));
        project.addArea(area);
        Iterator<Element> roomIterator = areaElement.elementIterator("Room");
        while (roomIterator.hasNext()) {
          Element roomElement = roomIterator.next();
          System.out.println("  Room " + roomElement.elementText("Name"));
          Room room = new Room(roomElement.elementText("Name"));
          area.addRoom(room);
          Iterator<Element> outputIterator = roomElement.element("Outputs").elementIterator("Output");
          while (outputIterator.hasNext()) {
            Element outputElement = outputIterator.next();
            String outputType = outputElement.elementText("Type");
            Output output = new Output(outputElement.elementText("Name"), outputType, outputElement.elementText("Address"));
            room.addOutput(output);
            
            System.out.println("    Output " + outputElement.elementText("Name") + " of type " + outputType);
            
            /*
            */
          }
          
          
          // TODO: handle inputs
          
        }
      }
    } catch (DocumentException e) {
      
      
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return project;
  }

}