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

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.suite.AllTests;
import org.openremote.controller.component.Sensory;
import org.openremote.controller.component.control.switchtoggle.Switch;
import org.openremote.controller.component.control.switchtoggle.SwitchBuilder;
import org.openremote.controller.exception.NoSuchComponentException;
import org.openremote.controller.exception.XMLParsingException;
import org.openremote.controller.exception.ConfigurationException;
import org.openremote.controller.utils.SpringTestContext;
import org.openremote.controller.utils.XMLUtil;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.statuscache.StatusCache;
import org.openremote.controller.service.Deployer;
import org.openremote.controller.service.ServiceContext;

/**
 * TODO
 *
 *   - see related tasks
 *     ORCJAVA-145  (http://jira.openremote.org/browse/ORCJAVA-145)
 *     ORCJAVA-146  (http://jira.openremote.org/browse/ORCJAVA-146)
 *     ORCJAVA-147  (http://jira.openremote.org/browse/ORCJAVA-147)

 *
 * @author Javen
 */
public class SwitchBuilderTest
{

  private Document doc = null;
  private SwitchBuilder builder = (SwitchBuilder)SpringContext.getInstance().getBean("switchBuilder");



  // Test Lifecycle -------------------------------------------------------------------------------

  @Before public void setUp() throws Exception
  {
    AllTests.replaceControllerXML(Constants.CONTROLLER_XML);

    Deployer deployer = (Deployer)SpringContext.getInstance().getBean("deployer");

    deployer.softRestart();

    doc = deployer.getControllerDocument();
  }



  // Tests ----------------------------------------------------------------------------------------

//  @Test public void testNoSuchSwitch() throws Exception
//  {
//    try
//    {
//      getSwitchByID("9", "on");
//      fail();
//    }
//    catch (XMLParsingException e)
//    {
//
//    }
//
//  }

  
  @Test public void testNoNull() throws Exception
  {
    Assert.assertNotNull(getSwitchByID("3", "on"));
  }

  @Test public void testGetCommand() throws Exception
  {
    Switch swh = getSwitchByID("4", "on");
    Assert.assertEquals(swh.getExecutableCommands().size(), 1);
    swh = getSwitchByID("4", "off");
    Assert.assertEquals(swh.getExecutableCommands().size(), 1);

    swh = getSwitchByID("4", "status");
    Assert.assertTrue(swh instanceof Sensory);
    Assert.assertTrue(((Sensory)swh).fetchSensorID() == 1004);
  }


  // Helpers --------------------------------------------------------------------------------------

  private Switch getSwitchByID(String switchID, String cmdParam)
      throws XMLParsingException, ConfigurationException
  {
    Element controlElement = getElementByID(switchID);

    Assert.assertTrue(
        "Was expecting 'switch', got '" + controlElement.getName() + "'.",
        controlElement.getName().equals("switch")
    );
    
//    if (!controlElement.getName().equals("switch"))
//    {
//      throw new NoSuchComponentException("switch .");
//    }

    return (Switch) builder.build(controlElement, cmdParam);
  }

  private Element getElementByID(String id)
  {
    return XMLUtil.getElementByID(doc, id);
  }

}
