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
package org.openremote.controller.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URL;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.xml.ObjectBuilder;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.exception.XMLParsingException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Document;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;

/**
 * TODO : ORCJAVA-115
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Deployer
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * TODO
   *
   * Indicates a controller schema version which the deployer attempts to map to its object model.
   */
  public enum ControllerSchemaVersion
  {
    /**
     * Version 2.0 : this is the schema for controller.xml file
     */
    VERSION_2_0,

    /**
     * Version 3.0 : this is the schema for openremote.xml file
     */
    VERSION_3_0
  }


  /**
   * TODO
   *
   * Indicates XML segments in the controller schema which the XML mapping implementations
   * can register with.
   */
  public enum XMLSegment
  {
    /**
     * XML segment identifier used for identifying XML mappers for {@code<sensors>} section
     * in the XML document instance.
     */
    SENSORS("sensors");


    // Fields -------------------------------------------------------------------------------------

    /**
     * Actual element name in the XML document instance that is used to identify the beginning
     * section of where the XML mapping implementation applies.
     */
    private String elementName;


    // Constructors -------------------------------------------------------------------------------

    /**
     * @param elementName   should be the actual element name in the XML document instance which
     *                      identifies the root node of where an XML mapping implementation
     *                      should apply.
     */
    private XMLSegment(String elementName)
    {
      this.elementName = elementName;
    }


    // Enum Instance Methods ----------------------------------------------------------------------

    /**
     * Returns the XML element name that indicates the root XML element of this mapping component.
     *
     * @return  root XML element name of the mapping component.
     */
    public String getName()
    {
      return elementName;
    }
  }


  
  // Class Members --------------------------------------------------------------------------------

  /**
   * Common log category for startup logging, with a specific sub-category for this deployer
   * implementation.
   */
  private final static Logger log = Logger.getLogger(Constants.DEPLOYER_LOG_CATEGORY);


  /**
   * TODO
   *
   * @param doc
   * @param xPath
   * @return
   */
  private static Element queryElementFromXML(Document doc, String xPath) throws XMLParsingException
  {
    try
    {
      XPath xpath = XPath.newInstance(xPath);
      xpath.addNamespace(Constants.OPENREMOTE_NAMESPACE, Constants.OPENREMOTE_WEBSITE);
      List elements = xpath.selectNodes(doc);

      if (!elements.isEmpty())
      {
        Object o = elements.get(0);

        if (o instanceof Element)
        {
          return (Element)o;
        }

        else
        {
          throw new XMLParsingException(
              "XPath query is expected to only return Element types, got ''{0}''", o.getClass()
          );
        }
      }

      else
      {
        return null;
      }
    }

    catch (JDOMException e)
    {
      throw new XMLParsingException(
          "Xpath evaluation ''{0}'' failed : {1}", e, xPath, e.getMessage()
      );
    }
  }



  /**
   * TODO : temp
   *
   * @param doc
   * @param id
   * @return
   * @throws InitializationException
   */
  public static Element queryComponentById(Document doc, int id) throws InitializationException
  {
    Element element = queryElementFromXML(
        doc, "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']"
    );

    if (element == null)
    {
      throw new XMLParsingException("No component found with id ''{0}''.", id);
    }

    return element;
  }


  // Private Fields -------------------------------------------------------------------------------


  /**
   * TODO
   */
  private StatusCache deviceStateCache;

  /**
   * TODO
   */
  private ControllerConfiguration controllerConfig;


  /**
   * TODO
   */
  private Map<BuilderKey, ObjectBuilder> objectBuilders = new HashMap<BuilderKey, ObjectBuilder>(10);


  /**
   * TODO
   */

  private ModelBuilder modelBuilder = null;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   *
   * @param deviceStateCache
   * @param controllerConfig
   */
  public Deployer(StatusCache deviceStateCache, ControllerConfiguration controllerConfig)
  {
    if (deviceStateCache == null || controllerConfig == null)
    {
      throw new IllegalArgumentException("Null parameters are not allowed.");
    }
    
    this.deviceStateCache = deviceStateCache;
    this.controllerConfig = controllerConfig;
  }
  

  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * TODO
   *
   * @param builder
   */
  public void registerObjectBuilder(ObjectBuilder builder)
  {
    if (builder == null)
    {
      log.error("Attempted to register a <null> object builder. Registration ignored.");

      return;
    }

    try
    {
      ControllerSchemaVersion version = builder.getSchemaVersion();
      XMLSegment segment = builder.getRootSegment();

      BuilderKey key = new BuilderKey(version, segment);

      ObjectBuilder previous = objectBuilders.put(key, builder);

      if (previous != null)
      {
        log.warn(
            "Double registration of an object builder ({0} was replaced with {1}). " +
            "This may indicate an error if unintended.", previous.toString(), builder.toString()
        );
      }
    }
    catch (Throwable t)
    {
      // catch broken implementations... log error and try to continue...

      log.error("Error registering object builder : {0}", t, t.getMessage());
    }
  }


  /**
   * TODO
   *
   * @param id
   * @return
   */
  public Sensor getSensor(int id)
  {
    Sensor sensor = deviceStateCache.getSensor(id);

    if (sensor == null)
    {
      // Write a log entry on accessing sensor ID that does not exist. At the moment letting
      // this continue as it is, that is returning a null pointer which is likely to blow up
      // elsewhere unless the calling code guards against it. May consider other ways of
      // handling it later.
      //                                                                                  [JPL]
      log.error(
          "Attempted to access sensor with id ''{0}'' which did not exist in device " +
          "state cache.", id
      );
    }

    return sensor;
  }


  /**
   * TODO : temporary -- these are here temporarily until ControllerXMLChangeServiceImpl has been completely phased out (part of ORCJAVA-115)
   */
  public void stopSensors()
  {
    Iterator<Sensor> allSensors = deviceStateCache.listSensors();

    while (allSensors.hasNext())
    {
      log.info("Stopping sensor ''{0}'' (ID = ''{1}'')...");

      allSensors.next().stop();
    }
  }

  /**
   * TODO : temporary -- these are here temporarily until ControllerXMLChangeServiceImpl has been completely phased out (part of ORCJAVA-115)
   */
  public void clearDeviceStateCache()
  {
    deviceStateCache.clear();
  }

  /**
   * TODO - temporary
   *
   * @return
   */
  public Document getControllerDocument()
  {
    return modelBuilder.getControllerDocument();
  }


  /**
   * TODO:
   *
   *   - stopSensors() and clearDeviceStateCache() should be eventually included here
   *   - currently multiple object models must be supported through class hierarchy. If
   *     this needs to change, Sensor must be defined via an interface
   */
  public void softRestart()
  {
    softShutdown();

    startup();
  }






  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * TODO:
   *   should implement the sequence of shutting down the currently deployed controller
   *   configuration (but not exit the process). Currently only manages sensor resources.
   *   Others should follow.
   */
  private void softShutdown()
  {

    log.info(
        "\n" +
        "********************************************************************\n\n" +
        "  SHUTTING DOWN CURRENT CONTROLLER RUNTIME...\n\n" +
        "********************************************************************\n"
    );

    deviceStateCache.unregisterAllSensors();

    // TODO : connection manager shutdowns

    // TODO : event processor shutdowns


    log.info("Shutdown complete.");
  }


  /**
   * TODO:
   *   should manage the build-up of controller runtime with object model creation
   *   from the XML document instance(s).
   *
   */
  private void startup()
  {
    log.info(
        "\n" +
        "********************************************************************\n\n" +
        "  CREATING NEW CONTROLLER RUNTIME...\n\n" +
        "********************************************************************\n"
    );

    ControllerSchemaVersion version = detectVersion();

    switch (version)
    {
      case VERSION_3_0:

        modelBuilder = new Version30ModelBuilder();
        break;

      case VERSION_2_0:

        modelBuilder = new Version20ModelBuilder();
        break;

      default:

        throw new Error("Unrecognized schema version " + version);
    }

    modelBuilder.buildModel();

    log.info("Startup complete.");
  }





  private ControllerSchemaVersion detectVersion()
  {
    String uri = new File(controllerConfig.getResourcePath()).toURI().toString() + "openremote.xml";

    if (new File(uri).exists())       // TODO : sec manager
    {
      return ControllerSchemaVersion.VERSION_3_0;
    }

    String xmlPath =
        PathUtil.addSlashSuffix(controllerConfig.getResourcePath()) +
        Constants.CONTROLLER_XML;

    if (new File(xmlPath).exists())   // TODO : sec manager
    {
      return ControllerSchemaVersion.VERSION_2_0;
    }

    return ControllerSchemaVersion.VERSION_2_0; // TODO: use DEFAULT_SCHEMA_VERSION;
  }







  // Inner Classes --------------------------------------------------------------------------------



  private class Version20ModelBuilder implements ModelBuilder
  {


    protected ControllerSchemaVersion version;



    protected Version20ModelBuilder()
    {
      this.version = ControllerSchemaVersion.VERSION_2_0;
    }



    // Implements ModelBuilder --------------------------------------------------------------------

    /**
     * Get a document for a user referenced controller.xml with xsd validation.
     *
     * @return a builded document for controller.xml.
     */
    @Override public Document getControllerDocument()
    {
      String xmlPath = PathUtil.addSlashSuffix(
          controllerConfig.getResourcePath()) + Constants.CONTROLLER_XML;

      SAXBuilder builder = new SAXBuilder();
      Document doc = null;

      try
      {
        builder.setValidation(true);
        File xsdfile = new File(Version20ModelBuilder.class.getResource(Constants.CONTROLLER_XSD_PATH).getPath());

        builder.setProperty(Constants.SCHEMA_LANGUAGE, Constants.XML_SCHEMA);
        builder.setProperty(Constants.SCHEMA_SOURCE, xsdfile);
        doc = builder.build(xmlPath);
      }

      catch (JDOMException e)
      {
        throw new RuntimeException(e);    // TODO : fix this
      }

      catch (IOException e)
      {
        throw new RuntimeException(e);    // TODO : fix this
      }

      return doc;
    }


    @Override public void buildModel()
    {
      buildSensorModel();
    }




    // Private Instance Methods -------------------------------------------------------------------

    private void buildSensorModel()
    {
      Set<Sensor> sensors = buildSensorObjectModelFromXML();

      for (Sensor sensor : sensors)
      {
        deviceStateCache.registerSensor(sensor);

        sensor.start();
      }
    }


    /**
     * TODO
     *
     * @return
     */
    private Set<Sensor> buildSensorObjectModelFromXML()
    {

      // Parse <sensors> element from the controller.xml...

      try
      {
        Element sensorsElement = querySensorSegment();

        if (sensorsElement == null)
        {
          log.info("No sensors found.");

          return new HashSet<Sensor>(0);
        }

        // Get the list of <sensor> elements from within <sensors>...

        Iterator<Element> sensorElementIterator = getSensorElements(sensorsElement).iterator();
        Set<Sensor> sensorModels = new HashSet<Sensor>();

        while (sensorElementIterator.hasNext())
        {
          try
          {
            Element sensorElement = sensorElementIterator.next();

            BuilderKey key = new BuilderKey(version, XMLSegment.SENSORS);

            ObjectBuilder ob = objectBuilders.get(key);

            if (ob == null)
            {
              throw new Error( // TODO : proper exception handling
                  "No object builder found for <" + XMLSegment.SENSORS.getName() + "> XML segment " +
                  "in schema " + version
              );
            }

            Sensor sensor = (Sensor)ob.build(sensorElement);

            log.debug(
                "Created object model for sensor ''{0}'' (ID = ''{1}'').",
                sensor.getName(), sensor.getSensorID()
            );

            sensorModels.add(sensor);
          }

          catch (Throwable t)
          {
            // If building the sensor fails for any reason, log it and skip it...

            // TODO :
            //   - at this point it is likely the component will fail in this case, unless and
            //     until its error handling is made more robust

            log.error("Creating sensor failed : " + t.getMessage(), t);
          }
        }

        return sensorModels;
      }
      catch (Throwable t)
      {
        log.error("No sensors found - {0}", t, t.getMessage());

        return new HashSet<Sensor>(0);
      }
    }


    /**
     * TODO
     *
     * @param sensorsElement
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<Element> getSensorElements(Element sensorsElement)
    {
      return sensorsElement.getChildren();
    }


    private Element querySensorSegment() throws InitializationException
    {
      SAXBuilder sb = new SAXBuilder();
      String xsdPath = Constants.CONTROLLER_XSD_PATH;
      URL resource = this.getClass().getResource(xsdPath);

      if (resource == null)
      {
        log.error("Unable to locate schema file ''{0}''. Disabling validation.", xsdPath);
      }

      else
      {
        try
        {
          String schemaFilePath = resource.getPath();

          // TODO : why are we decoding the XML schema path? This doesn't make sense to me. [JPL]
          schemaFilePath = URLDecoder.decode(schemaFilePath, Constants.CHARACTER_ENCODING_UTF8);
          sb.setProperty(Constants.SCHEMA_SOURCE, new File(schemaFilePath));
          sb.setProperty(Constants.SCHEMA_LANGUAGE, Constants.XML_SCHEMA);
          sb.setValidation(true);
        }
        catch (Throwable t)
        {
          throw new ConfigurationException("Decoding error : {0}", t, t.getMessage());
        }
      }

      String xmlPath = PathUtil.addSlashSuffix(
          controllerConfig.getResourcePath()) + Constants.CONTROLLER_XML;

      File controllerXMLFile = new File(xmlPath);

      if (!controllerXMLFile.exists())
      {
         throw new ConfigurationException(
             "Controller.xml not found -- make sure it's in " + controllerXMLFile.getAbsoluteFile()
         );
      }

      try
      {
        Document doc = sb.build(controllerXMLFile);
        String xPathExpression = "//" + Constants.OPENREMOTE_NAMESPACE + ":sensors";

        return queryElementFromXML(doc, xPathExpression);
      }

      catch (Throwable t)
      {
        throw new XMLParsingException(
            "Unable to parse ''{0}'': {1}", t, controllerXMLFile.getAbsoluteFile(), t.getMessage()
        );
      }
    }
  }

  


  

  // Nested Classes -------------------------------------------------------------------------------

  private static interface ModelBuilder
  {
    void buildModel();

    Document getControllerDocument();
  }

  private static class Version30ModelBuilder implements ModelBuilder
  {

    @Override public Document getControllerDocument()
    {
      return null;
    }

    @Override public void buildModel()
    {
      // nothing here yet...
    }
  }



  private static class BuilderKey
  {

    private ControllerSchemaVersion version;
    private XMLSegment segment;


    private BuilderKey(ControllerSchemaVersion version, XMLSegment rootSegment)
    {
      this.version = version;
      this.segment = rootSegment;
    }

    @Override public int hashCode()
    {
      return version.hashCode() + segment.hashCode();
    }

    @Override public boolean equals(Object o)
    {
      if (o == null)
      {
        return false;
      }

      if (o == this)
      {
        return true;
      }

      if (!(o.getClass().equals(this.getClass())))
      {
        return false;
      }

      BuilderKey other = (BuilderKey)o;

      return (this.version.equals(other.version)) && (this.segment.equals(other.segment));
    }
  }
}

