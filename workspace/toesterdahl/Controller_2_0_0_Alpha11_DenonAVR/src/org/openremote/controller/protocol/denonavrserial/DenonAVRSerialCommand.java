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
package org.openremote.controller.protocol.denonavrserial;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.spring.SpringContext;

/**
 * Implementation of serial support for Denon AV Receivers. 
 * 
 * The following models are supported:
 *   2106, 2803, 2805, 2807*, 3803, 3805, 3806, 4306, 4802, 4806, 5803, 5805
 * 
 * Verified models are indicated with *. Reports on successful use of this protocol on other models are appreciated. 
 * 
 * Not all models support all commands. Which commands work should be self-explaining 
 * and clear from the specification of the equipment. 
 * 
 * Note: This is the first throw at support for DenonAVR devices. 
 * Note: Configuration is not guaranteed to be compatible with future changes.  
 * 
 * @author Torbjörn Österdahl, toesterdahl@ultra-marine.org
 */
public class DenonAVRSerialCommand implements ExecutableCommand, StatusCommand {

	/** The logger. */
	private static Logger logger = Logger.getLogger(DenonAVRSerialCommand.class.getName());

	/** The command to perform the http get request on */
	private String command;

	private Byte[] bytes;
	
	public DenonAVRSerialCommand() {
		init();
	}
	
	private void init() {
		
		Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
		
		logger.info("CommPortIdentifiers :" + e);
		
		while (e.hasMoreElements()) {
			logger.info("CommPortIdentfier: " + e.nextElement());
		}
	}

	/**
	 * Gets the command
	 * 
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command
	 * 
	 * @param command
	 *            the new command
	 */
	public void setCommand(String command) {
		this.command = command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void send() {
		sendCommand();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String read(EnumSensorType sensoryType, Map<String, String> stateMap) {
		logger.info("Read: Not implemented, return an empty message");
		// Not implemented, return an empty message
		return "";
	}

	private void sendCommand() {
		try {
			Properties props = (Properties) SpringContext.getInstance().getBean("denonAVRSerialConfig");
			String commandString = props.getProperty(getCommand());
			byte[] dataBytes = javaStringToAsciiByteArray(commandString);
			logger.info("Command: " + getCommand() + " Command String: " + commandString + " DataBytes: " + Arrays.toString(dataBytes));
			if ("UDP".equals(props.getProperty("connection.type"))) {
				Socket socket = null;
				try {
					InetAddress addr = InetAddress.getByName(props.getProperty("udp.ip"));
					DatagramSocket clientSocket = new DatagramSocket();
					DatagramPacket sendPacket = new DatagramPacket(dataBytes, dataBytes.length, addr, 4008);
					clientSocket.send(sendPacket);
				} catch (Exception e) {
					logger.error("Error sending serial command over UDP", e);
				} finally {
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							logger.error("Error closing socket", e);
						}
					}
				}
			} else {
				try {
					CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(props.getProperty("com.port"));
					SerialPort serialPort = (SerialPort) id.open("ORBController", 2000);
					serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					OutputStream outputStream = serialPort.getOutputStream();
					outputStream.write(dataBytes);
					outputStream.close();
					serialPort.close();
				} catch (Exception e) {
					logger.error("Error sending serial command Port: " + props.getProperty("com.port"), e);
				}
			}
		} catch (Exception e) {
			logger.error("Could not send command: " + getCommand(),e);
		}
	}

	private final String USASCII = "US-ASCII";
	
	private byte[] javaStringToAsciiByteArray(String s) {
		try {
			String message = s+"\r";
			return message.getBytes(USASCII);
		} catch (UnsupportedEncodingException e) {
			logger.error("Unsupported Encoding for converting String to ASCII byte array: " + USASCII);
			throw new RuntimeException("Unsupported Encoding for converting String to ASCII byte array");
		}
	}
}
