package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;

/**
 * 
 * @author Rich Turner
 *	Static Console Unit which cannot be resized and is the same
 * size as the physical window it is being displayed on
 */
public class FullScreenUnit extends ConsoleUnit {
	public FullScreenUnit(int displayWidth, int displayHeight) {
		super(displayWidth, displayHeight);
	}
}
