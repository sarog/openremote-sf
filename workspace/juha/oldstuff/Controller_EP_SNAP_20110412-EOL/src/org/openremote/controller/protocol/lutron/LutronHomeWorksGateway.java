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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

public class LutronHomeWorksGateway /*implements ApplicationListener */{

	// TODO:
	// what on failed logout also ?
	// have a logout command on system down
	// security and vacation mode commands

	// Have a queue class with management of coalesce and TTL
	/*
	 * State machine describing the connection / string parsing
	 * not logged in -> send message -> log in
	 * receive log in confirm -> logged in, can send messages
	 * receive invalid log in -> stays not logged in, clear the queue, stops trying, refuse any messages
	 * socket closed -> not logged in, retry process queue
	*/

	// Class Members --------------------------------------------------------------------------------

	/**
	 * Lutron HomeWorks logger. Uses a common category for all Lutron related
	 * logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	private static HashMap<LutronHomeWorksAddress, HomeWorksDevice> deviceCache = new HashMap<LutronHomeWorksAddress, HomeWorksDevice>();

	// Don't ask this to the config factory when instantiating the bean, this
	// results in infinite recursion
	private LutronHomeWorksConfig lutronConfig;

	private MessageQueueWithPriorityAndTTL<LutronCommand> queue = new MessageQueueWithPriorityAndTTL<LutronCommand>();
	
	private LoginState loginState = new LoginState();

	private LutronHomeWorksConnectionThread connectionThread;
	
	public void startGateway() {
		if (lutronConfig == null) {
			lutronConfig = LutronHomeWorksConfig.readXML();
		}
		
		// Check config, report error if any -> TODO: auto discovery ?
		System.out.println("Got Lutron config");
		System.out.println("Address >" + lutronConfig.getAddress() + "<");
		System.out.println("Port >" + lutronConfig.getPort() + "<");
		System.out.println("UserName >" + lutronConfig.getUserName() + "<");
		System.out.println("Password >" + lutronConfig.getPassword() + "<");

		// Starts some thread that has the responsibility to establish connection and keep it alive 
		connectionThread = new LutronHomeWorksConnectionThread();
		connectionThread.start();
	}

	// TODO: can this be called from multiple threads ? If yes, must make sure startGateway is synchronized
	public void sendCommand(String command, LutronHomeWorksAddress address, String parameter) {
		System.out.println("Asked to send command " + command);
		if (connectionThread == null) {
			startGateway();
		}
		queue.add(new LutronCommand(command, address, parameter));
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
		deviceCache.put(address, device);
		return device;
	}

	/*
	@Override
	public void onApplicationEvent(ApplicationEvent applicationEvent) {
		if (applicationEvent instanceof ContextRefreshedEvent) {
			if ("Root WebApplicationContext".equals(((ContextRefreshedEvent) applicationEvent).getApplicationContext().getDisplayName())) {
				startGateway();
			}
		}
	}
	*/

	// ---
	
	private class LutronHomeWorksConnectionThread extends Thread {
		
		TelnetClient tc;
		private LutronHomeWorksReaderThread readerThread;
		private LutronHomeWorksWriterThread writerThread;
		
		@Override
		public void run() {
			if (tc == null) {
				tc = new TelnetClient();
				tc.setConnectTimeout(10000); // TODO: timeout in config ?
				while (!isInterrupted()) {
					try {
						System.out.println("Trying to connect to " + lutronConfig.getAddress() + " on port " + lutronConfig.getPort());
						tc.connect(lutronConfig.getAddress(), lutronConfig.getPort());
						System.out.println("Telnet client connected");
						readerThread = new LutronHomeWorksReaderThread(tc.getInputStream());
						readerThread.start();
						System.out.println("reader thread started");
						writerThread = new LutronHomeWorksWriterThread(tc.getOutputStream());
						writerThread.start();
						// Wait for the read thread to die, this would indicate the connection was dropped
						while (readerThread != null) {
							readerThread.join(1000);
							if (!readerThread.isAlive()) {
								System.out.println("Reader thread is dead, clean and re-try to connect");
								tc.disconnect();
								readerThread = null;								
								writerThread.interrupt();
								writerThread = null;
							}
						}
					} catch (SocketException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
						// We could not connect, sleep for a while before trying again
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						// We could not connect, sleep for a while before trying again
						try {
							Thread.sleep(15000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			// For now do not support discovery, use information defined in config			
		}
		
	}
	
	// ---
	
	private class LutronHomeWorksWriterThread extends Thread {
		
		private OutputStream os;
		
		public LutronHomeWorksWriterThread(OutputStream os) {
			super();
			this.os = os;
		}

		@Override
		public void run() {
			
			while (!isInterrupted()) {
				System.out.println("Writer thread starting");
				{
				PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));
				pr.println("");
				pr.flush();
				}
				synchronized(loginState) {
					while (!loginState.loggedIn) {
						try {
							while (!loginState.needsLogin && !loginState.loggedIn) {
								System.out.println("Not logged in, waiting to be woken up");
								// We're not logged in, wait until the reader thread ask to login and confirms we're logged in
								loginState.wait();
								System.out.println("Woken up on loggedIn, loggedIn: " + loginState.loggedIn + "- needsLogin: " + loginState.needsLogin);
							}
							if (!loginState.loggedIn) {
								// We've been awakened and we're not yet logged in. It means we need to send login info
								PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));
								pr.println(lutronConfig.getUserName() + "," + lutronConfig.getPassword());
								pr.flush();
								loginState.needsLogin = false;
								System.out.println("Sent log in info");
							}
							// We've been awakened and we're logged in, we'll just go out of the loop and proceed with normal execution
						} catch (InterruptedException e) {
							// We'll loop and test again for login
						}
					}
				}
				LutronCommand cmd = queue.blockingPoll();
				if (cmd != null) {
					System.out.println("tc output stream" + os);
					PrintWriter pr = new PrintWriter(new OutputStreamWriter(os));
					System.out.println("Print writer " + pr);
					pr.println(cmd.toString());
					pr.flush();
				}
			}
		}
		
	}

	// ---
	
	private class LutronHomeWorksReaderThread extends Thread {

		private InputStream is;
		
		public LutronHomeWorksReaderThread(InputStream is) {
			super();
			this.is = is;
		}

		@Override
		public void run() {
			System.out.println("TC input stream " + is);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			System.out.println("Buffered reader " + br);

			String line = null;
			try {
				System.out.println("Before read line");
				line = br.readLine();
				System.out.println("Read line " + line);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			do {
				try {
					System.out.println("Reader thread got line >" + line + "<");
					if (line.startsWith("LOGIN: login successful")) {
						synchronized (loginState) {
							loginState.loggedIn = true;
							loginState.invalidLogin = false;
							// We're logged in, notify writer thread to start sending messages
							loginState.notify();
						}
						// Configure Lutron "protocol" as desired
						queue.priorityAdd(new LutronCommand("PROMPTOFF", null, null));
						queue.priorityAdd(new LutronCommand("DLMON", null, null));
						queue.priorityAdd(new LutronCommand("GSMON", null, null));
						queue.priorityAdd(new LutronCommand("KLMON", null, null));
					} else if (line.startsWith("LOGIN:")) {
						System.out.println("Asked to login, wakening writer thread");
						synchronized (loginState) {
							// If we though we were already logged in, reset that
							loginState.loggedIn = false;

							loginState.needsLogin = true;
							// System asks for login, notify writer thread to send it
							loginState.notify();
						}
					} else if (line.startsWith("login incorrect")) {
						// TODO: close the connection, nothing we can do
						synchronized(loginState) {
							loginState.loggedIn = false;
							loginState.invalidLogin = true;
						}
					} else if (line.startsWith("closing connection")) {
						synchronized(loginState) {
							loginState.loggedIn = false;
						}
						// Get out of our read loop, this will terminate the thread
						break;
					} else {
						// Try parsing the line as a feedback / response from the system
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
							} else if ("DL".equals(response.response)) {
								Dimmer dim = (Dimmer)getHomeWorksDevice(response.address, Dimmer.class);
								if (dim != null) {
									dim.processUpdate(response.parameter);
								}
							}
						} else {
							// Unknown response
							// TODO
						}
					}
					line = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} while (line != null && !isInterrupted());
		}

	}

	private LutronResponse parseResponse(String responseText) {
		LutronResponse response = null;
		
		String[] parts = responseText.split(",");
		// All the responses we currently understand have 3 components
		if (parts.length == 3) {
			try {
//				System.out.println("Building response from Lutron feedback");
				response = new LutronResponse();
				response.response  = parts[0].trim();
				response.address = new LutronHomeWorksAddress(parts[1].trim());
				response.parameter = parts[2].trim();
				System.out.println("Response is (" + response.response + "," + response.address + "," + response.parameter + ")");
			} catch (InvalidLutronHomeWorksAddressException e) {
				// Invalid address, consider we got invalid response from Lutron
				response = null;
			}
		}
		return response;
	}
	
	public class LutronCommand {
		
		// TODO: protocol for coalesce
		
		private String command;
		private LutronHomeWorksAddress address;
		private String parameter;

		public LutronCommand (String command, LutronHomeWorksAddress address, String parameter) {
			this.command = command;
			this.address = address;
			this.parameter = parameter;
		}
		
		public String toString() {
			StringBuffer buf = new StringBuffer(command);
			if (address != null) {
				buf.append(", ");
				buf.append(address);
			}
			if (parameter != null) {
				buf.append(", ");
				buf.append(parameter);
			}
			return buf.toString();
		}
	}

	private class LutronResponse {

		public String response;
		public LutronHomeWorksAddress address;
		public String parameter;

	}
	
	private class LoginState {

		/**
		 * Indicates that we must send the login information.
		 */
		public boolean needsLogin;

		/**
		 * Indicates if we're logged into the system, if not commands must be queued.
		 */
		public boolean loggedIn;

		/**
		 * Indicates if we tried logging in and been refused the login, if so do not try again.
		 * TODO: there must be a way to reset this.
		 */
		public boolean invalidLogin;
	}

}
