/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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
package org.openremote.controller.protocol.knx;

import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Element;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;


/**
 * TODO :
 *   KNXEventBuilder is responsible for mapping the XML configuration model to Java object
 *   model
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class KNXCommandBuilder implements CommandBuilder
{
  /*
   * Implementation Notes:
   *
   *  Open questions:
   *
   *  1) When should KNX discovery be executed in Controller's lifecycle?
   *     - KNX gateway is a fairly static thing, so should be at bootup
   *     - However, should probably be executed asynchronously to avoid extending bootup times
   *        * properly done, discovery is going to rely on certain timeouts
   *        * timeouts should not affect bootup time especially on deployments where knx is not
   *          present
   *        * ultimately you could remove all KNX related bean config if not needed but acting
   *          asynchronously in the background is more user friendly (not strictly necessary for
   *          user to modify component config)
   *     - For development scenarios and to increase controller uptime there should be a mechanism
   *       to re-trigger the discovery
   *
   *  2) Alternative to multicast discovery is direct IP Address:Port configuration
   *     - discovery should fall back to this if available
   *     - changes should be possible at runtime
   *
   *  3) KNX Connection lifecycle
   *     - It seems many KNX IP gateways are limited to low number of concurrent connections
   *     - If I understood the spec correctly, the KNX requires a KNX individual address per
   *      open connection
   *     - This means many IP gateways default to one concurrent connection (single address)
   *     - Multiple KNX individual addresses would need to be commissioned via ETS (possible?)
   *     - Unclear if/how many KNX IP gateways support multiple individual addresses for
   *       multiple connections (?)
   *
   *     Therefore:
   *     - Should assume low connection concurrency
   *     - Simplistic mode: ORB creates and maintains a connection through-out its lifetime
   *       taking up one connection slot in the gateway
   *     - Yielding mode: open/close connection only when KNX event is triggered
   *        * This will potentially have a bad performance degradation if high number of events
   *          are triggered -- especially if within a macro
   *     - Managed mode: keep connection open while there's traffic, close after idle timeout
   *        * Less open/close overhead, still yields after a while if no more traffic
   *        * With sufficiently low timeout almost identical to yield mode (but could be high
   *          enough threshold to maintain a connection over most macro executions)
   *
   *  - Will do Simplistic at first to prototype.
   *
   */

  // Constants ------------------------------------------------------------------------------------

  public final static String KNX_LOG_CATEGORY  = "KNX";   // TODO : externalize user-friendly log category constants
  public final static String GROUP_ADDRESS_XML_ATTRIBUTE = "groupAddress";


  // Class Members --------------------------------------------------------------------------------

  private static Logger log = Logger.getLogger(KNX_LOG_CATEGORY);


  // Instance Fields ------------------------------------------------------------------------------


  // TODO : inject service dependency
  private final KNXConnectionManager connectionManager = new KNXConnectionManager();


  // Constructors ---------------------------------------------------------------------------------

  /**
   * TODO
   */
  public KNXCommandBuilder()
  {
    try
    {
      // TODO : this should be a container lifecycle method
      connectionManager.start();
    }
    catch (ConnectionException exception)
    {
      log.error("KNX connection manager failed to start: " + exception.getMessage(), exception);
    }
  }


  // Implements EventBuilder ----------------------------------------------------------------------

  // TODO: could use JAXB instead since its included in JDK

  /**
   * TODO
   *
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
public Command build(Element element)
  {
     String groupAddress = null;
     String knxCommandStr = element.getAttributeValue("value");
     List<Element> propertyEles = element.getChildren("property", element.getNamespace());
     for(Element ele : propertyEles){
        if(GROUP_ADDRESS_XML_ATTRIBUTE.equals(ele.getAttributeValue("name"))){
           groupAddress = ele.getAttributeValue("value");
           break;
        } 
     }

    KNXCommandType knxCommand = null;

    if (KNXCommandType.SWITCH_ON.isEqual(knxCommandStr))
      knxCommand = KNXCommandType.SWITCH_ON;
    else if (KNXCommandType.SWITCH_OFF.isEqual(knxCommandStr))
      knxCommand = KNXCommandType.SWITCH_OFF;
    else if (KNXCommandType.STATUS.isEqual(knxCommandStr)) {
       knxCommand = KNXCommandType.STATUS;
    } else {
       throw new NoSuchCommandException("Couldn't find command " + knxCommandStr + " in KNXCommandType.");
    }

    Command command = new KNXCommand(connectionManager, groupAddress, knxCommand);
    
    log.info("Created KNX Command " + knxCommand + " for group address '" + groupAddress + "'");

    return command;
  }


}
