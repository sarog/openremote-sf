package org.openremote.web.console.types;

import org.openremote.web.console.client.ConsoleUnit;

/**
 * 
 * @author Rich Turner
 *	Miniature Console Unit which cannot be resized and is the smaller
 * than the physical window it is being displayed on
 */
public class MiniatureUnit extends ConsoleUnit {

	public MiniatureUnit(int displayWidth, int displayHeight) {
		super(displayWidth, displayHeight);
	}
}
