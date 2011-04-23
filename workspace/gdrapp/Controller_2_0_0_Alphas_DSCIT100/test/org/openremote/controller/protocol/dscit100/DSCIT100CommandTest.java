/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.command.Command;

/**
 * @author Greg Rapp
 * 
 */
public class DSCIT100CommandTest
{

  private DSCIT100ConnectionManager connManager = null;

  /**
   * @throws java.lang.Exception
   */
  @Before
  public void setUp() throws Exception
  {
    connManager = new DSCIT100ConnectionManager();
  }

  /**
   * Test method for
   * {@link org.openremote.controller.protocol.dscit100.DSCIT100Command#createCommand(java.lang.String, java.lang.String, org.openremote.controller.protocol.dscit100.DSCIT100ConnectionManager)}
   * .
   */
  @Test
  public void testCreateCommand()
  {
    Command command = DSCIT100Command.createCommand("ARM", "1.1.1.1:50",
        "1234", "1", connManager);

    assertTrue(command instanceof DSCIT100Command);
  }

}
