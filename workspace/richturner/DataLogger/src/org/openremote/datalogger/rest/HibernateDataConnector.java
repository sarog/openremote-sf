/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2012, OpenRemote Inc.
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
package org.openremote.datalogger.rest;

import java.util.Date;
import java.util.HashMap;

import org.openremote.datalogger.exception.DataConnectorException;

/**
 *
 * 
 * @author <a href="mailto:richard@openremote.org">Richard Turner</a>
 *
 */
public class HibernateDataConnector implements DataConnector {

	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#init()
	 */
	@Override
	public boolean init() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#setCurrentFeedValue(java.lang.String, java.lang.Double)
	 */
	@Override
	public void setFeedCurrentValue(String apiKey, String feedId, String value) throws DataConnectorException {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#addFeedValues(java.lang.String, java.util.HashMap)
	 */
	@Override
	public void addFeedValues(String apiKey, String feedId, HashMap<Date, String> values) throws DataConnectorException {

		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.openremote.datalogger.rest.DataConnector#finish()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
