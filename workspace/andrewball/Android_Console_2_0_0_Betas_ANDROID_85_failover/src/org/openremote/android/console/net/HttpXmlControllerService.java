/* OpenRemote, the Home of the Digital Home.
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

package org.openremote.android.console.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import org.openremote.android.console.Constants;
import org.openremote.android.console.exceptions.ControllerAuthenticationFailureException;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.SecurityUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

import android.content.Context;
import android.util.Log;

/**
 * Implementation for the ControllerService interface that uses HTTP (or HTTPS) to
 * communicate with an OpenRemote 2.0 controller, using the XML API.
 *
 * TODO throw AuthenticatedRequiredExceptions where appropriate
 *
 * @author Andrew D. Ball <aball@osintegrators.com>
 */
public class HttpXmlControllerService implements ControllerService
{
  public static final String LOG_CATEGORY = Constants.LOG_CATEGORY + "ControllerServiceImpl";

  private Context ctx;

  @Inject
  public HttpXmlControllerService(Context ctx)
  {
    this.ctx = ctx;
  }

  /**
   * Returns the URL of the current controller, from the AppSettingsModel.
   */
  protected URL getControllerUrl()
  {
    return AppSettingsModel.getSecuredServer(ctx);
  }

  /**
   * Returns a default HttpClient.  Additional methods could return more specialized HttpClient
   * instances, such as one that has longer timeout values.
   */
  protected HttpClient getHttpClient()
  {
    HttpParams params = new BasicHttpParams();
    HttpConnectionParams.setConnectionTimeout(params, Constants.DEFAULT_CONTROLLER_CONNECTION_TIMEOUT);
    HttpConnectionParams.setSoTimeout(params, Constants.DEFAULT_CONTROLLER_SOCKET_TIMEOUT);
    return new DefaultHttpClient(params);
  }

  /**
   * Constructs an HttpGet object given a URL which specifies that self-signed SSL certificates
   * should be accepted.
   *
   * TODO add HTTP authentication header if needed
   *
   * @param httpClient the HttpClient instance that will be used for the request
   * @param url the URL that will be used for the request
   */
  protected HttpGet getHttpGetRequest(HttpClient httpClient, URL url)
  {
    HttpGet request = new HttpGet(url.toString());
    SecurityUtil.addCredentialToHttpRequest(ctx, request);

    // accept self-signed SSL certificates
    if ("https".equals(url.getProtocol()))
    {
      Scheme sch = new Scheme(url.getProtocol(), new SelfCertificateSSLSocketFactory(), url.getPort());
      httpClient.getConnectionManager().getSchemeRegistry().register(sch);
    }

    return request;
  }

  /**
   * Parse XML from an InputStream and return the resulting DOM tree as a Document.
   *
   * @param data InputStream containing the XML text
   *
   * @returns the DOM tree as a Document
   *
   * @throws IOException if the data could not be read
   * @throws SAXException if a parse error occurred
   *
   * @throws RuntimeException if an XML parser could not be constructed
   *
   * TODO This code probably belongs elsewhere.  It's here to prevent code duplication.
   */
  protected Document parseXml(InputStream data) throws IOException, SAXException
  {
    DocumentBuilderFactory factory = null;
    DocumentBuilder builder = null;
    try
    {
      factory = DocumentBuilderFactory.newInstance();
      builder = factory.newDocumentBuilder();
    }
    catch (ParserConfigurationException e)
    {
      Log.e(LOG_CATEGORY, "Can't construct new document builder", e);
      throw new RuntimeException("XML parsing configuration unrecoverably bad!", e);
    }

    return builder.parse(data);
  }

  /**
   * Returns a list of all of the controller's cluster group member URLs.
   *
   * @throws MalformedURLException if a URL from the controller was invalid
   *
   * TODO make a generic bad data from controller exception and throw it instead of
   * MalformedURLException?
   *
   * See {@link ControllerService.getServers}
   */
  public List<URL> getServers() throws Exception
  {
    URL url = new URL(getControllerUrl().toString() + "/rest/servers");
    HttpClient httpClient = getHttpClient();
    HttpGet request = getHttpGetRequest(httpClient, url);

    HttpResponse response = httpClient.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode != HttpURLConnection.HTTP_OK)
    {
      // TODO throw a better exception
      throw new Exception("getServers() request to controller failed with HTTP status code " + statusCode);
    }

    Document dom = parseXml(response.getEntity().getContent());

    ArrayList<URL> servers = new ArrayList<URL>();

    NodeList serverNodes = dom.getDocumentElement().getElementsByTagName("server");
    for (int i = 0; i < serverNodes.getLength(); i++)
    {
      servers.add(new URL(serverNodes.item(i).getAttributes().getNamedItem("url").getNodeValue()));
    }

    return servers;
  }

  /**
   * Returns an InputStream containing the contents of a panel.xml file from the controller.
   *
   * See {@link ControllerService.getPanel}
   */
  @Override
  public InputStream getPanel(String panelName) throws ControllerAuthenticationFailureException, Exception
  {
    String encodedPanelName = URLEncoder.encode(panelName, Constants.UTF8_ENCODING);

    URL url = new URL(getControllerUrl().toString() + "/rest/panel/" + encodedPanelName);
    HttpClient httpClient = getHttpClient();
    HttpGet request = getHttpGetRequest(httpClient, url);

    HttpResponse response = httpClient.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode != HttpURLConnection.HTTP_UNAUTHORIZED)
    {
      throw new ControllerAuthenticationFailureException("controller authentication required");
    }
    else if (statusCode == HttpURLConnection.HTTP_OK)
    {
      // TODO throw a better exception
      throw new Exception("getPanel() request to controller failed with HTTP status code " + statusCode);
    }

    return response.getEntity().getContent();
  }

  /**
   * Returns an InputStream containing the contents of a file (resource) from the controller.
   *
   * See {@link ControllerService.getResource}
   */
  @Override
  public InputStream getResource(String resourceName) throws Exception
  {
    String encodedResourceName = URLEncoder.encode(resourceName, Constants.UTF8_ENCODING);

    URL url = new URL(getControllerUrl().toString() + "/resources/" + encodedResourceName);
    HttpClient httpClient = getHttpClient();
    HttpGet request = getHttpGetRequest(httpClient, url);

    HttpResponse response = httpClient.execute(request);
    int statusCode = response.getStatusLine().getStatusCode();

    if (statusCode != HttpURLConnection.HTTP_OK)
    {
      // TODO throw a better exception
      throw new Exception("getResource() request to controller failed with HTTP status code " + statusCode);
    }

    return response.getEntity().getContent();
  }

}