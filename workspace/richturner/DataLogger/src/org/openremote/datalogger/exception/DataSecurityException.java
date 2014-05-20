/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2014, OpenRemote Inc.
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
package org.openremote.datalogger.exception;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class DataSecurityException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6866137482159742864L;

	/**
	 * @param msg
	 */
	public DataSecurityException(String msg) {
		super(msg);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param msg
	 * @param e
	 */
	public DataSecurityException(String msg, Exception e) {
		super(msg, e);
		// TODO Auto-generated constructor stub
	}


	public DataSecurityException(Throwable e) {
		super(e);
	// TODO Auto-generated constructor stub
	}
}
