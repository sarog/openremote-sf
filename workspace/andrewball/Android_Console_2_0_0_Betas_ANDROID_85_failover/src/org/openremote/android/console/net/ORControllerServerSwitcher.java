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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.StringUtil;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This is responsible for detecting failover group members and switching to available controller.
 * 
 * @author handy 2010-04-29
 * @author Andrew D. Ball <aball@osintegrators.com>
 */
@Singleton
public class ORControllerServerSwitcher
{
  private ORNetworkCheck orNetworkCheck;

  private Context context;

  public static class ControllerCheckResult
  {
    public ControllerCheckResult(boolean contactable, Date timeOfCheck)
    {
      this.contactable = contactable;
      this.timeOfCheck = timeOfCheck;
    }
    public boolean isContactable()
    {
      return contactable;
    }
    public Date getTimeOfCheck()
    {
      return timeOfCheck;
    }
    private boolean contactable;
    private Date timeOfCheck;
  }

  @Inject
  public ORControllerServerSwitcher(ORNetworkCheck orNetworkCheck, Context context)
  {
    this.orNetworkCheck = orNetworkCheck;
    this.context = context;

    controllerCheckStatus = new HashMap<URL, ControllerCheckResult>();
  }

  /**
   * This stores the result of controller presence checks, indexed by their URLs.
   */
  private Map<URL, ControllerCheckResult> controllerCheckStatus;

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
   * Serialize the group members into file named group_members.xml .
   *
   * @param context       global Android application context
   * @param groupMembers  controller cluster group members
   *
   * @return  true if save was successful, false otherwise
   */
  public boolean saveGroupMembersToFile(List<URL> groupMembers)
  {
    SharedPreferences.Editor editor =
        context.getSharedPreferences(SERIALIZE_GROUP_MEMBERS_FILE_NAME, 0).edit();

    editor.clear();
    editor.commit();

    for (int i = 0; i < groupMembers.size(); i++)
    {
      editor.putString(i+"", groupMembers.get(i).toString());
    }

    return editor.commit();
  }


  /**
   * Get the group members from the file group_members.xml .
   *
   * @return  list of controller URLs
   */
  @SuppressWarnings("unchecked")
  public List<URL> findAllGroupMembersFromFile()
  {
    List<URL> groupMemberUrls = new ArrayList<URL>();

    Map<String, String> groupMembersMap =
        (Map<String, String>) context.getSharedPreferences(SERIALIZE_GROUP_MEMBERS_FILE_NAME, 0).getAll();

    for(int i = 0; i < groupMembersMap.size(); i++)
    {
      String urlAsString = groupMembersMap.get(Integer.toString(i));
      try
      {
        groupMemberUrls.add(new URL(urlAsString));
      }
      catch (MalformedURLException e)
      {
        Log.e(LOG_CATEGORY, "received invalid failover group member URL from controller: " + urlAsString);
      }
    }

    return groupMemberUrls;
  }

  /**
   * Check each group member's URL and get an available one. Depends on the WiFi network.
   *
   * @param context global Android application context
   *
   * @return  TODO
   */
  private URL getOneAvailableFromGroupMemberURLs()
  {
    List<URL> allGroupMembers = findAllGroupMembersFromFile();

    Log.i(LOG_CATEGORY, "Checking for an available controller URL from group members: " + allGroupMembers);

    for (URL controllerURL : allGroupMembers)
    {

      HttpResponse response = null;

      try
      {
        response = orNetworkCheck.verifyControllerURL(controllerURL);
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
          String selectedControllerServerURL = StringUtil.markControllerServerURLSelected(controllerURL.toString());
          String customServerURLs = AppSettingsModel.getCustomServers(context);

          if (!customServerURLs.contains(selectedControllerServerURL))
          {
            customServerURLs = StringUtil.removeControllerServerURLSelected(customServerURLs);

            if (customServerURLs.contains(controllerURL.toString()))
            {
              customServerURLs = customServerURLs.replaceAll(controllerURL.toString(), selectedControllerServerURL);
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

}
