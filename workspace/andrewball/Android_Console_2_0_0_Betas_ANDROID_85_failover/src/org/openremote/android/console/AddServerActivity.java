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
package org.openremote.android.console;

import java.net.URI;
import java.net.URISyntaxException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.openremote.android.console.Constants;

import com.google.inject.Inject;

/**
 * This is the add custom server screen. Mainly it configures the URL of 
 * your device (which is assumed to be the root of controller.xml.
 * 
 * @author Andrew C. Oliver <acoliver at osintegrators.com>
 */
public class AddServerActivity extends GenericActivity {
    public static final String OPEN_REMOTE_PREFS = "openRemoteConfig";

    public static final String LOG_CATEGORY = Constants.LOG_CATEGORY + "AddServerActivity";
   
    //@Inject
   private DataHelper dh;
    // create a static DataHelper class
    // DataHelper dh=new DataHelper(this);

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.d(LOG_CATEGORY, "onCreate()");
        
        setContentView(R.layout.add_server);

        dh=new DataHelper(this);
        
        final EditText controllerUrlEditText = (EditText) findViewById(R.id.add_server_controller_url_edit_text); 

        Button saveButton = (Button) findViewById(R.id.add_server_save_button);

        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String noProtocolUrl = controllerUrlEditText.getText().toString();
                if (noProtocolUrl == null) {
                   return;
                }
                try {
                  String httpUrl = "http://" + noProtocolUrl;	//to check the db
                  
                  //find if the controller already exists, if so, show toast. else go on
                  
                  if(dh.find(httpUrl) > 0){
                	  Toast toast = Toast.makeText(getApplicationContext(), "Controller already exists.", 1);
                      toast.show();
                      return;
                  }
                  
                 
                  new URI(httpUrl);
                  Intent intent = getIntent();
                  intent.setData(Uri.parse(noProtocolUrl));
                  dh.closeConnection();
                  setResult(Constants.RESULT_CONTROLLER_URL, intent);
                  finish();
               } catch (URISyntaxException e) {
                  Toast toast = Toast.makeText(getApplicationContext(),e.getReason()+ "URL format is not correct.", 1);
                  toast.show();
               }
            }
        });
        
        Button cancelButton = (Button) findViewById(R.id.add_server_cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
            	dh.closeConnection();
              finish();
               }            
        });
        
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
