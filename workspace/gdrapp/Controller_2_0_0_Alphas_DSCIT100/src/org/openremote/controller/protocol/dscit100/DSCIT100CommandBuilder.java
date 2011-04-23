/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * @author Greg Rapp
 * 
 */
public class DSCIT100CommandBuilder implements CommandBuilder
{

  // Constants
  // ------------------------------------------------------------------------------------

  /**
   * A common log category name intended to be used across all classes related
   * to DSCIT100 implementation.
   */
  public final static String DSCIT100_LOG_CATEGORY = "DSCIT100";

  /**
   * String constant for parsing DSCIT100 protocol XML entries from
   * controller.xml file.
   * 
   * This constant is the expected property name value for DSCIT100 addresses (
   * <code>{@value}</code>):
   * 
   * <pre>
   * {@code
   * <command protocol = "dscit100" >
   *   <property name = "address" value = "x.x.x.x:xxxx"/>
   *   <property name = "command" value = "ARM"/>
   *   <property name = "code" value = "1234"/>
   *   <property name = "target" value = "1"/>
   * </command>
   * }
   * </pre>
   */
  public final static String DSCIT100_XMLPROPERTY_ADDRESS = "address";

  /**
   * String constant for parsing DSCIT100 protocol XML entries from
   * controller.xml file.
   * 
   * This constant is the expected property name value for DSCIT100 commands
   * ({@value} ):
   * 
   * <pre>
   * {@code
   * <command protocol = "dscit100" >
   *   <property name = "address" value = "x.x.x.x:xxxx"/>
   *   <property name = "command" value = "ARM"/>
   *   <property name = "code" value = "1234"/>
   *   <property name = "target" value = "1"/>
   * </command>
   * }
   * </pre>
   */
  public final static String DSCIT100_XMLPROPERTY_COMMAND = "command";

  /**
   * String constant for parsing DSCIT100 protocol XML entries from
   * controller.xml file.
   * 
   * This constant is the expected property name value for DSCIT100 commands
   * ({@value} ):
   * 
   * <pre>
   * {@code
   * <command protocol = "dscit100" >
   *   <property name = "address" value = "x.x.x.x:xxxx"/>
   *   <property name = "command" value = "ARM"/>
   *   <property name = "code" value = "1234"/>
   *   <property name = "target" value = "1"/>
   * </command>
   * }
   * </pre>
   */
  public final static String DSCIT100_XMLPROPERTY_CODE = "code";

  /**
   * String constant for parsing DSCIT100 protocol XML entries from
   * controller.xml file.
   * 
   * This constant is the expected property name value for DSCIT100 commands
   * ({@value} ):
   * 
   * <pre>
   * {@code
   * <command protocol = "dscit100" >
   *   <property name = "address" value = "x.x.x.x:xxxx"/>
   *   <property name = "command" value = "ARM"/>
   *   <property name = "code" value = "1234"/>
   *   <property name = "target" value = "1"/>
   * </command>
   * }
   * </pre>
   */
  public final static String DSCIT100_XMLPROPERTY_TARGET = "target";

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * Logging. Use common DSCIT100 log category for all KNX related classes.
   */
  private static Logger log = Logger.getLogger(DSCIT100_LOG_CATEGORY);

  // Instance Fields
  // ------------------------------------------------------------------------------

  private final DSCIT100ConnectionManager connectionManager = new DSCIT100ConnectionManager();

  // Constructors
  // ---------------------------------------------------------------------------------

  public DSCIT100CommandBuilder()
  {
    // TODO Auto-generated constructor stub
  }

  /**
   * Parses the DSCIT100 command XML snippets and builds a corresponding
   * DSCIT100 command instance.
   * <p>
   * 
   * The expected XML structure is:
   * 
   * <pre>
   * {@code
   * <command protocol = "dscit100" >
   *   <property name = "address" value = "x.x.x.x:xxxx"/>
   *   <property name = "command" value = "ARM"/>
   *   <property name = "code" value = "1234"/>
   *   <property name = "target" value = "1"/>
   * </command>
   * }
   * </pre>
   * 
   * Additional properties not listed here are ignored.
   * 
   * @see ExecuteCommand
   * 
   * @throws NoSuchCommandException
   *           if the DSCIT100 command instance cannot be constructed from the
   *           XML snippet for any reason
   * 
   * @return an immutable DSCIT100 command instance with known configured
   *         properties set
   */
  @Override
  public Command build(Element element)
  {

    String address = null;
    String command = null;
    String code = null;
    String target = null;

    // Get the list of properties from XML...

    @SuppressWarnings("unchecked")
    List<Element> propertyElements = element.getChildren(XML_ELEMENT_PROPERTY,
        element.getNamespace());

    for (Element el : propertyElements)
    {
      String propertyName = el.getAttributeValue(XML_ATTRIBUTENAME_NAME);
      String propertyValue = el.getAttributeValue(XML_ATTRIBUTENAME_VALUE);

      if (DSCIT100_XMLPROPERTY_ADDRESS.equalsIgnoreCase(propertyName))
      {
        address = propertyValue;
      }
      else if (DSCIT100_XMLPROPERTY_COMMAND.equalsIgnoreCase(propertyName))
      {
        command = propertyValue;
      }
      else if (DSCIT100_XMLPROPERTY_CODE.equalsIgnoreCase(propertyName))
      {
        code = propertyValue;
      }
      else if (DSCIT100_XMLPROPERTY_TARGET.equalsIgnoreCase(propertyName))
      {
        target = propertyValue;
      }
      else
      {
        log.warn("Unknown DSCIT100 property '<" + XML_ELEMENT_PROPERTY + " "
            + XML_ATTRIBUTENAME_NAME + " = \"" + propertyName + "\" "
            + XML_ATTRIBUTENAME_VALUE + " = \"" + propertyValue + "\"/>'.");
      }
    }

    // Sanity check on mandatory properties 'command' and 'address'...

    if (address == null || "".equals(address))
    {
      throw new NoSuchCommandException("DSCIT100 command must have a '"
          + DSCIT100_XMLPROPERTY_ADDRESS + "' property.");
    }

    if (command == null || "".equals(command))
    {
      throw new NoSuchCommandException("DSCIT100 command must have a '"
          + DSCIT100_XMLPROPERTY_COMMAND + "' property.");
    }

    Command cmd = DSCIT100Command.createCommand(command, address, code, target,
        connectionManager);

    log.info("Created DSCIT100 Command " + cmd);

    return cmd;
  } // end build(Element element)

}
