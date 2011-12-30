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
package org.openremote.controller.protocol.isy99;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.exception.NoSuchCommandException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
// Will put back later 
//import org.openremote.controller.Isy99Config;

/**
 * OpenRemote isy99 command implementation.  <p>
 *
 * Maintains a isy99-machine-wide state for each address.<p>  
 * TODO <p>
 * Parse the dot.properties file. 
 *
 * @author <a href="mailto:"></a>
 */
public class Isy99Command implements ExecutableCommand, StatusCommand
{

  // hostname, username, and password from properties file

  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging. Use common log category for all related classes.
   */
  private final static Logger log = Logger.getLogger(Isy99CommandBuilder.ISY99_LOG_CATEGORY);

  // Instance Fields ------------------------------------------------------------------------------

//  private Isy99Config isy99Config;

  private String host;
  private String userName;
  private String password;

  /**
   * The address for this particular 'command' instance.
   */
  private String address = null;

  /**
   * The command string for this particular 'command' instance.
   */
  private String command = null;

  private String commandParam = null;

  // Constructors ---------------------------------------------------------------------------------
  //Isy99Command( hostAsStr, userNameAsStr,passwordAsStr, addressAsStr, commandAsStr);
  public Isy99Command(String host, String userName, String password, String address, String command)
  {
    this.host = host;
    this.userName = userName;
    this.password = password;
    this.address = address;
    this.command = command;

    log.info("Got isy99 config");
    log.info("Host >" + this.host + "<");
    log.info("UserName >" + this.userName + "<");
    log.info("Password >" + this.password + "<");
    log.info("Switch Address >" + this.address + "<");
    log.info("Switch Command >" + this.command + "<");
  }

  //TODO this needs to be tested 
  // Called from the Isy99CommandBuilder
  public Isy99Command(String host, String userName, String password, String address, String command, String commandParam)
  {
    this(host, userName, password, address, command);
    this.commandParam = commandParam;
    log.info("Got isy99 config-commandParmam");
    log.info("Host >" + this.host + "<");
    log.info("UserName >" + this.userName + "<");
    log.info("Password >" + this.password + "<");
    log.info("Switch Address >" + this.address + "<");
    log.info("Switch Command >" + this.command + "<");
    log.info("Switch CommandParam >" + this.commandParam + "<");
  }

  // Implements ExecutableCommand -----------------------------------------------------------------

  public void send()
  {
    final String urlPath = "/rest/nodes/";
    final String preIsy99Cmd = "/cmd/";
    String urlStr = null;

    DefaultHttpClient httpclient = new DefaultHttpClient();
    try
    {
      httpclient.getCredentialsProvider().setCredentials(new AuthScope(host, 80),
          new UsernamePasswordCredentials(userName, password));

      // TODO handle percent encoding when constructing URL
      StringBuilder urlBuilder = new StringBuilder();
      // urlStr = "http://" + url + UrlPath + switchMac + PreIsy99Cmd + Isy99Cmd;
      // TODO support https scheme as well
      urlBuilder.append("http://");
      urlBuilder.append(host);
      urlBuilder.append(urlPath);
      urlBuilder.append(address.replaceAll(" ", "%20"));
      urlBuilder.append(preIsy99Cmd);
      urlBuilder.append(command);

      if (commandParam != null)
      {
        // TODO 
        urlBuilder.append("/");
        urlBuilder.append(commandParam);
        
        //TODO log to debug after clean up ? 
        log.warn("commandParam  "+urlBuilder.toString());
      }

      urlStr = urlBuilder.toString();
      log.debug("send(): URL is " + urlStr);
      log.warn("send(): URL is rest call  " + urlStr);

      HttpGet httpget = new HttpGet(urlStr);
      log.debug("executing request" + httpget.getRequestLine());
      HttpResponse response = httpclient.execute(httpget);

      int responseStatusCode = response.getStatusLine().getStatusCode();
      if (responseStatusCode != 200)
      {
        log.error("send(): response status code was " + responseStatusCode);
      }
    }
    catch (IOException e)
    {
      log.error("send(): IOException: address: " + address + "command: " +
          command, e);
    }
    finally
    {
      // TODO find out if this affects other code!

      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources

      // httpclient.getConnectionManager().shutdown();
    }
  }

  // Implements StatusCommand ---------------------------------------------------------------------

  public String read(EnumSensorType sensorType, Map<String, String> stateMap)
  {
    String urlPath = "/rest/nodes/";
    String preIsy99Cmd = "/";
    String urlStr = null;
    // String switchMac = args[0];
    // String Isy99Cmd = args[1];

    // TODO consider reducing the size of this try block
    DefaultHttpClient httpclient = new DefaultHttpClient();
    InputStream content = null;
    String value = null;
    try
    {
      //TODO
      //Need to change to https or http from .property file 
      httpclient.getCredentialsProvider().setCredentials(
          new AuthScope(host, 80),
          new UsernamePasswordCredentials(userName, password));
      // does this work with the map?
      urlStr = "http://" + host + urlPath + address.replaceAll(" ", "%20") + preIsy99Cmd;

      HttpGet httpget = new HttpGet(urlStr);

      log.debug("executing request " + httpget.getURI());

      HttpResponse response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();

      if (response.getStatusLine().getStatusCode() != 200)
      {
        // TODO log this
        log.debug("status line " + response.getStatusLine());
        if (entity != null)
        {
          // TODO log this
          log.debug("Response content length: " + entity.getContentLength());
        }
        // EntityUtils.consume(entity);
      }
      else
      {
        // TODO log this
        log.debug("Command was sent successfull");
      }

      log.debug("----------------------------------------");
      // log.debug(responseBody);
      log.debug("----------------------------------------");

      // Now we shall set up the xml parsing 

      SAXBuilder builder = new SAXBuilder();

      content = response.getEntity().getContent();

      Document document = (Document) builder.build(content);
      Element rootNode = document.getRootElement();

      @SuppressWarnings("unchecked")
      List<Element> list = rootNode.getChildren("node");

      for (Element node : list)
      {
        log.debug("XML Parsing ");
        log.debug("address : " + node.getChildText("address"));
        log.debug("name : " + node.getChildText("name"));
        log.debug("type: " + node.getChildText("type"));
        log.debug("enabled: " + node.getChildText("enabled"));
        log.debug("elk_id: " + node.getChildText("ELK_ID"));
        log.debug("property: " + node.getChildText("property"));
        value = node.getChild("property").getAttributeValue("value");
        log.debug("prop->value-> " + value);
      }
    }
    catch (IOException ioe)
    {
      log.error("IOException while reading data from ISY-99", ioe);
      // TODO make finally still happen
      return "";
    }
    catch (JDOMException jdomex)
    {
      log.error("error while parsing response from ISY-99", jdomex);
      // TODO make finally still happen
      return "";
    }
    finally
    {
      // TODO find out what effect that this has on other code

      // When HttpClient instance is no longer needed,
      // shut down the connection manager to ensure
      // immediate deallocation of all system resources

      // httpclient.getConnectionManager().shutdown();

      // TODO check whether we need to do this
      try
      {
        content.close();
      }
      catch (IOException e)
      {
        ; // swallowed on purpose! (Andrew D. Ball <aball@osintegrators.com>
      }
    }

    int integerValue = -1;
    try
    {
      integerValue = Integer.parseInt(value);
    }
    catch (NumberFormatException e)
    {
      // TODO log
      log.error("invalid sensor reading from ISY-99: expected an integer, got \"" + value + "\"");
      return "";
    }

    switch (sensorType)
    {

      case SWITCH:

        if (value == null)
        {
          return "off";
        }

        else if (integerValue >= 1)
        {
          return "on";
        }

        else if (integerValue == 0)
        {
          return "off";
        }

      case LEVEL:
        return "" + (integerValue * (100/250));

      case RANGE:

        return "" + integerValue;

      default:

        return "";
    }
  }
}

