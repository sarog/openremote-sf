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

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.exception.NoSuchCommandException;


public class DimmerCommand extends LutronHomeWorksCommand implements ExecutableCommand {

	public static LutronHomeWorksCommand createCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address, Integer scene, Integer key) {
		// Check for mandatory attributes
		if (address == null) {
		    throw new NoSuchCommandException("Address is required for any dimmer command");
		}
		
		return new DimmerCommand(name, gateway, address);
	}

	// Private Instance Fields
	// ----------------------------------------------------------------------

	/**
	 * Destination address for this command.
	 */
	private LutronHomeWorksAddress address;

	// Constructors
	// ---------------------------------------------------------------------------------

	public DimmerCommand(String name, LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(name, gateway);
		this.address = address;
	}

	  // Implements ExecutableCommand -----------------------------------------------------------------

	  /**
	   * {@inheritDoc}
	   */
	  public void send()
	  {
		  Dimmer dimmer = (Dimmer) gateway.getHomeWorksDevice(address, Dimmer.class);
		  if ("RAISE".equals(name)) {
			  dimmer.raise();
		  } else if ("LOWER".equals(name)) {
			  dimmer.lower();
		  } else if ("STOP".equals(name)) {
			  dimmer.stop();
		  }
	  }

}
