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
package org.openremote.controller.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.junit.Before;
import org.junit.Test;
import org.openremote.controller.Configuration;
import org.openremote.controller.RoundRobinConfig;
import org.openremote.controller.command.RemoteActionXMLParser;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.suite.AllTests;

/**
 * Test for config factory.
 * 
 * @author Dan Cong
 *
 */
public class ConfigFactoryTest
{
   
  private Document doc;

  // modified some attributes to simulate uploading new controller.xml
  private Document doc2;

  @Before public void setup()
  {
    String controllerXMLPath = AllTests.getFixtureFile("controller.xml");
    String controllerXML2Path = AllTests.getFixtureFile("controller2.xml");

    doc = XMLUtil.getControllerDocument(controllerXMLPath);
    doc2 = XMLUtil.getControllerDocument(controllerXML2Path);
  }

  @Test public void getBasicConfig()
  {

    Configuration config = getCustomBasicConfigFromControllerXML(doc);

    assertEquals("controller1", config.getWebappName());
    assertEquals(false, config.isCopyLircdconf());
  //      assertEquals("/home/openremote/controller", config.getResourcePath());
    assertEquals("/etc/lircd.conf", config.getLircdconfPath());
    assertEquals("http://openremote.org/beehvie/rest/", config.getBeehiveRESTRootUrl());
    assertEquals("192.168.4.63", config.getWebappIp());
    assertEquals(8888, config.getWebappPort());
    assertEquals("/usr/local/bin/irsend", config.getIrsendPath());
    assertEquals(500, config.getMacroIRExecutionDelay());
    assertEquals(3333, config.getMulticastPort());
    assertEquals("224.0.1.100", config.getMulticastAddress());

  }

  @Test public void getBasicConfig2()
  {

    Configuration config = getCustomBasicConfigFromControllerXML(doc2);

    assertEquals("controller2", config.getWebappName());
    assertEquals(true, config.isCopyLircdconf());
  //      assertEquals("/home/openremote/controller", config.getResourcePath());
    assertEquals("/etc/lircd.conf", config.getLircdconfPath());
    assertEquals("http://openremote.org/beehvie/rest/", config.getBeehiveRESTRootUrl());
    assertEquals("192.168.4.63", config.getWebappIp());
    assertEquals(8888, config.getWebappPort());
    assertEquals("/usr/local/bin/irsend", config.getIrsendPath());
    assertEquals(500, config.getMacroIRExecutionDelay());
    assertEquals(3333, config.getMulticastPort());
    assertEquals("224.0.1.100", config.getMulticastAddress());

  }

  @Test public void getRoundRobinConfig()
  {

    RoundRobinConfig config = getCustomRoundRobinConfigFromControllerXML(doc);
    assertEquals("controller1", config.getControllerApplicationName());
    assertEquals(true, config.getIsGroupMemberAutoDetectOn());
    assertEquals("openremote-office", config.getControllerGroupName());
    assertEquals("224.0.1.200", config.getRoundRobinMulticastAddress());
    assertEquals(20000, config.getRoundRobinMulticastPort());
    assertEquals(10000, config.getRoundRobinTCPServerSocketPort());

    String[] urls = "http://192.168.1.5:8080/controller/,http://192.168.1.100:8080/controller/,http://192.168.1.105:8080/controller/".split(",");
    assertTrue(Arrays.equals(urls,config.getGroupMemberCandidateURLs()));
  }

  @Test public void getRoundRobinConfig2()
  {

    RoundRobinConfig config = getCustomRoundRobinConfigFromControllerXML(doc2);
    assertEquals("controller2", config.getControllerApplicationName());
    assertEquals(false, config.getIsGroupMemberAutoDetectOn());
    assertEquals("openremote-home", config.getControllerGroupName());
    assertEquals("224.0.1.200", config.getRoundRobinMulticastAddress());
    assertEquals(20000, config.getRoundRobinMulticastPort());
    assertEquals(10000, config.getRoundRobinTCPServerSocketPort());

    String[] urls = "http://192.168.1.5:8080/controller/,http://192.168.1.100:8080/controller/,http://192.168.1.105:8080/controller/".split(",");
    assertTrue(Arrays.equals(urls,config.getGroupMemberCandidateURLs()));
  }



  // Helpers --------------------------------------------------------------------------------------

  private static Configuration getCustomBasicConfigFromControllerXML(Document doc)
  {
    Map<String, String> attrMap = parseCustomConfigAttrMap(doc);
    Configuration config = (Configuration) SpringContext.getInstance().getBean("configuration");
    config.setCustomAttrMap(attrMap);

    return config;
  }
  
  public static RoundRobinConfig getCustomRoundRobinConfigFromControllerXML(Document doc)
  {
    Map<String, String> attrMap = parseCustomConfigAttrMap(doc);
    RoundRobinConfig config = (RoundRobinConfig) SpringContext.getInstance().getBean("roundRobinConfig");
    config.setCustomAttrMap(attrMap);
    return config;
  }

  public static Map<String, String> parseCustomConfigAttrMap(Document doc)
  {
    Element element = ((RemoteActionXMLParser) SpringContext.getInstance().getBean("remoteActionXMLParser")).queryElementFromXMLByName(doc, "config");
    return ConfigFactory.pullAllCustomConfigs(element);
  }
  
}
