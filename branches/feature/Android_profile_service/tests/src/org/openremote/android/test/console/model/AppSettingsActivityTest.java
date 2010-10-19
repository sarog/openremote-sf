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
public class AppSettingsActivityTest extends ActivityInstrumentationTestCase2<AppSettingsActivity>
{

  private Context ctx;


  public AppSettingsActivityTest()
  {
    super("org.openremote.android.console", AppSettingsActivity.class);
  }

  public void setUp()
  {
    this.ctx = getInstrumentation().getTargetContext();
  }


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
   * TODO
   */
  public void testGetHTTPSControllerURL()
  {
    AppSettingsModel.setCurrentServer(ctx, "http://controller.openremote.org/test");

    String controller = AppSettingsModel.getSecuredServer(ctx);

    assertTrue("Expected 'https://controller.openremote.org/test:8443', got '" + controller + "'.",
                controller.equals("https://controller.openremote.org/test:8443"));
  }

}
