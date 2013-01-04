/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.component;

import java.util.List;
import java.text.MessageFormat;

import org.jdom.Element;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.command.Command;
import org.openremote.controller.component.control.Control;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.Constants;
import org.openremote.controller.service.ServiceContext;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.protocol.EventProducer;


/**
 * TODO : super class for parsing the <component> elements from controller.xml
 * 
 * @author Handy.Wang 2009-10-15
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public abstract class ComponentBuilder
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Use common log category for XML parsing related errors.
   */
  private final static Logger log = Logger.getLogger(Constants.XML_PARSER_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------

  protected RemoteActionXMLParser remoteActionXMLParser;
  protected CommandFactory commandFactory;


  // Instance Methods -----------------------------------------------------------------------------

//  /**
//   * TODO
//   *
//   * @param componentElement
//   * @param sensorIncludeElement
//   *
//   * @throws XMLParsingException
//   *
//   * @return
//   */
//  protected Sensor parseSensor(Element componentElement, Element sensorIncludeElement)
//      throws InitializationException
//  {
//    if (componentElement == null || sensorIncludeElement == null)
//      throw new InitializationException("null arguments");
//
//
//    String sensorID = sensorIncludeElement.getAttributeValue(Component.REF_ATTRIBUTE_NAME);
//
//    Element sensorElement = remoteActionXMLParser.queryElementFromXMLById(componentElement.getDocument(), sensorID);
//
//
//    SensorBuilder builder = (SensorBuilder)ServiceContext.getXMLBinding("sensor");
//
//    return builder.build(sensorElement.getDocument(), sensorElement);
//  }
//
//
//
//
//    if (sensorElement == null)
//    {
//     throw new XMLParsingException(MessageFormat.format(
//         "Component was configured with sensor (ID: {0}) which was not not found.",
//         sensorID
//     ));
//    }
//
//    List<Element> sensorSubElements = sensorElement.getChildren();
//    Sensor sensor = null;
//
//    for (Element sensorSubElement : sensorSubElements)
//    {
//      if (!hasEventProducer(sensorSubElement))
//        continue;
//
//      String eventProducerID = sensorSubElement.getAttributeValue(Component.REF_ATTRIBUTE_NAME);
//
//      Element eventProducerElement = remoteActionXMLParser.queryElementFromXMLById(
//          componentElement.getDocument(),
//          eventProducerID
//      );
//
//      if (eventProducerElement == null)
//      {
//        throw new XMLParsingException(MessageFormat.format(
//            "Configuration error in {0}: sensor with ID {1} has been configured to reference " +
//            "an event producer (command) with ID {2} -- command with this ID was not found.",
//            Constants.CONTROLLER_XML, sensorID, eventProducerID
//        ));
//      }
//
//      Command cmd = commandFactory.getCommand(eventProducerElement);
//
//      try
//      {
//        if (cmd instanceof EventProducer)
//        {
//          EventProducer eventProducer = (EventProducer) cmd;
//          sensor = new Sensor(eventProducer);
//          sensor.setSensorID(Integer.parseInt(sensorID));
//        }
//
//        break;
//      }
//
//      catch (NumberFormatException e)
//      {
//        log.warn(
//            "Sensor with ID = {0} could not be parsed to a unique integer ID. " +
//            "Skipping the sensor creation.", sensorID
//        );
//      }
//    }
//
//    if (sensor == null)
//    {
//      throw new XMLParsingException(
//          "Configured component sensor reference {0} could not be constructed.", sensorID
//      );
//    }
//
//    return sensor;
//  }
//
//
//
//
//  private boolean hasEventProducer(Element sensorSubElement)
//  {
//     return Component.INCLUDE_ELEMENT_NAME.equalsIgnoreCase(sensorSubElement.getName()) &&
//            Component.COMMAND_ELEMENT_NAME.equalsIgnoreCase(sensorSubElement.getAttributeValue(Component.INCLUDE_TYPE_ATTRIBUTE_NAME));
//  }
//




  protected boolean hasIncludeSensorElement(Element childElementOfControl) 
  {
    boolean hasIncludeElement =
        Control.INCLUDE_ELEMENT_NAME.equalsIgnoreCase(childElementOfControl.getName());

    boolean isIncludeSensor = Control.INCLUDE_TYPE_SENSOR.equals(
        childElementOfControl.getAttributeValue(Control.INCLUDE_TYPE_ATTRIBUTE_NAME)
    );

    return hasIncludeElement && isIncludeSensor;
  }


  /**
   * TODO : Builds the component.
   *
   * @param componentElement
   * @param commandParam
   *
   * @throws XMLParsingException
   *
   * @return TODO
   */
  public abstract Component build(Element componentElement, String commandParam)
      throws InitializationException;


  public void setRemoteActionXMLParser(RemoteActionXMLParser remoteActionXMLParser)
  {
      this.remoteActionXMLParser = remoteActionXMLParser;
  }

  public void setCommandFactory(CommandFactory commandFactory)
  {
      this.commandFactory = commandFactory;
  }

}
