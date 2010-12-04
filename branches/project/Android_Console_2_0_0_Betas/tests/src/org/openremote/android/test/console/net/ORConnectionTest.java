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
package org.openremote.android.test.console.net;

import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORHttpMethod;
import org.openremote.android.console.AppSettingsActivity;
import org.apache.http.HttpResponse;
import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;

/**
 * TODO
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public class ORConnectionTest extends ActivityInstrumentationTestCase2<AppSettingsActivity>
{

  // Instance Fields ------------------------------------------------------------------------------

  /**
   * Reference to the activity used to back the tests.
   */
  private Activity activity;


  // Constructors ---------------------------------------------------------------------------------

  public ORConnectionTest()
  {
    super("org.openremote.android.console", AppSettingsActivity.class);
  }


  // Test Setup -----------------------------------------------------------------------------------

  @Override public void setUp()
  {
    try
    {
      super.setUp();
    }
    catch (Throwable t)
    {
      fail ("Test setup failed in ActivityInstrumentationTestCase2: " + t.getMessage());
    }

    this.activity = getActivity();
  }

  // Tests ----------------------------------------------------------------------------------------

  public void testURLConnectionBasic()
  {
    final String URL = "http://contoller.openremote.org/test/controller";
    final boolean NO_HTTP_AUTH = false;

    HttpResponse response = ORConnection.checkURLWithHTTPProtocol(
        activity, ORHttpMethod.GET, URL, NO_HTTP_AUTH
    );

    
  }
}

