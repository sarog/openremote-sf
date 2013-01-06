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
import org.openremote.android.console.view.ControllerListItemLayout;
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
import android.view.Window;
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
 * Controller List View for displaying saved and auto-discovered controllers
 * and selecting which controller to load as well as which panel.
 * Can also clear the image cache.
 * 
 * @author Tomsky Wang
 * @author Dan Cong
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 */

public class AppSettingsActivity extends GenericActivity {
  public static final String TAG = Constants.LOG_CATEGORY + "AppSettingsActivity";
  
  private DataHelper dh;

  private LinearLayout appSettingsView;
  
  /** List View for Controllers */
  private ListView controllerListView;
  
  private ControllerObject selectedController;
  
  /** The view for selecting panel identity. */
  private PanelSelectSpinnerView panelSelectSpinnerView;
  
  /** The progress layout display auto discovery progress. */
  private LinearLayout progressLayout;

  private ControllerListAdapter serverListAdapter;
  
  private LinearLayout serversLayout;
  
  private ProgressDialog loadingPanelProgress;
  
  private IPAutoDiscoveryServer autoDiscoveryServer;

  private SavedServersNetworkCheckTestAsyncTask checkedControllers;
  
  //private boolean fullOptions=false;
  public int selectedPosition=-1;
 
  @Inject
  private ORControllerServerSwitcher orControllerServerSwitcher;

  @Override
  public void onCreate(Bundle savedInstanceState) { 
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    
    setContentView(R.layout.controller_list);
    
    loadingPanelProgress = new ProgressDialog(this);
    dh = new DataHelper(this);
    controllerListView = (ListView) findViewById(R.id.controller_list_view);
    appSettingsView = (LinearLayout) findViewById(R.id.appSettingsView);
    serversLayout = (LinearLayout) findViewById(R.id.custom_servers_layout);
    panelSelectSpinnerView = (PanelSelectSpinnerView) findViewById(R.id.panel_select_spinner_view);
    progressLayout = (LinearLayout) findViewById(R.id.choose_controller_progress);

    addListChangeListener();
    
    addButtonOnClickListeners();
    
    initControllerList();
      
    startControllerAutoDiscovery();
  }


  private void addListChangeListener() {
    controllerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    	
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      	ControllerObject newlySelectedController =  (ControllerObject) parent.getItemAtPosition(position);
      	ControllerListItemLayout listItem = (ControllerListItemLayout)view;
        
      	// Only proceed if list item is checked (means controller is available)
      	if (!listItem.isChecked())
      		return;
      	
      	if (selectedController != null && selectedController == newlySelectedController)
      		return;
      	
      	if (selectedController != null)
      		selectedController.setIs_Selected(false);
      	
      	selectedController = newlySelectedController;
      	
      	selectedController.setIs_Selected(true);
      	
      	panelSelectSpinnerView.setController(newlySelectedController);  
         
        serverListAdapter.notifyDataSetChanged();
      }
   });
  }
  
  /**
   * Adds the onclick listeners for the buttons in this view
   */
  private void addButtonOnClickListeners() {
	  Button btnAddServer = (Button) findViewById(R.id.button_add_controller);
    
    btnAddServer.setOnClickListener(new OnClickListener() {
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setClass(AppSettingsActivity.this, AddServerActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE);
      }
    });
    
//    Button btnDeleteServer = (Button) findViewById(R.id.delete_server_button);
//    
//    btnDeleteServer.setOnClickListener(new OnClickListener() {
//      @SuppressWarnings("unchecked")
//      public void onClick(View v) {
//        int checkedPosition = controllerListView.getCheckedItemPosition();
//        if (!(checkedPosition == ListView.INVALID_POSITION)) {
//        	controllerListView.setItemChecked(checkedPosition, false);
//        	
//        	ControllerObject toDelete = (ControllerObject)controllerListView.getItemAtPosition(checkedPosition);
//          ((ArrayAdapter<ControllerObject>) controllerListView.getAdapter()).remove(toDelete);
//          currentServer = "";
//          AppSettingsModel.setCurrentServer(AppSettingsActivity.this, null);
//          dh.delete(toDelete.getControllerName());
//          previousSelectedItem=-1;
//        }
//      }
//    });
  	
    Button btnClearCache = (Button) findViewById(R.id.button_clear_image_cache);
    
    btnClearCache.setOnClickListener(new OnClickListener() {
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
    
    
//    Button btnDone = (Button)findViewById(R.id.setting_done);
//    
//    btnDone.setOnClickListener(new OnClickListener() {
//      public void onClick(View v) {
//        if (selectedController == null) {
//          ViewHelper.showAlertViewWithTitle(AppSettingsActivity.this, "Warning",
//              "Please select a controller from the list!");
//          return;
//        }
//        
//        String selectedPanel = (String)panelSelectSpinnerView.getSelectedItem();
//        if (!TextUtils.isEmpty(selectedPanel) && !selectedPanel.equals(PanelSelectSpinnerView.CHOOSE_PANEL)) {
//          AppSettingsModel.setCurrentPanelIdentity(AppSettingsActivity.this, selectedPanel);
//        } else {
//          ViewHelper.showAlertViewWithTitle(AppSettingsActivity.this, "Warning",
//              "No Panel. Please configure Panel Identity manually.");
//          return;
//        }
//        
//        //TODO need to request after controller availability is found. So move this method to be called after "Done" is selected
//        requestFailoverGroupUrls(); //this will run aynchronously in the background. after finish() will not run because AppSettingsActivity is closed
//        
//        Intent intent = new Intent();
//        intent.setClass(AppSettingsActivity.this, Main.class);
//        startActivity(intent);
//        finish();
//        
//      
//      }
//    });
//
//    Button btnCancel = (Button)findViewById(R.id.setting_cancel);
//    btnCancel.setOnClickListener(new OnClickListener() {
//      public void onClick(View v) {
//    	  
//    	  dh.closeConnection();
//        finish();
//      }
//    });
  }

	/**
	 * Populate the list of Controllers from saved controller's
	 */
  private void initControllerList() {
    ArrayList<ControllerObject> savedControllers = dh.getControllerData();

	  serverListAdapter = new ControllerListAdapter(appSettingsView.getContext(), R.layout.controller_list_item, savedControllers);

	  controllerListView.setAdapter(serverListAdapter);    
    controllerListView.setItemsCanFocus(true);
  }
  
  /**
   * It contains a list view to display custom servers,
   * "Add" button to add custom server, "Delete" button to delete custom server.
   * The custom servers would be saved in customServers.xml. If click a list item, it would be saved as current server.
   *
   * @return the linear layout
   */
//  private void () {
//    ArrayList<ControllerObject> customServers = new ArrayList<ControllerObject>();
//   
//    
//	  checkedControllers = new SavedServersNetworkCheckTestAsyncTask(getApplicationContext()){
//			
//		 @Override
//			  protected void onPreExecute() {
//				  if (progressLayout != null) {
//					  progressLayout.setVisibility(View.VISIBLE);
//				  }
//			  }
//
//			  @Override
//			  protected void onPostExecute(ArrayList<ControllerObject> result) {
//				  
//			        if (progressLayout != null) {
//			            progressLayout.setVisibility(View.INVISIBLE);
//			          }
//
//
//				  Log.i("LOGGER", "Done...");
//
//				  if(result!=null){
//					// customServers=result.addAll(index, collection);
//					 // serverListAdapter.add(result.get(0));
//					  
//					//serverListAdapter.addAll(result); //2.1 update pphone doesnt have this method. tablet does
//					  
//					  for(int i=0;i<result.size();i++){
//					  serverListAdapter.add(result.get(i)); //this should work for 2.1 update
//					  }					   
//					  
////					  Toast toast= Toast.makeText(getApplicationContext(), "complete", Toast.LENGTH_LONG);
////					  toast.show();
//					  serverListAdapter.notifyDataSetChanged();
//					  
//				  }else{
//					  Toast.makeText(getApplicationContext(), "HttpResponse is null", Toast.LENGTH_LONG).show();
//				  }	
//				  
//				  //load the database first then autodiscover so duplicate entries wont try to add
//				//  startControllerAutoDiscovery();
//				  
//			  }
//		  };
//
//	  checkedControllers.execute();  //execute oes to doInBackground
//		  
//		
//
//
//    
//
//    
//   // serverListAdapter
//    
//    //gets the layout length, padding etc
//    
//
//    Log.e("AppSettingsActivity", "currentCustomServerIndex"+currentCustomServerIndex);
//
//    if (currentCustomServerIndex != -1) {
//
//    	customAndAutoListView.setItemChecked(currentCustomServerIndex, true);
//        currentServer = ((ControllerObject)customAndAutoListView.getItemAtPosition(currentCustomServerIndex)).getControllerName();
//     
//    }
//  }
  

  /**
   * Received custom server from AddServerActivity, add prefix "http://" before it.
   * Add the result in custom server list and set it as current server.
   *
   * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
   */
  @SuppressWarnings("unchecked")
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (data != null && resultCode == Constants.RESULT_CONTROLLER_URL) {
      String result = data.getDataString();
      
      if (TextUtils.isEmpty(result))
      	return;
      
      result = result.toLowerCase().indexOf("http") < 0 ? "http://" + result : result;

      // Check controller doesn't already exist
      for (int i=0; i<serverListAdapter.getCount(); i++)
      {
      	ControllerObject controllerObj = serverListAdapter.getItem(i);
      	if (controllerObj.getControllerName().equalsIgnoreCase(result))
      	{
      		return;
      	}
      }
      
      ControllerObject newController = new ControllerObject(result, "grp1", 0, 1, 0, "");
      
    	addController(newController);
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
    Log.i(TAG, "Start Controller Discovery");
    
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
        
        // Add each new controller to the controller list
        for (int i = 0; i < length; i++) {

        	if (!dh.controllerExists(result.get(i))) {
        		ControllerObject controller = new ControllerObject(result.get(i), "grp1", 1, 1, 0, "");
        		addController(controller);
        	}
        }
      }
    };

    autoDiscoveryServer.execute((Void) null);
  }

  public void stopControllerAutoDiscovery() {
    if (autoDiscoveryServer != null) {
      autoDiscoveryServer.cancel(true);
    }
  }
   
  private void addController(ControllerObject controller) {
  	if (controller == null)
  		return;
  	
  	String urlStr = controller.getControllerName();
  	
    // Check URL is valid
    try {
      URL url = new URL(urlStr);
    } catch (MalformedURLException e) {
      Log.e(TAG, "New Controller URL is invalid '" + urlStr + "'");
      Toast toast = Toast.makeText(getApplicationContext(),
          getString(R.string.incorrect_url_syntax) + ": " + urlStr, 1);
      toast.show();
      return;
    }
 	  serverListAdapter.add(controller);
 	  dh.addController(controller);
    serverListAdapter.notifyDataSetChanged();
    Log.i(TAG,"New Controller Added '" + urlStr + "'");
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

        
       // TODO: Re-enable group discovery
       //orControllerServerSwitcher.saveGroupMembersToDB(servers,AppSettingsActivity.currentServer );
        //i dont want to use files instead wnt to use database put the failover urls as separate rows.
        //So, url1's failover URLs will have url1 column as "FailoverFor" column. same as controller name
        //grup is not really needed maybe refactor that in subsequent code bases
        
      }

      return null;
    }
  }
}
