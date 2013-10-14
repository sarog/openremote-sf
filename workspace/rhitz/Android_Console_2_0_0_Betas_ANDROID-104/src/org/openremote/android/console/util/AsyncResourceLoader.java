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

package org.openremote.android.console.util;

import java.io.IOException;
import java.io.InputStream;

import org.openremote.android.console.AppSettingsActivity;
import org.openremote.android.console.GenericActivity;
import org.openremote.android.console.R;
import org.openremote.android.console.Constants;
import org.openremote.android.console.GroupActivity;
import org.openremote.android.console.LoginViewActivity;
import org.openremote.android.console.Main;
import org.openremote.android.console.exceptions.ControllerAuthenticationFailureException;
import org.openremote.android.console.exceptions.ORConnectionException;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.model.XMLEntityDataBase;
import org.openremote.android.console.net.ControllerService;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

import roboguice.util.RoboAsyncTask;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Load panel.xml and related images from the controller in a background thread,
 * updating a TextView if provided to show status messages.
 *
 * @author handy 2010-05-10
 * @author Dan Cong
 * @author Andrew D. Ball <aball@osintegrators.com>
 */
public class AsyncResourceLoader extends RoboAsyncTask<AsyncResourceLoaderResult>
{
  public static final String LOG_CATEGORY = Constants.LOG_CATEGORY + "AsyncResourceLoader";

  private static final int TO_LOGIN = 1;
  private static final int TO_GROUP = 2;

  private ControllerService controllerService;
  private Context context;

  private TextView loadingText;
  private Activity activity;

  @Inject
  public AsyncResourceLoader(ControllerService controllerService, Context context)
  {
    this.controllerService = controllerService;
    this.context = context;
  }

  /**
   * If a TextView needs to be updated with progress during execution of this
   * task, you can pass a reference to it here.
   *
   * @param loadingText
   */
  public void setLoadingText(TextView loadingText)
  {
    this.loadingText = loadingText;
  }

  /**
   * If the activity which launched this task needs its finish() method to be
   * called before starting a new activity, you can pass a reference to that
   * activity with this method.
   *
   * @param activity the activity to call finish() on before launching a different activity
   */
  public void setActivity(Activity activity)
  {
    this.activity = activity;
  }

  /**
   * Download panel.xml and images in the background.
   */
  public AsyncResourceLoaderResult call() throws Exception
  {
    final String logPrefix = "call(): ";

    AsyncResourceLoaderResult result = new AsyncResourceLoaderResult();
    String panelName = AppSettingsModel.getCurrentPanelIdentity(context);

    try
    {
      Log.i(LOG_CATEGORY, logPrefix + "Getting panel: " + panelName);
      updateLoadingTextOnUiThread("panel: " + panelName);

      InputStream panelStream = controllerService.getPanel(panelName);
      FileUtil.writeStreamToFile(context, panelStream, Constants.PANEL_XML);

      FileUtil.parsePanelXML(context);
      result.setAction(TO_GROUP);

      // now download images

      for (String imageName : XMLEntityDataBase.imageSet)
      {
        updateLoadingTextOnUiThread(imageName);

        // TODO image update capability?
        if (FileUtil.checkFileExists(context, imageName))
        {
          Log.i(LOG_CATEGORY, logPrefix + "not downloading image " + imageName + " because it is already in the cache");
        }
        else
        {
          Log.i(LOG_CATEGORY, logPrefix + "downloading new image " + imageName);
          InputStream imageStream = controllerService.getResource(imageName);
          FileUtil.writeStreamToFile(context, imageStream, imageName);
        }
      }
    }

    catch (ControllerAuthenticationFailureException e)
    {
      Log.i(LOG_CATEGORY, logPrefix + "not authenticated to controller. going to login activity.");
      result.setAction(TO_LOGIN);
    }

    catch (ORConnectionException e)
    {
      Log.e(LOG_CATEGORY, logPrefix + "could not connect to controller.  seeing if we can use local cache", e);

      try
      {
        FileUtil.parsePanelXML(context);
        result.setAction(TO_GROUP);
        result.setCanUseLocalCache(true);
      }
      catch (SAXException se)
      {
        Log.e(LOG_CATEGORY, logPrefix + "parse error on cached panel.xml.  " +
            "not using local cache", e);
        result.setCanUseLocalCache(false);
        throw se;
      }
      catch (IOException ie)
      {
        Log.e(LOG_CATEGORY, logPrefix + "IO error when trying to parse panel.xml.  " +
            "not using local cache", e);
        result.setCanUseLocalCache(false);
       throw ie;//throws exception but nothing happens when ok is clicked. Get back to Settings menu if controller is no longer avail
        /**
         * Forward to settings view.
         */
       /* private void doSettings() {
            Intent i = new Intent();
            i.setClassName(this.getClass().getPackage().getName(),
                  AppSettingsActivity.class.getName());
            startActivity(i);
            finish();
        }*/

 
      }
    }

    return result;
  }

  protected void onException(Exception e) throws RuntimeException
  {
    final String logPrefix = "onException(): ";
    if (e instanceof ORConnectionException)
    {
      Log.e(LOG_CATEGORY, logPrefix + "unable to contact a controller and cannot use cache", e);
      ViewHelper.showAlertViewWithTitle(context,
          context.getString(R.string.controller_error),
          context.getString(R.string.cannot_contact_controller_and_cannot_use_cache));
    }
    else if (e instanceof SAXException)
    {
      Log.e(LOG_CATEGORY, logPrefix + "parse error on panel.xml", e);
      ViewHelper.showAlertViewWithTitle(context,
          context.getString(R.string.error),
          context.getString(R.string.panel_xml_parse_error));
    }
    else if (e instanceof IOException)
    {
      // TODO make it easier to know exactly what went wrong here.  There are very many
      // places where IOException could be thrown in call().
     
 
          ViewHelper.showAlertViewWithTitleYesOrNo(context,
                  context.getString(R.string.error),
                  "Controller is not available",
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                  FileUtil.clearImagesInCache(context);//&&
                  
                 //doSettingsOnMain;
                  doSettingsOnMain: {
                      AppSettingsModel.setCurrentController(context, null);
                      AppSettingsModel.setCurrentPanelIdentity(context, null);
                     
                      Intent intent = new Intent();

                     
                        intent.setClass(context, Main.class);
                      

                      if (activity != null)
                      {
                        activity.finish();
                      }
                      context.startActivity(intent);
                  }
                  
                }
              });
  
      
      
 
    }
  }

  @Override
  protected void onSuccess(AsyncResourceLoaderResult result)
  {
    Intent intent = new Intent();

    switch (result.getAction())
    {
    case TO_GROUP:
      intent.setClass(context, GroupActivity.class);
      break;

    case TO_LOGIN:
      intent.setClass(context, LoginViewActivity.class);
      intent.setData(Uri.parse(Main.LOAD_RESOURCE));
      break;

    default:
      ViewHelper.showAlertViewWithTitle(context, "Send Request Error",
              ControllerException.exceptionMessageOfCode(result.getStatusCode()));
      return;
    }

    if (activity != null)
    {
      activity.finish();
    }
    context.startActivity(intent);
  }

  /**
   * Update progress message in the loadingText TextView if one was provided
   * via setLoadingText().
   */
  protected void updateLoadingTextOnUiThread(final String value) {
    if (loadingText != null)
    {
      Log.d(LOG_CATEGORY, "publishProgress: " + value);
      loadingText.post(new Runnable() {
        @Override
        public void run()
        {
          Log.d(LOG_CATEGORY, "in progress updater Runnable.run()");
          loadingText.setText("loading " + value + "...");
          loadingText.setEllipsize(TruncateAt.MIDDLE);
          loadingText.setSingleLine(true);
        }
      });
    }
  }
}

/**
 * To express the downloading result state.
 */
class AsyncResourceLoaderResult
{
  /** The action after downloading. */
  private int action;

  /** Download resources status code. */
  private int statusCode;

  /** If download failed, can use local cache or not. */
  private boolean canUseLocalCache;

  public AsyncResourceLoaderResult()
  {
    action = -1;
    statusCode = -1;
    canUseLocalCache = false;
  }

  public int getAction()
  {
    return action;
  }

  public void setAction(int action)
  {
    this.action = action;
  }

  public int getStatusCode()
  {
    return statusCode;
  }

  public void setStatusCode(int statusCode)
  {
    this.statusCode = statusCode;
  }

  public boolean isCanUseLocalCache()
  {
    return canUseLocalCache;
  }

  public void setCanUseLocalCache(boolean canUseLocalCache)
  {
    this.canUseLocalCache = canUseLocalCache;
  }
}
