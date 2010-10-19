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
    AppSettingsModel.setUseSSL(ctx, false);
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

    AppSettingsModel.setUseSSL(ctx, true);

    controller = AppSettingsModel.getSecuredServer(ctx);

    // Should have HTTPS added, but if not port specified (meaning port 80 is used for HTTP)
    // then SSL should default to 443 (port does not need to be explicitly specified)...

    assertTrue("Expected 'https://controller.openremote.org/test', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org/test"));

  }

  /**
   * Tests controller URL modifications when SSL is turned on and specific ports are used.
   */
  public void testGetHTTPSControllerURLWithExplicitPort()
  {
    AppSettingsModel.setUseSSL(ctx, true);

    // Controller URL with explicit port setting...

    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org/test:8111");

    String controller = AppSettingsModel.getSecuredServer(ctx);

    // Will return https at port 8443 which is the default Tomcat SSL port (fine by us,
    // we expect most controller instances run on Tomcat runtime and connected to directly)...

    assertTrue(
        "Expected 'https://controller.openremote.org/test:" +
        AppSettingsModel.DEFAULT_SSL_PORT + "', got '" + controller + "'.",
        controller.equals("https://controller.openremote.org/test:" +
        AppSettingsModel.DEFAULT_SSL_PORT)
    );


    // Test explicit port setting...

    AppSettingsModel.setSSLPort(ctx, 5000);

    controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'https://controller.openremote.org/test:5000', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org/test:5000"));



    // If explicit port is set to HTTPD default SSL port (443), it is still included in the URL.
    // Strictly not necessary but does no harm either...

    AppSettingsModel.setSSLPort(ctx, 443);

    controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'https://controller.openremote.org/test:443', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org/test:443"));


    // Reset back to defaults and make sure we still get the original controller URL...

    AppSettingsModel.setUseSSL(ctx, false);
    AppSettingsModel.setSSLPort(ctx, AppSettingsModel.DEFAULT_SSL_PORT);

    controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'http://controller.openremote.org/test:8111', got '" + controller + "'.",
                controller.equals("http://controller.openremote.org/test:8111"));

  }


  /**
   * Basic SSL enabled on/off test.
   */
  public void testEnableSSL()
  {
    AppSettingsModel.setUseSSL(ctx, true);

    assertTrue(AppSettingsModel.isUseSSL(ctx));

    AppSettingsModel.setUseSSL(ctx, false);

    assertTrue(!AppSettingsModel.isUseSSL(ctx));
  }

  /**
   * Basic set/get SSL port test.
   */
  public void testSetSSLPort()
  {
    assertTrue(AppSettingsModel.getSSLPort(ctx) == AppSettingsModel.DEFAULT_SSL_PORT);

    AppSettingsModel.setSSLPort(ctx, 9999);

    assertTrue(AppSettingsModel.getSSLPort(ctx) == 9999);
  }
}
