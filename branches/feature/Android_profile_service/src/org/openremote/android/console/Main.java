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

import java.util.List;

import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.image.ImageLoader;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.UserCache;
import org.openremote.android.console.net.IPAutoDiscoveryClient;
import org.openremote.android.console.util.AsyncResourceLoader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

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
    public static ImageLoader imageLoader;
    public static final String LOAD_RESOURCE = "loadResource";
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setContentView(R.layout.welcome_view);
        
        checkNetType();
        readDisplayMetrics();        
        if(!toLogginOrSetting()) {        
           new AsyncResourceLoader(this).execute((Void) null);
        }
    }
    
    private void checkNetType() {
       ConnectivityManager conn = (ConnectivityManager)(this).getSystemService(Context.CONNECTIVITY_SERVICE);
       if ("mobile".equals(conn.getActiveNetworkInfo().getTypeName().toLowerCase())) {
          IPAutoDiscoveryClient.IS_EMULATOR = true;
       }
    }
    
    private void readDisplayMetrics() {
      DisplayMetrics dm = new DisplayMetrics();
      dm = getApplicationContext().getResources().getDisplayMetrics();
      Screen.SCREEN_WIDTH = dm.widthPixels;
      Screen.SCREEN_HEIGHT = dm.heightPixels;
    }
    
    private boolean toLogginOrSetting () {
       Intent intent = new Intent();
       if (TextUtils.isEmpty(UserCache.getUsername(this)) || TextUtils.isEmpty(UserCache.getPassword(this))) {
          intent.setClass(this, LoginViewActivity.class);
          intent.setData(Uri.parse(LOAD_RESOURCE));
       } else if (TextUtils.isEmpty(AppSettingsModel.getCurrentServer(this)) || TextUtils.isEmpty(AppSettingsModel.getCurrentPanelIdentity(this))) {
          intent.setClass(this, AppSettingsActivity.class);
       } else {
          return false;
       }
       startActivity(intent);
       finish();
       return true;
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
   
}