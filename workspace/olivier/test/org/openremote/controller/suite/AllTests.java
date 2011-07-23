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
package org.openremote.controller.suite;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.Assert;
import org.openremote.controller.net.MulticastAutoDiscoveryTest;
import org.openremote.controller.utils.MacrosIrDelayUtilTest;
import org.openremote.controller.utils.PathUtil;
import org.openremote.controller.model.PanelTest;
import org.openremote.controller.spring.SpringContext;
import org.openremote.controller.Constants;
import org.openremote.controller.ControllerConfiguration;
import org.w3c.dom.Document;
import org.jdom.input.DOMBuilder;

/**
 * Collects *all* unit tests. Also, the implementation contains utility methods in common across
 * various test suites.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
@RunWith(Suite.class) @SuiteClasses(
{
   AllControlBuilderTests.class,
   AllServiceTests.class,
   MacrosIrDelayUtilTest.class,
   AllCommandBuildersTests.class,
   MulticastAutoDiscoveryTest.class,
   RoundRobinTests.class,
   UtilTests.class,
    
   RESTTests.class,
   KNXTests.class,
   VirtualProtocolTests.class,
   PanelTest.class,
   AsyncEventTests.class,
   ExceptionTests.class,
   ComponentTests.class,
   SensorTests.class,
   EventTests.class,
   ProtocolTests.class,
   BusTests.class
})


public class AllTests
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Currently used test port for the embedded container hosting HTTP/REST implementation
   */
  public final static int WEBAPP_PORT = 8090;

  /**
   * Localhost IP address.
   */
  public final static String LOCALHOST = "127.0.0.1";

  /**
   * Path to test fixture directory.
   */
  public final static String FIXTURE_DIR = "org/openremote/controller/fixture/";

  /**
   * Path to polling related fixture files.
   */
  public final static String POLLING_FIXTURES = FIXTURE_DIR + "polling/";


  // Class Members --------------------------------------------------------------------------------


  static
  {
    try
    {      
      new SpringContext();
    }
    catch (Throwable t)
    {
      System.err.println(
          "=================================================================================\n\n" +

          " Cannot initialize tests: " + t.getMessage() + "\n\n" +

          " Stack Trace: \n\n"
      );

      t.printStackTrace(System.err);

      System.exit(1);
    }
  }



  // XML Parser Utilities -------------------------------------------------------------------------

  /**
   * Builds a *JDOM* Document from a file.
   *
   * @param f     file name path
   *
   * @return  JDOM document instance
   *
   * @throws Exception    in case there's an error
   */
  public static org.jdom.Document getJDOMDocument(File f) throws Exception
  {
    return new DOMBuilder().build(getDOMDocument(f));
  }

  /**
   * Builds a *DOM* document from a file.
   *
   * @param f   file name path
   *
   * @return  DOM document instance
   *
   * @throws Exception    in case there's an error
   */
  public static Document getDOMDocument(File f) throws Exception
  {
    return getDOMDocument(new FileInputStream(f));
  }

  /**
   * Builds a *DOM* document from a I/O stream
   *
   * @param in    input stream
   *
   * @return  DOM document instance
   *
   * @throws Exception    in case there's an error
   */
  public static Document getDOMDocument(InputStream in) throws Exception
  {
    BufferedInputStream bin = new BufferedInputStream(in);

    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder parser = domFactory.newDocumentBuilder();

    return parser.parse(bin);
  }



  // Container Controller.xml Utilities -----------------------------------------------------------

  public static void restoreControllerXML()
  {
    String controllerXML = AllTests.getFixtureFile(Constants.CONTROLLER_XML);

    if (new File(controllerXML + ".bak").exists())
    {
       new File(controllerXML + ".bak").renameTo(new File(controllerXML));
    }
  }


  public static void replaceControllerXML(String filename)
  {
    String fixtureFile = AllTests.getFixtureFile(filename);

    String controllerXML = getControllerXML();

    if (new File(controllerXML).exists())
    {
       new File(controllerXML).renameTo(new File(controllerXML + ".bak"));
    }

    copyFile(fixtureFile, controllerXML);
  }


  private static String getControllerXML()
  {
    return PathUtil.addSlashSuffix(
        ControllerConfiguration.readXML().getResourcePath()) +
        Constants.CONTROLLER_XML;
  }

  public static void deleteControllerXML()
  {
    String controllerXML = getControllerXML();

    deleteFile(controllerXML);
  }



  // Container Panel.xml Utilities ----------------------------------------------------------------

  public static void restorePanelXML()
  {
    String panelXML = AllTests.getFixtureFile(Constants.PANEL_XML);

    if (new File(panelXML + ".bak").exists())
    {
       new File(panelXML + ".bak").renameTo(new File(panelXML));
    }
  }


  public static void replacePanelXML(String filename)
  {
    String fixtureFile = AllTests.getFixtureFile(filename);

    String panelXML = getPanelXML();

    if (new File(panelXML).exists())
    {
       new File(panelXML).renameTo(new File(panelXML + ".bak"));
    }

    copyFile(fixtureFile, panelXML);
  }


  private static String getPanelXML()
  {
    return PathUtil.addSlashSuffix(
        ControllerConfiguration.readXML().getResourcePath()) +
        Constants.PANEL_XML;
  }


  // File Helpers ---------------------------------------------------------------------------------


  /**
   * Returns a path to a fixture (resource) file used by tests. Test fixtures are stored in their
   * own (fixed) location in the test directories. This method resolves the path to a given file
   * name in the directory.
   *
   * @param name    name of the fixture file (without path)
   *
   * @return        full path to the fixture file
   *
   * @throws  AssertionError   if the file is not found
   */
  public static String getFixtureFile(String name)
  {
    ClassLoader cl = Thread.currentThread().getContextClassLoader();

    String resource = FIXTURE_DIR + name;

    Assert.assertNotNull("Got null resource from '" + resource + "'.", cl.getResource(resource));

    return cl.getResource(resource).getFile();
  }



  private static void copyFile(String src, String dest)
  {
    File inputFile = new File(src);
    File outputFile = new File(dest);

    FileReader in;

    try
    {
      in = new FileReader(inputFile);

      if (!outputFile.getParentFile().exists())
      {
        outputFile.getParentFile().mkdirs();
      }

      if (!outputFile.exists())
      {
        outputFile.createNewFile();
      }

      FileWriter out = new FileWriter(outputFile);

      int c;

      while ((c = in.read()) != -1)
      {
        out.write(c);
      }

      in.close();
      out.close();
    }

    catch (IOException e)
    {
      e.printStackTrace();
    }
  }


  private static void deleteFile(String fileName)
  {

    File f = new File(fileName);

    if (!f.exists())
      return;

    f.delete();
  }


}
