/* OpenRemote, the Home of the Digital Home.
 * Copyright 2009, OpenRemote Inc.
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
package org.openremote.android.console;

import java.util.Iterator;
import java.util.List;

import org.openremote.android.console.bindings.XScreen;
import org.openremote.android.console.image.ImageLoader;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.UserCache;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.model.XMLEntityDataBase;
import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.net.ORControllerServerSwitcher;
import org.openremote.android.console.net.ORNetworkCheck;
import org.openremote.android.console.util.FileUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This class represents the main OpenRemote activity. It starts up, reads the
 * xml file, and displays the list of activities. If the user clicks an
 * activity, it is launched via an intent to the ActivityHandler class.
 * 
 * The XML file is parsed using SimpleBinder which returns a list of
 * org.openremote.android.console.bindings.ORActivity instances which in turn
 * contain the Screen and Button instances. The actual configuration of Screens
 * and such is handled by ActivityHandler.
 * 
 * Note that ORActivity, ORButton, ORScreen are convenience interfaces which are
 * used solely for not conflicting with the Android classes for the same name.
 * 
 * @author Andrew C. Oliver <acoliver at osintegrators.com>
 */
public class Main extends Activity {

    LinearLayout activitiesListView;
    private TextView loadingText;
    private RelativeLayout loadingView;
    public static ImageLoader imageLoader;
    private static final int TO_LOGIN = 0xF00A;
    private static final int TO_SETTING = 0xF00B;
    private static final int TO_GROUP = 0xF00C;
    private static final int SWITCH_TO_OTHER_CONTROLER = 0xF00D;
    public static final String LOAD_RESOURCE = "loadResource";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.welcome_view);
        loadingText = (TextView)findViewById(R.id.loading_text);
        new AsyncResourceLoader().execute((Void) null);
    }

    private void doSettings(String error) {
        Intent i = new Intent();
        i.setClassName(this.getClass().getPackage().getName(),
                ConfigureActivity.class.getName());
        if (TextUtils.isEmpty(error))
            ;
        i.putExtra(Constants.ERROR, error);
        startActivity(i);
    }

    /**
     * Constructs the ListView which is the main display element for the Main
     * activity. This is just a list of the activities. The activityNames
     * parameter is a string list of ORActivity.getName() constructed by
     * parseXML.
     * 
     * @param activityNames
     * @return
     */
    public ListView constructListView(List<String> activityNames) {
        ListView lv = new ListView(this);
        lv.setBackgroundColor(0);
        lv.setCacheColorHint(0);
        ArrayAdapter<String> aa = new ArrayAdapter<String>(activitiesListView
                .getContext(), android.R.layout.simple_list_item_1,
                activityNames);

        lv.setAdapter(aa);
        lv.setItemsCanFocus(false);
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        return lv;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Main.populateMenu(menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        handleMenu(item);
        return true;
    }

    public void handleMenu(MenuItem item) {
        switch (item.getItemId()) {
        case Constants.MENU_ITEM_CONFIG:
            doSettings(null);
            break;
        case Constants.MENU_ITEM_QUIT:
            System.exit(0);
            break;
        }
    }

    public static void populateMenu(Menu menu) {
        menu.setQwertyMode(true);
        MenuItem configItem = menu.add(0, Constants.MENU_ITEM_CONFIG, 0,
                R.string.configure);
        configItem.setIcon(R.drawable.ic_menu_manage);
        MenuItem quit = menu.add(0, Constants.MENU_ITEM_QUIT, 0, R.string.quit);
        quit.setIcon(R.drawable.ic_menu_close_clear_cancel);
    }

    private void checkNetType() {
       ConnectivityManager conn = (ConnectivityManager)(Main.this).getSystemService(Context.CONNECTIVITY_SERVICE);
       if ("mobile".equals(conn.getActiveNetworkInfo().getTypeName().toLowerCase())) {
          IPAutoDiscoveryClient.IS_EMULATOR = true;
       }
    }
    
    private void readDisplayMetrics() {
      DisplayMetrics dm = new DisplayMetrics();
      dm = getApplicationContext().getResources().getDisplayMetrics();
      XScreen.SCREEN_WIDTH = dm.widthPixels;
      XScreen.SCREEN_HEIGHT = dm.heightPixels;
    }
    
    private class AsyncResourceLoader extends AsyncTask<Void, String, Integer> {
      @Override
      protected Integer doInBackground(Void... params) {
         checkNetType();
         readDisplayMetrics();

         if (TextUtils.isEmpty(UserCache.getUsername(Main.this)) || TextUtils.isEmpty(UserCache.getPassword(Main.this))) {
            return TO_LOGIN;
         }
         
         String serverUrl = AppSettingsModel.getCurrentServer(Main.this);
         String panelName = AppSettingsModel.getCurrentPanelIdentity(Main.this);
         if (TextUtils.isEmpty(serverUrl) || TextUtils.isEmpty(panelName)) {
            return TO_SETTING;
         }

         boolean isControllerAvailable = false;
         publishProgress(panelName);
         if (ORNetworkCheck.checkControllerAvailable(Main.this)) {
            isControllerAvailable = true;
            if (ORNetworkCheck.checkPanelXMlOfCurrentPanelIdentity(Main.this)) {
               int statusCode = HTTPUtil.downLoadPanelXml(Main.this, serverUrl, panelName);
               if (statusCode != 200 && statusCode != 0) {
                  return statusCode;
               }
            }
         }
         
         if (Main.this.getFileStreamPath(Constants.PANEL_XML).exists()) {
            FileUtil.parsePanelXML(Main.this);
         } else {
            return SWITCH_TO_OTHER_CONTROLER;
         }
         
         Iterator<String> images = XMLEntityDataBase.imageSet.iterator();
         if (isControllerAvailable) {
            String imageName = "";
            while (images.hasNext()) {
               imageName = images.next();
               publishProgress(imageName);
               HTTPUtil.downLoadImage(Main.this, AppSettingsModel.getCurrentServer(Main.this), imageName);
            }
         }
         
         new Thread(new Runnable() {
            public void run() {
               ORControllerServerSwitcher.detectGroupMembers(Main.this);
            }
         }).start(); 
         return TO_GROUP;
      }

      @Override
      protected void onProgressUpdate(String... values) {
         if (loadingView == null) {
            loadingView = (RelativeLayout) findViewById(R.id.welcome_view);
            ImageView globalLogo = (ImageView) findViewById(R.id.global_logo);
            globalLogo.setImageResource(R.drawable.global_logo);
            loadingView.setBackgroundResource(R.drawable.loading);
         }
         loadingText.setText("loading..." + values[0]);
      }

      @Override
      protected void onPostExecute(Integer result) {
         Intent intent = new Intent();
         switch (result) {
         case TO_LOGIN:
            intent.setClass(Main.this, LoginViewActivity.class);
            intent.setData(Uri.parse(LOAD_RESOURCE));
            break;
         case TO_SETTING:
            intent.setClass(Main.this, AppSettingsActivity.class);
            break;
         case TO_GROUP:
            intent.setClass(Main.this, GroupHandler.class);
            break;
         case SWITCH_TO_OTHER_CONTROLER:
            ORControllerServerSwitcher.doSwitch(Main.this);
            return;

         default:
            ViewHelper.showAlertViewWithTitle(Main.this, "Send Request Error", ControllerException
                  .exceptionMessageOfCode(result));
            return;
         }
         startActivity(intent);
         finish();
      }

   }
   
}