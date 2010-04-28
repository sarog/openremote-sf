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

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.openremote.android.console.exceptions.ORConnectionException;

/**
 * This is responsible for manage the connection of android console to controller
 * 
 * @author handy 2010-04-27
 *
 */

public class ORConnection {	
	private static final int HTTP_REQUEST_SUCCESS = 200;
	
	private HttpClient httpClient;
	private HttpRequestBase httpRequest;
	private HttpResponse httpResponse;
	private ORConnectionDelegate delegate;
	
	/** 
	 * Establish the httpconnection with url for caller,<br />
	 * and then the caller can deal with the httprequest result in ORConnectionDelegate instance.
	 */
	public ORConnection (ORHttpMethod httpMethod, String url, ORConnectionDelegate delegateParam) {
		delegate = delegateParam;
		httpClient = new DefaultHttpClient();
		if (ORHttpMethod.POST.equals(httpMethod)) {
			httpRequest = new HttpPost(url);
		} else if (ORHttpMethod.GET.equals(httpMethod)) {
			httpRequest = new HttpGet(url);
		}
		
		if (httpRequest == null) {
			throw new ORConnectionException("Create HttpRequest fail.");
		}
        try {
        	httpResponse = httpClient.execute(httpRequest);
		} catch (ClientProtocolException e) {
			connectionDidFailWithException(new ORConnectionException("Httpclient execute httprequest fail.", e));
			return;
		} catch (IOException e) {
			connectionDidFailWithException(new ORConnectionException("Httpclient execute httprequest fail.", e));
			return;
		}
		dealWithResponse();
	}

	/** Deal with the response while httpconnection of android console to controller success. */
	private void dealWithResponse() {
		connectionDidReceiveResponse();
		connectionDidReceiveData();
	}
	
	/**  delegate methods of self */
	
	/** 
	 * This method is invoked by self while the connection of android console to controller was failed,
	 * and sends a notification to delegate with <b>urlConnectionDidFailWithException</b> method calling and
	 * switching the connection of android console to a available controller server in groupmembers of self.
	 */
	private void connectionDidFailWithException(ORConnectionException e) {
		delegate.urlConnectionDidFailWithException(e);
		switchControllerToGroupMember();
	}
	
	/** 
	 * This method is invoked by self while the connection of android console to controller received response,
	 * and sends a notification to delegate with <b>urlConnectionDidReceiveResponse</b> method calling.
	 */
	private void connectionDidReceiveResponse() {
		delegate.urlConnectionDidReceiveResponse(httpResponse);
	}
	
	/**
	 * This method is invoked by self while the connection of android console to controller received data,
	 * and sends a notification to delegate with <b>urlConnectionDidReceiveData</b> method calling.
	 */
	private void connectionDidReceiveData() {
		try {
			if (httpResponse.getStatusLine().getStatusCode() == HTTP_REQUEST_SUCCESS) {
				delegate.urlConnectionDidReceiveData(httpResponse.getEntity().getContent());
			} else {
				new ORConnectionException("Get the entity's content of httpresponse fail."); 
			}
		} catch (IllegalStateException e) {
			new ORConnectionException("Get the entity's content of httpresponse fail.", e);
		} catch (IOException e) {
			new ORConnectionException("Get the entity's content of httpresponse fail.", e);
		}
	}
	
	/** private methods */
	
	/** Switch urlconnnection to a available controller server */
	private void switchControllerToGroupMember() {
		// TODO: removeBadCurrentServerURL
		// TODO: checkGroupMemberServers, if there is one available, then get it.
		// TODO: if get a available url, then updateControllerWith:controllerServerUrl, else alert "no server available" to users
	}
	
}
