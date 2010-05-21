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

package org.openremote.android.console.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openremote.android.console.Constants;
import org.openremote.android.console.Main;
import org.openremote.android.console.exceptions.ORConnectionException;
import org.openremote.android.console.model.AppSettingsModel;
import org.openremote.android.console.model.ViewHelper;
import org.openremote.android.console.util.SecurityUtil;
import org.openremote.android.console.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * This is responsible for detecting groupmembers and switching to available controller.
 * 
 * @author handy 2010-04-29
 *
 */
public class ORControllerServerSwitcher {

	private static final String SERIALIZE_GROUP_MEMBERS_FILE_NAME = "group_members";
	public static final int SWITCH_CONTROLLER_SUCCESS = 1;
	public static final int SWITCH_CONTROLLER_FAIL = 2;

	/**
	 * Detect the groupmembers of current server url 
	 */
	public static boolean detectGroupMembers(Context context) {
		Log.i("GROUP MEMBER", "Detecting group members with current controller server url "
            + AppSettingsModel.getCurrentServer(context));
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
      HttpConnectionParams.setSoTimeout(params, 5 * 1000);
      HttpClient httpClient = new DefaultHttpClient(params);
      HttpGet httpGet = new HttpGet(AppSettingsModel.getCurrentServer(context) + "/rest/servers");
		
		if (httpGet == null) {
			Log.e("GROUP MEMBER", "Create HttpRequest fail.");
			return false;
		}
		SecurityUtil.addCredentialToHttpRequest(context, httpGet);		
	   try {
	    	HttpResponse httpResponse = httpClient.execute(httpGet);
	    	
	    	try {
				if (httpResponse.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS) {
					InputStream data = httpResponse.getEntity().getContent();
					
					try{
						   DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
						   DocumentBuilder builder = factory.newDocumentBuilder();
						   Document dom = builder.parse(data);
						   Element root = dom.getDocumentElement();
						   
						   NodeList nodeList = root.getElementsByTagName("server");
						   int nodeNums = nodeList.getLength();
						   List<String> groupMembers = new ArrayList<String>();
						   for (int i = 0; i < nodeNums; i++) {
							   groupMembers.add(nodeList.item(i).getAttributes().getNamedItem("url").getNodeValue());
						   }
						   Log.i("GROUP MEMBER", "Detected groupmembers. Groupmembers are " + groupMembers);
						   return saveGroupMembersToFile(context, groupMembers);
						} catch (IOException e) {
							Log.e("GROUP MEMBER", "The data is from ORConnection is bad", e);
						} catch (ParserConfigurationException e) {
							Log.e("GROUP MEMBER", "Cant build new Document builder", e);
						} catch (SAXException e) {
							Log.e("GROUP MEMBER", "Parse data error", e);
						}
					
				} else {
				   Log.e("GROUP MEMBER", "detectGroupMembers Parse data error");
				}
			} catch (IllegalStateException e) {
			   Log.e("GROUP MEMBER", "detectGroupMembers Parse data error", e);
			} catch (IOException e) {
			   Log.e("GROUP MEMBER", "detectGroupMembers Parse data error", e);
			}
	   } catch (ConnectException e) {
         Log.e("GROUP MEMBER", "Connection refused: " + AppSettingsModel.getCurrentServer(context), e);
      } catch (ClientProtocolException e) {
         Log.e("GROUP MEMBER", "Can't Detect groupmembers with current controller server "
               + AppSettingsModel.getCurrentServer(context), e);
      } catch (SocketTimeoutException e) {
         Log.e("GROUP MEMBER", "Can't Detect groupmembers with current controller server "
               + AppSettingsModel.getCurrentServer(context), e);
      } catch (IOException e) {
         Log.e("GROUP MEMBER", "Can't Detect groupmembers with current controller server "
               + AppSettingsModel.getCurrentServer(context), e);
      } catch (IllegalArgumentException e) {
         Log.e("GROUP MEMBER", "Host name can be null :" + AppSettingsModel.getCurrentServer(context), e);
      }
      return false;
	}
	
	/**
	 * Serialize the groupmembers into file named group_members.xml .
	 */
	private static boolean saveGroupMembersToFile(Context context, List<String> groupMembers) {
		SharedPreferences.Editor editor = context.getSharedPreferences(SERIALIZE_GROUP_MEMBERS_FILE_NAME, 0).edit();
		editor.clear();
		editor.commit();
		for (int i = 0; i < groupMembers.size(); i++) {
			editor.putString(i+"", groupMembers.get(i));
		}
		return editor.commit();
	}
	
	/**
	 * Get the groupmembers from the file group_members.xml .
	 */
	@SuppressWarnings("unchecked")
	public static List<String> findAllGroupMembersFromFile(Context context) {
		List<String> groupMembers = new ArrayList<String>();
		Map<String, String> groupMembersMap = (Map<String, String>) context.getSharedPreferences(SERIALIZE_GROUP_MEMBERS_FILE_NAME, 0).getAll();
		for(int i = 0; i <groupMembersMap.size(); i++) {
			groupMembers.add(groupMembersMap.get(i+""));
		}
		return groupMembers;
	}
	
	/** 
	 * Get a available controller server url and switch to it.
	 */
	public static int doSwitch(Context context) {
		String availableGroupMemberURL = getOneAvailableFromGroupMemberURLs(context);
		List<String> allGroupMembers = findAllGroupMembersFromFile(context);
		if (availableGroupMemberURL != null && !"".equals(availableGroupMemberURL)) {
			Log.i("GROUP MEMBER", "Got a available controller url from groupmembers" + allGroupMembers);
			switchControllerWithURL(context, availableGroupMemberURL);
		} else {
			Log.i("GROUP MEMBER", "Didn't get a available controller url from groupmembers " + allGroupMembers + ". Try to detect groupmembers again.");
			if (!detectGroupMembers(context)) {
			   ViewHelper.showAlertViewWithSetting(context, "Update fail", "There's no controller server available. Leave this problem?");
			   return SWITCH_CONTROLLER_FAIL;
			}
			availableGroupMemberURL = getOneAvailableFromGroupMemberURLs(context);
			if (availableGroupMemberURL != null && !"".equals(availableGroupMemberURL)) {
				Log.i("GROUP MEMBER", "Got a available controller url from groupmembers " + allGroupMembers + " in second groupmembers detection attempt.");
				switchControllerWithURL(context, availableGroupMemberURL);
			} else {
				Log.i("GROUP MEMBER", "There's no controller server available.");
				ViewHelper.showAlertViewWithSetting(context, "Update fail", "There's no controller server available. Leave this problem?");
				return SWITCH_CONTROLLER_FAIL;
			}
		}
		return SWITCH_CONTROLLER_SUCCESS;
	}

	/**
	 * Check all groupmembers' url and get a available one, this function deponds on the WIFI network.
	 */
	private static String getOneAvailableFromGroupMemberURLs(Context context) {
		List<String> allGroupMembers = findAllGroupMembersFromFile(context);
		Log.i("GROUP MEMBER", "Checking a available controller url from groupmembers " + allGroupMembers);
		for (String controllerServerURL : allGroupMembers) {
		   HttpResponse response = ORNetworkCheck.checkAllWithControllerServerURL(context, controllerServerURL);
			if (response != null && response.getStatusLine().getStatusCode() == Constants.HTTP_SUCCESS) {
				if (!AppSettingsModel.isAutoMode(context)) {//custom model
					String selectedControllerServerURL = StringUtil.markControllerServerURLSelected(controllerServerURL);
					String customServerURLs = AppSettingsModel.getCustomServers(context);
					if (!customServerURLs.contains(selectedControllerServerURL)) {
						customServerURLs = StringUtil.removeControllerServerURLSelected(customServerURLs);
						if (customServerURLs.contains(controllerServerURL)) {
							customServerURLs = customServerURLs.replaceAll(controllerServerURL, selectedControllerServerURL);
						} else {
							customServerURLs = customServerURLs + "," + selectedControllerServerURL;
						}
						AppSettingsModel.setCustomServers(context, customServerURLs);
					}
				}
				return controllerServerURL;
			}
		}
		return null;
	}

	/**
	 * Switch to the controller identified by the availableGroupMemberURL
	 */
	private static void switchControllerWithURL(Context context, String availableGroupMemberURL) {
	   Main.prepareToastForSwitchingController();
		Log.i("GROUP MEMBER", "ControllerServerSwitcher is switching controller to " + availableGroupMemberURL);
		AppSettingsModel.setCurrentServer(context, availableGroupMemberURL);
		Intent intent = new Intent();
		intent.setClass(context, Main.class);
		context.startActivity(intent);
	}
	
}
