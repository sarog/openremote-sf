package org.openremote.controller.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.openremote.controller.protocol.dscit100.DSCIT100CommandBuilderTest;
import org.openremote.controller.protocol.dscit100.DSCIT100CommandTest;
import org.openremote.controller.protocol.dscit100.PacketTest;
import org.openremote.controller.protocol.dscit100.PanelStateTest;
import org.openremote.controller.protocol.dscit100.StateDefinitionTest;

/**
 * All OpenRemote DSCIT100 tests aggregated here.
 *
 * @author <a href="mailto:gdrapp@gmail.com">Greg Rapp</a>
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
   {
     PacketTest.class,
     DSCIT100CommandTest.class,
     DSCIT100CommandBuilderTest.class,
     StateDefinitionTest.class,
     PanelStateTest.class
   }
)

public class DSCIT100Tests
{

}
