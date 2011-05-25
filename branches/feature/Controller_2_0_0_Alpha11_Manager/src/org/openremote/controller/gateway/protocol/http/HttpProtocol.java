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
package org.openremote.controller.gateway.protocol.http;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.net.HttpURLConnection;
import org.apache.log4j.Logger;
import java.net.URL;
import java.net.MalformedURLException;
import org.openremote.controller.utils.ProtocolUtil;
import org.openremote.controller.gateway.Gateway;
import org.openremote.controller.gateway.exception.GatewayException;
import org.openremote.controller.gateway.protocol.Protocol;
import org.openremote.controller.gateway.protocol.ProtocolInterface;
import org.openremote.controller.gateway.EnumGatewayConnectionType;
import org.openremote.controller.gateway.EnumGatewayPollingMethod;
import org.openremote.controller.gateway.command.Action;
import org.openremote.controller.gateway.command.EnumCommandActionType;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The HTTP Protocol.
 * 
 * Author Rich Turner 2011-04-10
 */
public class HttpProtocol extends Protocol {

   // Common Protocol Properties --------------------------------------------------------
   /** The logger. */
   private static Logger logger = Logger.getLogger(HttpProtocol.class.getName());

   /** A name to identify protocol in logs */
   protected String name;
   
   /* Set supported connection Types */
   protected static List<EnumGatewayConnectionType> allowedConnectionTypes = Arrays.asList(EnumGatewayConnectionType.NONE);
   
   /* Set supported polling methods */
   protected static List<EnumGatewayPollingMethod> allowedPollingMethods = Arrays.asList(EnumGatewayPollingMethod.QUERY);

   /* Set supported polling methods */
   List<String> allowedHttpMethods = Arrays.asList("GET", "POST");
   
   private String responseBuffer;
   
   private String DEFAULT_METHOD = "GET";
   
   private String DEFAULT_PORT = "80";
   
   private String DEFAULT_CONTENT_TYPE = "application/x-www-form-urlencoded";
   
   // Protocol Properties ----------------------------------------------------------------
   String host = "";
   
   String port = DEFAULT_PORT;
   
   String method = DEFAULT_METHOD;
   
   String contentType = DEFAULT_CONTENT_TYPE;
   
   // Protocol Get Set Methods -----------------------------------------------------------
   public List<EnumGatewayConnectionType> getAllowedConnectionTypes() {
      return allowedConnectionTypes;
   }
   
   public List<EnumGatewayPollingMethod> getAllowedPollingMethods() {
      return allowedPollingMethods;
   }
   
   /**
    * Gets the host
    * 
    * @return the host
    */
   public String getHost() {
      return host;
   }

   /**
    * Sets and validates the host
    * 
    * @param host
    *           the new host
    */
   public void setHost(String host) {
   	this.host = host;
   }
   
   /**
    * Gets the port
    * 
    * @return the port
    */
   public String getPort() {
      return this.port;
   }

   /**
    * Sets the port 
    * @param port the new port
    */
   public void setPort(String port) {
      this.port = port;
   }
   
   /**
    * Gets the method
    * 
    * @return the method
    */
   public String getMethod() {
      return method;
   }

   /**
    * Sets the method
    * 
    * @param method
    *           the new method
    */
   public void setMethod(String method) {
      if (allowedHttpMethods.contains(method.toUpperCase())) {
         this.method = method.toUpperCase();
      } else {
      	logger.warn("HTTP method '" + method + "' is not supported.");
      }
   }
   
   /**
    * Gets the content type
    * 
    * @return the content type
    */
   public String getContentType() {
      return contentType;
   }

   /**
    * Sets the content type
    * 
    * @param contentType
    *           the new contentType
    */
   public void setContentType(String contentType) {
	   this.contentType = contentType.toLowerCase();
   }

   // Protocol Methods --------------------------------------------------------------------
   public String buildNameString() {
      return "http://" + getHost() + ":" + getPort() + "/";
   }
   
   /**
    * Use this method to validate a command action for this protocol
    */
   public Boolean isValidAction(Action action) {
      // Ensure required args are supplied and action type is supported
      Boolean result = true;
      return result;
   }
   
   /**
    * Perform protocol action usually send and read actions
    */
   public String doAction(Action commandAction) throws Exception {
      Map<String, String> args = commandAction.getArgs();
      EnumCommandActionType actionType = commandAction.getType();
      String actionResult = "";
      
      switch (actionType) {
         case SEND:
         	String method = getMethod();
         	String contentType = getContentType();
         	String url = "http://" + getHost() + ":" + getPort() + "/";
         	String pathUrl = "";
         	String data = "";
         	
            // Apply action specific parameters
            if (args.containsKey("method")) {
               method = args.get("method").toUpperCase();
            } else if (args.containsKey("contenttype")) {
               contentType = args.get("contenttype").toLowerCase();
            } else if (args.containsKey("postdata")) {
               contentType = args.get("postdata").toLowerCase();
            }
            
            if (!allowedHttpMethods.contains(method.toUpperCase())) {
            	throw new GatewayException("Invalid HTTP method specified for command Action.");
            }
            
            String commandStr = Pattern.compile("^/").matcher(args.get("command")).replaceAll("");
            if ("GET".equals(method)) {
            	pathUrl = commandStr;
            } else {
            	String[] argStr = args.get("command").split("\\?");
            	pathUrl = argStr[0];
            	data = argStr[1];
            }
         	
         	url += pathUrl;
         	
         	// Exceptions handled by gateway
         	URL urlObj = new URL(url);
         	HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
         	conn.setUseCaches(false);
         	conn.setRequestMethod(method);
         	conn.setConnectTimeout(getConnectTimeout());
         	
         	if ("POST".equals(method)) {
         		conn.setDoOutput(true);
         		conn.setDoInput(true);
         		
         		conn.setRequestProperty("Content-Type", contentType);
         		conn.setRequestProperty("Content-Length", Integer.toString(data.length()));
         		
            	OutputStream outputStream = conn.getOutputStream();
            	outputStream.write(data.getBytes());
            	outputStream.flush();
            }
         	
         	// Store response in responseBuffer
         	InputStream inputStream = conn.getInputStream();
         	
         	// Check response Code
         	if (conn.getResponseCode() != 200) {
         		Integer responseCode = conn.getResponseCode();
         		conn.disconnect();
         		throw new GatewayException("Invalid response from server, expecting status code 200 but received " + responseCode.toString());
         	}
         	
            Calendar endTime = Calendar.getInstance();
            endTime.add(Calendar.MILLISECOND, getReadTimeout());
            while (Calendar.getInstance().before(endTime) && inputStream.available() == 0) {
               try {
                  Thread.sleep(50);
               } catch (Exception e) {}
            }
               
            while (inputStream.available() > 0) {
               actionResult += (char) inputStream.read();
            }

            // If data received then assume this is what we're waiting for
            if (actionResult.length() > 0) {
            	responseBuffer = actionResult;
            	actionResult = "";
               break;
            }
         	
         	conn.disconnect();
            break;
         case READ:
         	actionResult = responseBuffer;
         	responseBuffer = "";
            break;
      }
      return actionResult;
   }
}