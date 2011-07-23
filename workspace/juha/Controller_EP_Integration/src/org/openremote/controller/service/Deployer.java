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
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.xml.ObjectBuilder;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.statuscache.ChangedStatusTable;
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
 * Deployer service centralizes access to the controller's runtime state information. It maintains
 * the controller object model (declared in the XML documents controller deploys), and also
 * acts as a mediator for some other key services in the controller. <p>
 *
 * Mainly the tasks relate to objects and services that maintain some state in-memory of
 * the controller, and where access to such objects or services needs to be shared across
 * multiple threads (rather than created per thread or invocation). Deployer manages the lifecycle
 * of such stateful objects and services to ensure proper state transitions when new controller
 * definitions (from the XML model) are loaded, for instance. <p>
 *
 * Main parts of this implementation relate to managing the XML to Java object mapping --
 * transferring the information from the XML document instance that describe controller
 * behavior into a runtime object model -- and managing the lifecycle of services through
 * restarts and reloading the controller descriptions. In addition, this deployer provides
 * access to the object model instances it generetaes (such as references to the sensor
 * implementations).
 *
 *
 * @see #softRestart
 * @see #getSensor
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class Deployer
{

  // Enums ----------------------------------------------------------------------------------------

  /**
   * Indicates a controller schema version which the deployer attempts to map to its object model.
   * <p>
   *
   * These enums act as keys (or part of keys) to locate XML mapping components and object model
   * builders associated with specific XML document schemas.
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
   * Utility method to execute a given XPath expression against the given XML document
   * instance. This implementation is limited to XPath expressions that target XML document
   * elements only.
   *
   * @param doc     XML document instance
   * @param xPath   XPath expression to return a single XML document element
   *
   * @return  One XML document element or <tt>null</tt> if nothing was found
   *
   * @throws  XMLParsingException   if there were errors creating the XPath expression
   *                                or executing it
   */
  private static Element queryElementFromXML(Document doc, String xPath) throws XMLParsingException
  {
    if (doc == null)
    {
      throw new XMLParsingException(
          "Cannot executed XPath expression ''{0}'' -- XML document instance was null.", xPath
      );
    }

    if (xPath == null || xPath.equals(""))
    {
      throw new XMLParsingException(
          "Null or empty XPath expression for document {0}", doc
      );
    }

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
   * TODO : temporarly here, may be moved later
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
   * Reference to status cache instance that does the actual lifecycle management of sensors
   * (and receives the event updates). This implementation delegates these tasks to it.
   */
  private StatusCache deviceStateCache;


  /**
   * User defined controller configuration variables.
   */
  private ControllerConfiguration controllerConfig;


  /**
   * Acts as a registry of object builders for this deployer. Object builders are responsible
   * for mapping the XML document model into Java object model for specific XML schema versions. <p>
   *
   * The key to this map contains the expected schema version and the XML segment root element
   * which the builder is able to map to Java objects. <p>
   *
   * Map of object builders is maintained through the lifecycle of the JVM -- they are not
   * reset at controller soft restarts.
   *
   * @see org.openremote.controller.model.xml.ObjectBuilder
   * @see org.openremote.controller.model.xml.SensorBuilder
   * @see ModelBuilder
   * @see BuilderKey
   * @see #softRestart
   */
  private Map<BuilderKey, ObjectBuilder> objectBuilders = new HashMap<BuilderKey, ObjectBuilder>(10);


  /**
   * Model builders are sequences of actions to construct the controller's object model (a.k.a
   * strategy pattern). Different model builders may therefore act on differently
   * structured XML document instances. <p>
   *
   * This model builder's lifecycle is delimited by the controller's soft restart 
   * lifecycle (see {@link #softRestart()}. Each deploy lifecycle represents one object model
   * (and therefore one model builder instance) that matches a particular XML schema structure.
   *
   * @see ModelBuilder
   * @see #softRestart
   */
  private ModelBuilder modelBuilder = null;


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Creates a new deployer service with a given device state cache implementation and user
   * configuration variables.
   *
   * @param deviceStateCache    device cache instance for this deployer
   * @param controllerConfig    user configuration of this deployer's controller
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
   * Allows object builders to be registered with this deployer. Object builders can be
   * registered for multiple different XML schema versions.
   *
   * @see org.openremote.controller.service.Deployer.ControllerSchemaVersion
   * @see org.openremote.controller.model.xml.ObjectBuilder
   * @see org.openremote.controller.model.xml.SensorBuilder
   *
   * @param builder   object builder instance to register
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
   * Returns a registered sensor instance. Sensor instances are shared across threads.
   * Retrieving a sensor with the same ID will yield a same instance. <p>
   *
   * If the sensor with given ID is not found, a <tt>null</tt> is returned.
   *
   * TODO - define Sensor interface
   *
   * @see org.openremote.controller.model.sensor.Sensor#getSensorID()
   *
   * @param id    sensor ID
   *
   * @return      sensor instance, or null if sensor with given ID was not found
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
   * TODO - temporary
   *
   * @return
   */
  public Document getControllerDocument()
  {
    try
    {
      return modelBuilder.getControllerDocument();
    }

    catch (InitializationException e)
    {
      log.error("Unable to retrieve the controller's document instance : {0}", e, e.getMessage());

      return null;
    }
  }


  /**
   * Initiate a shutdown/startup sequence.  <p>
   *
   * Shutdown phase will undeploy the current runtime object model. Resources will be stopped and
   * freed. This however only impacts the runtime object model of the controller. The controller
   * itself stays at an init level where a new object model can be loaded into the system. The JVM
   * process will not exit. <p>
   *
   * Startup phase loads back a runtime object model into the controller. The loading is done
   * from the controller definition file, path of which is indicated by the
   * {@link org.openremote.controller.ControllerConfiguration#getResourcePath()} method. <p>
   *
   * After the startup phase is done, a complete functional controller definition has been
   * loaded into the controller (unless fatal errors occured), has been initialized and
   * started as is ready to handle incoming requests.
   *
   * @see org.openremote.controller.ControllerConfiguration#getResourcePath
   * @see #softShutdown
   * @see #startup()
   *
   * @throws InitializationException    if the restart encounters errors it cannot recover from
   */
  public void softRestart() throws InitializationException
  {
    softShutdown();

    startup();
  }






  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Implements the sequence of shutting down the currently deployed controller
   * runtime (but will not exit the VM process). <p>
   *
   * After this method completes, the controller has no active runtime object model but is
   * at an init level where a new one can be loaded in.
   */
  private void softShutdown()
  {

    log.info(
        "\n" +
        "********************************************************************\n\n" +
        "  SHUTTING DOWN CURRENT CONTROLLER RUNTIME...\n\n" +
        "********************************************************************\n"
    );


    deviceStateCache.shutdown();


    // TODO : stop file watcher
    
    // TODO : connection manager shutdowns

    // TODO : event processor shutdowns


    log.info("Shutdown complete.");
  }


  /**
   * Manages the build-up of controller runtime with object model creation
   * from the XML document instance(s). Attempts to detect from configuration files
   * which version of object model and corresponding XML schema should be used to
   * build the runtime model. <p>
   *
   * Once this method returns, the controller runtime is 'ready' -- that is, the object
   * model has been created but also initialized, registered and started so it is fully
   * functional and able to receive requests.
   *
   * @throws InitializationException
   *              If the startup cannot be completed. Note that partial failures (such as
   *              minor errors in the controller's object model definition) may not prevent
   *              the startup from completing, and will be logged as errors instead.
   */
  private void startup() throws InitializationException
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

        // NOTE: the schema 2.0 builder auto-starts all the sensors it locates in the
        //       controller.xml definition

        modelBuilder = new Version20ModelBuilder(this);
        break;

      default:

        throw new Error("Unrecognized schema version " + version);
    }

    modelBuilder.buildModel();

    log.info("Startup complete.");
  }


  /**
   * A simplistic attempt at detecting which schema version we should use to build
   * the object model.
   *
   * TODO -- the document instances we use need to start declaring version information
   *
   * @return  the detected schema version
   *
   * @throws  ConfigurationException    if we can't find any controller definition files to load
   */
  private ControllerSchemaVersion detectVersion() throws ConfigurationException
  {

    // Check if 3.0 schema instance is in place (this doesn't actually exist yet...)

    if (Version30ModelBuilder.checkControllerDefinitionExists(controllerConfig))
    {
      return ControllerSchemaVersion.VERSION_3_0;
    }

    // Check for 2.0 schema instance...

    if (Version20ModelBuilder.checkControllerDefinitionExists(controllerConfig))
    {
      return ControllerSchemaVersion.VERSION_2_0;
    }

    throw new ConfigurationException(
        "Could not find a controller definition to load at path ''{0}''",
        Version20ModelBuilder.getControllerDefinitionFile(controllerConfig)
    );
  }








  // Inner Classes --------------------------------------------------------------------------------


  /**
   * Controller's object model builder for the current 2.0 version of the implementation.
   */
  private static class Version20ModelBuilder implements ModelBuilder
  {


    // Class Members ------------------------------------------------------------------------------

    /**
     * Utility method to return a Java I/O File instance representing the artifact with
     * controller runtime object model definition.
     *
     * @param   config    controller's user configuration
     *
     * @return  file representing an object model definition for a controller
     */
    private static File getControllerDefinitionFile(ControllerConfiguration config)
    {
      String xmlPath = PathUtil.addSlashSuffix(config.getResourcePath()) + Constants.CONTROLLER_XML;

      return new File(xmlPath);
    }


    /**
     * Utility method to isolate the privileged code block for file read access check (exists)
     *
     * @param   config    controller's user configuration
     *
     * @return  true if file exists; false if file does not exists or was denied by
     *          security manager
     */
    private static boolean checkControllerDefinitionExists(ControllerConfiguration config)
    {
      final File file = getControllerDefinitionFile(config);

      // BEGIN PRIVILEGED CODE BLOCK ----------------------------------------------------------------

      return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Boolean>()
      {
        @Override public Boolean run()
        {
          try
          {
            return file.exists();
          }

          catch (SecurityException e)
          {
            log.error(
                "Security manager prevented read access to file ''{0}'' : {1}",
                e, file.getAbsoluteFile(), e.getMessage()
            );

            return false;
          }
        }
      });

      // END PRIVILEGED CODE BLOCK ------------------------------------------------------------------
    }




    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Contains the schema version for this implementation. This is used by some generic
     * implementations in this class which may be overriden by subclasses that implement
     * minor (but incompatible) changes to schema as it evolves. Therefore in some cases
     * it may be possible to extend and override this implementation for a next minor schema
     * version rather than implement the entire model builder from scratch.
     */
    protected ControllerSchemaVersion version;

    private Deployer deployer;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Initialize this model builder instance to schema version 2.0
     * ({@link ControllerSchemaVersion#VERSION_2_0}).
     *
     * @param deployer
     */
    protected Version20ModelBuilder(Deployer deployer)
    {
      this.deployer = deployer;
      this.version = ControllerSchemaVersion.VERSION_2_0;
    }



    // Implements ModelBuilder --------------------------------------------------------------------

    /**
     * TODO :
     * 
     *   Get a document for a user referenced controller.xml with xsd validation.
     *
     *   This implementation is here temporarily to support existing client interface access
     *   to deployer service -- it may however be removed later since it should not be necessary
     *   to expose the XML document instance to outside services beyond what is provided by the
     *   Deployer API.
     *
     * @return a built document for controller.xml.
     */
    @Override public Document getControllerDocument() throws InitializationException
    {
      SAXBuilder builder = new SAXBuilder();
      String xsdPath = Constants.CONTROLLER_XSD_PATH;
      File controllerXMLFile = getControllerDefinitionFile(deployer.controllerConfig);

      try
      {
        URL xsdResource = Version20ModelBuilder.class.getResource(xsdPath);

        if (xsdResource == null)
        {
          log.error("Cannot find XSD schema ''{0}''. Disabling validation...", xsdPath);
        }

        else
        {
          xsdPath = xsdResource.getPath();

          builder.setProperty(Constants.SCHEMA_LANGUAGE, Constants.XML_SCHEMA);
          builder.setProperty(Constants.SCHEMA_SOURCE, new File(xsdPath));
          builder.setValidation(true);
        }

        if (!checkControllerDefinitionExists(deployer.controllerConfig))
        {
           throw new ConfigurationException(
               "Controller.xml not found -- make sure it's in " + controllerXMLFile.getAbsoluteFile()
           );
        }

        return builder.build(controllerXMLFile);
      }

      catch (Throwable t)
      {
        throw new XMLParsingException(
            "Unable to parse controller definition from " +
            "''{0}'' (accessing schema from ''{1}'') : {2}",
            t, controllerXMLFile.getAbsoluteFile(), xsdPath, t.getMessage()
        );
      }
    }


    /**
     * TODO:
     *    - Sequence of actions to build object model based on the current 2.0 schema.
     *      Right now just has sensors.
     */
    @Override public void buildModel() throws InitializationException
    {
      // TODO : command model
      
      buildSensorModel();
    }




    // Protected Instance Methods -----------------------------------------------------------------

    /**
     * Build concrete sensor Java instances from the XML declaration. <p>
     *
     * NOTE: this implementation will register and start the sensors at build time automatically.
     *
     * @throws XMLParsingException  if building the model fails because of parser errors
     */
    protected void buildSensorModel() throws XMLParsingException
    {

      // Build...

      Set<Sensor> sensors = buildSensorObjectModelFromXML();

      // Register and start...

      for (Sensor sensor : sensors)
      {
        deployer.deviceStateCache.registerSensor(sensor);

        sensor.start();
      }
    }



    /**
     * Parse sensor definitions from controller.xml and create the corresponding Java objects. <p>
     *
     * This method is somewhat generic in that it delegates to object builder instances that
     * register with {@link XMLSegment#SENSORS} identifier. Therefore if the top-level
     * {@code <sensors>} element is present in other schema definitions, this implementation
     * may be reused by registering an alternative object builder with the same <tt>SENSORS</tt>
     * identifier. The schema version part of the key in {@link Deployer#objectBuilders} is
     * determined by subclassing and overriding this implementations {@link #version} field.
     *
     * @see org.openremote.controller.model.xml.ObjectBuilder
     * @see #version
     * @see Deployer#registerObjectBuilder
     *
     * @return  list of sensor instances that were succesfully built from the controller.xml
     *          declaration
     *
     * @throws XMLParsingException if appropriate sensor object builder has not been registered
     */
    protected Set<Sensor> buildSensorObjectModelFromXML() throws XMLParsingException
    {
      try
      {
        // Parse <sensors> element from the controller.xml...

        Document doc = getControllerDocument();
        String xPathExpression = "//" + Constants.OPENREMOTE_NAMESPACE + ":sensors";

        Element sensorsElement = queryElementFromXML(doc, xPathExpression);

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

            // Get and build using the registered sensor builder implementation
            // (needs to have a matching schema version and correct XML segment identifier)

            BuilderKey key = new BuilderKey(version, XMLSegment.SENSORS);

            ObjectBuilder ob = deployer.objectBuilders.get(key);

            if (ob == null)
            {
              throw new XMLParsingException(
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



    // Private Instance Methods -------------------------------------------------------------------

    /**
     * Isolated this one method call to suppress warnings (JDOM API does not use generics).
     *
     * @param sensorsElement  the {@code <sensors>} element of controller.xml file
     *
     * @return  the child elements of {@code <sensors>}
     */
    @SuppressWarnings("unchecked")
    private List<Element> getSensorElements(Element sensorsElement)
    {
      return sensorsElement.getChildren();
    }
  }

  


  

  // Nested Classes -------------------------------------------------------------------------------

  /**
   * Model builders are sequences of actions which construct the controller's object model.
   * Therefore it implements a strategy pattern. Different model builders may act on differently
   * structured XML document instances. <p>
   *
   * The implementation of a model builder is expected not only to create the Java object instances
   * representing the object model, but also initialize, register and start all the created
   * resources as necessary. On returning from the {@link #buildModel()} method, the controller's
   * object model is expected to be running and fully functional.
   */
  private static interface ModelBuilder
  {
    /**
     * Responsible for constructing the controller's object model. Implementation details
     * vary depending on the schema and source of defining artifacts.
     *
     * @throws InitializationException
     *            If there's a failure to build the controller's object model for any reason.
     *            Notice that partial failures (on particular isolated object instances, for
     *            example) may be tolerated by the model builder implementation. This exception
     *            usually indicates a fatal problem in initializing the object model (such
     *            as failure to read the model definitions altogether).
     */
    void buildModel() throws InitializationException;

    /**
     * TODO : temporary
     *
     * @return
     *
     * @throws XMLParsingException 
     */
    Document getControllerDocument() throws InitializationException;
  }


  /**
   * TODO :
   *
   *   placeholder for the next major schema version that is currently in planning stages.
   */
  private static class Version30ModelBuilder implements ModelBuilder
  {

    @Override public Document getControllerDocument()
    {
      return null;
    }

    @Override public void buildModel() throws InitializationException
    {
      // nothing here yet...
    }

    /**
     * Utility method to return a Java I/O File instance representing the artifact with
     * controller runtime object model definition.
     *
     * @param   config    controller's user configuration
     *
     * @return  file representing an object model definition for a controller
     */
    private static File getControllerDefinitionFile(ControllerConfiguration config)
    {
      String uri = new File(config.getResourcePath()).toURI().toString() + "openremote.xml";

      return new File(uri);
    }

    /**
     * Utility method to isolate the privileged code block for file read access check (exists)
     *
     * @param   config    controller's user configuration
     *
     * @return  true if file exists; false if file does not exists or was denied by
     *          security manager
     */
    private static boolean checkControllerDefinitionExists(ControllerConfiguration config)
    {
      final File file = getControllerDefinitionFile(config);

      // BEGIN PRIVILEGED CODE BLOCK ----------------------------------------------------------------

      return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Boolean>()
      {
        @Override public Boolean run()
        {
          try
          {
            return file.exists();
          }

          catch (SecurityException e)
          {
            log.error(
                "Security manager prevented read access to file ''{0}'' : {1}",
                e, file.getAbsoluteFile(), e.getMessage()
            );

            return false;
          }
        }
      });

      // END PRIVILEGED CODE BLOCK ------------------------------------------------------------------
    }

  }


  /**
   * Key implementation for object builders. Constructs a key based on the controller schema
   * version and xml segment identifier provided by a concrete object builder implementation.
   *
   * @see org.openremote.controller.model.xml.ObjectBuilder#getSchemaVersion()
   * @see org.openremote.controller.model.xml.ObjectBuilder#getRootSegment()
   */
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

