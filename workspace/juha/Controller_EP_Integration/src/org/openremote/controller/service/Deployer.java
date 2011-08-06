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

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.OpenRemoteRuntime;
import org.openremote.controller.exception.ControllerDefinitionNotFoundException;
import org.openremote.controller.exception.InitializationException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.model.XMLMapping;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.xml.ObjectBuilder;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.PathUtil;

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
 * access to the object model instances it generates (such as references to the sensor
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

  /*
   *  IMPLEMENTATION NOTES:
   *
   *   Relevant tasks todo:
   *     -  ORCJAVA-123 (http://jira.openremote.org/browse/ORCJAVA-123) : introduce an immutable
   *        sensor interface for plugins to use.
   *
   */


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
    SENSORS("sensors"),

    /**
     * TODO
     */
    SLIDER("slider"),

    /**
     * TODO
     */
    CONFIG("config");


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
    // TODO : this method should probably be under the ModelBuilder hierarchy

    if (doc == null)
    {
      throw new XMLParsingException(
          "Cannot execute XPath expression ''{0}'' -- XML document instance was null.", xPath
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
        if (elements.size() > 1)
        {
          throw new XMLParsingException(
              "Expression ''{0}'' matches more than one element : {1}",
              xPath, elements.size()
          );
        }

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
          "XPath evaluation ''{0}'' failed : {1}", e, xPath, e.getMessage()
      );
    }
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
   * Model builder implementations in general delegate sub-tasks to various object builders
   * that have been registered for the relevant XML schema. <p>
   *
   * This model builder's lifecycle is delimited by the controller's soft restart 
   * lifecycle (see {@link #softRestart()}. Each deploy lifecycle represents one object model
   * (and therefore one model builder instance) that matches a particular XML schema structure.
   *
   * @see ModelBuilder
   * @see #softRestart
   */
  private ModelBuilder modelBuilder = null;


  /**
   * This is a file watch service for the controller definition associated with the current
   * object model builder (i.e. the currently deployed controller XML schema). <p>
   *
   * Depending on the implementation (deletegated to current model builder instance) it may
   * detect changes from the file's timestamp, adding/deleting particular files, etc. and
   * control the deployer lifecycle accordingly, initiating soft restarts, shutdowns, and so on.
   */
  private ControllerDefinitionWatch controllerDefinitionWatch;

  
  /**
   * This is a generic state flag indicator for this deployer service that its operations are
   * in a 'paused' state -- these may occur during periods where the internal object model is
   * changed, such as during a soft restart. <p>
   *
   * Method implementations in this class may use this flag to check whether to service the
   * incoming call, block it until pause flag is cleared, or immediately return back to the
   * caller.
   */
  private boolean isPaused = false;


  /**
   * Human readable service name for this deployer service. Useful for some logging and
   * diagnostics.
   */
  private String name = "<undefined>";



  // Constructors ---------------------------------------------------------------------------------

  /**
   * Creates a new deployer service with a given device state cache implementation and user
   * configuration variables. <p>
   *
   * Creating a deployer instance will not make it 'active' -- no controller object model is
   * loaded (or attempted to be loaded) until a {@link #startController()} method is called.
   * The <tt>startController</tt> therefore acts as an initializer method for the controller
   * runtime.
   *
   * @see #startController()
   *
   * @param serviceName         human-readable name for this deployer service
   * @param deviceStateCache    device cache instance for this deployer
   * @param controllerConfig    user configuration of this deployer's controller
   */
  public Deployer(String serviceName, StatusCache deviceStateCache,
                  ControllerConfiguration controllerConfig)
  {
    if (deviceStateCache == null || controllerConfig == null)
    {
      throw new IllegalArgumentException("Null parameters are not allowed.");
    }
    
    this.deviceStateCache = deviceStateCache;
    this.controllerConfig = controllerConfig;

    if (name != null)
    {
      this.name = serviceName;
    }
    
    this.controllerDefinitionWatch = new ControllerDefinitionWatch(this);

    log.debug("Deployer ''{0}'' initialized.", name);
  }
  

  // Public Instance Methods ----------------------------------------------------------------------


  /**
   * This method initializes the controller's runtime model, making it 'active'. The method should
   * be called once during the lifecycle of the controller JVM -- subsequent re-deployments of
   * controller's runtime should go via {@link #softRestart()} method. <p>
   *
   * If a controller definition is present, it is loaded and the object model created accordingly.
   * If no definition is found, the controller is left in an init state where adding the
   * required artifacts to the controller will trigger the deployment of controller definition.
   *
   * @see #softRestart()
   */
  public void startController()
  {
    try
    {
      startup();
    }

    catch (ControllerDefinitionNotFoundException e)
    {
      log.info(
         "\n\n" +
         "********************************************************************************\n" +
         "\n" +
         " Controller definition was not found in this OpenRemote Controller instance.      \n" +
         "\n" +
         " If you are starting the controller for the first time, please use your web     \n" +
         " browser to connect to the controller home page and synchronize it with your    \n" +
         " online account. \n" +
         "\n" +
         "********************************************************************************\n\n" +

         "\n" + e.getMessage()
      );
    }

    catch (Throwable t)
    {
      log.error("!!! CONTROLLER STARTUP FAILED : {0} !!!", t, t.getMessage());
    }


    controllerDefinitionWatch.start();

    // TODO : register shutdown hook
  }


  /**
   * Indicates the current state of the deployer. Deployer may be 'paused' during certain
   * lifecycle stages, such as reloading the controller's internal object model. During those
   * phases, other deployer operations may opt to block calling threads until deployer has
   * resumed, or return calls immediately without servicing them.
   * 
   * @return    true to indicate deployer is currently paused, false otherwise
   */
  public boolean isPaused()
  {
    return isPaused;
  }


  /**
   * Allows object builders to be registered with this deployer. Object builders can be
   * registered for multiple different XML schema versions.  <p>
   *
   * A deployer is associated with one active model builder at a time (per controller model
   * deployment). Model builders may delegate their tasks to registered object builders as
   * necessary.
   *
   * @see ModelBuilder
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
      // this continue as it is, that is, returning a null pointer which is likely to blow up
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
   * started and is ready to handle incoming requests. <p>
   *
   * <b>NOTE : </b> This method call should only be used after {@link #startController()}
   * has been invoked once. The <tt>startController</tt> method will initialize this deployer
   * instance's lifecycle, and perform first deployment of controller definition, if the
   * required artifacts are present in the controller. </p>
   *
   * Subsequent soft restarts of this controller/deployer should use this method instead.
   *
   * @see #startController()
   * @see org.openremote.controller.ControllerConfiguration#getResourcePath
   * @see #softShutdown
   * @see #startup()
   *
   * @throws ControllerDefinitionNotFoundException
   *            If there are no controller definitions to load from. This exception indicates that
   *            the {@link #startup} phase of the restart cannot complete. The controller/deployer
   *            is left in an init state where the previous controller object model has been
   *            undeployed, and a new one will be deployed once the required artifacts have been
   *            added to the controller.
   */
  public void softRestart() throws ControllerDefinitionNotFoundException
  {
    try
    {
      pause();

      softShutdown();

      startup();
    }

    finally
    {

      resume();
    }
  }



  /**
   * TODO : temporarly here, may be moved later, see above
   *
   * @param id
   * @return
   * @throws InitializationException
   */
  public Element queryElementById(int id) throws InitializationException
  {
    if (modelBuilder == null)
    {
      throw new IllegalStateException("Runtime object model has not been initialized.");
    }

    Element element = queryElementFromXML(
        modelBuilder.getControllerDocument(), "//" + Constants.OPENREMOTE_NAMESPACE + ":*[@id='" + id + "']"
    );

    if (element == null)
    {
      throw new XMLParsingException("No component found with id ''{0}''.", id);
    }

    return element;
  }

  /**
   * TODO : temporarly here, may be moved later, see above
   *
   * @param xmls
   * @return
   * @throws InitializationException
   */
  public Element queryElementByName(XMLSegment xmls) throws InitializationException
  {
    if (modelBuilder == null)
    {
      throw new IllegalStateException("Runtime object model has not been initialized.");
    }

    Element element = queryElementFromXML(
        modelBuilder.getControllerDocument(), "//" + Constants.OPENREMOTE_NAMESPACE + ":" + xmls.getName()
    );

    if (element == null)
    {
      throw new XMLParsingException("No XML elements found with name ''{0}''.", xmls.getName());
    }

    return element;
  }

  
  /**
   * TODO : Temporary -- Builds a sensor from XML element, see above
   *
   * @param componentIncludeElement   JDOM element for sensor
   *
   * @throws InitializationException    if the sensor model cannot be built from the given XML
   *                                    element
   *
   * @return sensor
   */
  public Sensor getSensorFromComponentInclude(Element componentIncludeElement)
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

      return getSensor(sensorID);
    }

    catch (NumberFormatException e)
    {
        throw new InitializationException(
            "Currently only integer values are accepted as unique sensor ids. " +
            "Could not parse {0} to integer.", refAttributeValue
        );
    }
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
        "\n\n" +
        "--------------------------------------------------------------------\n\n" +
        "  UNDEPLOYING CURRENT CONTROLLER RUNTIME...\n\n" +
        "--------------------------------------------------------------------\n"
    );


    deviceStateCache.shutdown();


    // TODO : event processor shutdowns
    // TODO : connection manager shutdowns


    modelBuilder = null;                // null here indicates to other services that this deployer
                                        // installer currently has no object model deployed
    
    log.info("Shutdown complete.");
  }


  /**
   * Manages the build-up of controller runtime with object model creation
   * from the XML document instance(s). Attempts to detect from configuration files
   * which version of object model and corresponding XML schema should be used to
   * build the runtime model. <p>
   *
   * Once this method returns, the controller runtime is 'ready' -- that is, the object
   * model has been created and also initialized, registered and started so it is fully
   * functional and able to receive requests.   <p>
   *
   * Note that partial failures (such as errors in the controller's object model definition) may
   * not prevent the startup from completing. Such errors may be logged instead, leaving the
   * controller with an object model that is only partial from the intended one.
   *
   * @throws ControllerDefinitionNotFoundException
   *              If the startup could not be completed because no controller definition
   *              was found.
   */
  private void startup() throws ControllerDefinitionNotFoundException
  {
    ControllerSchemaVersion version = detectVersion();

    log.info(
        "\n\n" +
        "--------------------------------------------------------------------\n\n" +
        "  Deploying NEW CONTROLLER RUNTIME...\n\n" +
        "--------------------------------------------------------------------\n"
    );

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
   * Sets this deployer in 'paused' state. Method implementations can use this to determine
   * whether to reject or block incoming requests for deployer services.
   *
   * @see #resume
   */
  private void pause()
  {
    isPaused = true;

    controllerDefinitionWatch.pause();
  }


  /**
   * Resumes this deployer from a previously 'paused' state.
   *
   * @see #pause
   */
  private void resume()
  {
    try
    {
      controllerDefinitionWatch.resume();
    }

    finally
    {
      isPaused = false;
    }
  }

  /**
   * A simplistic attempt at detecting which schema version we should use to build
   * the object model.
   *
   * TODO -- the document instances we use need to start declaring version information
   *
   * @return  the detected schema version
   *
   * @throws  ControllerDefinitionNotFoundException
   *              if we can't find any controller definition files to load
   */
  private ControllerSchemaVersion detectVersion() throws ControllerDefinitionNotFoundException
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

    // TODO - update message below once 3.0 is in place

    throw new ControllerDefinitionNotFoundException(
        "Could not find a controller definition to load at path ''{0}'' (for version 2.0)",
        Version20ModelBuilder.getControllerDefinitionFile(controllerConfig)
    );
  }




  // Nested Classes -------------------------------------------------------------------------------


  /**
   * This service performs the automated file watching of the controller definition artifacts
   * (depending on the model builder implementation). <p>
   *
   * Per the rules defined in this implementation and in combination with those provided by
   * model builders via their
   * {@link org.openremote.controller.service.Deployer.ModelBuilder#hasControllerDefinitionChanged()}
   * method implementations, this service controls the deployer lifecycle through
   * {@link org.openremote.controller.service.Deployer#softRestart()} and
   * {@link Deployer#softShutdown()} methods.
   *
   * @see org.openremote.controller.service.Deployer#softRestart()
   * @see org.openremote.controller.service.Deployer#softShutdown()
   */
  private static class ControllerDefinitionWatch implements Runnable
  {

    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Deployer reference for this service to control the deployer lifecycle.
     */
    private Deployer deployer;

    /**
     * Indicates the watcher thread is running.
     */
    private volatile boolean running = true;

    /**
     * Indicates the wather thread should temporarily pause and not trigger any actions
     * on the deployer.
     */
    private volatile boolean paused = false;

    /**
     * The actual thread reference.
     */
    private Thread watcherThread;


    // Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new controller file watcher for a given deployer. Use {@link #start()} to
     * make this service active (start the relevant thread(s)).
     *
     * @param deployer  reference to the deployer whose lifecycle this watcher service controls
     */
    private ControllerDefinitionWatch(Deployer deployer)
    {
      this.deployer = deployer;
    }


    // Instance Methods ---------------------------------------------------------------------------

    /**
     * Starts the controller definition watcher thread.
     */
    public void start()
    {
      watcherThread = OpenRemoteRuntime.createThread(
          "Controller Definition File Watcher for " + deployer.name, this
      );

      watcherThread.start();

      log.info("{0} started.", watcherThread.getName());
    }


    /**
     * Stops (and kills) the controller definition watcher thread.
     */
    public void stop()
    {
      running = false;

      watcherThread.interrupt();
    }

    /**
     * Temporarily pauses the controller definition watcher thread, preventing any state
     * modifications to the associated deployment service.
     *
     * @see #resume
     */
    public void pause()
    {
      paused = true;
    }

    /**
     * Resumes the controller definition watcher thread after it has been {@link #pause() paused}.
     *
     * @see #pause
     */
    public void resume()
    {
      paused = false;
    }


    // Implements Runnable ------------------------------------------------------------------------

    /**
     * Runs the watcher thread using the following logic:  <p>
     *
     * - If paused, do nothing  <br>
     *
     * - If cannot detect controller definition files for any known schemas, keep waiting <br>
     *
     * - If detects a controller definition has been added but not deployed, run
     *   deployer.softRestart()  <br>
     *
     * - If has an existing controller object model deployed but the model builder reports
     *   a change in it (what constitutes a change depends on deployed model builder implementation),
     *   then run deployer.softRestart() <br>
     *
     * - If an existing controller model was deployed but the controller definition is removed
     *   (as reported by {@link org.openremote.controller.service.Deployer#detectVersion()})
     *   then undeploy the object model.
     */
    @Override public void run()
    {
      while (running)
      {
        if (paused)
          continue;

        try
        {
          deployer.detectVersion();     // will throw an exception if no known schemas are found...


          if (deployer.modelBuilder == null || deployer.modelBuilder.hasControllerDefinitionChanged())
          {
            try
            {
              deployer.softRestart();
            }

            catch (ControllerDefinitionNotFoundException e)
            {
              log.error(
                  "Soft restart cannot complete, controller definition not found : {0}",
                  e.getMessage()
              );
            }

            catch (Throwable t)
            {
              log.error(
                  "Controller soft restart failed : {0}",
                  t, t.getMessage()
              );
            }
          }
        }

        catch (ControllerDefinitionNotFoundException e)
        {
          if (deployer.modelBuilder != null)
          {
            deployer.softShutdown();
          }

          else
          {
            log.trace("Did not locate controller definitions for any known schema...");
          }
        }

        try
        {
          Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
          running = false;

          Thread.currentThread().interrupt();
        }
      }

      log.info("{0} has been stopped.", watcherThread.getName());
    }
  }



  /**
   * Controller's object model builder for the current 2.0 version of the implementation.
   */
  private static class Version20ModelBuilder implements ModelBuilder
  {


    // Class Members ------------------------------------------------------------------------------

    /**
     * Utility method to return a Java I/O File instance representing the artifact with
     * controller runtime object model (version 2.0) definition.
     *
     * @param   config    controller's user configuration
     *
     * @return  file representing an object model definition for a controller
     */
    private static File getControllerDefinitionFile(ControllerConfiguration config)
    {
      try
      {
        URI uri = new URI(config.getResourcePath());

        return new File(uri.resolve(Constants.CONTROLLER_XML));
      }
      catch (Throwable t)
      {
        // legacy...

        String xmlPath = PathUtil.addSlashSuffix(config.getResourcePath()) + Constants.CONTROLLER_XML;

        return new File(xmlPath);
      }
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

      try
      {
        // BEGIN PRIVILEGED CODE BLOCK ------------------------------------------------------------

        return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Boolean>()
        {
          @Override public Boolean run()
          {
            return file.exists();
          }
        });

        // END PRIVILEGED CODE BLOCK --------------------------------------------------------------
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




    // Instance Fields ----------------------------------------------------------------------------

    /**
     * Contains the schema version for this implementation. This is used by some generic
     * implementations in this class which may be overriden by subclasses that implement
     * minor (but incompatible) changes to schema as it evolves. Therefore in some cases
     * it may be possible to extend and override this implementation for a next minor schema
     * version rather than implement the entire model builder from scratch.
     */
    protected ControllerSchemaVersion version;

    /**
     * Reference to the deployer service this model builder instance is associated with.
     */
    private Deployer deployer;


    /**
     * Indicates whether the controller.xml for this schema implementation has been found.
     *
     * @see #hasControllerDefinitionChanged()
     */
    private boolean controllerDefinitionIsPresent;

    /**
     * Last known timestamp of controller.xml file.
     */
    private long lastTimeStamp = 0L;



    // Constructors -------------------------------------------------------------------------------

    /**
     * Initialize this model builder instance to schema version 2.0
     * ({@link ControllerSchemaVersion#VERSION_2_0}).
     *
     * @param deployer  reference to the deployer this model builder is associated with
     */
    protected Version20ModelBuilder(Deployer deployer)
    {
      this.deployer = deployer;
      this.version = ControllerSchemaVersion.VERSION_2_0;

      controllerDefinitionIsPresent = checkControllerDefinitionExists(deployer.controllerConfig);

      if (controllerDefinitionIsPresent)
      {
        lastTimeStamp = getControllerXMLTimeStamp();
      }
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

      if (!checkControllerDefinitionExists(deployer.controllerConfig))
      {
         throw new ControllerDefinitionNotFoundException(
             "Controller.xml not found -- make sure it's in " + controllerXMLFile.getAbsoluteFile()  // TODO: sec manager
         );
      }


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
     * 
     *    - Sequence of actions to build object model based on the current 2.0 schema.
     *      Right now just has sensors.
     */
    @Override public void buildModel()
    {
      // TODO : command model
      
      buildSensorModel();
    }


    /**
     * Attempts to determine whether the controller.xml 'last modified' timestamp has changed,
     * or if the file has been removed altogether, or if the file was not present earlier but
     * has been added since last check. <p>
     *
     * All the above cases yield an indication that the controller's model definition has changed
     * which can in turn result in reloading the model by the deployer (see
     * {@link org.openremote.controller.service.Deployer.ControllerDefinitionWatch} for more
     * details).
     * 
     * @return  true if controller.xml has been changed, removed or added since last check,
     *          false otherwise
     */
    @Override public boolean hasControllerDefinitionChanged()
    {
      if (controllerDefinitionIsPresent)
      {
        if (!checkControllerDefinitionExists(deployer.controllerConfig))
        {
          // it was there before, now it's gone...
          controllerDefinitionIsPresent = false;

          return true;
        }

        long lastModified = getControllerXMLTimeStamp();

        if (lastModified > lastTimeStamp)
        {
          lastTimeStamp = lastModified;

          return true;
        }
      }

      else
      {
        if (checkControllerDefinitionExists(deployer.controllerConfig))
        {
          controllerDefinitionIsPresent = true;

          return true;
        }
      }

      return false;
    }


    // Protected Instance Methods -----------------------------------------------------------------

    /**
     * Build concrete sensor Java instances from the XML declaration. <p>
     *
     * NOTE: this implementation will register and start the sensors at build time automatically.
     */
    protected void buildSensorModel()
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
     */
    protected Set<Sensor> buildSensorObjectModelFromXML()
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
     * Returns the timestamp of controller.xml file of this controller object model.
     *
     * @return  last modified timestamp, or zero if the timestamp cannot be accessed
     */
    private long getControllerXMLTimeStamp()
    {
      final File controllerXML = getControllerDefinitionFile(deployer.controllerConfig);

      try
      {
        // ----- BEGIN PRIVILEGED CODE BLOCK --------------------------------------------------------

        return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Long>()
        {
          @Override public Long run()
          {
            return controllerXML.lastModified();
          }
        });

        // ----- END PRIVILEGED CODE BLOCK ----------------------------------------------------------
      }

      catch (SecurityException e)
      {
        log.error(
            "Security manager prevented access to timestamp of file ''{0}'' ({1}). " +
            "Automatic detection of controller.xml file modifications are disabled.",
            e, controllerXML, e.getMessage()
        );

        return 0L;
      }
    }

    
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
     */
    void buildModel();


    /**
     * Model builder (schema) specific implementation to determine whether the controller
     * definition artifacts have changed in such a way that should result in redeploying the
     * object model.
     *
     * @see org.openremote.controller.service.Deployer.ControllerDefinitionWatch
     *
     * @return  true if the object model should be reloaded, false otherwise
     */
    boolean hasControllerDefinitionChanged();


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

    @Override public void buildModel() //throws InitializationException
    {
      // nothing here yet...
    }

    @Override public boolean hasControllerDefinitionChanged()
    {
      return false;
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
      String uri = new File(config.getResourcePath()).toURI().resolve("openremote.xml").getPath();

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

      try
      {
        // BEGIN PRIVILEGED CODE BLOCK ------------------------------------------------------------

        return AccessController.doPrivilegedWithCombiner(new PrivilegedAction<Boolean>()
        {
          @Override public Boolean run()
          {
              return file.exists();
          }
        });

        // END PRIVILEGED CODE BLOCK --------------------------------------------------------------
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

