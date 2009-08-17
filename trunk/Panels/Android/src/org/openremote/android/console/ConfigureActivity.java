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

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This is the "settings" screen. It also appears if you have a communication
 * error of some kind. Mainly it configures the URL of your device (which is
 * assumed to be the root of iphone.xml.
 * 
 * @author Andrew C. Oliver <acoliver at osintegrators.com>
 */
public class ConfigureActivity extends Activity {
    public static final String OPEN_REMOTE_PREFS = "openRemoteConfig";

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        Log.d(this.toString(), "onCreate for configure activity");
        String url = getUrl();
        String error = getIntent().getExtras().getString(Constants.ERROR);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(new LinearLayout.LayoutParams(320, 480));
        if (!TextUtils.isEmpty(error)) {
            TextView tv = new TextView(this);
            tv.setText(error);
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tv.setTextColor(Color.RED);
            tv.setTypeface(Typeface.DEFAULT_BOLD);
            tv.setTextSize(18);
            layout.addView(tv);
        }

        TextView tv = new TextView(this);
        tv.setText(R.string.enter_device_url);
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setTextSize(18);
        tv.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        EditText et = new EditText(this);
        et.setTag(Constants.URL);
        if (!TextUtils.isEmpty(url)) {
            et.setText(url);
        }
        Button b = new Button(this);
        b.setText(R.string.save);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = ((EditText) ((View) v.getParent())
                        .findViewWithTag(Constants.URL)).getText().toString();
                SharedPreferences prefs = getSharedPreferences(
                        OPEN_REMOTE_PREFS, 0);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(Constants.URL, url);
                editor.commit();
                System.exit(0);
            }
        });
        layout.addView(tv);
        layout.addView(et);
        layout.addView(b);
        this.setContentView(layout);
    }

    private String getUrl() {
        SharedPreferences settings = getSharedPreferences(
                ConfigureActivity.OPEN_REMOTE_PREFS, 0);
        String val = settings.getString(Constants.URL, "");
        return val;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        return handleMenu(item);
    }

    public boolean handleMenu(MenuItem item) {
        switch (item.getItemId()) {
        case Constants.MENU_ITEM_QUIT:
            System.exit(0);
            return true;
        }
        return false;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        populateMenu(menu);
        return true;
    }

    public void populateMenu(Menu menu) {
        menu.setQwertyMode(true);
        MenuItem quit = menu.add(0, Constants.MENU_ITEM_QUIT, 0, R.string.back);
        quit.setIcon(R.drawable.ic_menu_revert);
    }

}
