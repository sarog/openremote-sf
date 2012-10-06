/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
package org.openremote.controller.component.control;

import java.util.Properties;
import java.net.URI;

import junit.framework.Assert;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.protocol.virtual.VirtualCommandBuilder;
import org.openremote.controller.command.CommandFactory;
import org.openremote.controller.component.control.gesture.Gesture;
import org.openremote.controller.component.control.gesture.GestureBuilder;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.statuscache.ChangedStatusTable;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.statuscache.EventProcessorChain;
import org.openremote.controller.suite.AllTests;

/**
 * TODO :
 *
 *  - see related tasks
 *     ORCJAVA-158  (http://jira.openremote.org/browse/ORCJAVA-158)
 *     ORCJAVA-159  (http://jira.openremote.org/browse/ORCJAVA-159)
 *
 */
public class GestureBuilderTest
{


  // Instance Fields ------------------------------------------------------------------------------

  private GestureBuilder builder;
  private Deployer deployer;


  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    ChangedStatusTable cst = new ChangedStatusTable();
    EventProcessorChain echain = new EventProcessorChain();

    StatusCache sc = new StatusCache(cst, echain);
    
    ControllerConfiguration cc = new ControllerConfiguration();
    URI deploymentURI = AllTests.getAbsoluteFixturePath().resolve("builder/gesture");
    cc.setResourcePath(deploymentURI.getPath());

    deployer = new Deployer("Deployer for " + deploymentURI, sc, cc);


    CommandFactory cf = new CommandFactory();
    Properties p = new Properties();
    p.put("virtual", VirtualCommandBuilder.class.getName());
    cf.setCommandBuilders(p);

    builder = new GestureBuilder();
    builder.setCommandFactory(cf);
    builder.setDeployer(deployer);


    deployer.softRestart();
  }


  // Tests ----------------------------------------------------------------------------------------

  @Test public void testGetGestureforRealID() throws Exception
  {
    Element controlElement = deployer.queryElementById(7);

    Gesture gesture = (Gesture)builder.build(controlElement, "test");

    Assert.assertNotNull(gesture);

    // TODO : make test more complete -- assert associated commands, etc.
  }

  @Test public void testGetGestureforInvalidGesture() throws Exception
  {
    Element controlElement = deployer.queryElementById(9);

    Gesture g = (Gesture)builder.build(controlElement, "test");

    Assert.assertNull(
        "Expected null gesture (or exception) when building with wrong XML content", g
    );
  }

  @Test public void testBuildNullArg() throws Exception
  {
    Gesture g = (Gesture)builder.build(null, "test");

    Assert.assertNull(
        "Expected null gesture (or exception) when building " +
        "with non-existent element", g
    );
  }



   
}
