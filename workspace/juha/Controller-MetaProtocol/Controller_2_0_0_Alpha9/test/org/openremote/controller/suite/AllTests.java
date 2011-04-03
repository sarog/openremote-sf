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
import org.openremote.controller.net.MulticastAutoDiscoveryTest;
import org.openremote.controller.utils.MacrosIrDelayUtilTest;

/**
 * Collects *all* unit tests.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@RunWith(Suite.class)
@SuiteClasses(
{
   AllControlBuilderTests.class,
   AllServiceTests.class,
   AllRESTfulAPIMockTests.class,
   MacrosIrDelayUtilTest.class,
   AllCommandBuildersTests.class,
   MulticastAutoDiscoveryTest.class,
   RoundRobinTests.class,
   AllUtilTests.class,
   KNXTests.class
}
)
public class AllTests
{

  public final static int WEBAPP_PORT = 8090;
  public final static String WEBAPP_IP = "127.0.0.1";
  public final static String FIXTURE_DIR = "./org/openremote/controller/fixture/";
  public final static String FIXTURE_DIR_OF_POLLING_MACHINES = "./org/openremote/controller/fixture/polling/";
  public static final String FIXTURE_DIR_OF_RESTFUL_SERVICE_JSON_SUPPORT = "./org/openremote/controller/fixture/rest/support/json/";
}
