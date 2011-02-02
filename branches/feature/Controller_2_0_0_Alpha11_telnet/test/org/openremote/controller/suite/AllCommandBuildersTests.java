/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
package org.openremote.controller.suite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import org.openremote.controller.protocol.http.HttpGetCommandBuilderTest;
import org.openremote.controller.protocol.infrared.IRCommandBuilderTest;
import org.openremote.controller.protocol.knx.KNXCommandBuilderTest;
import org.openremote.controller.protocol.socket.TCPSocketCommandBuilderTest;
import org.openremote.controller.protocol.telnet.TelnetCommandBuilderTest;
import org.openremote.controller.protocol.x10.X10CommandBuilderTest;
import org.openremote.controller.protocol.upnp.UPnPCommandBuilderTest;
import org.openremote.controller.component.SensorBuilderTest;

/**
 * All tests for protocol command builders.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 * @author Javen
 *
 */
@RunWith(Suite.class)
@SuiteClasses(
   {
      HttpGetCommandBuilderTest.class,
      TelnetCommandBuilderTest.class,
      IRCommandBuilderTest.class,
      TCPSocketCommandBuilderTest.class,
      X10CommandBuilderTest.class,
      //KNXCommandBuilderTest.class,      // moved to KNXTests Suite
      UPnPCommandBuilderTest.class,
      SensorBuilderTest.class
   }
)
public class AllCommandBuildersTests
{

}
