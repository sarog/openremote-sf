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
package org.openremote.controller.model.xml;

import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.Namespace;
import org.openremote.controller.model.XMLMapping;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.protocol.EventProducer;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.command.Command;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.component.LevelSensor;

/**
 * XML Binding from XML document to sensor object model.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class SensorBuilder
{

  // TODO :
  //  - current implementation is using JDOM through-out so sticking to that, converting to
  //    JAXB longer term might make sense though.
  //                                                                                  [JPL]


  // Sensor Element XML Constants -----------------------------------------------------------------

  /**
   * Sensor element 'type' attribute in controller.xml file, i.e.
   *
   * <pre>
   * {@code
   *       <sensor id = "nnn" name = "sensor-name" type = "[range|level|switch|custom]">
   *         ...
   *       </sensor>
   * }</pre>
   */
  public final static String XML_SENSOR_ELEMENT_TYPE_ATTR = "type";


  /**
   * State element in controller.xml state sensor definition, i.e.
   *
   * <pre>
   * {@code
   *        <sensor id = "nnn" name = "sensor-name" type = "custom">
   *          <state name = "Rain Clouds"   value = "1" />
   *          <state name = "Cloudy"        value = "2" />
   *          <state name = "Partly Cloudy" value = "3" />
   *          <state name = "Sunny"         value = "4" />
   *
   *          <include type = "command" ref = "mmm"/>
   *        </sensor>
   * }</pre>
   */
  public final static String XML_SENSOR_STATE_ELEMENT_NAME = "state";

  /**
   * State element 'name' attribute in controller.xml state sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Rain Clouds"   value = "1" />
   * }</pre>
   */
  public final static String XML_SENSOR_STATE_NAME_ATTR = "name";

  /**
   * State element 'value' attribute in controller.xml state sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Rain Clouds"   value = "1" />
   * }</pre>
   */
  public final static String XML_SENSOR_STATE_VALUE_ATTR = "value";

  /**
   * A valid 'on' value in state element's 'value' attribute in switch sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Ouvert"   value = "on" />
   * }</pre>
   */
  public final static String XML_SWITCH_STATE_VALUE_ON = "on";

  /**
   * A valid 'off' value in state element's 'value' attribute in switch sensor definition, i.e.
   *
   * <pre>
   * {@code
   *          <state name = "Ouvert"   value = "off" />
   * }</pre>
   */
  public final static String XML_SWITCH_STATE_VALUE_OFF = "off";


  /**
   * Max element in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *    <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *      <min value = "-50"/>
   *      <max value = "50"/>
   *      ...
   *    </sensor>
   * }</pre>
   */
  public final static String XML_RANGE_MAX_ELEMENT_NAME = "max";

  /**
   * Min element in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *    <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *      <min value = "-50"/>
   *      <max value = "50"/>
   *      ...
   *    </sensor>
   * }</pre>
   */
  public final static String XML_RANGE_MIN_ELEMENT_NAME = "min";

  /**
   * Max element 'value' attribute in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *     <max value = "50"/>
   * }</pre>
   */
  public final static String XML_RANGE_MAX_VALUE_ATTR = "value";

  /**
   * Min element 'value' attribute in controller.xml range sensor definition, i.e.
   *
   * <pre>
   * {@code
   *     <min value = "50"/>
   * }</pre>
   */
  public final static String XML_RANGE_MIN_VALUE_ATTR = "value";




  // Class Members --------------------------------------------------------------------------------

  private static Namespace orNamespace = Namespace.getNamespace(Constants.OPENREMOTE_WEBSITE);

  
  /**
   * Common log category for all XML parsing related activities.
   */
  private final static Logger log = Logger.getLogger(Constants.SENSOR_XML_PARSER_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  private RemoteActionXMLParser controllerXMLParser;
  private CommandFactory protocolHandlerFactory;


  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * Constructs a sensor instance from controller's controller.xml document using a JDOM XML
   * element pointing to <tt>{@code <sensor id = "nnn" name = "sensor-name" type = "<datatype>"/>}</tt>
   * entry. The JDOM element must belong to the controller's controller.xml document.
   *
   * @param sensorElement     JDOM element for sensor
   *
   * @throws InitializationException    if the sensor model cannot be build from the given XML
   *                                    element
   *
   * @return initialized sensor instance
   */
  public Sensor build(Element sensorElement) throws InitializationException
  {
    return build(sensorElement.getDocument(), sensorElement);
  }


  /**
   * TODO : Builds a sensor from XML element.
   *
   * @param componentIncludeElement   JDOM element for sensor
   *
   * @throws InitializationException    if the sensor model cannot be built from the given XML
   *                                    element
   *
   * @return sensor
   */
  public Sensor buildFromComponentInclude(Element componentIncludeElement)
      throws InitializationException
  {

    if (componentIncludeElement == null)
    {
      throw new InitializationException(
          "Implementation error, null reference on expected " +
          "<include type = \"sensor\" ref = \"nnn\"/> element."
      );
    }

    Attribute includeTypeAttr =
        componentIncludeElement.getAttribute(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_ATTR);

    String typeAttributeValue = includeTypeAttr.getValue();

    if (!typeAttributeValue.equals(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_SENSOR))
    {
      throw new XMLParsingException(
          "Expected to include 'sensor' type, got {0} instead.", typeAttributeValue
      );
    }


    Attribute includeRefAttr =
        componentIncludeElement.getAttribute(XMLMapping.XML_INCLUDE_ELEMENT_REF_ATTR);

    String refAttributeValue = includeRefAttr.getValue();


    try
    {
      int sensorID = Integer.parseInt(refAttributeValue);
      Document document = componentIncludeElement.getDocument();

      Element sensorElement = controllerXMLParser.queryElementById(document, sensorID);

      Sensor sensor = this.build(document, sensorElement);

      // Pull out a specific log category just to log the creation of sensor objects
      // in this method (happens at startup or soft restart)...

      Logger.getLogger(Constants.SENSOR_INIT_LOG_CATEGORY)
          .info("BUG ORCJAVA-118 -- Create sensor : {0}", sensor.toString());

      return sensor;
    }

    catch (NumberFormatException e)
    {
        throw new InitializationException(
            "Currently only integer values are accepted as unique sensor ids. " +
            "Could not parse {0} to integer.", refAttributeValue
        );
    }
  }



  // Service Dependencies -------------------------------------------------------------------------

  public void setRemoteActionXMLParser(RemoteActionXMLParser controllerXMLParser)
  {
     this.controllerXMLParser = controllerXMLParser;
  }

  public void setCommandFactory(CommandFactory protocolHandlerFactory)
  {
     this.protocolHandlerFactory = protocolHandlerFactory;
  }



  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Constructs a sensor instance from a given document and JDOM XML element pointing to a
   * <tt>{@code <sensor id = "nnn" name = "sensor-name" type = "<datatype>"/>}</tt> element
   * in the controller.xml file.
   *
   * @param document        TODO specified JDOM document of controller.xml, use default
   *                        controller.xml if it's null.
   * @param sensorElement   JDOM element for sensor
   *
   * @throws InitializationException    if the sensor model cannot be built from the given XML
   *                                    element
   *
   * @return initialized sensor instance
   */
  private Sensor build(Document document, Element sensorElement) throws InitializationException
  {
    String sensorIDValue = sensorElement.getAttributeValue("id");
    String sensorName = sensorElement.getAttributeValue("name");

    EnumSensorType type = parseSensorType(sensorElement);

    try
    {
      int sensorID = Integer.parseInt(sensorIDValue);
      EventProducer ep = parseSensorEventProducer(document, sensorElement);

      switch (type)
      {
        case RANGE:

          int min = getMinProperty(sensorElement);
          int max = getMaxProperty(sensorElement);

          return new RangeSensor(sensorName, sensorID, ep, min, max);

        case LEVEL:

          return new LevelSensor(sensorName, sensorID, ep);

        case SWITCH:

          StateSensor.DistinctStates states = getSwitchStateMapping(sensorElement);

          return new SwitchSensor(sensorName, sensorID, ep, states);

        case CUSTOM:

          StateSensor.DistinctStates stateMapping = getDistinctStateMapping(sensorElement);

          return new StateSensor(sensorName, sensorID, ep, stateMapping);

        default:

          throw new InitializationException(
              "Using an unknown sensor type {0} -- SensorBuilder implementation must be " +
              "updated to handle this new type.", type
          );
      }
    }

    catch (NumberFormatException e)
    {
        throw new XMLParsingException(
            "Currently only integer values are accepted as unique sensor ids. " +
            "Could not parse {0} to integer.", sensorIDValue
        );
    }
  }


  private EnumSensorType parseSensorType(Element sensorElement) throws XMLParsingException
  {
    String typeValue = sensorElement.getAttributeValue(XML_SENSOR_ELEMENT_TYPE_ATTR);

    return parseSensorType(typeValue);
  }

  private EnumSensorType parseSensorType(String value) throws XMLParsingException
  {
    try
    {
      return EnumSensorType.valueOf(value.toUpperCase());
    }
    catch (IllegalArgumentException e)
    {
      throw new XMLParsingException(
          "Sensor type {0} is not a valid sensor datatype.", value
      );
    }
  }


  private EventProducer parseSensorEventProducer(Document doc, Element sensorElement)
    throws InitializationException
  {
    List<Element> sensorPropertyElements = getChildren(sensorElement);
    String sensorIDValue = sensorElement.getAttributeValue("id");

    for (Element sensorProperty : sensorPropertyElements)
    {
      if (sensorProperty.getName().equalsIgnoreCase(XMLMapping.XML_INCLUDE_ELEMENT_NAME))
      {
        String includeTypeAttrValue =
            sensorProperty.getAttributeValue(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_ATTR);

        if (includeTypeAttrValue.equalsIgnoreCase(XMLMapping.XML_INCLUDE_ELEMENT_TYPE_COMMAND))
        {
          String eventProducerRefValue =
              sensorProperty.getAttributeValue(XMLMapping.XML_INCLUDE_ELEMENT_REF_ATTR);

          try
          {
            int eventProducerID = Integer.parseInt(eventProducerRefValue);

            Element eventProducerElement =
                controllerXMLParser.queryElementById(doc, eventProducerID);

            Command eventProducer = protocolHandlerFactory.getCommand(eventProducerElement);

            if (eventProducer instanceof EventProducer)
            {
              return (EventProducer) eventProducer;
            }

            else
            {
              throw new XMLParsingException(
                  "The included 'command' reference in a sensor (ID = {0}) is not " +
                  "an event producer (Command id : {0}, Type : {1})",
                  sensorIDValue, eventProducerID, eventProducer.getClass().getName()
              );
            }
          }

          catch (NumberFormatException e)
          {
            // This ought to be caught by schema validation but in case schema changes or
            // validation is off...

            throw new XMLParsingException(
                "The <include> element in sensor (ID = {0}) contains an invalid reference " +
                "identifier. The value is not a valid integer : {1}",
                sensorIDValue, eventProducerRefValue
            );
          }
        }
      }
    }

    throw new XMLParsingException(
        "Sensor (ID = {0}) does not include a reference to an event producer.", sensorIDValue
    );
  }



  /**
   * Parses the <tt>{@code<min>}</tt> element on a range sensor. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *   <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *     <min value = "-50"/>
   *     <max value = "50"/>
   *
   *     <include type = "command" ref = "mmm"/>
   *   </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "range">}</tt> section in controller.xml file.
   *
   * @throws  XMLParsingException   if the range sensor element does not define a min element
   *                                or if the value of the min is not a valid integer
   *
   * @return  range min value
   */
  private int getMinProperty(Element sensorElement) throws XMLParsingException
  {
    Element min = sensorElement.getChild(XML_RANGE_MIN_ELEMENT_NAME, orNamespace);
    String sensorID = sensorElement.getAttributeValue("id");

    if (min == null)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) does not define <min> element.", sensorID
      );
    }

    String minAttrValue = min.getAttributeValue(XML_RANGE_MIN_VALUE_ATTR);

    try
    {
      return Integer.parseInt(minAttrValue);
    }

    catch (NumberFormatException e)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) <min value = {1}/> is not a valid integer number.", sensorID, minAttrValue
      );
    }
  }


  /**
   * Parses the <tt>{@code<max>}</tt> element on a range sensor. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *   <sensor id = "nnn" name = "Outdoor Temp" type = "range">
   *     <min value = "-50"/>
   *     <max value = "50"/>
   *
   *     <include type = "command" ref = "mmm"/>
   *   </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "range">}</tt> section in controller.xml file.
   *
   * @throws  XMLParsingException   if the range sensor element does not define a max element
   *                                or if the value of the max is not a valid integer
   *
   * @return  range max value
   */
  private int getMaxProperty(Element sensorElement) throws XMLParsingException
  {
    Element max = sensorElement.getChild(XML_RANGE_MAX_ELEMENT_NAME, orNamespace);
    String sensorID = sensorElement.getAttributeValue("id");

    if (max == null)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) does not define <max> element.", sensorID
      );
    }

    String maxAttrValue = max.getAttributeValue(XML_RANGE_MAX_VALUE_ATTR);

    try
    {
      return Integer.parseInt(maxAttrValue);
    }

    catch (NumberFormatException e)
    {
      throw new XMLParsingException(
          "Range sensor (ID = {0}) <max value = {1}/> is not a valid integer number.", sensorID, maxAttrValue
      );
    }
  }




  /**
   * Parses the <tt>{@code<state>}</tt> elements on a state (a.k.a 'custom') sensor. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *        <sensor id = "nnn" name = "sensor-name" type = "custom">
   *          <state name = "Rain Clouds"   value = "1" />
   *          <state name = "Cloudy"        value = "2" />
   *          <state name = "Partly Cloudy" value = "3" />
   *          <state name = "Sunny"         value = "4" />
   *
   *          <include type = "command" ref = "mmm"/>
   *        </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "custom">}</tt> section in controller.xml file.
   *
   * @return  state values and mappings if specified
   */
  private StateSensor.DistinctStates getDistinctStateMapping(Element sensorElement)
  {
    List<Element>sensorChildren = getChildren(sensorElement);
    StateSensor.DistinctStates states = new StateSensor.DistinctStates();

    for (Element sensorChild : sensorChildren)
    {
      if (sensorChild.getName().equalsIgnoreCase(XML_SENSOR_STATE_ELEMENT_NAME))
      {
        states.addStateMapping(
            sensorChild.getAttributeValue(XML_SENSOR_STATE_VALUE_ATTR),
            sensorChild.getAttributeValue(XML_SENSOR_STATE_NAME_ATTR)
        );
      }
    }

    return states;
  }

  /**
   * Parses the <tt>{@code<state>}</tt> elements on a switch sensor. Only accepts values for
   * "on" and "off" states, rest are ignored. <p>
   *
   * The section of the XML being parsed is following:
   *
   * <pre>
   * {@code
   *        <sensor id = "nnn" name = "Localized Door Sensor" type = "switch">
   *          <state name = "Ouvert"   value = "on" />
   *          <state name = "Ferme"    value = "off" />
   *
   *          <include type = "command" ref = "mmm"/>
   *        </sensor>
   * }
   * </pre>
   *
   * @param sensorElement   JDOM element pointing to <tt>{@code<sensor id = "nnn" name = "xxx"
   *                        type = "switch">}</tt> section in controller.xml file. Only applies
   *                        to 'switch' type sensors.
   *
   * @return  state mapping for 'on' and 'off' values for a switch sensor
   */
  private StateSensor.DistinctStates getSwitchStateMapping(Element sensorElement)
  {
    String sensorIDValue = sensorElement.getAttributeValue("id");
    String sensorName = sensorElement.getAttributeValue(XML_SENSOR_STATE_NAME_ATTR);

    List<Element> sensorChildren = getChildren(sensorElement);
    StateSensor.DistinctStates mapping = new StateSensor.DistinctStates();
    
    for (Element sensorChild : sensorChildren)
    {
      if (sensorChild.getName().equalsIgnoreCase(XML_SENSOR_STATE_ELEMENT_NAME))
      {
        String nameAttr = sensorChild.getAttributeValue(XML_SENSOR_STATE_NAME_ATTR);
        String valueAttr = sensorChild.getAttributeValue(XML_SENSOR_STATE_VALUE_ATTR);

        if (valueAttr == null)
        {
          // TODO :
          //   - this is really an error, declaring the states for switch sensor without mapping
          //     is completely redundant (as they're always 'on' and 'off'), however the tooling
          //     in its current state practices this redundancy in the XML documents it creates
          //     so generating model that redundantly maps <state name = 'on' value = 'on'>
          //     and <state name = 'off' value = 'off'>

          log.debug(
            "A switch sensor (Name = ''{0}'', ID = {1}) has an incomplete <state> element mapping, " +
            "the 'value' attribute is missing in <state name = {2}/>.",
            sensorName, sensorIDValue, nameAttr
          );

          mapping.addStateMapping(nameAttr, nameAttr);

          continue;
        }

        if (valueAttr.equalsIgnoreCase(XML_SWITCH_STATE_VALUE_ON))
        {
          mapping.addStateMapping("on", nameAttr);
        }

        else if (valueAttr.equalsIgnoreCase(XML_SWITCH_STATE_VALUE_OFF))
        {
          mapping.addStateMapping("off", nameAttr);
        }
      }
    }

    // Check if states have been filled in by explicit XML settings. If not, create them
    // for our object model. Thus we ensure 'switch' will always have on/off states in our
    // internal model.

    if (!mapping.hasState("on"))
    {
      mapping.addState("on");
    }

    if (!mapping.hasState("off"))
    {
      mapping.addState("off");
    }

    // Done!

    return mapping;
  }


  /**
   * This method only exists to limit the suppress warnings on an unchecked operation (due to
   * JDOM API) in a single location
   *
   * @param el    JDOM element
   *
   * @return      child elements of the given JDOM element
   */
  @SuppressWarnings("unchecked") private List<Element> getChildren(Element el)
  {
    return el.getChildren();
  }


}
