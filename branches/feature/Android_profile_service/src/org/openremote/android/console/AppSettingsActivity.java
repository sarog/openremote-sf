package org.openremote.android.console;

import java.util.ArrayList;
import java.util.List;

import org.openremote.android.console.util.FileUtil;
import org.openremote.android.console.util.IPAutoDiscoveryUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
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
   private ListView autoListView;
   private int currentId = -1;
   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      appSettingsView = new LinearLayout(this);
      appSettingsView.setBackgroundColor(0);
      appSettingsView.setTag(R.string.settings);
      appSettingsView.setLayoutParams(new LinearLayout.LayoutParams(320, 480));
      appSettingsView.setOrientation(LinearLayout.VERTICAL);
      
      LinearLayout autoLayout = new LinearLayout(this);
      autoLayout.setOrientation(LinearLayout.HORIZONTAL);
      TextView autoText = new TextView(this);
      autoText.setText("AutoDiscovery:");
      ToggleButton autoButton = new ToggleButton(this);
      autoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
               setTitle("checked");
               appSettingsView.removeViewAt(2);
               appSettingsView.addView(constructAutoServersView(), 2);
            } else {
               setTitle("not checked");
               appSettingsView.removeViewAt(2);
               appSettingsView.addView(constructCustomeServersView(), 2);
               
            }
         }
      });
      autoLayout.addView(autoText);
      autoLayout.addView(autoButton);
      
      TextView csText = new TextView(this);
      csText.setText("Choose server:");
      
      appSettingsView.addView(autoLayout);
      appSettingsView.addView(csText);
      appSettingsView.addView(constructCustomeServersView());
      setContentView(appSettingsView);
   }

   private void getCustomServersFromFile(ArrayList<String> customServers) {
      String storedUrls = FileUtil.ReadSettings(this);
      if (! TextUtils.isEmpty(storedUrls)) {
         String[] data = storedUrls.split(",");
         int dataNum = data.length;
         for (int i = 0; i < dataNum; i++) {
            if(!data[i].startsWith("+")){
               customServers.add(data[i]);
            } else {
               currentId = i;
               customServers.add(data[i].substring(1));
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
      Button saveServers = new Button(this);
      saveServers.setText("Save");
      saveServers.setOnClickListener(new OnClickListener() {
         public void onClick(View v) {
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
                  FileUtil.WriteSettings(customeListView.getContext(), customServerUrls);
               }
            }
         }
      });
      
      buttonsView.addView(addServer);
      buttonsView.addView(deleteServer);
      buttonsView.addView(saveServers);
      
      customeListView = new ListView(this);
      customeListView.setBackgroundColor(0);
      customeListView.setCacheColorHint(0);
      ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
              customServers);
      customeListView.setAdapter(serverListAdapter);
      customeListView.setItemsCanFocus(true);
      customeListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      if (currentId != -1) {
         customeListView.setItemChecked(currentId, true);
      }
      
      custumeView.addView(customeListView);
      custumeView.addView(buttonsView);
      return custumeView;
  }

   @SuppressWarnings("unchecked")
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if(Constants.REQUEST_CODE == requestCode && Constants.RESULT_CODE == resultCode) {
         String result = data.getDataString();
         if (!TextUtils.isEmpty(result)) {
            ((ArrayAdapter<String>)customeListView.getAdapter()).add("http://" + result + "/controller");
         }
      }
   }
  
   private ListView constructAutoServersView() {
      ListView lv = new ListView(this);
      lv.setBackgroundColor(0);
      lv.setCacheColorHint(0);
      ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
            IPAutoDiscoveryUtil.getAutoDiscoveryIPs());
      lv.setAdapter(serverListAdapter);
      lv.setItemsCanFocus(true);
      // TODO store the first item.
      if (serverListAdapter.getCount() > 0) {
         lv.setItemChecked(0, true);
      }
      lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
      
      return lv;
   }
   
}
