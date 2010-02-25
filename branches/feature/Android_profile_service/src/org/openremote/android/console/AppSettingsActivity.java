/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
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

import java.util.ArrayList;

import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.FileUtil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AppSettingsActivity extends Activity{

   private LinearLayout appSettingsView;
   private ListView customeListView;
   private Button choosePanelButton;
   private int currentCustomServerIndex = -1;
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setTitle(R.string.settings);
      ScrollView scroll = new ScrollView(this);
      scroll.setVerticalScrollBarEnabled(false);
      scroll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      
      appSettingsView = new LinearLayout(this);
      appSettingsView.setBackgroundColor(0);
      appSettingsView.setTag(R.string.settings);
      appSettingsView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
//      DisplayMetrics dm = new DisplayMetrics();
//      dm = getApplicationContext().getResources().getDisplayMetrics();
//      appSettingsView.setLayoutParams(new LinearLayout.LayoutParams(dm.widthPixels, dm.heightPixels));
      appSettingsView.setOrientation(LinearLayout.VERTICAL);
      
      RelativeLayout autoLayout = new RelativeLayout(this);
      autoLayout.setPadding(10, 5, 10, 10);
      autoLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80));
//      autoLayout.setOrientation(LinearLayout.HORIZONTAL);
      TextView autoText = new TextView(this);
      RelativeLayout.LayoutParams autoTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      autoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      autoTextLayout.addRule(RelativeLayout.CENTER_VERTICAL);
      autoText.setLayoutParams(autoTextLayout);
      autoText.setText("Auto Discovery");

      ToggleButton autoButton = new ToggleButton(this);
      autoButton.setWidth(100);
      RelativeLayout.LayoutParams autoButtonLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      autoButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      autoButtonLayout.addRule(RelativeLayout.CENTER_VERTICAL);
      autoButton.setLayoutParams(autoButtonLayout);
      boolean autoMode = AppSettingsModel.isAutoMode(AppSettingsActivity.this);
      autoButton.setChecked(autoMode);
      autoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               appSettingsView.removeViewAt(2);
               appSettingsView.addView(constructAutoServersView(), 2);
            } else {
               appSettingsView.removeViewAt(2);
               appSettingsView.addView(constructCustomeServersView(), 2);
            }
            AppSettingsModel.setAutoMode(AppSettingsActivity.this, isChecked);
         }
      });
      
      TextView infoText = new TextView(this);
      RelativeLayout.LayoutParams infoTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      infoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
      infoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
      infoText.setLayoutParams(infoTextLayout);
      infoText.setTextSize(10);
      infoText.setText("Turn off auto-discovery to input controller url manually.");
      
      autoLayout.addView(autoText);
      autoLayout.addView(autoButton);
      autoLayout.addView(infoText);

      TextView csText = new TextView(this);
      csText.setText("Choose Controller:");
      csText.setPadding(10, 5, 0, 5);
      csText.setBackgroundColor(Color.DKGRAY);
      
      TextView choosePanelInfo = new TextView(this);
      choosePanelInfo.setPadding(10, 10, 0, 5);
      choosePanelInfo.setText("Choose Panel Identity:");
      choosePanelInfo.setBackgroundColor(Color.DKGRAY);
      
      choosePanelButton = new Button(this);
//      choosePanelButton.setPadding(10, 10, 5, 20);
      choosePanelButton.setText("choose panel");
      String currentPanel = AppSettingsModel.getCurrentPanelIdentity(AppSettingsActivity.this);
      if (!TextUtils.isEmpty(currentPanel)) {
         choosePanelButton.setText(currentPanel);
      }
      choosePanelButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      choosePanelButton.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            String currentServer = AppSettingsModel.getCurrentServer(AppSettingsActivity.this);
            if (!TextUtils.isEmpty(currentServer)) {
               Intent intent = new Intent();
               intent.setData(Uri.parse(currentServer));
               intent.setClass(AppSettingsActivity.this, PanelSelectorActivity.class);
               startActivityForResult(intent, Constants.REQUEST_CODE);
            }
         }
      });
      
      LayoutInflater inflater = (AppSettingsActivity.this).getLayoutInflater();
      LinearLayout saveAndCancelLayout = (LinearLayout)inflater.inflate(R.layout.bottom_button_bar, null);
      
      appSettingsView.addView(autoLayout);
      appSettingsView.addView(csText);
      if (autoMode) {
         appSettingsView.addView(constructAutoServersView());
      } else {
         appSettingsView.addView(constructCustomeServersView());
      }
      appSettingsView.addView(choosePanelInfo);
      appSettingsView.addView(choosePanelButton);
      appSettingsView.addView(saveAndCancelLayout);
      scroll.addView(appSettingsView);
      
      setContentView(scroll);
      Button doneButton = (Button)findViewById(R.id.done);
      doneButton.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            String serverUrl = AppSettingsModel.getCurrentServer(AppSettingsActivity.this);
            String panelName = AppSettingsModel.getCurrentPanelIdentity(AppSettingsActivity.this);
            HTTPUtil.downLoadPanelXml(AppSettingsActivity.this, serverUrl, panelName);
            FileUtil.parsePanelXML(AppSettingsActivity.this);
            Intent intent = new Intent();
            intent.setClass(AppSettingsActivity.this, GroupHandler.class);
            startActivity(intent);
         }
      });
   }

   private void getCustomServersFromFile(ArrayList<String> customServers) {
      String storedUrls = AppSettingsModel.getCustomServers(this);
      if (! TextUtils.isEmpty(storedUrls)) {
         String[] data = storedUrls.split(",");
         int dataNum = data.length;
         for (int i = 0; i < dataNum; i++) {
            if(!data[i].startsWith("+")){
               customServers.add(data[i]);
            } else {
               currentCustomServerIndex = i;
               customServers.add(data[i].substring(1));
               AppSettingsModel.setCurrentServer(AppSettingsActivity.this, data[i].substring(1));
            }
         }
      }
   }
   
   private LinearLayout constructCustomeServersView() {
      LinearLayout custumeView = new LinearLayout(this);
      custumeView.setOrientation(LinearLayout.VERTICAL);
      custumeView.setPadding(20, 5, 5, 0);
      custumeView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
      
      ArrayList<String> customServers = new ArrayList<String>();
      getCustomServersFromFile(customServers);
      
      RelativeLayout buttonsView = new RelativeLayout(this);
      buttonsView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 80));
      Button addServer = new Button(this);
      addServer.setWidth(80);
      RelativeLayout.LayoutParams addServerLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      addServerLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
      addServer.setLayoutParams(addServerLayout);
      addServer.setText("Add");
      addServer.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(AppSettingsActivity.this, ConfigureActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
         }
         
      });
      Button deleteServer = new Button(this);
      deleteServer.setWidth(80);
      RelativeLayout.LayoutParams deleteServerLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      deleteServerLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      deleteServer.setLayoutParams(deleteServerLayout);
      deleteServer.setText("Delete");
      deleteServer.setOnClickListener(new OnClickListener() {
         @SuppressWarnings("unchecked")
         public void onClick(View v) {
            int checkedPosition = customeListView.getCheckedItemPosition();
            if (!(checkedPosition == ListView.INVALID_POSITION)) {
               customeListView.setItemChecked(checkedPosition, false);
               ((ArrayAdapter<String>)customeListView.getAdapter()).remove(customeListView.getItemAtPosition(checkedPosition).toString());
            }
         }
      });
      
      buttonsView.addView(addServer);
      buttonsView.addView(deleteServer);
      
      customeListView = new ListView(this);
      customeListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
      customeListView.setCacheColorHint(0);
      final ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
              customServers);
      customeListView.setAdapter(serverListAdapter);
      customeListView.setItemsCanFocus(true);
      customeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      if (currentCustomServerIndex != -1) {
         customeListView.setItemChecked(currentCustomServerIndex, true);
      }
      customeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, (String)parent.getItemAtPosition(position));
            writeCustomServerToFile();
         }
         
      });
      
      custumeView.addView(customeListView);
      custumeView.addView(buttonsView);
      return custumeView;
  }

   @SuppressWarnings("unchecked")
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (data != null) {
         String result = data.getDataString();
         if (Constants.REQUEST_CODE == requestCode && !TextUtils.isEmpty(result)) {
            if (Constants.RESULT_CONTROLLER_URL == resultCode) {
               ((ArrayAdapter<String>) customeListView.getAdapter()).add("http://" + result + "/controller");
               writeCustomServerToFile();
            } else if (Constants.RESULT_PANEL_SELECTED == resultCode) {
               choosePanelButton.setText(result);
            }
         }
      }
   }
  
   private ListView constructAutoServersView() {
      ListView lv = new ListView(this);
      lv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
      lv.setPadding(20, 5, 5, 10);
      lv.setBackgroundColor(0);
      lv.setCacheColorHint(0);
      lv.setItemsCanFocus(true);
      lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
            AppSettingsModel.getAutoServers());
      lv.setAdapter(serverListAdapter);
      if (serverListAdapter.getCount() > 0) {
         lv.setItemChecked(0, true);
         AppSettingsModel.setCurrentServer(AppSettingsActivity.this, serverListAdapter.getItem(0));
      }
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, (String)parent.getItemAtPosition(position));
         }
      });
      
      return lv;
   }

   /**
    * 
    */
   private void writeCustomServerToFile() {
      int customServerCount = customeListView.getCount();
      if (customServerCount > 0) {
         int checkedPosition = customeListView.getCheckedItemPosition();
         String customServerUrls = "";
         for (int i = 0; i < customServerCount; i++) {
            if (i != checkedPosition) {
               customServerUrls = customServerUrls + customeListView.getItemAtPosition(i).toString() + ",";
            } else {
               customServerUrls = customServerUrls + "+" + customeListView.getItemAtPosition(i).toString() + ",";
            }
         }
         if (!TextUtils.isEmpty(customServerUrls)) {
            AppSettingsModel.setCustomServers(customeListView.getContext(), customServerUrls);
         }
      }
   }
   
   public int sum() {
      return 2+5;
   }

}
