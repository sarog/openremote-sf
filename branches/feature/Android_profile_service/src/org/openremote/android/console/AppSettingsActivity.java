package org.openremote.android.console;

import java.util.ArrayList;

import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.util.FileUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
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
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class AppSettingsActivity extends Activity {

   private LinearLayout appSettingsView;
   private ListView customeListView;
   private Button choosePanelButton;
   private int currentCustomServerIndex = -1;
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setTitle(R.string.settings);
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
      
      TextView choosePanelInfo = new TextView(this);
      choosePanelInfo.setPadding(10, 10, 0, 5);
      choosePanelInfo.setText("Choose Panel Identity:");
      
      choosePanelButton = new Button(this);
      choosePanelButton.setPadding(10, 10, 5, 5);
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
      
      RelativeLayout saveAndCancelLayout = new RelativeLayout(this);
      saveAndCancelLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80));
      saveAndCancelLayout.setPadding(20, 10, 10, 5);
      
      Button saveButton = new Button(this);
      saveButton.setWidth(80);
      RelativeLayout.LayoutParams saveButtonLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      saveButtonLayout.addRule(RelativeLayout.CENTER_HORIZONTAL);
      saveButtonLayout.addRule(RelativeLayout.CENTER_VERTICAL);
      saveButton.setLayoutParams(saveButtonLayout);
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
      cancelButton.setWidth(80);
      RelativeLayout.LayoutParams cancelButtonLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
      cancelButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
      cancelButtonLayout.addRule(RelativeLayout.CENTER_VERTICAL);
      cancelButton.setLayoutParams(cancelButtonLayout);
      cancelButton.setText(R.string.cancel);
      
      saveAndCancelLayout.addView(saveButton);
      saveAndCancelLayout.addView(cancelButton);
      
      appSettingsView.addView(autoLayout);
//      appSettingsView.addView(infoText);
      appSettingsView.addView(csText);
      if (autoMode) {
         appSettingsView.addView(constructAutoServersView());
      } else {
         appSettingsView.addView(constructCustomeServersView());
      }
      appSettingsView.addView(choosePanelInfo);
      appSettingsView.addView(choosePanelButton);
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
      custumeView.setPadding(50, 5, 5, 0);
      
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
      customeListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 80));
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
               choosePanelButton.setText(result);
            }
         }
      }
   }
  
   private ListView constructAutoServersView() {
      ListView lv = new ListView(this);
      lv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 100));
      lv.setPadding(50, 5, 5, 10);
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
