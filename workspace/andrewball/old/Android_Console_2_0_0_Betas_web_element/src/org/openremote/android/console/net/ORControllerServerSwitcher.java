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
package org.openremote.android.console.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openremote.android.console.Constants;
import org.openremote.android.console.Main;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.util.SecurityUtil;
import org.openremote.android.console.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This is responsible for detecting groupmembers and switching to available controller.
 * 
 * @author handy 2010-04-29
 *
 */
public class ORControllerServerSwitcher
{

  // Constants ------------------------------------------------------------------------------------

  /**
   * Common log category for fail-over functionality.
   */
  public final static String LOG_CATEGORY = Constants.LOG_CATEGORY + "Failover";

  private static final String SERIALIZE_GROUP_MEMBERS_FILE_NAME = "group_members";
  public static final int SWITCH_CONTROLLER_SUCCESS = 1;
  public static final int SWITCH_CONTROLLER_FAIL = 2;


  // Class Members --------------------------------------------------------------------------------

  /**
   * Detect the group members of current controller
   *
   * @return true if successful, false otherwise
   *
   * @todo propagate exceptions higher and return void
   */
  public static boolean detectGroupMembers(Context context)
  {
    Log.i(LOG_CATEGORY, "Detecting group members with current controller server URL " +
          AppSettingsModel.getCurrentServer(context));

    HttpParams params = new BasicHttpParams();
    // TODO: use constants for these
    HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
    HttpConnectionParams.setSoTimeout(params, 5 * 1000);
    HttpClient httpClient = new DefaultHttpClient(params);
    URL url = AppSettingsModel.getSecuredServer(context);
    HttpGet httpGet = new HttpGet(url + "/rest/servers");

    SecurityUtil.addCredentialToHttpRequest(context, httpGet);

    // TODO : fix the exception handling in this method -- it is ridiculous. (It's still ridiculous, but hopefully a little better (ADB))

    if ("https".equals(url.getProtocol()))
    {
      Scheme sch = new Scheme(url.getProtocol(), new SelfCertificateSSLSocketFactory(), url.getPort());
      httpClient.getConnectionManager().getSchemeRegistry().register(sch);
    }

    DocumentBuilderFactory factory = null;
    DocumentBuilder builder = null;
    try
    {
      factory = DocumentBuilderFactory.newInstance();
      builder = factory.newDocumentBuilder();
    }
    catch (ParserConfigurationException e)
    {
      Log.e(LOG_CATEGORY, "Can't build new Document builder", e);
      return false;
    }

    try
    {
      HttpResponse httpResponse = httpClient.execute(httpGet);

      if (httpResponse.getStatusLine().getStatusCode() != Constants.HTTP_SUCCESS)
      {
        Log.e(LOG_CATEGORY, "detectGroupMembers(): HTTP response code was not " + Constants.HTTP_SUCCESS);
        return false;
      }

      try
      {
        InputStream data = httpResponse.getEntity().getContent();

        Document dom = builder.parse(data);
        Element root = dom.getDocumentElement();

        NodeList nodeList = root.getElementsByTagName("server");
        List<String> groupMembers = new ArrayList<String>();

        for (int i = 0; i < nodeList.getLength(); i++)
        {
          groupMembers.add(nodeList.item(i).getAttributes().getNamedItem("url").getNodeValue());
        }

        Log.i(LOG_CATEGORY, "Detected group members. Group members are " + groupMembers);

        return saveGroupMembersToFile(context, groupMembers);
      }

      catch (IOException e)
      {
        Log.e(LOG_CATEGORY, "detectGroupMembers(): IOException while trying to parse XML from controller", e);
      }

      catch (SAXException e)
      {
        Log.e(LOG_CATEGORY, "detectGroupMembers(): parse error", e);
      }

      catch (IllegalStateException e)
      {
        Log.e(LOG_CATEGORY, "detectGroupMembers(): parse error", e);
      }
    }

    catch (ConnectException e)
    {
      Log.e(LOG_CATEGORY, "Connection refused: " + AppSettingsModel.getCurrentServer(context), e);
    }

    catch (ClientProtocolException e)
    {
      Log.e(LOG_CATEGORY, "Can't Detect groupmembers with current controller server " +
            AppSettingsModel.getCurrentServer(context), e);
    }

    catch (SocketTimeoutException e)
    {
      Log.e(LOG_CATEGORY, "Can't Detect groupmembers with current controller server " +
            AppSettingsModel.getCurrentServer(context), e);
    }

    catch (IOException e)
    {
      Log.e(LOG_CATEGORY, "Can't Detect groupmembers with current controller server " +
            AppSettingsModel.getCurrentServer(context), e);
    }

    catch (IllegalArgumentException e)
    {
      Log.e(LOG_CATEGORY, "Host name can be null :" + AppSettingsModel.getCurrentServer(context), e);
    }

    return false;
  }


  /**
   * Serialize the groupmembers into file named group_members.xml .
   *
   * @param context       global Android application context
   * @param groupMembers  controller cluster group members
   *
   * @return  true if save was successful, false otherwise
   */
  private static boolean saveGroupMembersToFile(Context context, List<String> groupMembers)
  {
    // TODO: Use URL class instead of String

    SharedPreferences.Editor editor =
        context.getSharedPreferences(SERIALIZE_GROUP_MEMBERS_FILE_NAME, 0).edit();

    editor.clear();
    editor.commit();

    for (int i = 0; i < groupMembers.size(); i++)
    {
      editor.putString(i+"", groupMembers.get(i));
    }

    return editor.commit();
  }


  /**
   * Get the groupmembers from the file group_members.xml .
   *
   * @param context   global Android application context
   *
   * @return  list of controller URLs
   */
  @SuppressWarnings("unchecked")
  public static List<String> findAllGroupMembersFromFile(Context context)
  {
    // TODO: Use URL in the API instead of strings

    List<String> groupMembers = new ArrayList<String>();

    Map<String, String> groupMembersMap =
        (Map<String, String>) context.getSharedPreferences(SERIALIZE_GROUP_MEMBERS_FILE_NAME, 0).getAll();

    for(int i = 0; i <groupMembersMap.size(); i++)
    {
      groupMembers.add(groupMembersMap.get(i+""));
    }

    return groupMembers;
  }



  /**
   * Get an available controller server URL and switch to it.
   *
   * @param context global Android application context
   *
   * @return  TODO
   */
  public static int doSwitch(Context context)
  {
    URL availableGroupMemberURL = getOneAvailableFromGroupMemberURLs(context);

    List<String> allGroupMembers = findAllGroupMembersFromFile(context);

    if (availableGroupMemberURL != null)
    {
      Log.i(LOG_CATEGORY, "Got an available controller URL from group members" + allGroupMembers);

      switchControllerWithURL(context, availableGroupMemberURL);
    }

    else
    {
      Log.i(
          LOG_CATEGORY,
          "Didn't get an available controller URL from group members " + allGroupMembers +
          ". Try to detect group members again."
      );

      if (!detectGroupMembers(context))
      {
         // TODO: say something more idiomatic than "Leave this problem?" below
         ViewHelper.showAlertViewWithSetting(
             context,
             "Update failed",
             "There's no controller server available. Leave this problem?"
         );

         return SWITCH_CONTROLLER_FAIL;
      }

      availableGroupMemberURL = getOneAvailableFromGroupMemberURLs(context);

      if (availableGroupMemberURL != null && !"".equals(availableGroupMemberURL))
      {
        Log.i(
            LOG_CATEGORY,
            "Got a available controller url from groupmembers " + allGroupMembers +
            " in second groupmembers detection attempt."
        );

        switchControllerWithURL(context, availableGroupMemberURL);
      }

      else
      {
        Log.i(LOG_CATEGORY, "There's no controller server available.");

        ViewHelper.showAlertViewWithSetting(
            context,
            "Update fail",
            "There's no controller server available. Leave this problem?"
        );

        return SWITCH_CONTROLLER_FAIL;
      }
    }

    return SWITCH_CONTROLLER_SUCCESS;
  }



  /**
   * Check each group member's URL and get an available one. Depends on the WiFi network.
   *
   * @param context global Android application context
   *
   * @return  TODO
   */
  private static URL getOneAvailableFromGroupMemberURLs(Context context)
  {
    List<String> allGroupMembers = findAllGroupMembersFromFile(context);

    Log.i(LOG_CATEGORY, "Checking for an available controller URL from group members: " + allGroupMembers);

    for (String urlString : allGroupMembers)
    {
      URL controllerURL;
      try {
        controllerURL = new URL(urlString);
      } catch (MalformedURLException e) {
        Log.e(LOG_CATEGORY, "invalid controller URL from list returned by findAllGroupMembersFromFile(): " + urlString);
        continue;
      }

      HttpResponse response = null;

      try
      {
        response = ORNetworkCheck.verifyControllerURL(context, controllerURL);
      }
      catch (IOException e)
      {
        // TODO :
        //
        //    In case of a fail-over scenario, don't propagate the exception higher up.
        //    The logic depends on null return values and since there are no tests to back it
        //    up, leaving it untouched for now.
        //
        //    Logging and keeping the null.
        //                                                                                [JPL]

        Log.i(LOG_CATEGORY, "TODO: need to refactor this logic to rely on exception instead of null return values");
        Log.i(LOG_CATEGORY, "Error was " + e.getMessage(), e);
      }


      if (response != null && response.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS)
      {
        if (!AppSettingsModel.isAutoMode(context))
        {
          String selectedControllerServerURL = StringUtil.markControllerServerURLSelected(urlString);
          String customServerURLs = AppSettingsModel.getCustomServers(context);

          if (!customServerURLs.contains(selectedControllerServerURL))
          {
            customServerURLs = StringUtil.removeControllerServerURLSelected(customServerURLs);

            if (customServerURLs.contains(urlString))
            {
              customServerURLs = customServerURLs.replaceAll(urlString, selectedControllerServerURL);
            }

            else
            {
              customServerURLs = customServerURLs + "," + selectedControllerServerURL;
            }

            AppSettingsModel.setCustomServers(context, customServerURLs);
          }
        }
        return controllerURL;
      }
    }

    return null;
  }



  /**
   * Switch to the controller identified by the availableGroupMemberURL
   *
   * @param context                 global Android application context
   * @param availableGroupMemberURL TODO
   */
  private static void switchControllerWithURL(Context context, URL availableGroupMemberURL)
  {
    if (availableGroupMemberURL.equals(AppSettingsModel.getCurrentServer(context)))
    {
      Log.i(
          LOG_CATEGORY,
          "The current server is already: " + availableGroupMemberURL +
          ", should not switch to self."
      );

      return;
    }

    Main.prepareToastForSwitchingController();
    
    Log.i(LOG_CATEGORY, "ControllerServerSwitcher is switching controller to " + availableGroupMemberURL);

    AppSettingsModel.setCurrentServer(context, availableGroupMemberURL);

    Intent intent = new Intent();
    intent.setClass(context, Main.class);
    context.startActivity(intent);
  }
	
}
