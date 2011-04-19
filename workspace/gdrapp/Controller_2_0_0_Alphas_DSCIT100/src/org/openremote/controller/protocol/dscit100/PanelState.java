/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * @author Greg Rapp
 * 
 */
public class PanelState
{
  // Constants
  // --------------------------------------------------------------------------------

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * DSCIT100 logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  // Instance Fields
  // ------------------------------------------------------------------------------

  public interface State
  {
  }

  public enum StateType
  {
    ZONE, ZONE_ALARM, PARTITION
  }

  public enum ZoneState implements State
  {
    OPEN, RESTORED, FAULT
  }

  public enum AlarmState implements State
  {
    NORMAL, ALARM
  }

  public enum PartitionState implements State
  {
    READY, NOTREADY, ARMED_AWAY, ARMED_STAY, ARMED_AWAY_NODELAY, ARMED_STAY_NODELAY, ALARM, DISARMED, EXITDELAY, ENTRYDELAY, FAILTOARM, BUSY
  }

  private Map<StateType, Map<String, State>> internalState;

  // Constructors
  // ------------------------------------------------------------------------------

  public PanelState()
  {
    internalState = new HashMap<StateType, Map<String, State>>();
  }

  // Private Instance Methods
  // -------------------------------------------------------------

  private void updateInternalState(StateType type, String item, State state)
  {
    Map<String, State> tmp;

    if (internalState.containsKey(type) && internalState.get(type) != null)
    {
      tmp = internalState.get(type);
      tmp.put(item, state);
    }
    else
    {
      tmp = new HashMap<String, State>();
      tmp.put(item, state);
    }
    internalState.put(type, tmp);
  }

  private String trimLeadingZeros(String str)
  {
    return str.replaceFirst("^0+(?!$)", "");
  }

  // Public Instance Methods
  // -------------------------------------------------------------

  public synchronized void processPacket(Packet packet)
  {
    if (packet.getCommand().equals("601"))
    { // Zone Alarm
      String partition = packet.getData().substring(0, 1);
      String zone = packet.getData().substring(1, 4);
      zone = trimLeadingZeros(zone);
      log.debug("Zone alarm [partition=" + partition + ",zone=" + zone + "]");
      updateInternalState(StateType.ZONE_ALARM, zone, AlarmState.ALARM);
    }
    else if (packet.getCommand().equals("602"))
    { // Zone Alarm Restore
      String partition = packet.getData().substring(0, 1);
      String zone = packet.getData().substring(1, 4);
      zone = trimLeadingZeros(zone);
      log.debug("Zone alarm restore [partition=" + partition + ",zone=" + zone
          + "]");
      updateInternalState(StateType.ZONE_ALARM, zone, AlarmState.NORMAL);
    }
    else if (packet.getCommand().equals("609"))
    { // Zone open
      String zone = packet.getData().substring(0, 3);
      zone = trimLeadingZeros(zone);
      log.debug("Zone open [zone=" + zone + "]");

      updateInternalState(StateType.ZONE, zone, ZoneState.OPEN);
    }
    else if (packet.getCommand().equals("610"))
    { // Zone restored
      String zone = packet.getData().substring(0, 3);
      zone = trimLeadingZeros(zone);
      log.debug("Zone restored [zone=" + zone + "]");

      updateInternalState(StateType.ZONE, zone, ZoneState.RESTORED);
    }
    else if (packet.getCommand().equals("650"))
    { // Partition ready
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition ready [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition, PartitionState.READY);
    }
    else if (packet.getCommand().equals("651"))
    { // Partition not ready
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition not ready [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition,
          PartitionState.NOTREADY);
    }
    else if (packet.getCommand().equals("652"))
    { // Partition armed
      String partition = packet.getData().substring(0, 1);
      String mode = packet.getData().substring(1, 2);
      log.debug("Partition armed [partition=" + partition + ",mode=" + mode
          + "]");
      if (mode.equals("0"))
        updateInternalState(StateType.PARTITION, partition,
            PartitionState.ARMED_AWAY);
      else if (mode.equals("1"))
        updateInternalState(StateType.PARTITION, partition,
            PartitionState.ARMED_STAY);
      else if (mode.equals("2"))
        updateInternalState(StateType.PARTITION, partition,
            PartitionState.ARMED_AWAY_NODELAY);
      else if (mode.equals("3"))
        updateInternalState(StateType.PARTITION, partition,
            PartitionState.ARMED_STAY_NODELAY);
    }
    else if (packet.getCommand().equals("654"))
    { // Partition alarm
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition in alarm [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition, PartitionState.ALARM);
    }
    else if (packet.getCommand().equals("655"))
    { // Partition alarm
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition disarmed [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition,
          PartitionState.DISARMED);
    }
    else if (packet.getCommand().equals("656"))
    { // Partition exit delay
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition in exit delay [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition,
          PartitionState.EXITDELAY);
    }
    else if (packet.getCommand().equals("657"))
    { // Partition entry delay
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition in entry delay [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition,
          PartitionState.ENTRYDELAY);
    }
    else if (packet.getCommand().equals("672"))
    { // Partition failed to arm
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition failed to arm [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition,
          PartitionState.FAILTOARM);
    }
    else if (packet.getCommand().equals("673"))
    { // Partition busy
      String partition = packet.getData().substring(0, 1);
      log.debug("Partition busy [partition=" + partition + "]");

      updateInternalState(StateType.PARTITION, partition, PartitionState.BUSY);
    }
  }

  // Public Instance Methods
  // -------------------------------------------------------------

  public synchronized State getState(StateDefinition stateDefinition)
  {
    if (!this.internalState.containsKey(stateDefinition.getType()))
    {
      log.warn("Cannot find state type information for " + stateDefinition);
      return null;
    }

    Map<String, State> members = this.internalState.get(stateDefinition
        .getType());

    if (members == null || (!members.containsKey(stateDefinition.getItem())))
    {
      log.warn("Cannot find state item information for " + stateDefinition);
      return null;
    }

    return members.get(stateDefinition.getItem());
  }

  public synchronized ZoneState getZoneState(Integer zone)
  {
    if (!this.internalState.containsKey(StateType.ZONE))
      return null;

    Map<String, State> zones = this.internalState.get(StateType.ZONE);

    if (zones == null || (!zones.containsKey(String.valueOf(zone))))
      return null;

    return (ZoneState) zones.get(String.valueOf(zone));
  }

  public synchronized AlarmState getZoneAlarmState(Integer zone)
  {
    if (!this.internalState.containsKey(StateType.ZONE_ALARM))
      return null;

    Map<String, State> zones = this.internalState.get(StateType.ZONE_ALARM);

    if (zones == null || (!zones.containsKey(String.valueOf(zone))))
      return null;

    return (AlarmState) zones.get(String.valueOf(zone));
  }

  public synchronized PartitionState getPartitionState(Integer partition)
  {
    if (!this.internalState.containsKey(StateType.PARTITION))
      return null;

    Map<String, State> partitions = this.internalState.get(StateType.PARTITION);

    if (partitions == null
        || (!partitions.containsKey(String.valueOf(partition))))
      return null;

    return (PartitionState) partitions.get(String.valueOf(partition));
  }
}
