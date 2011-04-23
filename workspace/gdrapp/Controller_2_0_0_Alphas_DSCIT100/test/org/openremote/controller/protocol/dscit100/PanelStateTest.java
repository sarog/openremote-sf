/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.dscit100.PanelState.AlarmState;
import org.openremote.controller.protocol.dscit100.PanelState.PartitionState;
import org.openremote.controller.protocol.dscit100.PanelState.StateType;
import org.openremote.controller.protocol.dscit100.PanelState.ZoneState;

/**
 * @author Greg Rapp
 * 
 */
public class PanelStateTest
{
  private PanelState panelState;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
    panelState = new PanelState();
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.PanelState#processPacket(org.openremote.controller.protocol.dscit100.Packet)}
   * .
   */
  @Test
  public void testProcessPacket()
  {
    Packet packet;

    packet = new Packet("570", "001Label");
    panelState.processPacket(packet);
    assertEquals("Label",
        panelState.getState(new StateDefinition(StateType.LABEL, "1"))
            .toString());

    packet = new Packet("601", "1001");
    panelState.processPacket(packet);
    assertEquals(AlarmState.ALARM,
        panelState.getState(new StateDefinition(StateType.ZONE_ALARM, "1")));

    packet = new Packet("602", "1001");
    panelState.processPacket(packet);
    assertEquals(AlarmState.NORMAL,
        panelState.getState(new StateDefinition(StateType.ZONE_ALARM, "1")));

    packet = new Packet("609", "001");
    panelState.processPacket(packet);
    assertEquals(ZoneState.OPEN,
        panelState.getState(new StateDefinition(StateType.ZONE, "1")));

    packet = new Packet("610", "001");
    panelState.processPacket(packet);
    assertEquals(ZoneState.RESTORED,
        panelState.getState(new StateDefinition(StateType.ZONE, "1")));

    packet = new Packet("650", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.READY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("651", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.NOTREADY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("652", "10");
    panelState.processPacket(packet);
    assertEquals(PartitionState.ARMED_AWAY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("652", "11");
    panelState.processPacket(packet);
    assertEquals(PartitionState.ARMED_STAY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("652", "12");
    panelState.processPacket(packet);
    assertEquals(PartitionState.ARMED_AWAY_NODELAY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("652", "13");
    panelState.processPacket(packet);
    assertEquals(PartitionState.ARMED_STAY_NODELAY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("654", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.ALARM,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("655", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.DISARMED,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("656", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.EXITDELAY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("657", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.ENTRYDELAY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("672", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.FAILTOARM,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));

    packet = new Packet("673", "1");
    panelState.processPacket(packet);
    assertEquals(PartitionState.BUSY,
        panelState.getState(new StateDefinition(StateType.PARTITION, "1")));
  }
}
