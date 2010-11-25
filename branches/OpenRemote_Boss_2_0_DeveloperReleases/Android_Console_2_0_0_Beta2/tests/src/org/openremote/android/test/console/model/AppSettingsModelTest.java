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
package org.openremote.android.test.console.model;

import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.model.AppSettingsModel;

import android.test.ActivityInstrumentationTestCase2;
import android.content.Context;


/**
 * Tests for {@link org.openremote.android.console.model.AppSettingsModel} class.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 *
 */
public class AppSettingsModelTest extends ActivityInstrumentationTestCase2<AppSettingsActivity>
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Make Android application context available to all tests.
   */
  private Context ctx;



  // Constructors ---------------------------------------------------------------------------------

  public AppSettingsModelTest()
  {
    super("org.openremote.android.console", AppSettingsActivity.class);
  }



  // Test Set Up ----------------------------------------------------------------------------------

  /**
   * Initialize Android application context for each test.
   */
  public void setUp()
  {
    this.ctx = getInstrumentation().getTargetContext();
  }

  /**
   * Reset application settings after each test.
   */
  public void tearDown()
  {
    AppSettingsModel.enableSSL(ctx, false);
    AppSettingsModel.setSSLPort(ctx, AppSettingsModel.DEFAULT_SSL_PORT);
  }



  // Tests ----------------------------------------------------------------------------------------
  
  /**
   * Test basic set/get on Controller URL setting.
   */
  public void testSetCurrentServer()
  {
    AppSettingsModel.setCurrentServer(ctx, "localhost");

    String controller = AppSettingsModel.getCurrentServer(ctx);

    assertTrue("Expected 'localhost', got '" + controller + "'.",
                controller.equals("localhost"));
  }


  /**
   * Test setting controller URL to null value, should return an empty string
   * (defaults to empty if no value is present)
   */
  public void testSetCurrentServerNull()
  {
    AppSettingsModel.setCurrentServer(ctx, null);

    String controller = AppSettingsModel.getCurrentServer(ctx);

    assertTrue("Expected empty string, got '" + controller + "'.",
                controller.equals(""));
  }

  /**
   * Test setting empty string as controller URL. Should return an empty string.
   */
  public void testSetCurrentServerEmpty()
  {
    AppSettingsModel.setCurrentServer(ctx, "");

    String controller = AppSettingsModel.getCurrentServer(ctx);

    assertTrue("Expected empty string, got '" + controller + "'.",
                controller.equals(""));
  }


  /**
   * Since we don't enforce URL in the API, any string will actually do.
   *
   * This should eventually go away with API evolution/fix.
   */
  public void testSetCurrentServerBadAPIDesign()
  {
    AppSettingsModel.setCurrentServer(ctx, "any value will do");

    String controller = AppSettingsModel.getCurrentServer(ctx);

    assertTrue("Expected 'any value will do', got '" + controller + "'.",
                controller.equals("any value will do"));

    fail("App settings don't validate URLs");
  }


  /**
   * Test basic controller URL modifications when SSL is enabled.
   */
  public void testGetHTTPSControllerURL()
  {
    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org/test");

    String controller = AppSettingsModel.getSecuredServer(ctx);

    // should return controller URL as-is, since SSL still disabled...

    assertTrue("Expected 'http://controller.openremote.org/test', got '" + controller + "'.",
                controller.equals("http://controller.openremote.org/test"));


    // Turn on SSL...

    AppSettingsModel.enableSSL(ctx, true);

    controller = AppSettingsModel.getSecuredServer(ctx);

    // Without explicit port in the original user configured controller URL (meaning port 80
    // is used for HTTP), default to httpd SSL port 443...

    assertTrue("Expected 'https://controller.openremote.org:443/test', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org:443/test"));

  }

  /**
   * Tests controller URL modifications when SSL is turned on and specific ports are used.
   */
  public void testGetHTTPSControllerURLWithExplicitPort()
  {
    AppSettingsModel.enableSSL(ctx, true);

    // Controller URL with explicit port setting...

    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org:8111/test");

    String controller = AppSettingsModel.getSecuredServer(ctx);

    // Will return https at port 8443 which is the default Tomcat SSL port, when explicit
    // port is used we assume OpenRemote/Tomcat runtime (which usually means port 8080 is
    // explicitly set)

    assertTrue(
        "Expected 'https://controller.openremote.org:8443/test" +
        "', got '" + controller + "'.",
        controller.equals("https://controller.openremote.org:8443/test"
        )
    );


    // Test explicit port setting...

    AppSettingsModel.setSSLPort(ctx, 5000);

    controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'https://controller.openremote.org:5000/test', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org:5000/test"));


    AppSettingsModel.setSSLPort(ctx, 443);

    controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'https://controller.openremote.org:443/test', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org:443/test"));


    // Reset back to defaults and make sure we still get the original controller URL...

    AppSettingsModel.enableSSL(ctx, false);
    AppSettingsModel.setSSLPort(ctx, AppSettingsModel.DEFAULT_SSL_PORT);

    controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'http://controller.openremote.org:8111/test', got '" + controller + "'.",
                controller.equals("http://controller.openremote.org:8111/test"));

  }


  /**
   * Making sure if explicit SSL port has been configured, it is used.
   */
  public void testGetHTTPSControllerURLWithExplicitPort2()
  {
    AppSettingsModel.enableSSL(ctx, true);

    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org/test");

    AppSettingsModel.setSSLPort(ctx, 5000);

    String controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'https://controller.openremote.org:5000/test', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org:5000/test"));

  }
  
  /**
   * Basic SSL enabled on/off test.
   */
  public void testEnableSSL()
  {
    AppSettingsModel.enableSSL(ctx, true);

    assertTrue(AppSettingsModel.isSSLEnabled(ctx));

    AppSettingsModel.enableSSL(ctx, false);

    assertTrue(!AppSettingsModel.isSSLEnabled(ctx));
  }

  /**
   * Basic set/get SSL port test.
   */
  public void testSetGetSSLPort()
  {
    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org/test");

    // defaults to httpd's SSL 443 if no explicit port has been set...

    assertTrue(AppSettingsModel.getSSLPort(ctx) == 443);

    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org:8080/test");

    // defaults to OR runtime/tomcat's SSL port 8443 if explicit port 8080 has been set...

    assertTrue(AppSettingsModel.getSSLPort(ctx) == 8443);

    AppSettingsModel.setSSLPort(ctx, 9999);

    // if explicit SSL port has been set, must return it...
    
    assertTrue(AppSettingsModel.getSSLPort(ctx) == 9999);
  }

  /**
   * In case of config failure, still try to give a reasonable return value
   */
  public void testGetSSLPortWithNullController()
  {
    AppSettingsModel.setCurrentServer(ctx, null);

    // best guess...

    assertTrue(AppSettingsModel.getSSLPort(ctx) == 8443);
  }

  /**
   * Tests setSSLPort edge cases
   */
  public void testSetGetSSLPortEdgeCases()
  {
    AppSettingsModel.setSSLPort(ctx, 0);
    assertTrue(AppSettingsModel.getSSLPort(ctx) == 0);

    AppSettingsModel.setSSLPort(ctx, 65535);
    assertTrue(AppSettingsModel.getSSLPort(ctx) == 65535);

    AppSettingsModel.setSSLPort(ctx, AppSettingsModel.DEFAULT_SSL_PORT);

    try
    {
      AppSettingsModel.setSSLPort(ctx, -100);

      fail("should not get here");
    }
    catch (IllegalArgumentException e)
    {
      // this is fine
    }

    try
    {
      AppSettingsModel.setSSLPort(ctx, 100000);

      fail("should not get here");
    }
    catch (IllegalArgumentException e)
    {
      // this is fine
    }
  }
}
