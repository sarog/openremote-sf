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

import org.openremote.android.console.model.UserCache;

import android.net.http.AndroidHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import android.util.Base64;
import android.util.Log;
import android.app.Activity;
import android.content.Context;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.FileReader;
import java.io.BufferedReader;
import android.content.Intent;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

/*
 *  This class downloads the files from Beehive for serverless operation
 *  
 *  @author <a href="mailto:marcf@openremote.org">Marc Fleury</a>
 */
public class ServerlessConfigurator  {

	private String username, password;
	private final String beehiveRoot = "http://beehive.openremote.org/3.0/alpha5/rest/user/";
	private Activity activity;
	

	public ServerlessConfigurator(final Activity activity) 
	{
		this.activity = activity;
	}
	public void configure()
	{
		//get username
		username = UserCache.getUsername(activity);
		
		// If we have no credentials, get them
		if (username == "") {
			getCredentials();
			return;
		}
				
		// We have credentials supposedly
		new Thread(new Runnable() {
			public void run() {
				downloadOpenremoteZipFromBeehiveAndUnzip();
			}	
		}).start();
	}


	private void downloadOpenremoteZipFromBeehiveAndUnzip() {

		AndroidHttpClient httpClient = AndroidHttpClient.newInstance("OpenRemote");


		//get username
		username = UserCache.getUsername(activity);
		//get password
		password = UserCache.getPassword(activity);
		
		// Assemble the URI to get the file from OpenRemote
		HttpGet httpGet = new HttpGet(beehiveRoot+username+"/openremote.zip");

		// OR.org uses Basic Base64(MD5) encryption, we do it here
		httpGet.setHeader("Authorization", "Basic "+encode(username,password));

		InputStream inputStream = null;

		try {

			HttpResponse response = httpClient.execute(httpGet);

			if (200 == response.getStatusLine().getStatusCode()) {

				Log.i("ServerlessConfigurator", httpGet.getURI() + " is available.");

				inputStream = response.getEntity().getContent();

				writeZipAndUnzip(inputStream);

			} else if (400 == response.getStatusLine().getStatusCode()) {
				Log.e("Serverless Configurator", "Not found", new Exception("400 Malformed")); 	 
			} else if (401 == response.getStatusLine().getStatusCode() || 404 == response.getStatusLine().getStatusCode()) {
				Log.e("Serverless Configurator", "Not found", new Exception("401 Not authorized"));
				// Go get the credentials again.  We will call back on activity finish
				getCredentials();
							
			} else {
				Log.e("Serverless Configurator", "failed to download resources for template, The status code is: "
						+ response.getStatusLine().getStatusCode());
			}
		} catch (IOException e) {
			Log.e("SeverlessConfigurator","failed to connect to Beehive.");
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					Log.e("ServerlessConfigurator","failed to close input stream while downloading " + httpGet.getURI());
				}
			}

			httpClient.close();
		}

		Log.i("ServerlessConfigurator","Done downloading and unzipping files from OR.org");

	}

	/*
	 * This method encodes a username and String password with a MD5 hash.  
	 * We use the password{username} combination used by beehive
	 * 
	 */
	private String encode(String username, String password) {

		try {

			// Format is password{credential}   
			String mergedCredentials = password + "{" + username + "}";

			// Use MD5 as the encryption algorythm
			MessageDigest md = MessageDigest.getInstance("MD5"); 

			byte[] digest = md.digest(mergedCredentials.getBytes());

			//The next sequence of instruction is to make sure the format is correct, a straight digest.toString() doesn't work on Android.
			BigInteger number = new BigInteger(1,digest);

			String md5 = number.toString(16);

			while (md5.length() < 32) md5 = "0" + md5;

			// Base64 encoding of the string username:md5(password{username}).  This is what the server side beehive understands.  Without a trim() the server responds 400 bad formatting.
			return Base64.encodeToString((username+":"+md5).getBytes(),Base64.DEFAULT).trim();
		}

		catch (Exception e) {Log.e("ServerConfigurator", "couldn't encode with MD5", e); return null;} 

	}

	private void writeZipAndUnzip(InputStream inputStream) {

		FileOutputStream fos = null;
		FileOutputStream fos2 = null; 
		ZipInputStream zis = null; 
		try {
			// Store the file to internal storage under the name openremote.zip
			fos = activity.openFileOutput("openremote.zip",activity.MODE_WORLD_READABLE);

			// Define variable for reading stream
			byte[] buffer = new byte[1024]; int len = 0;

			while ((len = inputStream.read(buffer)) != -1) { fos.write(buffer, 0, len);}

			Log.i("OpenRemote/DOWNLOAD","Done downloading file from internet");
			//Save stream
		
			zis = new ZipInputStream(activity.openFileInput("openremote.zip"));

			ZipEntry zipEntry;

			while ((zipEntry = zis.getNextEntry()) != null) {

				Log.i("OpenRemote/DOWNLOAD", "new File in ZIP" + zipEntry.getName());
				
				fos2 = activity.openFileOutput(zipEntry.getName(),activity.MODE_WORLD_READABLE);

				byte[] buffer2 = new byte[1024]; int len2 = 0;
				
				while ((len2 = zis.read(buffer2)) != -1) {fos2.write(buffer2, 0, len2);}

			}

		} catch (IOException e) {e.printStackTrace();}

		finally {

			try {
				inputStream.close();
				activity.deleteFile("openremote.zip");
				zis.closeEntry();
				fos.close();
				fos2.close();
			} catch (IOException e) { Log.e("ServerConfigurator", "Error while closing stream.", e);}			   
		}
	}

	/*
	 * A helper method to check if credentials exist and if not to start the LoginView activity
	 * 
	 */

	private void getCredentials() {

		Intent i = new Intent();
		i.setClassName(activity.getClass().getPackage().getName(),
				LoginViewActivity.class.getName());
		activity.startActivityForResult(i,Constants.REQUEST_CODE_LOGIN_VIEW);
	}
}