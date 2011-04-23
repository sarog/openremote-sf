/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;

/**
 * @author Greg Rapp
 * 
 */
public class ReadCommand extends DSCIT100Command implements StatusCommand
{

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * Factory method for creating DSCIT100 command instances
   * {@link ExecuteCommand} and {@link ReadCommand} based on a human-readable
   * configuration strings.
   * <p>
   * 
   * Each DSCIT100 command instance is associated with a link to a connection
   * manager and a destination address.
   * 
   * @param name
   *          Command lookup name. This is usually a human-readable string used
   *          in configuration and tools. Note that multiple lookup names can be
   *          used to return Java equal() (but not same instance) commands.
   * @param mgr
   *          DSCIT100 connection manager used to transmit this command
   * @param address
   *          DSCIT100 destination address.
   * 
   * @return new DSCIT100 command instance
   */
  public static ReadCommand createCommand(String name, String address,
      String target, DSCIT100ConnectionManager mgr)
  {
    if (name == null || address == null || mgr == null)
      return null;

    name = name.trim().toUpperCase();

    StateDefinition stateDefinition = Lookup.get(name, target);

    if (stateDefinition == null)
      return null;

    return new ReadCommand(stateDefinition, address, target, mgr);
  }

  // Constructors
  // ---------------------------------------------------------------------------------

  /**
   * @param stateDefinition
   * @param address
   *          IT100 address
   * @param target
   *          Target partition or zone
   * @param connectionManager
   */
  public ReadCommand(StateDefinition stateDefinition, String address,
      String target, DSCIT100ConnectionManager connectionManager)
  {
    super(address, target, connectionManager);
    this.stateDefinition = stateDefinition;
    log.info("Instantiating new ReadCommand instance - " + this.toString());
  }

  // Object Overrides
  // -----------------------------------------------------------------------------

  /**
   * Returns a string representation of this command. Expected output is:
   * 
   * <pre>
   * {@code
   * 
   * [STATE:type=xx, target=xx, address=xx]
   * 
   * }
   * </pre>
   * 
   * @return this command as string
   */
  @Override
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();

    String stateType = stateDefinition.getType().toString();
    String stateTarget = stateDefinition.getTarget().toString();

    buffer.append("[STATE:type=").append(stateType).append(", target=")
        .append(stateTarget).append(", address=").append(address).append("]");

    return buffer.toString();
  }

  // Private Instance Fields
  // ----------------------------------------------------------------------
  /**
   * DSCIT100 logger. Uses a common category for all DSCIT100 related logging.
   */
  private static final Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  /**
   * StateDefinition type to read.
   */
  private StateDefinition stateDefinition;

  // Public Instance Methods
  // -------------------------------------------------------------

  /**
   * @return the StateDefinition
   */
  public StateDefinition getStateDefinition()
  {
    return stateDefinition;
  }

  @Override
  public String read(EnumSensorType sensorType, Map<String, String> stateMap)
  {
    log.debug("Sensor read request [type=" + sensorType.toString()
        + ", stateMap=" + stateMap.toString() + "]");
    log.debug("Reading state for - " + this);
    PanelState.State state = super.read(this);
    if (state != null)
      log.debug("[state=" + state.toString().toUpperCase() + "]");
    else
      log.debug("[state=NULL]");
    if (sensorType == EnumSensorType.CUSTOM)
    {
      return state != null ? state.toString().toUpperCase() : "";
    }
    else
    {
      return "";
    }
  }

  // Nested Classes
  // -------------------------------------------------------------------------------

  private static class Lookup
  {
    private static StateDefinition get(String name, String target)
    {
      if (name.equals("PARTITION_STATE"))
      {
        return new StateDefinition(PanelState.StateType.PARTITION, target);
      }
      else if (name.equals("ZONE_STATE"))
      {
        return new StateDefinition(PanelState.StateType.ZONE, target);
      }
      else if (name.equals("LABEL"))
      {
        return new StateDefinition(PanelState.StateType.LABEL, target);
      }
      else
      {
        return null;
      }
    }
  }
}
