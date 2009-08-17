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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.openremote.android.console.bindings.Button;
import org.openremote.android.console.bindings.ORActivity;
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.image.ImageLoader;
import org.openremote.android.xml.SimpleBinder;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private List<ORActivity> activities;
    private String ip;
    public static ImageLoader imageLoader;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader();
        ArrayList<String> activityNames = new ArrayList<String>(); // used
        // solely for
        // making our
        // list view
        activities = new ArrayList<ORActivity>();
        this.ip = getIp();
        if (!TextUtils.isEmpty(ip)) {
            String error = null;
            try {
                parseXML(ip + "/iphone.xml", activityNames, activities);
                startLoadingImages();
            } catch (IllegalArgumentException e) {
                Log.e(this.toString(), "Error in " + ip
                        + "/iphone.xml syntax (most likely binding)", e);
                error = getApplication().getString(R.string.config_file_at)
                        + ip + "/iphone.xml "
                        + getApplication().getString(R.string.has_an_error);
                doSettings(error);
            } catch (ParserConfigurationException e) {
                Log.e(this.toString(), "Error in " + ip
                        + "/iphone.xml syntax (most likely bad code)", e);
                error = getApplication().getString(R.string.config_file_at)
                        + ip + "/iphone.xml "
                        + getApplication().getString(R.string.has_an_error);
                doSettings(error);
            } catch (SAXException e) {
                Log
                        .e(
                                this.toString(),
                                "Error in "
                                        + ip
                                        + "/iphone.xml syntax (most likely invalid conformance or non-wellformed)",
                                e);
                error = getApplication().getString(R.string.config_file_at)
                        + ip + "/iphone.xml "
                        + getApplication().getString(R.string.has_an_error);
                doSettings(error);
            } catch (IOException e) {
                Log.e(this.toString(), "Error connecting to " + ip
                        + " probably bad URL", e);
                error = getApplication()
                        .getString(R.string.error_connecting_to)
                        + ip + "/iphone.xml";
                doSettings(error);
            } catch (IllegalAccessException e) {
                Log.e(this.toString(), "Error in " + ip
                        + "/iphone.xml probably bad XML bound to wrong stuff",
                        e);
                error = getApplication().getString(R.string.config_file_at)
                        + ip + "/iphone.xml "
                        + getApplication().getString(R.string.has_an_error);
                doSettings(error);
            } catch (InstantiationException e) {
                Log.e(this.toString(), "Error in " + ip
                        + "/iphone.xml probably bad XML bound to wrong stuff",
                        e);
                error = getApplication().getString(R.string.config_file_at)
                        + ip + "/iphone.xml "
                        + getApplication().getString(R.string.has_an_error);
                doSettings(error);
            } catch (InvocationTargetException e) {
                Log.e(this.toString(), "Error in " + ip
                        + "/iphone.xml probably bad XML bound to wrong stuff",
                        e);
                error = getApplication().getString(R.string.config_file_at)
                        + ip + "/iphone.xml "
                        + getApplication().getString(R.string.has_an_error);
                doSettings(error);
            }

            // Set main.XML as the layout for this Activity
            activitiesListView = new LinearLayout(this);
            activitiesListView.setBackgroundColor(0);
            activitiesListView.setTag(R.string.activities);
            activitiesListView.setLayoutParams(new LinearLayout.LayoutParams(
                    320, 480));
            ListView lv = constructListView(activityNames);
            lv.setCacheColorHint(0);
            lv.setBackgroundColor(0);

            activitiesListView.addView(lv);
            this.setContentView(activitiesListView);

            // add click listener to the list
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                        int pos, long id) {
                    Log.d(this.toString(), "onClick");
                    String selected = (String) parent.getItemAtPosition(pos);
                    for (ORActivity activity : activities) {
                        if (selected.equals(activity.getName())) {
                            Intent intent = new Intent();
                            intent
                                    .setClassName(this.getClass().getPackage()
                                            .getName(), ActivityHandler.class
                                            .getName());
                            intent.putExtra(Constants.ACTIVITY, activity);
                            startActivity(intent);
                        }
                    }
                }

            });

        } else {
            doSettings(null);
        }
    }

    private void startLoadingImages() {
        for (ORActivity activity : activities) {
            List<Screen> screens = activity.getScreens();
            for (Screen screen : screens) {
                List<Button> buttons = screen.getButtons();
                for (Button button : buttons) {
                    if (button.getIcon() != null) {
                        imageLoader
                                .load(this.ip + "/" + button.getIcon(), null);
                    }
                }
            }
        }
        imageLoader.start();
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

    private String getIp() {
        SharedPreferences settings = getSharedPreferences(
                ConfigureActivity.OPEN_REMOTE_PREFS, 0);
        String val = settings.getString(Constants.URL, "");
        return val;
    }

    /**
     * Parse the xml file. A urlString and an initialized but empty list of
     * activityNames and activities are passed in. The method will fill
     * activityNames and activities with the names of the activities in the file
     * (so we can construct the list elegantly) and instances of the ORActivity
     * as bound by SimpleBinder (again from the XML file). Various IO, XML,
     * reflection type exception are thrown if there is a problem with the XML
     * file or the classes in org.openremote.android.console.bindings.
     * 
     * @param urlString
     * @param activityNames
     * @param activities
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws InvocationTargetException
     */
    @SuppressWarnings("unchecked")
    private void parseXML(String urlString, List<String> activityNames,
            List<ORActivity> activities) throws ParserConfigurationException,
            SAXException, IOException, IllegalArgumentException,
            IllegalAccessException, InstantiationException,
            InvocationTargetException {
        URL url = new URL(urlString);
        List<Class> classes = new ArrayList<Class>();
        classes.add(org.openremote.android.console.bindings.Activity.class);
        List<String> ignoreNodes = new ArrayList<String>();
        ignoreNodes.add(Constants.ELEMENT_BUTTONS); // because we don't want to
        // have a Buttons class, we
        // "ignore" it in the node
        // tree
        SimpleBinder b = new SimpleBinder(Constants.ELEMENT_OPENREMOTE,
                classes, ignoreNodes);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        InputStream stream = conn.getInputStream();
        List objs = b.bindStuff(stream);

        for (Object object : objs) {
            org.openremote.android.console.bindings.Activity act = (org.openremote.android.console.bindings.Activity) object;
            activityNames.add(act.getName());
        }

        activities.addAll((List<ORActivity>) objs); // in order to have a typed
        // list and two return
        // values we have to do this
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