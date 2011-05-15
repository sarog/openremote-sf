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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.net.IPAutoDiscoveryServer;
import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORConnectionDelegate;
import org.openremote.android.console.net.ORHttpMethod;
import org.openremote.android.console.util.FileUtil;
import org.openremote.android.console.util.StringUtil;
import org.openremote.android.console.view.PanelSelectSpinnerView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * Application global settings view.
 * It is for configuring controller server, panel identity and security.
 * It also can delete image caches.
 * 
 * @author Tomsky Wang
 * @author Dan Cong
 *
 */

public class AppSettingsActivity extends GenericActivity implements ORConnectionDelegate {
  public static final String TAG = Constants.LOG_CATEGORY + "AppSettingsActivity";

  /** The app settings view contains auto discovery, auto servers, custom servers,
   * select panel identity, clear image cache and security configuration. 
   */
  private LinearLayout appSettingsView;
  
  /** The custom list view contains custom server list. */
  private ListView customListView;
  private int currentCustomServerIndex = -1;
  
  /** The auto mode is to indicate if auto discovery servers. */
  private boolean autoMode;
  public static String currentServer = "";
  
  /** The view for selecting panel identity. */
  private PanelSelectSpinnerView panelSelectSpinnerView;
  
  /** The progress layout display auto discovery progress. */
  private LinearLayout progressLayout;
  
  private ListView autoServersListView;
  /** ListAdapter for automatically detected controllers */
  private ArrayAdapter<String> autoServerListAdapter;
  private LinearLayout customServersLayout;
  
  private ProgressDialog loadingPanelProgress;
  
  private IPAutoDiscoveryServer autoDiscoveryServer;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setTitle(R.string.settings);
    
    this.autoMode = AppSettingsModel.isAutoMode(AppSettingsActivity.this);
    
    setContentView(R.layout.app_settings);
    
    autoServersListView = (ListView) findViewById(R.id.auto_servers_list_view);
    customServersLayout = (LinearLayout) findViewById(R.id.custom_servers_layout);
    
    loadingPanelProgress = new ProgressDialog(this);
    
    ScrollView scrollView = (ScrollView) findViewById(R.id.settingsScrollView);
    scrollView.setVerticalScrollBarEnabled(true);
    
    appSettingsView = (LinearLayout) findViewById(R.id.appSettingsView);
    
    createAutoLayout();
    
    currentServer = "";
    
    constructAutoServersView();
    constructCustomServersView();
    if (autoMode) {
      startControllerAutoDiscovery();
    } else {
      switchToCustomServersView();
    }
    
    panelSelectSpinnerView = (PanelSelectSpinnerView) findViewById(R.id.panel_select_spinner_view);
    
    createClearImageCacheButton();

    initSSLState();
    addOnclickListenerOnDoneButton();
    addOnclickListenerOnCancelButton();
    progressLayout = (LinearLayout) findViewById(R.id.choose_controller_progress);
  }
  
  /**
   * Switches what gets displayed in the controller selection frame layout to auto-discovered controllers.
   */
  private void switchToAutoServersView() {
    autoServersListView.setVisibility(View.VISIBLE);
    customServersLayout.setVisibility(View.GONE);
  }
  
  /**
   * Switches what gets display in the controller selection frame layout to manually specified controllers.
   */
  private void switchToCustomServersView() {
    autoServersListView.setVisibility(View.GONE);
    customServersLayout.setVisibility(View.VISIBLE);
  }

  /**
   * Initializes the SSL related UI widget properties and event handlers to deal with user
   * interactions.
   */
  private void initSSLState()
  {
    // Get UI Widget references...

    final ToggleButton sslToggleButton = (ToggleButton)findViewById(R.id.ssl_toggle);
    final EditText sslPortEditField = (EditText)findViewById(R.id.ssl_port);

    // Configure UI to current settings state...

    boolean sslEnabled = AppSettingsModel.isSSLEnabled(this);

    sslToggleButton.setChecked(sslEnabled);
    sslPortEditField.setText("" + AppSettingsModel.getSSLPort(this));

    // If SSL is off, disable the port edit field by default...

    if (!sslEnabled)
    {
      sslPortEditField.setEnabled(false);
      sslPortEditField.setFocusable(false);
      sslPortEditField.setFocusableInTouchMode(false);
    }

    // Manage state changes to SSL toggle...

    sslToggleButton.setOnCheckedChangeListener(
        new OnCheckedChangeListener()
        {
          public void onCheckedChanged(CompoundButton buttonView, boolean isEnabled)
          {

            // If SSL is being disabled, and the user had soft keyboard open, close it...

            if (!isEnabled)
            {
              InputMethodManager input = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
              input.hideSoftInputFromWindow(sslPortEditField.getWindowToken(), 0);
            }

            // Set SSL state in config model accordingly...

            AppSettingsModel.enableSSL(AppSettingsActivity.this, isEnabled);

            // Enable/Disable SSL Port text field according to SSL toggle on/off state...

            sslPortEditField.setEnabled(isEnabled);
            sslPortEditField.setFocusable(isEnabled);
            sslPortEditField.setFocusableInTouchMode(isEnabled);
          }
        }
    );


    // ...


    sslPortEditField.setOnKeyListener(new OnKeyListener()
    {
      public boolean onKey(View v, int keyCode, KeyEvent event)
      {
        if (keyCode == KeyEvent.KEYCODE_ENTER)
        {
          String sslPortStr = ((EditText)v).getText().toString();

          try
          {
            int sslPort = Integer.parseInt(sslPortStr.trim());
            AppSettingsModel.setSSLPort(AppSettingsActivity.this, sslPort);
          }

          catch (NumberFormatException ex)
          {
            Toast toast = Toast.makeText(getApplicationContext(), "SSL port format is not correct.", 1);
            toast.show();

            return false;
          }

          catch (IllegalArgumentException e)
          {
            Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), 2);
            toast.show();

            sslPortEditField.setText("" + AppSettingsModel.getSSLPort(AppSettingsActivity.this));
             
            return false;
          }
        }

        return false;
      }

    });
    
  }




  /**
   * Adds the onclick listener on done button.
   *
   * If success, start Main activity and reload the application.
   */
  private void addOnclickListenerOnDoneButton() {
    Button doneButton = (Button)findViewById(R.id.setting_done);
    doneButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        if ("".equals(currentServer)) {
          ViewHelper.showAlertViewWithTitle(AppSettingsActivity.this, "Warning",
              "No controller. Please configure Controller URL manually.");
          return;
        }
        String selectedPanel = (String)panelSelectSpinnerView.getSelectedItem();
        if (!TextUtils.isEmpty(selectedPanel) && !selectedPanel.equals(PanelSelectSpinnerView.CHOOSE_PANEL)) {
          AppSettingsModel.setCurrentPanelIdentity(AppSettingsActivity.this, selectedPanel);
        } else {
          ViewHelper.showAlertViewWithTitle(AppSettingsActivity.this, "Warning",
              "No Panel. Please configure Panel Identity manually.");
          return;
        }
        Intent intent = new Intent();
        intent.setClass(AppSettingsActivity.this, Main.class);
        startActivity(intent);
        finish();
      }
    });
  }
  
  /**
   * Finish the settings activity.
   */
  private void addOnclickListenerOnCancelButton() {
    Button cancelButton = (Button)findViewById(R.id.setting_cancel);
    cancelButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        finish();
      }
    });
  }

  /**
   * Creates the clear image cache button, add listener for clear image cache.
   * @return the relative layout
   */
  private void createClearImageCacheButton() {
    Button clearImageCacheButton = (Button) findViewById(R.id.clear_image_cache_button);
    
    clearImageCacheButton.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        ViewHelper.showAlertViewWithTitleYesOrNo(AppSettingsActivity.this, "",
            "Are you sure you want to clear image cache?",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int which) {
                FileUtil.clearImagesInCache(AppSettingsActivity.this);
              }
            });
      }
    });
  }

  /**
   * Creates the auto layout.
   * It contains toggle button to switch auto state, two text view to indicate auto messages.
   *
   * @return the relative layout
   */
  private RelativeLayout createAutoLayout() {
    ToggleButton autoButton = (ToggleButton) findViewById(R.id.autoButton);

    autoButton.setChecked(autoMode);
    
    autoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        currentServer = "";
        if (isChecked) {
          IPAutoDiscoveryServer.isInterrupted = false;
          switchToAutoServersView();
          startControllerAutoDiscovery();
        } else {
          IPAutoDiscoveryServer.isInterrupted = true;
          stopControllerAutoDiscovery();
          switchToCustomServersView();
        }
        AppSettingsModel.setAutoMode(AppSettingsActivity.this, isChecked);
      }
    });
    
    return (RelativeLayout) findViewById(R.id.autoLayout);
  }

  /**
   * Loads the custom server URL from customServers.xml, setting the current controller
   * URL to the last entry that began with a plus sign if such an entry was found.
   *
   * @param customServers the custom servers
   */
  private void initCustomServersFromFile(ArrayList<String> customServers) {
    String storedUrls = AppSettingsModel.getCustomServers(this);
    if (! TextUtils.isEmpty(storedUrls)) {
      String[] data = storedUrls.split(",");
      int dataNum = data.length;
      for (int i = 0; i < dataNum; i++) {
        if (!data[i].startsWith("+")){
          customServers.add(data[i]);
        } else {
          currentCustomServerIndex = i;
          customServers.add(data[i].substring(1));
          try {
            URL controllerURL = new URL(data[i].substring(1));
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, controllerURL);
          } catch (MalformedURLException e) {
            Log.e(TAG, "incorrect URL syntax in customServers.xml: \"" + data[i].substring(1) +
                "\"");
            Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.incorrect_url_syntax_from_saved_configuration), 1);
            toast.show();
          }
        }
      }
    }
  }
   
  /**
   * It contains a list view to display custom servers,
   * "Add" button to add custom server, "Delete" button to delete custom server.
   * The custom servers would be saved in customServers.xml. If click a list item, it would be saved as current server.
   *
   * @return the linear layout
   */
  private void constructCustomServersView() {
    ArrayList<String> customServers = new ArrayList<String>();
    initCustomServersFromFile(customServers);
    
    Button addServer = (Button) findViewById(R.id.add_server_button);
    
    addServer.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(AppSettingsActivity.this, AddServerActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
      }
    });
    
    Button deleteServer = (Button) findViewById(R.id.delete_server_button);
    
    deleteServer.setOnClickListener(new OnClickListener() {
      @SuppressWarnings("unchecked")
      public void onClick(View v) {
        int checkedPosition = customListView.getCheckedItemPosition();
        if (!(checkedPosition == ListView.INVALID_POSITION)) {
          customListView.setItemChecked(checkedPosition, false);
          ((ArrayAdapter<String>) customListView.getAdapter()).remove(customListView.getItemAtPosition(checkedPosition).toString());
          currentServer = "";
          AppSettingsModel.setCurrentServer(AppSettingsActivity.this, null);
          writeCustomServerToFile();
        }
      }
    });
    
    customListView = (ListView) findViewById(R.id.custom_server_list_view);
    
    final ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
        customServers);
    customListView.setAdapter(serverListAdapter);
    customListView.setItemsCanFocus(true);
    
    if (currentCustomServerIndex != -1) {
       customListView.setItemChecked(currentCustomServerIndex, true);
       currentServer = (String)customListView.getItemAtPosition(currentCustomServerIndex);
    }
    customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          String selectedController = (String) parent.getItemAtPosition(position);
          try {
            URL url = new URL(selectedController);
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, url);
            currentServer = selectedController;
          } catch (MalformedURLException e) {
            Log.e(TAG, "incorrect URL syntax: \"" + selectedController + "\"");
            Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.incorrect_url_syntax) + ": " + selectedController, 1);
            toast.show();
          }
          writeCustomServerToFile();
          requestPanelList();
       }
    });
    
    requestPanelList();
  }

  /**
   * Received custom server from AddServerActivity, add prefix "http://" before it.
   * Add the result in custom server list and set it as current server.
   *
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null) {
      String result = data.getDataString();
      if (Constants.REQUEST_CODE == requestCode && !TextUtils.isEmpty(result)) {
        if (Constants.RESULT_CONTROLLER_URL == resultCode) {
          String proposedServer = "http://" + result;

          ArrayAdapter<String> customeListAdapter = (ArrayAdapter<String>) customListView.getAdapter();
          customeListAdapter.add(proposedServer);
          customListView.setItemChecked(customeListAdapter.getCount() - 1, true);
          try {
            URL url = new URL(proposedServer);
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, url);
            currentServer = proposedServer;
          } catch (MalformedURLException e) {
            Log.e(TAG, "incorrect URL syntax in onActivityResult(): " + proposedServer);
            Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.incorrect_url_syntax) + ": " + proposedServer, 1);
            toast.show();
          }
          writeCustomServerToFile();
          requestPanelList();
        }
      }
    }
  }
  
  /**
   * Do initialization that can't be done in XML for the ListView showing controllers
   * discovered automatically.
   */
  private void constructAutoServersView() {
    autoServersListView.setItemsCanFocus(true);
    
    autoServerListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
        new ArrayList<String>());
    autoServersListView.setAdapter(autoServerListAdapter);
    
    autoServersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String proposedServer = (String) parent.getItemAtPosition(position);
        try {
          URL controllerURL = new URL(proposedServer);
          AppSettingsModel.setCurrentServer(AppSettingsActivity.this, controllerURL);
          currentServer = proposedServer;
        } catch (MalformedURLException e) {
          Log.e(TAG, "invalid URL syntax in selected auto-discovered controller URL: \"" +
              proposedServer + "\"");
          Toast toast = Toast.makeText(getApplicationContext(),
              getString(R.string.incorrect_url_syntax) + ": \"" + proposedServer + "\"", 1);
          toast.show();
          return;
        }
        requestPanelList();
      }
    });
  }

  /**
   * Starts a new {@link IPAutoDiscoveryServer} thread, which starts a server to receive
   * messages declaring the presence of controllers.  Once the server is started,
   * a message is sent via multicast over UDP to announce that such messages are desired.
   * 
   * The list of controllers discovered this way is cleared before the new discovery
   * thread begins.
   * 
   * If any controllers are found, the first one found is automatically selected and its
   * panel list is requested.
   */
  private void startControllerAutoDiscovery() {
    autoServerListAdapter.clear();
    
    autoDiscoveryServer = new IPAutoDiscoveryServer() {
      @Override
      protected void onProgressUpdate(Void... values) {
        if (progressLayout != null) {
          progressLayout.setVisibility(View.VISIBLE);
        }
      }

      @Override
      protected void onPostExecute(List<String> result) {
        if (progressLayout != null) {
          progressLayout.setVisibility(View.INVISIBLE);
        }

        int length = result.size();
        for (int i = 0; i < length; i++) {
          autoServerListAdapter.add(result.get(i));
        }
        if (length > 0) {
          autoServersListView.setItemChecked(0, true);
          String proposedServer = autoServerListAdapter.getItem(0);
          try {
            URL controllerURL = new URL(proposedServer);
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, controllerURL);
            currentServer = proposedServer;
          } catch (MalformedURLException e) {
            Log.e(TAG, "first auto-discovered URL had incorrect syntax: \"" + proposedServer + "\"");
            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.incorrect_url_syntax) + ": \"" +
                proposedServer + "\"", 1);
            toast.show();
            return;
          }
        }
        requestPanelList();
      }
    };

    autoDiscoveryServer.execute((Void) null);
  }

  public void stopControllerAutoDiscovery() {
    if (autoDiscoveryServer != null) {
      autoDiscoveryServer.cancel(true);
    }
  }
  
  /**
   *  Construct custom servers to a string which split by "," and write it to customServers.xml.
   */
  private void writeCustomServerToFile() {
    int customServerCount = customListView.getCount();
    if (customServerCount > 0) {
      int checkedPosition = customListView.getCheckedItemPosition();
      String customServerUrls = "";
      for (int i = 0; i < customServerCount; i++) {
        if (i != checkedPosition) {
          customServerUrls = customServerUrls + customListView.getItemAtPosition(i).toString();
        } else {
          customServerUrls = customServerUrls + StringUtil.markControllerServerURLSelected(customListView.getItemAtPosition(i).toString());
        }
        if (i != customServerCount - 1) {
          customServerUrls = customServerUrls + ",";
        }
      }
      if (!TextUtils.isEmpty(customServerUrls)) {
        AppSettingsModel.setCustomServers(customListView.getContext(), customServerUrls);
      }
    }
  }

  /**
   * Request panel identity list from controller.
   */
  private void requestPanelList() {
    setEmptySpinnerContent();
    if (!TextUtils.isEmpty(AppSettingsActivity.currentServer)) {
      loadingPanelProgress.show();
      new ORConnection(this.getApplicationContext(), ORHttpMethod.GET, true, AppSettingsActivity.currentServer + "/rest/panels", this);
    }
  }
  
  @Override
  public void urlConnectionDidFailWithException(Exception e) {
    loadingPanelProgress.dismiss();
  }

  @Override
  public void urlConnectionDidReceiveData(InputStream data) {
    loadingPanelProgress.dismiss();
    try {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document dom = builder.parse(data);
      Element root = dom.getDocumentElement();

      NodeList nodeList = root.getElementsByTagName("panel");
      int nodeNums = nodeList.getLength();
      if (nodeNums == 1) {
        panelSelectSpinnerView.setOnlyPanel(nodeList.item(0).getAttributes().getNamedItem("name").getNodeValue());
      }
    } catch (IOException e) {
      Log.e(Constants.LOG_CATEGORY + "PANEL LIST", "The data is from ORConnection is bad", e);
      return;
    } catch (ParserConfigurationException e) {
      Log.e(Constants.LOG_CATEGORY + "PANEL LIST", "Cant build new Document builder", e);
      return;
    } catch (SAXException e) {
      Log.e(Constants.LOG_CATEGORY + "PANEL LIST", "Parse data error", e);
      return;
    }
  }

  @Override
  public void urlConnectionDidReceiveResponse(HttpResponse httpResponse) {
    int statusCode = httpResponse.getStatusLine().getStatusCode();
    if (statusCode != Constants.HTTP_SUCCESS) {
      loadingPanelProgress.dismiss();
      if (statusCode == ControllerException.UNAUTHORIZED) {
        LoginDialog loginDialog = new LoginDialog(this);
        loginDialog.setOnClickListener(loginDialog.new OnloginClickListener() {
          @Override
          public void onClick(View v) {
            super.onClick(v);
            requestPanelList();
          }
        });
      } else {
        // The following code customizes the dialog, because the finish method should do after dialog show and click ok.
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Panel List Not Found");
        alertDialog.setMessage(ControllerException.exceptionMessageOfCode(statusCode));
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int which) {
            return;
          }
        });
        alertDialog.show();
      }
    }
  }
  
  private void setEmptySpinnerContent() {
    if (panelSelectSpinnerView != null) {
      panelSelectSpinnerView.setOnlyPanel(PanelSelectSpinnerView.CHOOSE_PANEL);
    }
  }
}
