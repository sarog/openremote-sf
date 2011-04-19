package org.openremote.controller.protocol.dscit100;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.protocol.dscit100.PanelState.StateType;

public class StateDefinitionTest
{

  @Before
  public void setUp() throws Exception
  {
  }

  @Test
  public void testDSCIT100StateDefinition()
  {
    StateDefinition sd = new StateDefinition(StateType.PARTITION,"1");
    
    assertEquals(StateType.PARTITION, sd.getType());
    assertEquals("1", sd.getItem());
  }
}
