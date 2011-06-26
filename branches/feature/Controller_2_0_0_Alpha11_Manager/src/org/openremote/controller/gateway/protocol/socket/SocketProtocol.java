/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.controller.gateway.protocol.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.openremote.controller.gateway.protocol.Protocol;
import org.openremote.controller.gateway.protocol.ProtocolInterface;
import org.openremote.controller.gateway.EnumGatewayConnectionType;
import org.openremote.controller.gateway.EnumGatewayPollingMethod;
import org.openremote.controller.gateway.command.Action;
import org.openremote.controller.gateway.command.EnumCommandActionType;

/**
 *  SocketProtocol
 * @author P.Lavender 21-06-11
 * 
 */
public class SocketProtocol extends Protocol {

	
	// Common Protocol Properties -------------------------------------------------------------------------------
	/** The logger. */
	private static Logger logger = Logger.getLogger(SocketProtocol.class.getName());
	
	/** A name to indentify protocol in logs*/
	protected String name;
	
	/* Set supported connection Types */
	protected static List<EnumGatewayConnectionType> allowedConnectionTypes = Arrays.asList(EnumGatewayConnectionType.MANAGED, EnumGatewayConnectionType.PERMANENT, EnumGatewayConnectionType.TIMED);
	
	/* Set supported polling methods */
	protected static List<EnumGatewayPollingMethod> allowedPollingMethods = Arrays.asList(EnumGatewayPollingMethod.QUERY, EnumGatewayPollingMethod.BROADCAST);
	
	/* This is the string that marks the end of a command */
	private String sendTerminator = "\n";
	
	private InputStream inputStream;
	private OutputStream outputStream;
	
	// Protocol Properties ----------------------------------------------------------------
	private String host;
	
	private int port;
	
	private Socket socket;
	
	// Protocol Get Set Methods -----------------------------------------------------------
	public List<EnumGatewayConnectionType> getAllowedConnectionTypes() {
		return allowedConnectionTypes;
	}
	
	public List<EnumGatewayPollingMethod> getAllowedPollingMethods() {
		return allowedPollingMethods;
	}

	/**
    * Gets the send terminator
    * 
    * @return the send terminator
    */
	
	public String getSendTerminator() {
		return this.sendTerminator;
	}
	
	/**
	 * Sets the send terminator
	 * @param send terminator
	 */
	public void setSendTerminator(String terminator) {
		this.sendTerminator = terminator;
	}
	
	
   /**
    * Gets the host
    * @return the host
    */
   public String getHost() {
      return this.host;
   }

   /**
    * Sets the host
    * @param host the new host
    */
   public void setHost(String host) {
      this.host = host;
   }

   /**
    * Gets the port
    * @return the port
    */
   public int getPort() {
      return this.port;
   }

	/**
	 * Sets the port
	 * @param port the new port
	 */
   public void setPort(String port) {
      this.port = Integer.parseInt(port);
   }
	
	// Protocol Methods --------------------------------------------------------------------
	public String buildNameString() {
		return "socket://" + getHost() + ":" + getPort() + "/";
	}
	
	/**
	 * Connects to the server and establishes the communication objects
	 * Exception handling is done by the Gateway class
	 */
	public void connect() throws Exception {
		socket = new Socket(getHost(), getPort());
		inputStream = this.socket.getInputStream();
		outputStream = this.socket.getOutputStream();
		
		//Flush out any connection mesages returned by the server on connection
		try{
			Thread.sleep(50);
		} catch (Exception e) {}
		clearBuffer();
	}
	
	/**
	 * Disconnects from the telnet server and nulls the input/output streams
	 * Exception handling is done by the Gateway class    
	 */
	
	 public void disconnect() throws Exception {
		 try{
			 this.socket.close();
		 }catch (IOException e) {/* Todo: Logger*/}
		 inputStream = null;
		 outputStream = null;
	 }
	
	/* Clear the inputStream buffer when requested by the gateway */
	public void clearBuffer() throws Exception {
		while (inputStream.available() != 0) {
			inputStream.read();
		}  
	}
	
	/**
	 * Ue this method to validate a command action for this protocol
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
				// Apply action specifc parameters
				String sendTerminator = this.sendTerminator;
				if (args.containsKey("sendterminator")) {
					sendTerminator = args.get("sendterminator");  
				}
				outputStream.write((args.get("command") + sendTerminator).getBytes());
				outputStream.flush();
				break;
			case READ:
				// Apply action specific parameters
				Calendar endTime = Calendar.getInstance();
				endTime.add(Calendar.MILLISECOND, getReadTimeout());
				while (Calendar.getInstance().before(endTime) && inputStream.available() == 0) {
					try {
						Thread.sleep(50);
					} catch (Exception e) {}
				}
				
				while (inputStream.available() > 0) {
					actionResult += (char) inputStream.read(); // reads a byte at a time
				}
				
				// If data received then assume this is what we're waiting for
				if (actionResult.length() > 0) {
					break;
				}
				break;
		}
		return actionResult;
	}
}
	