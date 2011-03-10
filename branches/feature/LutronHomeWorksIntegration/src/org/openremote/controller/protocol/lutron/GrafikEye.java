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

/**
 * Represents a GrafikEye component on the Lutron bus.
 * This class sends command to the HomeWorks processor for action.
 * It also listen for feedback from the processor and keeps its state up to date.
 * 
 * @author ebr
 *
 */
public class GrafikEye extends HomeWorksDevice {

	/**
	 * Currently selected scene, as reported by the system. Null if we don"t have this info.
	 */
	private Integer selectedScene;

	public Integer getSelectedScene() {
		return selectedScene;
	}

	public GrafikEye(LutronHomeWorksGateway gateway, LutronHomeWorksAddress address) {
		super(gateway, address);
	}

	public void selectScene(Integer scene) {
		this.gateway.sendCommand("GSS, " + address + ", " + scene); 
	}
	
	@Override
	public void processUpdate(String info) {
		selectedScene = Integer.parseInt(info);
	}

}
