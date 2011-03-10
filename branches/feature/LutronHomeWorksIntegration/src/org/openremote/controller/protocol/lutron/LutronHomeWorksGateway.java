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
package org.openremote.controller.protocol.lutron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketException;
import java.util.HashMap;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.log4j.Logger;
import org.openremote.controller.LutronHomeWorksConfig;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class LutronHomeWorksGateway implements ApplicationListener {

	// TODO: maintain the login/logout state
	// failed logout also ?
	// have a logout command
	// on login, configure lutron as we expect -> no need to do it in HWI :
	// PROMPTOFF, DLMON, GSMON, KLMON
	// process feedback commands received and update devices in the cache
	// handle socket close

	// Have a queue class with management of coalesce and TTL

	// Class Members
	// --------------------------------------------------------------------------------

	/**
	 * Lutron HomeWorks logger. Uses a common category for all Lutron related
	 * logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	private static HashMap<LutronHomeWorksAddress, HomeWorksDevice> deviceCache = new HashMap<LutronHomeWorksAddress, HomeWorksDevice>();

	// Don't ask this to the config factory when instantiating the bean, this
	// results in infinite recursion
	private LutronHomeWorksConfig lutronConfig;

	private TelnetClient tc;
	private LutronHomeWorksReaderThread readerThread;
	private boolean loggedIn = false;

	public LutronHomeWorksGateway() {
		super();
		System.out.println();
	}

	public void startGateway() {
		if (lutronConfig == null) {
			lutronConfig = LutronHomeWorksConfig.readXML();
		}
		System.out.println("Config is : " + lutronConfig.getAddress());

		if (tc == null) {
			tc = new TelnetClient();
			try {
				// tc.connect(lutronConfig.getAddress(),
				// lutronConfig.getPort());
				tc.connect("192.168.1.98", 23);
				System.out.println("Telnet client connected");
				readerThread = new LutronHomeWorksReaderThread();
				readerThread.start();
				System.out.println("reader thread started");
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// For now do not support discovery, use information defined in config

	}

	public void sendCommand(String command) {
		System.out.println("Asked to send command " + command);
		if (tc == null)
			startGateway();
		try {
			System.out.println("tc " + tc);
			System.out.println("tc output stream" + tc.getOutputStream());
			System.out.println("tc os wrtier" + new OutputStreamWriter(tc.getOutputStream()));
			PrintWriter pr = new PrintWriter(new OutputStreamWriter(tc.getOutputStream()));
			System.out.println("Print writer " + pr);
			pr.println(command);
			pr.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the HomeWorks device from the cache, creating it if not already
	 * present.
	 * 
	 * @param address
	 * @return
	 * @return
	 */
	public HomeWorksDevice getHomeWorksDevice(LutronHomeWorksAddress address, Class<? extends HomeWorksDevice> deviceClass) {
		HomeWorksDevice device = deviceCache.get(address);
		if (device == null) {
			// No device yet in the cache, try to create one
			try {
				Constructor<? extends HomeWorksDevice> constructor = deviceClass.getConstructor(LutronHomeWorksGateway.class, LutronHomeWorksAddress.class);
				device = constructor.newInstance(this, address);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (!(deviceClass.isInstance(device))) {
			throw new RuntimeException("Invalid device type found at given address"); // TODO,
																						// have
																						// a
																						// typed
																						// exception
		}
		return device;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextRefreshedEvent) {
			if ("Root WebApplicationContext".equals(((ContextRefreshedEvent) applicationEvent).getApplicationContext().getDisplayName())) {
				startGateway();
			}
		}
	}

	private class LutronHomeWorksReaderThread extends Thread {

		@Override
		public void run() {
			System.out.println("Reader thread started");
			System.out.println("isInterrupted " + isInterrupted());
			System.out.println("TC " + tc);
			System.out.println("TC input stream " + tc.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(tc.getInputStream()));
			System.out.println("Buffered reader " + br);

			while (true /* TODO: !isInterrupted() */) {
				try {
					System.out.println("Before read line");
					String line = br.readLine();
					System.out.println("Reader thread got line >" + line + "<");
					if (line.startsWith("LOGIN:")) {
						PrintWriter pr = new PrintWriter(new OutputStreamWriter(tc.getOutputStream()));
						pr.println("iphone,iphone");
						pr.flush();
						System.out.println("Has sent login");
					} else if (line.startsWith("login successful")) {
						loggedIn = true;
					} else if (line.startsWith("closing connection")) {
						loggedIn = false;

						// TODO: call a helper method parsing the string as a
						// LutronResponse, if not handled -> unknown, if handle
						// -> returns a structure that makes sense -> command /
						// address / parameter

					} else {
						LutronResponse response = parseResponse(line);
						if (response != null) {
							if ("GSS".equals(response.response)) {
								// GrafikEye scene feedback: GSS, [01:05:01], 1
								GrafikEye ge = (GrafikEye) getHomeWorksDevice(response.address, GrafikEye.class);
								if (ge != null) {
									ge.processUpdate(response.parameter);
								}
							} else if ("KLS".equals(response.response)) {
								// Keypad LED feedback: KLS, [01:06:01], 110000001000010000000000
								// Do not handle for now
							}
						} else {
							// Unknown response
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private LutronResponse parseResponse(String responseText) {
		LutronResponse response = null;
		
		String[] parts = responseText.split(",");
		// All the responses we currently understand have 3 components
		if (parts.length != 3) {
			try {
				response = new LutronResponse();
				response.response  = parts[0];
				response.address = new LutronHomeWorksAddress(parts[1]);
				response.parameter = parts[2];
			} catch (InvalidLutronHomeWorksAddressException e) {
				// Invalid address, consider we got invalid response from Lutron
				response = null;
			}
		}
		return response;
	}

	private class LutronResponse {

		public String response;
		public LutronHomeWorksAddress address;
		public String parameter;

	}

}
