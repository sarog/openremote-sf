/* OpenRemote, the Home of the Digital Home.
 * Copyright 2009, OpenRemote Inc.
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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.openremote.android.console.exceptions.ORConnectionException;
import org.openremote.android.console.net.ORButtonConnectionDelegate;
import org.openremote.android.console.net.ORCommandConnectionDelegate;
import org.openremote.android.console.net.ORConnection;
import org.openremote.android.console.net.ORGetPanelsConnectionDelegate;
import org.openremote.android.console.net.ORHttpMethod;
import org.openremote.android.console.util.SecurityUtil;

import android.content.Context;
import android.util.Log;

/**
 * Does the HTTP stuff, anything related to HttpClient should go here.
 * 
 * @author Andrew C. Oliver <acoliver at osintegrators.com>
 */
public class HTTPUtil {

    public static final String CLICK = "click";
    public static final String PRESS = "press";
    public static final String RELEASE = "release";

    /**
     * Sends a button action of some kind
     * 
     * @param url
     * @param id
     * @param command
     * @return http_status_code
     * @throws ClientProtocolException
     * @throws IOException
     */
    @SuppressWarnings("deprecation")
	public static int sendButton(Context context, String url, String id, String command)
    throws ClientProtocolException, IOException {
		//String connectString = url + "/rest/button/" + id + "/" + command;
		//HttpClient client = new DefaultHttpClient();
		//HttpPost post = new HttpPost(connectString);
		//HttpResponse response = client.execute(post);
		//int code = response.getStatusLine().getStatusCode();
		//return code;
//		String connectString = url + "/rest/button/" + id + "/" + command;
    	String connectString = url + "/rest/control/" + id + "/" + command;
		ORButtonConnectionDelegate delegate =  new ORButtonConnectionDelegate();
		new ORConnection(context, ORHttpMethod.POST, true, connectString, delegate);
		return delegate.getHttpResponseStatusCode();
	}
	
	@SuppressWarnings("deprecation")
	public static int sendCommand(Context context, String url, int id, String command)
	throws ClientProtocolException, IOException {
		//String connectString = url + "/rest/control/" + id + "/" + command;
		//HttpClient client = new DefaultHttpClient();
		//HttpPost post = new HttpPost(connectString);
		//HttpResponse response = client.execute(post);
		//int code = response.getStatusLine().getStatusCode();
		//return code;
		String connectString = url + "/rest/control/" + id + "/" + command;
		ORCommandConnectionDelegate delegate =  new ORCommandConnectionDelegate();
		new ORConnection(context, ORHttpMethod.POST, true, connectString, delegate);
		return delegate.getHttpResponseStatusCode();
	}

    @SuppressWarnings("deprecation")
	public static List<String> getPanels(Context context, String serverUrl){
/*       List<String> panelList = new ArrayList<String>();
      try {
         HttpClient client = new DefaultHttpClient();
         HttpGet get = new HttpGet(serverUrl + "/rest/panels");
         HttpResponse response = client.execute(get);
         if (response.getStatusLine().getStatusCode() == 200) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(response.getEntity().getContent());
            Element root = dom.getDocumentElement();
            
            NodeList nodeList = root.getElementsByTagName("panel");
            int nodeNums = nodeList.getLength();
            for (int i = 0; i < nodeNums; i++) {
               panelList.add(nodeList.item(i).getAttributes().getNamedItem("name").getNodeValue());
            }
         }
      } catch (IOException e) {
         Log.e("HTTPUtil", "can not open connection to url(" + serverUrl + "/rest/panels)", e);
      } catch (ParserConfigurationException e) {
         Log.e("HTTPUtil", "can not build new Document builder", e);
      } catch (SAXException e) {
         Log.e("HTTPUtil", "parse panels error", e);
      }*/
//      for (Panel panel : panelList) {
//         Log.d("tt", "---------------------id:"+panel.getId() + "name:" + panel.getName()+"------------------");
//      }
//       final Panel currentMessage = new Panel();
//       RootElement root = new RootElement("http://www.openremote.org:openremote");
//       final List<Panel> messages = new ArrayList<Panel>();
//       Element item = root.getChild("panel");
//       item.setEndElementListener(new EndElementListener(){
//          public void end() {
//             messages.add(currentMessage.copy());
//          }
//       });
//       try {
//          Xml.parse(stream, Xml.Encoding.UTF_8, root.getContentHandler());
//       } catch (Exception e) {
//          throw new RuntimeException(e);
//       }
//       for (Panel panel : messages) {
//          Log.d("tt", "id:"+panel.getId() + "name:" + panel.getName());
//      }
       /*return panelList;*/
    	ORGetPanelsConnectionDelegate delegate = new ORGetPanelsConnectionDelegate();
    	new ORConnection(context ,ORHttpMethod.GET, true, serverUrl + "/rest/panels", delegate);
    	return delegate.getPanelsName();
    }
    
    public static int downLoadPanelXml(Context context, String serverUrl, String panelName) {
       return downLoadFile(context, serverUrl + "/rest/panel/" + panelName, Constants.PANEL_XML);
    }
    
    public static int downLoadImage(Context context, String serverUrl, String imageName) {
       return downLoadFile(context, serverUrl + "/resources/" + imageName, imageName);
    }
    
    private static int downLoadFile(Context context, String serverUrl, String fileName) {
       HttpParams params = new BasicHttpParams();
       HttpConnectionParams.setConnectionTimeout(params, 5 * 1000);
       HttpConnectionParams.setSoTimeout(params, 5 * 1000);
       HttpClient client = new DefaultHttpClient(params);
       int statusCode = 0;
       try {
         HttpGet get = new HttpGet(serverUrl);
         SecurityUtil.addCredentialToHttpRequest(context, get);
         HttpResponse response = client.execute(get);
         statusCode = response.getStatusLine().getStatusCode();
         if (statusCode == 200) {
            FileOutputStream fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            InputStream is = response.getEntity().getContent();
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0) {
               fOut.write(buf, 0, len);
            }
            fOut.close();
            is.close();
         }
      } catch (ClientProtocolException e) {
         throw new ORConnectionException("Httpclient execute httprequest fail.", e);
      } catch (IOException e) {
         throw new ORConnectionException("Httpclient execute httprequest fail.", e);
      }
      return statusCode;
    }
}
