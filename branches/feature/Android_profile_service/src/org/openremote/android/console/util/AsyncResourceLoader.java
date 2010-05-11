/* OpenRemote, the Home of the Digital Home.
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

package org.openremote.android.console.util;

import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.openremote.android.console.Constants;
import org.openremote.android.console.GroupActivity;
import org.openremote.android.console.LoginViewActivity;
import org.openremote.android.console.Main;
import org.openremote.android.console.R;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.model.XMLEntityDataBase;
import org.openremote.android.console.net.ORControllerServerSwitcher;
import org.openremote.android.console.net.ORNetworkCheck;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * It's responsible for downloading resources in backgroud.
 * 
 * @author handy 2010-05-10
 * @author Dan Cong
 *
 */
public class AsyncResourceLoader extends AsyncTask<Void, String, AsyncResourceLoaderResult> {
   private static final int TO_LOGIN = 0xF00A;
//   private static final int TO_SETTING = 0xF00B;
   private static final int TO_GROUP = 0xF00C;
   private static final int SWITCH_TO_OTHER_CONTROLER = 0xF00D;
   
   private Activity activity;
   
   public AsyncResourceLoader(Activity activity) {
      this.activity = activity;
   }
   
   @Override
   protected AsyncResourceLoaderResult doInBackground(Void... params) {
      AsyncResourceLoaderResult result = new AsyncResourceLoaderResult();

      boolean isControllerAvailable = false;
      String panelName = AppSettingsModel.getCurrentPanelIdentity(activity);
      String serverUrl = AppSettingsModel.getCurrentServer(activity);
      publishProgress(panelName);

      HttpResponse checkResponse = ORNetworkCheck.checkAllWithControllerServerURL(activity, serverUrl);
      isControllerAvailable = (checkResponse != null && checkResponse.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS) ? true : false;

      if (isControllerAvailable) {
         int downLoadPanelXMLStatusCode = HTTPUtil.downLoadPanelXml(activity, serverUrl, panelName);
         if (downLoadPanelXMLStatusCode != Constants.HTTP_SUCCESS) { // download panel xml fail.
            Log.e("INFO", "Download file panel.xml fail.");
            if (downLoadPanelXMLStatusCode == ControllerException.UNAUTHORIZED) {
             result.setAction(TO_LOGIN);
             result.setStatusCode(downLoadPanelXMLStatusCode);
             return result;
            }
            
            if (activity.getFileStreamPath(Constants.PANEL_XML).exists()) {
               Log.e("INFO", "Download file panel.xml fail, so use local cache.");
               FileUtil.parsePanelXML(activity);
               result.setCanUseLocalCache(true);
               result.setStatusCode(downLoadPanelXMLStatusCode);
               result.setAction(TO_GROUP);
            } else {
               Log.e("INFO", "No local cache is available, ready to switch controller.");
               result.setAction(SWITCH_TO_OTHER_CONTROLER);
               return result;
            }
         } else { // download panel xml success.
            Log.e("INFO", "Download file panel.xml successfully.");
            if (activity.getFileStreamPath(Constants.PANEL_XML).exists()) {
               FileUtil.parsePanelXML(activity);
               result.setAction(TO_GROUP);
            } else {
               Log.e("INFO","No local cache is available authouth downloaded file panel.xml successfully, ready to switch controller.");
               result.setAction(SWITCH_TO_OTHER_CONTROLER);
               return result;
            }

            Iterator<String> images = XMLEntityDataBase.imageSet.iterator();
            String imageName = "";
            while (images.hasNext()) {
               imageName = images.next();
               publishProgress(imageName);
               HTTPUtil.downLoadImage(activity, AppSettingsModel.getCurrentServer(activity), imageName);
            }
         }
      } else { // controller isn't available
         if (checkResponse != null && checkResponse.getStatusLine().getStatusCode() == ControllerException.UNAUTHORIZED) {
            result.setAction(TO_LOGIN);
            result.setStatusCode(ControllerException.UNAUTHORIZED);
            return result;
         }
         
         if (activity.getFileStreamPath(Constants.PANEL_XML).exists()) {
            Log.e("INFO", "Current controller server isn't available, so use local cache.");
            FileUtil.parsePanelXML(activity);
            result.setCanUseLocalCache(true);
            result.setAction(TO_GROUP);
            if (checkResponse == null) {
               result.setStatusCode(ControllerException.CONTROLLER_UNAVAILABLE);
            } else {
               result.setStatusCode(checkResponse.getStatusLine().getStatusCode());
            }
         } else {
            Log.e("INFO", "No local cache is available, ready to switch controller.");
            result.setAction(SWITCH_TO_OTHER_CONTROLER);
            return result;
         }
      }

      new Thread(new Runnable() {
         public void run() {
            ORControllerServerSwitcher.detectGroupMembers(activity);
         }
      }).start();
      return result;
   }

   @Override
   protected void onProgressUpdate(String... values) {
      RelativeLayout loadingView = (RelativeLayout) (activity.findViewById(R.id.welcome_view));
      ImageView globalLogo = (ImageView) (activity.findViewById(R.id.global_logo));
      globalLogo.setImageResource(R.drawable.global_logo);
      loadingView.setBackgroundResource(R.drawable.loading);
      
      TextView loadingText = (TextView)(activity.findViewById(R.id.loading_text));
      loadingText.setText("loading " + values[0] + "...");
      loadingText.setEllipsize(TruncateAt.MIDDLE);
      loadingText.setSingleLine(true);
   }

   @Override
   protected void onPostExecute(AsyncResourceLoaderResult result) {
      Intent intent = new Intent();
      switch (result.getAction()) {
      case TO_GROUP:
         intent.setClass(activity, GroupActivity.class);
         if (result.isCanUseLocalCache()) {
            intent.setData(Uri.parse(ControllerException.exceptionMessageOfCode(result.getStatusCode())));
         }
         break;
      case TO_LOGIN:
         intent.setClass(activity, LoginViewActivity.class);
         intent.setData(Uri.parse(Main.LOAD_RESOURCE));
         break;
      case SWITCH_TO_OTHER_CONTROLER:
         ORControllerServerSwitcher.doSwitch(activity);
         return;
      default:
         ViewHelper.showAlertViewWithTitle(activity, "Send Request Error", ControllerException.exceptionMessageOfCode(result.getStatusCode()));
         return;
      }
      activity.startActivity(intent);
      activity.finish();
   }

}

class AsyncResourceLoaderResult {
   private int action;
   private int statusCode;
   private boolean canUseLocalCache;

   public AsyncResourceLoaderResult() {
      action = -1;
      statusCode = -1;
      canUseLocalCache = false;
   }

   public int getAction() {
      return action;
   }

   public void setAction(int action) {
      this.action = action;
   }

   public int getStatusCode() {
      return statusCode;
   }

   public void setStatusCode(int statusCode) {
      this.statusCode = statusCode;
   }

   public boolean isCanUseLocalCache() {
      return canUseLocalCache;
   }

   public void setCanUseLocalCache(boolean canUseLocalCache) {
      this.canUseLocalCache = canUseLocalCache;
   }
}
