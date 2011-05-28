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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;

public class LutronHomeWorksCommand implements Command {

	// Class Members --------------------------------------------------------------------------------

	/**
	 * Lutron logger. Uses a common category for all Lutron related logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	private static HashMap<String, Class<? extends LutronHomeWorksCommand>> commandClasses = new HashMap<String, Class<? extends LutronHomeWorksCommand>>();

	static {
		commandClasses.put("RAISE", DimmerCommand.class);
		commandClasses.put("LOWER", DimmerCommand.class);
		commandClasses.put("STOP", DimmerCommand.class);
		commandClasses.put("FADE", DimmerCommand.class);
		commandClasses.put("STATUS_DIMMER", DimmerCommand.class);
		commandClasses.put("SCENE", GrafikEyeCommand.class);
		commandClasses.put("STATUS_SCENE", GrafikEyeCommand.class);
		commandClasses.put("PRESS", KeypadCommand.class);
		commandClasses.put("RELEASE", KeypadCommand.class);
		commandClasses.put("HOLD", KeypadCommand.class);
		commandClasses.put("DOUBLE_TAP", KeypadCommand.class);
		commandClasses.put("STATUS_KEYPADLED", null);
	}

	/**
	 * Factory method for creating Lutron HomeWorks command instances based on a
	 * human-readable configuration strings.
	 * <p>
	 * 
	 * 
	 * @return new Lutron HomeWorks command instance
	 */
	static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key, Integer level) {
		name = name.trim().toUpperCase();

		System.out.println("in command builder");

		Class<? extends LutronHomeWorksCommand> commandClass = commandClasses.get(name);

		System.out.println("Command class for command " + name + " is " + commandClass);

		if (commandClass == null) {
			throw new NoSuchCommandException("Unknown command '" + name + "'.");
		}
		LutronHomeWorksCommand cmd = null;
		try {
			System.out.println("Trying to get method");
			Method method = commandClass.getMethod("createCommand", String.class, LutronHomeWorksGateway.class, LutronHomeWorksAddress.class, Integer.class, Integer.class, Integer.class);

			System.out.println("Have creation method, will call it");
			cmd = (LutronHomeWorksCommand) method.invoke(null, name, gateway, address, scene, key, level);
			System.out.println("Creation method returned " + cmd);
		} catch (SecurityException e) {
			// TODO: should this be logged, check other source code
			throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
		} catch (NoSuchMethodException e) {
			// TODO: should this be logged, check other source code
			throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
		} catch (IllegalArgumentException e) {
			// TODO: should this be logged, check other source code
			throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
		} catch (IllegalAccessException e) {
			// TODO: should this be logged, check other source code
			throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
		} catch (InvocationTargetException e) {
			// TODO: should this be logged, check other source code
			throw new NoSuchCommandException("Impossible to create command '" + name + "'.");
		}
		return cmd;
	}

	// Instance Fields ------------------------------------------------------------------------------

	/**
	 * Gateway to be used to transmit this command.
	 */
	protected LutronHomeWorksGateway gateway;

	protected String name;
	
	// Constructors ---------------------------------------------------------------------------------

	/**
	 * Constructs a Lutron HomeWorks command with a given gateway.
	 * 
	 * @param gateway
	 *            Lutron gatewate instance used for transmitting this commnad
	 */
	public LutronHomeWorksCommand(String name, LutronHomeWorksGateway gateway) {
		this.name = name;
		this.gateway = gateway;
	}

}
