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
import org.openremote.android.console.bindings.Screen;
import org.openremote.android.console.exceptions.ORConnectionException;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ControllerException;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.net.AsyncPanelListReader;
import org.openremote.android.console.net.ControllerService;
import org.openremote.android.console.net.IPAutoDiscoveryServer;

import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORConnectionDelegate;

import org.openremote.android.console.net.ORControllerServerSwitcher;
import org.openremote.android.console.net.ORHttpMethod;
import org.openremote.android.console.net.ORNetworkCheck;
import org.openremote.android.console.net.SavedServersNetworkCheckTestAsyncTask;
import org.openremote.android.console.util.FileUtil;
import org.openremote.android.console.util.StringUtil;
import org.openremote.android.console.view.PanelSelectSpinnerView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

import roboguice.util.RoboAsyncTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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
  
  private DataHelper dh;
  //private ControllerDBHelper dBHelper;
  //private SQLiteDatabase liteDB;
  
  private int previousSelectedItem=-1;
  
  private int currentSelectedItem;
  
  private LinearLayout appSettingsView;
  
  /** The custom list view contains custom server list. */
  private ListView customAndAutoListView;
  private int currentCustomServerIndex = -1;
  
  /** The auto mode is to indicate if auto discovery servers. */
  private boolean autoMode;
  public static String currentServer = "";
  
  /** The view for selecting panel identity. */
  private PanelSelectSpinnerView panelSelectSpinnerView;
  
  /** The progress layout display auto discovery progress. */
  private LinearLayout progressLayout;
  
 // private ListView autoServersListView;
  /** ListAdapter for automatically detected controllers */
  private IconicAdapter serverListAdapter;
  private LinearLayout serversLayout;
  
  private ProgressDialog loadingPanelProgress;
  
  private IPAutoDiscoveryServer autoDiscoveryServer;

  private SavedServersNetworkCheckTestAsyncTask checkedControllers;
  
  private boolean fullOptions=false;
  public int selectedPosition=0;
 
  


  @Inject
  private ORControllerServerSwitcher orControllerServerSwitcher;

  @Override
  public void onCreate(Bundle savedInstanceState) { 
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setTitle(R.string.settings);
    
    this.autoMode = AppSettingsModel.isAutoMode(AppSettingsActivity.this);
   
    /*
     * @yusha
     * This is the layout that loads on this activity create
     * Change xml screen based on device size
     */
    
  /*  if(Screen.SCREEN_WIDTH < 100){
    	setContentView(R.layout.app_settings);
    	addOnclickListenerSecurityButton();
   
    Toast toast = Toast.makeText(getApplicationContext(), "Screen.SCREEN_WIDTH"+Screen.SCREEN_WIDTH, 1);
        toast.show();      
       }
    
    else{   
    	fullOptions=true;
        setContentView(R.layout.all_options_app_settings);
         	
    }*/
    
    fullOptions=false;
    setContentView(R.layout.app_settings);
  
    loadingPanelProgress = new ProgressDialog(this);
    
  // ScrollView scrollView = (ScrollView) findViewById(R.id.serversScrollView);
    //scrollView.setVerticalScrollBarEnabled(true);
    
    appSettingsView = (LinearLayout) findViewById(R.id.appSettingsView);
    
    
    serversLayout = (LinearLayout) findViewById(R.id.custom_servers_layout);
    
    currentServer = "";
    
 
    this.dh = new DataHelper(this);   


    constructServersView();
    
    serversLayout.setVisibility(View.VISIBLE);
   
      
      Log.i(TAG,"startControllerAutoDiscovery at switch" );
 
      startControllerAutoDiscovery();
    panelSelectSpinnerView = (PanelSelectSpinnerView) findViewById(R.id.panel_select_spinner_view);
    /*
     * @yusha
     */
  
   if(fullOptions){
   initSSLState(); 
   createClearImageCacheButton();
   }
  
    addOnclickListenerOnDoneButton();
    addOnclickListenerOnCancelButton();
    progressLayout = (LinearLayout) findViewById(R.id.choose_controller_progress);
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
 * Create onclick listener button to view
 */
  
  private void addOnclickListenerSecurityButton() {
    Button securityButton = (Button)findViewById(R.id.security);
    securityButton.setOnClickListener(new OnClickListener() {
        public void onClick(View v) {
         Intent intent = new Intent();
          intent.setClass(AppSettingsActivity.this, SecuritySettingsActivity.class);
          startActivityForResult(intent, Constants.REQUEST_CODE);
          //finish();
         // wait for that activity to process and then finish();
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
        
        //TODO need to request after controller availability is found. So move this method to be called after "Done" is selected
        requestFailoverGroupUrls(); //this will run aynchronously in the background. after finish() will not run because AppSettingsActivity is closed
        
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
    	  
    	  dh.closeConnection();
        finish();
      }
    });
  }

  /**
   * Creates the clear image cache button, add listener for clear image cache.
   * @return the relative layout
   */
  private void createClearImageCacheButton() {
    Button clearImageCacheButton = (Button) findViewById(R.id.button_clear_image_cache);
    
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
   * It contains a list view to display custom servers,
   * "Add" button to add custom server, "Delete" button to delete custom server.
   * The custom servers would be saved in customServers.xml. If click a list item, it would be saved as current server.
   *
   * @return the linear layout
   */
  private void constructServersView() {
    ArrayList<ControllerObject> customServers = new ArrayList<ControllerObject>();
   
	  checkedControllers = new SavedServersNetworkCheckTestAsyncTask(getApplicationContext()){
	  
			
		 @Override
			  protected void onPreExecute() {
				  if (progressLayout != null) {
					  progressLayout.setVisibility(View.VISIBLE);
				  }
			  }

			  @Override
			  protected void onPostExecute(ArrayList<ControllerObject> result) {
				  
			        if (progressLayout != null) {
			            progressLayout.setVisibility(View.INVISIBLE);
			          }


				  Log.i("LOGGER", "Done...");

				  if(result!=null){
					// customServers=result.addAll(index, collection);
					 // serverListAdapter.add(result.get(0));
					  
					//serverListAdapter.addAll(result); //2.1 update pphone doesnt have this method. tablet does
					  
					  for(int i=0;i<result.size();i++){
					  serverListAdapter.add(result.get(i)); //this should work for 2.1 update
					  }					   
					  
					  Toast toast= Toast.makeText(getApplicationContext(), "complete", Toast.LENGTH_LONG);
					  toast.show();
					  serverListAdapter.notifyDataSetChanged();
					  
				  }else{
					  Toast.makeText(getApplicationContext(), "HttpResponse is null", Toast.LENGTH_LONG).show();
				  }	
				  
				  //load the database first then autodiscover so duplicate entries wont try to add
				//  startControllerAutoDiscovery();
				  
			  }
		  };

		  checkedControllers.execute();  //execute oes to doInBackground
		  
		
		  serverListAdapter = new IconicAdapter(appSettingsView.getContext(), R.layout.row, 
			      customServers);

    
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
        int checkedPosition = customAndAutoListView.getCheckedItemPosition();
        if (!(checkedPosition == ListView.INVALID_POSITION)) {
        	customAndAutoListView.setItemChecked(checkedPosition, false);
        	
        	ControllerObject toDelete = (ControllerObject)customAndAutoListView.getItemAtPosition(checkedPosition);
          ((ArrayAdapter<ControllerObject>) customAndAutoListView.getAdapter()).remove(toDelete);
          currentServer = "";
          AppSettingsModel.setCurrentServer(AppSettingsActivity.this, null);
          dh.delete(toDelete.getControllerName());
          previousSelectedItem=-1;
        }
      }
    });
    
   // serverListAdapter
    
    //gets the layout length, padding etc
    
    customAndAutoListView = (ListView) findViewById(R.id.custom_server_list_view);
    
 
    
  //  serverListAdapter = new
    //customAndAutoListView.setChoiceMode(customAndAutoListView.CHOICE_MODE_SINGLE);
    customAndAutoListView.setAdapter(serverListAdapter);
    
    customAndAutoListView.setItemsCanFocus(true);
    Log.e("AppSettingsActivity", "currentCustomServerIndex"+currentCustomServerIndex);

    if (currentCustomServerIndex != -1) {

    	customAndAutoListView.setItemChecked(currentCustomServerIndex, true);
        currentServer = ((ControllerObject)customAndAutoListView.getItemAtPosition(currentCustomServerIndex)).getControllerName();
     
    }
 
    customAndAutoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    	
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	   
    	  Log.e("ASA", "position"+position);
    	 
    	  
    	   //selectedPosition = position;
          ControllerObject selectedController =  (ControllerObject) parent.getItemAtPosition(position);
          selectedController.setIs_Selected(true);
          //TODO do the same in db
          currentSelectedItem=position;
          
          if(previousSelectedItem < 0){
        	  previousSelectedItem= currentSelectedItem;

          }
          
          else if(previousSelectedItem == currentSelectedItem){
        	  //do nothing
          }
 
          else{
              ControllerObject pselectedController =  (ControllerObject) parent.getItemAtPosition(previousSelectedItem);
              pselectedController.setIs_Selected(false);
            //TODO do the same in db
          }
          
          Log.e("ASA", "previousSelectedItem"+previousSelectedItem);
          
          //first ping and then fetch
        // checkControllerUp(selectedController.getControllerName());
 //shouldnt these process linearly. I guess not because network operations are run on a separate thread
         
         
         // requestPanelList(selectedController.getControllerName());
          
          checkControllerUp(selectedController.getControllerName());
          
          
          
          //set the controller URL after panels can be found. Since Main will try to pull panels of a selected controller in AppSettingsModel
      
          try {
            URL controllerURL = new URL(selectedController.getControllerName());
            AppSettingsModel.setCurrentServer(AppSettingsActivity.this, controllerURL);
            currentServer = controllerURL.toString();
          } catch (MalformedURLException e) {
            Log.e(TAG, "incorrect URL syntax: \"" + selectedController + "\"");
            Toast toast = Toast.makeText(getApplicationContext(),
                getString(R.string.incorrect_url_syntax) + ": " + selectedController, 1);
            toast.show();
          }

         
          
          serverListAdapter.notifyDataSetChanged();
          previousSelectedItem = currentSelectedItem;
 
         
          
       }
    });
    
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
      String proposedServer = "http://" + result;
      if (Constants.REQUEST_CODE == requestCode && !TextUtils.isEmpty(result)) {
        if (Constants.RESULT_CONTROLLER_URL == resultCode) {
          

         ControllerObject addController = new ControllerObject(proposedServer,"grp1",0,1,0,"");
          
         
         if(this.dh.find(proposedServer)==0){
         	  serverListAdapter.add(addController);
              this.dh.insert(proposedServer, "grp1", 0, 1, 0, "");
              Log.i(TAG,"inserted "+proposedServer+" in database");
         }

          }
  		
         // customAndAutoListView.setItemChecked(serverListAdapter.getCount() - 1, true);
          
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
          
          //writeCustomServerToFile();
         // requestPanelList();
          serverListAdapter.notifyDataSetChanged();
          previousSelectedItem = currentSelectedItem;
        }
      }
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
    //autoServerListAdapter.clear();//may be not delete
    
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
        	//add the icon+the text view somehow
//          serverListAdapter.add(new ControllerObject(result.get(i)));
        	
       
          //TODO before setting this to selected, unselect previously selected
          //even if autodiscovered, first check that it is not a saved one
        	
        	if(dh.find(result.get(i))==0){
        		
        		//add to the adapter if doesnt exist in the list
        		serverListAdapter.add(new ControllerObject(result.get(i), "grp1",1,1,0,""));
        	
          dh.insert(result.get(i), "grp1", 1,1,0,"");
        	}
        	else{
        		length--;
        	}
          
          Log.i(TAG,"result.get(i): "+result.get(i) );
        }
        if (length > 0) {
        	//customAndAutoListView.setItemChecked(0, true);
          String proposedServer = serverListAdapter.getItem(0).getControllerName();
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
        //I dont think requestPanelList(); is needed after autodiscovery
        serverListAdapter.notifyDataSetChanged();
       
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
   * Request panel identity list from controller.
   * On ItemClick
   */
  private void requestPanelList(String url) {
    setEmptySpinnerContent();
    if (!TextUtils.isEmpty(AppSettingsActivity.currentServer)) {
      loadingPanelProgress.show();
      new ORConnection(this.getApplicationContext(), ORHttpMethod.GET, true,
          url + "/rest/panels", this);
    }
  }
  
  /**
   * Request panel identity list from controller.
   */
  private void requestPanelList() {
    setEmptySpinnerContent();
    if (!TextUtils.isEmpty(AppSettingsActivity.currentServer)) {
      loadingPanelProgress.show();
      new ORConnection(this.getApplicationContext(), ORHttpMethod.GET, true,
          AppSettingsActivity.currentServer + "/rest/panels", this);
    }
  }
  
  private void checkControllerUp(String url){
      //first ping and then fetch ORConnection has handler which gives a 
	  new ORConnection(this.getApplicationContext(), ORHttpMethod.GET, true,
	          url, this);
  }

  public static class FetchControllerFailoverGroupUrls extends RoboAsyncTask<Void>
  {
    @Inject
    private ControllerService controllerService;

    @Inject
    private ORControllerServerSwitcher orControllerServerSwitcher;

    @Override
    public Void call() throws Exception
    {
      List<URL> servers = controllerService.getServers();//this does not work
      
      Log.e(TAG, "controllerService.getServers() " + servers);

      for (URL url : servers)
      {
        Log.e(TAG, "received failover group member URL " + url);

        
        
       orControllerServerSwitcher.saveGroupMembersToDB(servers,AppSettingsActivity.currentServer );
        //i dont want to use files instead wnt to use database put the failover urls as separate rows.
        //So, url1's failover URLs will have url1 column as "FailoverFor" column. same as controller name
        //grup is not really needed maybe refactor that in subsequent code bases
        
      }

      return null;
    }
  }

  private void requestFailoverGroupUrls()
  {
    Log.d(TAG, "requestFailoverGroupUrls(): entry");
    getInjector().getInstance(FetchControllerFailoverGroupUrls.class).execute();
  }

  @Override
  public void urlConnectionDidFailWithException(Exception e) {
    loadingPanelProgress.dismiss();
    AlertDialog alertDialog = new AlertDialog.Builder(this).create(); //getBaseContext() crashing the app
    alertDialog.setTitle("Invalid URL selected");
   alertDialog.setMessage(ControllerException.exceptionMessageOfCode(ControllerException.REQUEST_ERROR));
   
   // alertDialog.setMessage("could not connect to host");

    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
       public void onClick(DialogInterface dialog, int which) {
    	   //keep the selection s user can see what he selected, but remove the panel associated with previous selection.
    	   //Also remove from arrayAdapter
    	   AppSettingsModel.setCurrentPanelIdentity(AppSettingsActivity.this, null);
    	   panelSelectSpinnerView.setDefaultAdapterContent();
    	   
          return;
       }
    });
    
    alertDialog.show();
  
    //TODO also update on the adapter and database as such
    
   
   Log.e(TAG, "Can not get panel identity list", e);
  }

  @Override
  public void urlConnectionDidReceiveData(InputStream data) {
	    AsyncPanelListReader asyncReader = new AsyncPanelListReader() {
	        protected void onPostExecute(List<String> panelList) {
	          loadingPanelProgress.dismiss();
	          if (!panelList.isEmpty())
	            panelSelectSpinnerView.setOnlyPanel(panelList.get(0));
	        }
	      };
	      asyncReader.execute(data);
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
            requestPanelList(); // if on login, do the request again
          }
        });
      } else {
        // The following code customizes the dialog, because the finish method should do after dialog show and click ok.
    	  //this would be when the controller is reachable but no panels found on it
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
    if (true) {
      panelSelectSpinnerView.setOnlyPanel(PanelSelectSpinnerView.CHOOSE_PANEL);
    }
  }
}
