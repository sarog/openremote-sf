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
package org.openremote.controller.protocol.russound;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.spring.SpringContext;

/**
 * @author Marcus 2009-4-26
 */
public class RussoundCommand implements ExecutableCommand, StatusCommand {

	/** The logger. */
	private static Logger logger = Logger.getLogger(RussoundCommand.class.getName());

	/** The command to perform the http get request on */
	private String command;

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
		// Not implemented, return an empty message
		return "";
	}

	private void sendCommand() {
		try {
			Properties props = (Properties) SpringContext.getInstance().getBean("russoundConfig");
			byte[] dataBytes = hexStringToByteArray(props.getProperty(getCommand()).replaceAll(" ", "").toLowerCase());
			logger.info("Command: " + getCommand() + " DataBytes: " + dataBytes);
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
					serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
					OutputStream outputStream = serialPort.getOutputStream();
					outputStream.write(dataBytes);
					outputStream.close();
					serialPort.close();
				} catch (Exception e) {
					e.printStackTrace();
					logger.error("Error sending serial command", e);
				}

			}
		} catch (Exception e) {
			logger.error("Could not send command: " + getCommand(),e);
		}
	}

	private byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
