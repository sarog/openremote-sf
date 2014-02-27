/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */

package org.openremote.controller.protocol.isy99;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.net.telnet.TelnetClient;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.openremote.controller.Constants;
import org.openremote.controller.utils.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.tools.internal.xjc.reader.gbind.ConnectedComponent;

/**
 * Sets up a Soap Subscription to ISY device, and then passes status (ST) reported by the ISY to the appropriate
 * commands
 * 
 * @author craigh
 * 
 */
public class Isy99UpdateGateway extends Thread {

   private static Logger logger = Logger.getLogger(Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "isy99");
   private Map<InsteonDeviceAddress, List<StatusChangeListener>> mDevices = new Hashtable<InsteonDeviceAddress, List<StatusChangeListener>>();

   private String mHost;
   private String mUsername;
   private String mPassword;
   private TelnetClient mTelnetClient = null;
   private boolean mIsConnected = false;

   public Isy99UpdateGateway(String hostName, String userName, String password) {
      // TODO handle port passed in with hostName
      mHost = hostName;
      mUsername = userName;
      mPassword = password;
      start();
   }

   /*
    * Look for xml messages from the ISY, and parse those
    */
   private String parseInput(String line) throws XPathExpressionException, SAXException, IOException {
      String returnValue = null;
      if (line != null) {
         if (line.startsWith("<?xml version=\"1.0\"?>")) {
            int postStart = line.indexOf("POST");
            if (postStart > 0) parseXML(line.substring(0, postStart));
         }
      }
      return returnValue;
   }

   /*
    * Detects xml message denoting devices status updates...control is ST, action is new value, node is the address of
    * the Insteon device
    */
   private String parseXML(String xml) throws SAXException, IOException, XPathExpressionException {
      String returnValue = null;
      DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = null;
      try {
         builder = builderFactory.newDocumentBuilder();
      } catch (ParserConfigurationException e) {
         e.printStackTrace();
      }
      try {
         Document document = builder.parse(new InputSource(new ByteArrayInputStream(xml.getBytes())));
         XPath xPath = XPathFactory.newInstance().newXPath();

         String controlExpression = "/Event/control";
         String actionExpression = "/Event/action";
         String nodeExpression = "/Event/node";

         String control = xPath.compile(controlExpression).evaluate(document);
         String action = xPath.compile(actionExpression).evaluate(document);
         String node = xPath.compile(nodeExpression).evaluate(document);

         if ("ST".equals(control)) {
            List<StatusChangeListener> sensors = mDevices.get(new InsteonDeviceAddress(node));
            if (sensors != null) {
               for (StatusChangeListener sensor : sensors) {
                  sensor.update(action);
               }
            }
            logger.debug("update, node: " + node + ",value: " + action);
         }
      } catch (java.net.MalformedURLException ex) {
         ex.printStackTrace();
      }
      return returnValue;
   }

   @Override
   public void run() {

      String line;
      while (true) {
         if (!mIsConnected) connect();
         sendSubscriptionRequest();
         BufferedReader rd = new BufferedReader(new InputStreamReader(mTelnetClient.getInputStream()));
         while (mIsConnected) {
            try {
               line = rd.readLine();
               if (line != null) {
                  logger.debug("Msg received: " + line);
                  // TODO should we watch response to confirm subscription? Right now we just watch for status updates
                  parseInput(line);
               }
            } catch (IOException e) {
               logger.error("Error", e);
            } catch (XPathExpressionException e) {
               logger.error("XPath", e);
            } catch (SAXException e) {
               logger.error("Error", e);
            }
         }
      }
   }

   private void sendSubscriptionRequest() {
      logger.debug("Sending subscription request");
      PrintWriter out = new PrintWriter(mTelnetClient.getOutputStream(), true);
      String subreq = "<s:Envelope><s:Body><u:Subscribe xmlns:u=\"urn:udi-com:service:X_Insteon_Lighting_Service:1\"><reportURL>REUSE_SOCKET</reportURL><duration>infinite</duration></u:Subscribe></s:Body></s:Envelope>";
      String userpassword = mUsername + ":" + mPassword;
      String encodedAuthorization = new String(Base64.encodeBase64((userpassword.getBytes())));

      out.println("POST /services HTTP/1.1");
      out.println("Host: 192.168.0.80:80");
      out.println("Content-Type: text/xml; charset=\"utf-8\"");
      out.println("Authorization: Basic " + encodedAuthorization);
      out.println("Content-Length: " + subreq.length());
      out.println("SOAPAction:\"urn:udi-com:device:X_Insteon_Lighting_Service:1#Subscribe\"");
      out.println();
      out.println(subreq);
      out.flush();
   }

   private void connect() {

      int nextRetryDelay = 1;

      mIsConnected = false;

      mTelnetClient = new TelnetClient();
      while (!mIsConnected) {
         // max we will wait is 1 hour, so if wait is larger, let's cap it
         if (nextRetryDelay > 1 * 1000 * 60 * 60) nextRetryDelay = 1 * 1000 * 60 * 60;

         // we wait....
         if (nextRetryDelay > 1) {
            logger.debug("Waiting: " + nextRetryDelay + " seconds");
            try {
               Thread.sleep(1000 * nextRetryDelay);
            } catch (InterruptedException e1) {
               logger.error("thread interrupted", e1);
            }
         }
         try {
            logger.debug("Starting connect");
            mTelnetClient.connect(mHost, 80);
            mIsConnected = true;
            logger.debug("Telnet connection active");
         } catch (SocketException e) {
            logger.error("ISY not available at '" + mHost + "'", e);
            nextRetryDelay = nextRetryDelay * 2;
         } catch (IOException e) {
            logger.error("IOException", e);
            nextRetryDelay = nextRetryDelay * 2;
         }
      }

   }

   public void addStatusChangeListener(InsteonDeviceAddress insteonAddress, StatusChangeListener listener) {

      List<StatusChangeListener> listeners = mDevices.get(insteonAddress);
      if (listeners == null) {
         listeners = new ArrayList<StatusChangeListener>();
         mDevices.put(insteonAddress, listeners);
      }
      listeners.add(listener);
   }

   public void removeStatusChangeListener(InsteonDeviceAddress insteonAddress, StatusChangeListener listener) {
      List<StatusChangeListener> listeners = mDevices.get(insteonAddress);
      if (listeners != null) listeners.remove(listener);
   }
}
