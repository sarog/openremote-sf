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

public class Keypad extends HomeWorksDevice 
{

	// Private Instance Fields ----------------------------------------------------------------------

	// TODO: implement LED feedback
	
	// Constructors ---------------------------------------------------------------------------------

	public Keypad(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}
	
	// Command methods ------------------------------------------------------------------------------

	public void press(Integer key) {
		this.gateway.sendCommand("KBP", address, Integer.toString(key));
	}

	public void release(Integer key)
  {
		this.gateway.sendCommand("KBR", address, Integer.toString(key)); 
	}

	public void hold(Integer key)
  {
		this.gateway.sendCommand("KBH", address, Integer.toString(key)); 
	}

	public void doubleTap(Integer key)
  {
		this.gateway.sendCommand("KBDT", address, Integer.toString(key)); 
	}
}
