/*
    Calimero - A library for KNX network access
    Copyright (C) 2006-2008 W. Kastner

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package test.tuwien.auto.calimero.log;

import junit.framework.TestCase;
import tuwien.auto.calimero.log.LogLevel;

/**
 * @author B. Malinowsky
 */
public class LogLevelTest extends TestCase
{

	/**
	 * Test method for {@link tuwien.auto.calimero.log.LogLevel#equals(java.lang.Object)}.
	 */
	public void testEqualsObject()
	{
		assertTrue(LogLevel.ALL.equals(LogLevel.ALL));
		assertTrue(LogLevel.INFO.equals(LogLevel.INFO));
		assertFalse(LogLevel.TRACE.equals(LogLevel.OFF));
		assertFalse(LogLevel.WARN.equals(LogLevel.INFO));
		assertFalse(LogLevel.FATAL.equals(new Object()));
		assertFalse(LogLevel.FATAL.equals(null));
	}

	/**
	 * Test method for {@link tuwien.auto.calimero.log.LogLevel#higher
	 * (tuwien.auto.calimero.log.LogLevel)}.
	 */
	public void testHigher()
	{
		assertTrue(LogLevel.ALL.higher(LogLevel.TRACE));
		assertTrue(LogLevel.TRACE.higher(LogLevel.INFO));
		assertTrue(LogLevel.INFO.higher(LogLevel.WARN));
		assertTrue(LogLevel.WARN.higher(LogLevel.ERROR));
		assertTrue(LogLevel.ERROR.higher(LogLevel.FATAL));
		assertTrue(LogLevel.FATAL.higher(LogLevel.OFF));
		assertFalse(LogLevel.OFF.higher(LogLevel.OFF));
	}

}
