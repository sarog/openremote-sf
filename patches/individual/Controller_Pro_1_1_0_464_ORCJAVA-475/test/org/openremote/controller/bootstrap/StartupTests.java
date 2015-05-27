/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.controller.bootstrap;

import junit.framework.Assert;
import org.junit.Test;

import java.security.Security;

/**
 * Unit tests for {@link org.openremote.controller.bootstrap.Startup} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class StartupTests
{

  @Test public void testBCInit() throws Exception
  {
    try
    {
      Startup.installBouncyCastleSecurityProvider();

      Assert.assertTrue(Security.getProvider("BC") != null);
    }

    finally
    {
      Security.removeProvider("BC");
    }
  }

}

