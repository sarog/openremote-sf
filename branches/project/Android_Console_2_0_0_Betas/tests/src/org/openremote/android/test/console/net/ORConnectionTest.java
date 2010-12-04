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

import java.net.HttpURLConnection;
import java.net.UnknownHostException;
import java.net.MalformedURLException;
import java.io.IOException;

import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORHttpMethod;
import org.openremote.android.console.AppSettingsActivity;
import org.apache.http.HttpResponse;
import android.test.ActivityInstrumentationTestCase2;
import android.app.Activity;

/**
 * Tests for {@link ORConnection} class.
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



  /**
   * Tests a basic GET through {@link ORConnection#checkURLWithHTTPProtocol} method.
   *
   * @throws IOException see checkURLWithHTTPProtocol javadoc for details
   */
  public void testURLConnectionBasicGET() throws IOException
  {
    final String URL = "http://controller.openremote.org/test/controller";
    final String HTTP_CONTENT_TYPE_HEADER = "content-type";
    final String HTTP_MIME_CONTENT_TYPE = "text/html";

    final boolean NO_HTTP_AUTH = false;


    HttpResponse response = ORConnection.checkURLWithHTTPProtocol(
        activity, ORHttpMethod.GET, URL, NO_HTTP_AUTH
    );

    assertTrue("Expected response to " + URL + ", got <null>", response != null);

    int  httpResponseCode = response.getStatusLine().getStatusCode();
    long httpResponseContentLen = response.getEntity().getContentLength();
    String httpContentTypeHeader = response.getEntity().getContentType().getName();
    String httpMimeContentType = response.getEntity().getContentType().getValue();

    assertTrue(
        "Expected HTTP Response '" + HttpURLConnection.HTTP_OK + "', got '" + httpResponseCode + "'.",
        httpResponseCode == HttpURLConnection.HTTP_OK
    );

    assertTrue(
        "Expected content length > 0, got " + httpResponseContentLen,
        response.getEntity().getContentLength() > 0
    );

    assertTrue(
        "Expected HTTP Header '" + HTTP_CONTENT_TYPE_HEADER + "', got '" + httpContentTypeHeader + "'.",
        httpContentTypeHeader.equalsIgnoreCase(HTTP_CONTENT_TYPE_HEADER)
    );

    assertTrue(
        "Expected HTTP Mime type '" + HTTP_MIME_CONTENT_TYPE + "', got '" + httpMimeContentType + "'.",
        httpMimeContentType.startsWith("text/html")
    );
  }


  /**
   * Tests a GET behavior on an unknown (no DNS resolution) host.
   */
  public void testURLConnectionUnknownHost() throws IOException
  {
    final String URL = "http://controller.openremotetest.org/test/controller";

    final boolean NO_HTTP_AUTH = false;


    try
    {
      HttpResponse response = ORConnection.checkURLWithHTTPProtocol(
          activity, ORHttpMethod.GET, URL, NO_HTTP_AUTH
      );

      fail ("Should not get here...");
    }
    catch (UnknownHostException e)
    {
      // expected...
    }
  }


  public void testURLConnectionWithNullArg() throws IOException
  {
    final String URL = null;

    final boolean NO_HTTP_AUTH = false;

    try
    {
      HttpResponse response = ORConnection.checkURLWithHTTPProtocol(
          activity, ORHttpMethod.GET, URL, NO_HTTP_AUTH
      );

      fail ("Should not get here...");
    }
    catch (MalformedURLException e)
    {
      // expected...
    }
  }

}

