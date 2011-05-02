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

package org.openremote.android.test.console.bindings;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.openremote.android.console.bindings.Component;
import org.openremote.android.console.bindings.Web;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests the {@link org.openremote.android.console.bindings.Component} class.
 *
 * @author <a href="mailto:aball@osintegrators.com">Andrew Ball</a>
 */
public class ComponentTest extends TestCase
{

  /** TODO put this somewhere else, it's copied from the WebTest class */
  private Element parseXml(String xmlText)
  {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = null;
    try
    {
      builder = factory.newDocumentBuilder();
    }
    catch (ParserConfigurationException e)
    {
      fail("failed to initialize XML parser");
    }

    InputSource in = new InputSource();
    in.setCharacterStream(new StringReader(xmlText));

    Document doc = null;
    try
    {
      doc = builder.parse(in);
    }
    catch (SAXException e)
    {
      fail("parse error");
    }
    catch (IOException e)
    {
      fail("IOException while parsing XML");
    }

    return doc.getDocumentElement();
  }

  /** Tests that we can construct a Web object from the buildFromXml() method */
  public void testBuildFromXmlWeb() {
    final int id = 12;
    final String url = "http://muppets.com/videofeed";
    final String username = "fozzy";
    final String password = "bear";
    final String xmlText = "<web id='" + id + "' src='" + url + "' username='" + username + "' password='" + password + "' />";

    Node parsedXml = parseXml(xmlText);
    Web web = (Web) Component.buildWithXML(parsedXml);

    assertEquals(web.getSrc().toString(), url);
    assertEquals(web.getComponentId(), id);
    assertEquals(web.getUsername(), username);
    assertEquals(web.getPassword(), password);
  }
}
