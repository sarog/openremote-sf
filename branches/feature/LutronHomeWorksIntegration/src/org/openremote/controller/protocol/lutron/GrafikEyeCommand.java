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

import java.util.Map;

import org.apache.log4j.Logger;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.command.StatusCommand;
import org.openremote.controller.component.EnumSensorType;
import org.openremote.controller.exception.NoSuchCommandException;

public class GrafikEyeCommand extends LutronHomeWorksCommand implements ExecutableCommand, StatusCommand {

	// Class Members
	// --------------------------------------------------------------------------------

	/**
	 * Lutron logger. Uses a common category for all Lutron related logging.
	 */
	private final static Logger log = Logger.getLogger(LutronHomeWorksCommandBuilder.LUTRON_LOG_CATEGORY);

	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key) {
		
		log.info("createCommand ("+ name + "," + gateway + "," + address + "," + scene + "," +key + ")");

		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any GrafikEye command");
		}
		
		if (scene == null) {
		    throw new NoSuchCommandException("Scene is required for any GrafikEye command");
		}
		
		return new GrafikEyeCommand(name, gateway, address, scene);
	}

	// Private Instance Fields
	// ----------------------------------------------------------------------

	/**
	 * Destination address for this command.
	 */
	private LutronHomeWorksAddress address;
	
	/**
	 * Number of the scene this command must select.
	 */
	private Integer scene;

	// Constructors
	// ---------------------------------------------------------------------------------

	public GrafikEyeCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene) {
		super(name, gateway);
		this.address = address;
		this.scene = scene;
	}

	  // Implements ExecutableCommand -----------------------------------------------------------------

	  /**
	   * {@inheritDoc}
	   */
	  public void send()
	  {
		  GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
		  grafikEye.selectScene(scene);
	  }

	@Override
	public String read(EnumSensorType sensorType, Map<String, String> stateMap) {
		System.out.println("GrafikEye.read, sensorType " + sensorType);
		System.out.println("State map " + stateMap);
		GrafikEye grafikEye = (GrafikEye) gateway.getHomeWorksDevice(address, GrafikEye.class);
		return Integer.toString(grafikEye.getSelectedScene());
	}
}
