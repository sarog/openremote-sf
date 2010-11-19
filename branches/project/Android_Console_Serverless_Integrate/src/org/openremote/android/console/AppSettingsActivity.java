/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2010, OpenRemote Inc.
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
import org.openremote.android.console.model.UserCache;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
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
 * There are 3 states to the panel
 * 
 * A/ ORB:NO  B/ORB: YES AUTO:YES  C/ ORB:YES AUTO:NO
 * 
 * In case of A: We need to capture credentials and we go get information from the or.org website through login
 * 
 * In case of B: We scan for available servers, layout is automatic
 * 
 * In case of C: Layout enables manual entering of controller information
 * 
 * 
 * 
 * 
 * @author Tomsky Wang
 * @author Dan Cong
 * @author <a href="mailto:marcf@openremote.org">Marc Fleury</a>
 * 
 */

public class AppSettingsActivity extends GenericActivity implements ORConnectionDelegate {

	/** The app settings view contains auto discovery, auto servers, custom servers,
	 * select panel identity, clear image cache and security configuration. 
	 */
	private LinearLayout appSettingsView;

	/** The custom list view contains custom server list. */
	private ListView customListView;
	private int currentCustomServerIndex = -1;

	/** hasORB indicates whether a controller is present in this setup **/
	private boolean hasORB;

	/** The auto mode is to indicate if auto discovery servers. */
	private boolean autoMode;

	public static String currentServer = "";

	/** Keep a reference to the choose panel for layout **/
	private View choosePanelLabel;

	/** The view for selecting panel identity. */
	private PanelSelectSpinnerView panelSelectSpinnerView;

	/** The progress layout display auto discovery progress. */
	private LinearLayout progressLayout;

  private ProgressDialog loadingPanelProgress;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setTitle(R.string.settings);

		this.autoMode = AppSettingsModel.isAutoMode(AppSettingsActivity.this);
		this.hasORB = AppSettingsModel.hasORB(AppSettingsActivity.this);

		// The main layout contains all application configuration items.
		LinearLayout mainLayout = new LinearLayout(this);
		mainLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setBackgroundColor(0);
		mainLayout.setTag(R.string.settings);

    loadingPanelProgress = new ProgressDialog(this);

		// The scroll view contains appSettingsView, it is scrollable and contains the main functional views.
		ScrollView scroll = new ScrollView(this);
		scroll.setVerticalScrollBarEnabled(true);
		scroll.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, 1));
		appSettingsView = new LinearLayout(this);
		appSettingsView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		appSettingsView.setOrientation(LinearLayout.VERTICAL);


		// First we ask the user if the setup has an ORB 
		appSettingsView.addView(createHasORBLayout());

		
		if (!hasORB) { 
			populateNoORBView();
		}
		if (hasORB) {
			populateORBView();
		}

		choosePanelLabel = createChoosePanelLabel();

		appSettingsView.addView(choosePanelLabel);
		panelSelectSpinnerView = new PanelSelectSpinnerView(this);
		appSettingsView.addView(panelSelectSpinnerView);

		appSettingsView.addView(createCacheText());
		appSettingsView.addView(createClearImageCacheButton());


		//		appSettingsView.addView(createSSLLayout());


		scroll.addView(appSettingsView);
		//initSSLState();

		mainLayout.addView(scroll);
		mainLayout.addView(createDoneAndCancelLayout());

		setContentView(mainLayout);
		addOnclickListenerOnDoneButton();
		addOnclickListenerOnCancelButton();
		progressLayout = (LinearLayout)findViewById(R.id.choose_controller_progress);
	}


	public void populateNoORBView() {
		appSettingsView.addView(createRefreshLayout(),1);
	}
	
	public void removeNoORBView() {
		appSettingsView.removeViewAt(1);
	}
	public void populateORBView(){

		appSettingsView.addView(createAutoLayout(),1);
		appSettingsView.addView(createChooseControllerLabel(),2);

		if (autoMode) {
			appSettingsView.addView(constructAutoServersView(),3);
		} else {
			appSettingsView.addView(constructCustomServersView(),3);
		}
	}

	public void removeORBView() {
		appSettingsView.removeViews(1, appSettingsView.indexOfChild(choosePanelLabel)-1);
	}
	
	public RelativeLayout createRefreshLayout() {
		RelativeLayout refreshLayout = new RelativeLayout(this);
		refreshLayout.setPadding(10, 5, 10, 10);
		refreshLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80));

		TextView refreshText = new TextView(this);
		RelativeLayout.LayoutParams refreshTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		refreshTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		refreshTextLayout.addRule(RelativeLayout.CENTER_VERTICAL);
		refreshText.setLayoutParams(refreshTextLayout);
		refreshText.setText("Refresh Config");

		Button refreshButton = new Button(this);
		refreshButton.setWidth(50);
		RelativeLayout.LayoutParams refreshButtonLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		refreshButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		refreshButtonLayout.addRule(RelativeLayout.CENTER_VERTICAL);
		refreshButton.setLayoutParams(refreshButtonLayout);
		refreshButton.setOnClickListener(new OnClickListener() {
			 public void onClick(View v) {
					new ServerlessConfigurator(AppSettingsActivity.this).configure();
			 }
		});

		TextView infoText = new TextView(this);
		RelativeLayout.LayoutParams infoTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		infoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		infoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		infoText.setLayoutParams(infoTextLayout);
		infoText.setTextSize(10);
		infoText.setText("Press to download configuration from the web");

		refreshLayout.addView(refreshText);
		refreshLayout.addView(refreshButton);
		refreshLayout.addView(infoText);
		
		return refreshLayout;
	}
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	/*
	 * A helper method to check if credentials exist and if not to start the LoginView activity
	 * 
	 */
	private void checkForCredentials() {

		// Check that username and password are stored somewhere
		String username = UserCache.getUsername(this);

		// If it isn't then assume a fresh install and get the username and password through a new activity
		if (username.trim() =="") {
			Intent i = new Intent();
			i.setClassName(this.getClass().getPackage().getName(),
					LoginViewActivity.class.getName());
			startActivity(i);
		}

	}
	/**
	 * First question asked to the user: do you have a controller? 
	 * 
	 * @return the relative layout
	 */
	private RelativeLayout createHasORBLayout() {
		RelativeLayout hasORBLayout = new RelativeLayout(this);
		hasORBLayout.setPadding(10, 5, 10, 10);
		hasORBLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80));

		TextView hasORBText = new TextView(this);
		RelativeLayout.LayoutParams hasORBTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		hasORBTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		hasORBTextLayout.addRule(RelativeLayout.CENTER_VERTICAL);
		hasORBText.setLayoutParams(hasORBTextLayout);
		hasORBText.setText("Do you have a Controller?");

		ToggleButton hasORBButton = new ToggleButton(this);
		hasORBButton.setWidth(50);
		hasORBButton.setTextOn("Yes");
		hasORBButton.setTextOff("No");
		RelativeLayout.LayoutParams hasORBButtonLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		hasORBButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		hasORBButtonLayout.addRule(RelativeLayout.CENTER_VERTICAL);
		hasORBButton.setLayoutParams(hasORBButtonLayout);
		hasORBButton.setChecked(hasORB);
		hasORBButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					AppSettingsModel.setHasORB(AppSettingsActivity.this, true);
					removeNoORBView();
					populateORBView();
				} else {
					AppSettingsModel.setHasORB(AppSettingsActivity.this, false);
					removeORBView();
					populateNoORBView();
				}
				AppSettingsModel.setHasORB(AppSettingsActivity.this, isChecked);
			}
		});

		TextView infoText = new TextView(this);
		RelativeLayout.LayoutParams infoTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		infoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		infoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		infoText.setLayoutParams(infoTextLayout);
		infoText.setTextSize(10);
		infoText.setText("Turn off if you haven't installed an ORB controller.");

		hasORBLayout.addView(hasORBText);
		hasORBLayout.addView(hasORBButton);
		hasORBLayout.addView(infoText);
		return hasORBLayout;
	}
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW
	//NEW

	/**
	 * Creates the image cache text view.
	 * 
	 * @return the text view
	 */
	private TextView createCacheText() {
		TextView cacheText = new TextView(this);
		cacheText.setPadding(10, 5, 0, 5);
		cacheText.setText("Image Cache:");
		cacheText.setBackgroundColor(Color.DKGRAY);
		return cacheText;
	}

	/**
	 * Creates the choose panel identity text.
	 * 
	 * @return the text view
	 */
	private TextView createChoosePanelLabel() {
		TextView choosePanelInfo = new TextView(this);
		choosePanelInfo.setPadding(10, 5, 0, 5);
		choosePanelInfo.setText("Choose a panel set");
		choosePanelInfo.setBackgroundColor(Color.DKGRAY);
		return choosePanelInfo;
	}

	/**
	 * Creates the choose controller bar, which contains "Choose controller:" text and 
	 * a progress bar(used in auto discovery servers).
	 * 
	 * @return the linear layout
	 */
	private LinearLayout createChooseControllerLabel() {
		LayoutInflater inflater = (AppSettingsActivity.this).getLayoutInflater();
		LinearLayout chooseController = (LinearLayout)inflater.inflate(R.layout.choose_controller_bar, null);
		return chooseController;
	}

	/**
	 * Creates the done and cancel layout that is inflated from xml.
	 * 
	 * @return the linear layout
	 */
	private LinearLayout createDoneAndCancelLayout() {
		LayoutInflater inflater = (AppSettingsActivity.this).getLayoutInflater();
		LinearLayout saveAndCancelLayout = (LinearLayout)inflater.inflate(R.layout.bottom_button_bar, null);
		return saveAndCancelLayout;
	}

	/**
	 * Creates the ssl layout.
	 * It contains ssl switch and ssl port.
	 * 
	 * @return the linear layout
	 */
	private LinearLayout createSSLLayout() {
		LayoutInflater inflater = (AppSettingsActivity.this).getLayoutInflater();
		LinearLayout sslLayout = (LinearLayout)inflater.inflate(R.layout.ssl_field_view, null);

		return sslLayout;
	}


  
  /**
   * Set the ssl switch state and ssl port value.
   */
  private void initSSLState() {
     ToggleButton sslBtn = (ToggleButton)findViewById(R.id.ssl_toggle);
     sslBtn.setChecked(AppSettingsModel.isSSLEnabled(this));
     sslBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
           if (isChecked) {
              AppSettingsModel.enableSSL(AppSettingsActivity.this, true);
           } else {
              AppSettingsModel.enableSSL(AppSettingsActivity.this, false);
           }
        }
     });

     EditText sslPortText = (EditText)findViewById(R.id.ssl_port);
     sslPortText.setText("" + AppSettingsModel.getSSLPort(this));
     sslPortText.setOnKeyListener(new OnKeyListener() {
        public boolean onKey(View v, int keyCode, KeyEvent event) {
           if (keyCode == KeyEvent.KEYCODE_ENTER) {
              String sslPortStr = ((EditText)v).getText().toString();
              try {
                  int sslPort = Integer.parseInt(sslPortStr.trim());
                  AppSettingsModel.setSSLPort(AppSettingsActivity.this, sslPort);
              }
              catch (NumberFormatException ex) {
                 Toast toast = Toast.makeText(getApplicationContext(), "SSL port format is not correct.", 1);
                 toast.show();
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
				boolean hasORB= AppSettingsModel.hasORB(AppSettingsActivity.this);
				if (!hasORB) {            

					// REFACTOR ME.  If we have no ORB we still put something in the registry for the rest of the app to work 
					AppSettingsModel.setCurrentServer(AppSettingsActivity.this, "NO ORB");
				}
				if ("".equals(currentServer) && hasORB) {
					ViewHelper.showAlertViewWithTitle(AppSettingsActivity.this, "Warning",
					"No controller. Please configure Controller URL manually.");
					return;
				}

				String selectedPanel = (String)panelSelectSpinnerView.getSelectedItem();
				System.out.println("REFACTOR ME: APPSETTINGACTIVITY : Panel Selected : "+selectedPanel);
				
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
	private RelativeLayout createClearImageCacheButton() {
		RelativeLayout clearImageView = new RelativeLayout(this);
		clearImageView.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		Button clearImgCacheBtn = new Button(this);
		clearImgCacheBtn.setText("Clear Image Cache");
		RelativeLayout.LayoutParams clearButtonLayout = new RelativeLayout.LayoutParams(150, LayoutParams.WRAP_CONTENT);
		clearButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		clearImgCacheBtn.setLayoutParams(clearButtonLayout);
		clearImgCacheBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ViewHelper.showAlertViewWithTitleYesOrNo(AppSettingsActivity.this, "",
						"Are you sure you want to clear image cache?", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						FileUtil.clearImagesInCache(AppSettingsActivity.this);
					}
				});
			}
		});

		clearImageView.addView(clearImgCacheBtn);
		return clearImageView;
	}

	/**
	 * Creates the auto layout.
	 * It contains toggle button to switch auto state, two text view to indicate auto messages.
	 * 
	 * @return the relative layout
	 */
	private RelativeLayout createAutoLayout() {
		RelativeLayout autoLayout = new RelativeLayout(this);
		autoLayout.setPadding(10, 5, 10, 10);
		autoLayout.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, 80));

		TextView autoText = new TextView(this);
		RelativeLayout.LayoutParams autoTextLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		autoTextLayout.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		autoTextLayout.addRule(RelativeLayout.CENTER_VERTICAL);
		autoText.setLayoutParams(autoTextLayout);
		autoText.setText("Auto Discovery");

		ToggleButton autoButton = new ToggleButton(this);
		autoButton.setWidth(50);
		RelativeLayout.LayoutParams autoButtonLayout = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		autoButtonLayout.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		autoButtonLayout.addRule(RelativeLayout.CENTER_VERTICAL);
		autoButton.setLayoutParams(autoButtonLayout);
		autoButton.setChecked(autoMode);
		autoButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				appSettingsView.removeViewAt(3);
				currentServer = "";
				if (isChecked) {
					IPAutoDiscoveryServer.isInterrupted = false;
					appSettingsView.addView(constructAutoServersView(), 3);
				} else {
					IPAutoDiscoveryServer.isInterrupted = true;
					appSettingsView.addView(constructCustomServersView(),3);
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
		return autoLayout;
	}


  /**
   * Inits the custom servers from customServers.xml.
   *
   * @param customServers the custom servers
   */
  private void initCustomServersFromFile(ArrayList<String> customServers) {
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



  /**
   * It contains a list view to display custom servers,
   * "Add" button to add custom server, "Delete" button to delete custom server.
   * The custom servers would be saved in customServers.xml. If click a list item, it would be saved as current server.
   *
   * @return the linear layout
   */
  private LinearLayout constructCustomServersView() {
     LinearLayout custumeView = new LinearLayout(this);
     custumeView.setOrientation(LinearLayout.VERTICAL);
     custumeView.setPadding(20, 5, 5, 0);
     custumeView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

     ArrayList<String> customServers = new ArrayList<String>();
     initCustomServersFromFile(customServers);

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
           intent.setClass(AppSettingsActivity.this, AddServerActivity.class);
           startActivityForResult(intent, Constants.REQUEST_CODE_ADD_SERVER);
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
           int checkedPosition = customListView.getCheckedItemPosition();
           if (!(checkedPosition == ListView.INVALID_POSITION)) {
              customListView.setItemChecked(checkedPosition, false);
              ((ArrayAdapter<String>)customListView.getAdapter()).remove(customListView.getItemAtPosition(checkedPosition).toString());
              currentServer = "";
              AppSettingsModel.setCurrentServer(AppSettingsActivity.this, currentServer);
              writeCustomServerToFile();
           }
        }
     });

     buttonsView.addView(addServer);
     buttonsView.addView(deleteServer);

     customListView = new ListView(this);
     customListView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
     customListView.setCacheColorHint(0);
     final ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
             customServers);
     customListView.setAdapter(serverListAdapter);
     customListView.setItemsCanFocus(true);
     customListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
     if (currentCustomServerIndex != -1) {
        customListView.setItemChecked(currentCustomServerIndex, true);
        currentServer = (String)customListView.getItemAtPosition(currentCustomServerIndex);
     }
     customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
           currentServer = (String)parent.getItemAtPosition(position);
           AppSettingsModel.setCurrentServer(AppSettingsActivity.this, currentServer);
           writeCustomServerToFile();
           requestPanelList();
        }

     });

     custumeView.addView(customListView);
     custumeView.addView(buttonsView);
     requestPanelList();
     return custumeView;
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


		// Called by LoginViewActivity we need to configure
		if (requestCode == Constants.REQUEST_CODE_LOGIN_VIEW) {
			new ServerlessConfigurator(this).configure();
		}		

		// called by AddServerActivity
		if (data != null) {
			String result = data.getDataString();
			if (Constants.REQUEST_CODE_ADD_SERVER == requestCode && !TextUtils.isEmpty(result)) {
				if (Constants.RESULT_CONTROLLER_URL == resultCode) {
					currentServer = "http://" + result;
					ArrayAdapter<String> customeListAdapter = (ArrayAdapter<String>) customListView.getAdapter();
					customeListAdapter.add(currentServer);
					customListView.setItemChecked(customeListAdapter.getCount() - 1, true);
					AppSettingsModel.setCurrentServer(AppSettingsActivity.this, currentServer);
					writeCustomServerToFile();
          requestPanelList();
				}
			}
		}
	}



  /**
   * Auto discovery servers and add them in a list view.
   * Click a list item and make it as current server.
   *
   * @return the list view
   */
  private ListView constructAutoServersView() {
     final ListView lv = new ListView(this);
     lv.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 200));
     lv.setPadding(20, 5, 5, 10);
     lv.setBackgroundColor(0);
     lv.setCacheColorHint(0);
     lv.setItemsCanFocus(true);
     lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
     final ArrayAdapter<String> serverListAdapter = new ArrayAdapter<String>(appSettingsView.getContext(), R.layout.server_list_item,
           new ArrayList<String>());
     lv.setAdapter(serverListAdapter);

     new IPAutoDiscoveryServer(){
        @Override
        protected void onProgressUpdate(Void... values) {
           if (progressLayout != null) {
              progressLayout.setVisibility(View.VISIBLE);
           }
        }

       @Override
       protected void onPostExecute(List<String> result) {
          int length = result.size();
          for (int i = 0; i < length; i++) {
             serverListAdapter.add(result.get(i));
          }
          if (length > 0) {
             lv.setItemChecked(0, true);
             currentServer = serverListAdapter.getItem(0);
             AppSettingsModel.setCurrentServer(AppSettingsActivity.this, currentServer);
          }
          if (progressLayout != null) {
             progressLayout.setVisibility(View.INVISIBLE);
          }
          requestPanelList();
       }
    }.execute((Void) null);

    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
       public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
          currentServer = (String)parent.getItemAtPosition(position);
          AppSettingsModel.setCurrentServer(AppSettingsActivity.this, currentServer);
          requestPanelList();
       }
    });

    return lv;
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
    * 
    * @param ORConnectionDelegate the delegate to handle the connection
    */
   private void requestPanelList() {
      setEmptySpinnerContent();
      if (!TextUtils.isEmpty(AppSettingsActivity.currentServer)) {
         loadingPanelProgress.show();
         new ORConnection(this.getApplicationContext() ,ORHttpMethod.GET, true, AppSettingsActivity.currentServer + "/rest/panels", this);
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
