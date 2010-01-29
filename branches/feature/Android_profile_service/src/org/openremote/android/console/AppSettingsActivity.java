package org.openremote.android.console;

import java.util.ArrayList;

import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.FileUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AppSettingsActivity extends Activity {

   private LinearLayout appSettingsView;
   private ListView customeListView;
   private Button choosePanel;
   private int currentCustomServerIndex = -1;
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setTitle(R.string.settings);
      appSettingsView = new LinearLayout(this);
      appSettingsView.setBackgroundColor(0);
      appSettingsView.setTag(R.string.settings);
      appSettingsView.setLayoutParams(new LinearLayout.LayoutParams(320, 480));
      appSettingsView.setOrientation(LinearLayout.VERTICAL);
      
      LinearLayout autoLayout = new LinearLayout(this);
      autoLayout.setOrientation(LinearLayout.HORIZONTAL);
      TextView autoText = new TextView(this);
      autoText.setText("Auto Discovery");
      ToggleButton autoButton = new ToggleButton(this);
      boolean autoMode = AppSettingsModel.isAutoMode(AppSettingsActivity.this);
      autoButton.setChecked(autoMode);
      autoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               appSettingsView.removeViewAt(3);
               appSettingsView.addView(constructAutoServersView(), 3);
            } else {
               appSettingsView.removeViewAt(3);
               appSettingsView.addView(constructCustomeServersView(), 3);
            }
            AppSettingsModel.setAutoMode(AppSettingsActivity.this, isChecked);
         }
      });
      autoLayout.addView(autoText);
      autoLayout.addView(autoButton);
      
      TextView infoText = new TextView(this);
      infoText.setTextSize(10);
      infoText.setText("Turn off auto-discovery to input controller url manually.");
      
      TextView csText = new TextView(this);
      csText.setText("Choose Controller:");
      
      TextView choosePanelInfo = new TextView(this);
      choosePanelInfo.setText("Choose Panel Identity:");
      
      choosePanel = new Button(this);
      choosePanel.setText("choose panel");
      String currentPanel = AppSettingsModel.getCurrentPanelIdentity(AppSettingsActivity.this);
      if (!TextUtils.isEmpty(currentPanel)) {
         choosePanel.setText(currentPanel);
      }
      choosePanel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
      choosePanel.setOnClickListener(new OnClickListener() {
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
      
      LinearLayout saveAndCancelLayout = new LinearLayout(this);
      saveAndCancelLayout.setOrientation(LinearLayout.HORIZONTAL);
      Button saveButton = new Button(this);
      saveButton.setText(R.string.done);
      saveButton.setOnClickListener(new OnClickListener() {
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
      
      Button cancelButton = new Button(this);
      cancelButton.setText(R.string.cancel);
      
      saveAndCancelLayout.addView(saveButton);
      saveAndCancelLayout.addView(cancelButton);
      
      appSettingsView.addView(autoLayout);
      appSettingsView.addView(infoText);
      appSettingsView.addView(csText);
      if (autoMode) {
         appSettingsView.addView(constructAutoServersView());
      } else {
         appSettingsView.addView(constructCustomeServersView());
      }
      appSettingsView.addView(choosePanelInfo);
      appSettingsView.addView(choosePanel);
      appSettingsView.addView(saveAndCancelLayout);
      
      setContentView(appSettingsView);
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
      
      ArrayList<String> customServers = new ArrayList<String>();
      getCustomServersFromFile(customServers);
      
      LinearLayout buttonsView = new LinearLayout(this);
      Button addServer = new Button(this);
      addServer.setText("Add");
      addServer.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
            Intent intent = new Intent();
            intent.setClass(AppSettingsActivity.this, ConfigureActivity.class);
            startActivityForResult(intent, Constants.REQUEST_CODE);
         }
         
      });
      Button deleteServer = new Button(this);
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
      customeListView.setCacheColorHint(0);
      ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
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
               choosePanel.setText(result);
            }
         }
      }
   }
  
   private ListView constructAutoServersView() {
      ListView lv = new ListView(this);
      lv.setBackgroundColor(0);
      lv.setCacheColorHint(0);
      ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
            AppSettingsModel.getAutoServers());
      lv.setAdapter(serverListAdapter);
      lv.setItemsCanFocus(true);
      if (serverListAdapter.getCount() > 0) {
         lv.setItemChecked(0, true);
         AppSettingsModel.setCurrentServer(AppSettingsActivity.this, serverListAdapter.getItem(0));
      }
      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
         public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, (String)parent.getItemAtPosition(position));
         }
      });
      lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      
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
