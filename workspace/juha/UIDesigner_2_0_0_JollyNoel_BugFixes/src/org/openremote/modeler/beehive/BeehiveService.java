/*
 * OpenRemote, the Home of the Digital Home.
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
package org.openremote.modeler.beehive;

import org.openremote.modeler.domain.User;
import org.openremote.modeler.exception.NetworkException;
import org.openremote.modeler.exception.ConfigurationException;

/**
 * This interface abstracts the <b>client</b> side of the Beehive REST API. <p>
 *
 * Interface is introduced to allow multiple implementations, both in order to
 * evolve with the Beehive API (if the interface methods can be sufficiently
 * supported with new API) and to allow for network service mocking for unit
 * tests.  <p>
 *
 * The goal is to migrate all HTTP related communication to Beehive service
 * through this interface implementation(s). The interface implementations can
 * therefore act as centralized points to outbound communication to Beehive.
 *
 * @author <a href="mailto:juha@openremote.org">Juha Lindfors</a>
 */
public interface BeehiveService <T, U>
{

  // Constants ------------------------------------------------------------------------------------


  // TODO :
  //   These are temporary constants for logging categories. A consistent logging interface
  //   (see also DesignerUIState, UserServiceImpl, knx import) is needed but should come via
  //   a common set of base classes between designer, beehive and controller.

  final static String BEEHIVE_SERVICE_LOG_CATEGORY =
      "OpenRemote.Designer.BeehiveService";
  
  final static String BEEHIVE_NETWORK_PERF_LOG_CATEGORY =
      BEEHIVE_SERVICE_LOG_CATEGORY + ".Performance.Network";

  final static String BEEHIVE_DOWNLOAD_PERF_LOG_CATEGORY =
      BEEHIVE_NETWORK_PERF_LOG_CATEGORY + ".Download";


  /**
   * Downloads an archive containing user account artifacts stored in Beehive.
   *
   * @param user      a reference to user whose account is accessed
   * @param callback  a reference to callback object that can be invoked by the concrete
   *                  implementations to communicate service state back to the caller
   *
   * @return reference to an object that contains the downloaded artifacts for user
   *
   * @throws NetworkException
   *                  If (possibly recoverable) network errors occured during the service
   *                  operation. Network errors may be recoverable in which case this operation
   *                  could be re-attempted. See {@link NetworkException.Severity} for an
   *                  indication of the network error type.
   *
   * @throws ConfigurationException
   *                  If designer configuration error prevents the service from executing
   *                  normally. Often a fatal error type that should be logged/notified to
   *                  admins, configuration corrected and application re-deployed.
   *
   * @throws BeehiveServiceException
   *                  If service implementation specific errors occur. This is a generic exception
   *                  type defined for the interface. Concrete implementations can (and often
   *                  should) implement more specific exception types to indicate issues specific
   *                  to their implementation details.
   *
   */
  U downloadArchive(User user, T callback) throws NetworkException, BeehiveServiceException,
                                                  ConfigurationException;




  // Nested Classes -------------------------------------------------------------------------------


  /**
   * Exception type to indicate potential issues with the Beehive server being
   * connected to that manifest themselves through the client API (such as unexpected
   * or erroneous responses on the REST API).
   */
  public static class ServerException extends BeehiveServiceException
  {
    /**
     * Constructs a new exception with a given message
     *
     * @param msg   exception message
     */
    ServerException(String msg)
    {
      super(msg);
    }

    /**
     * Constructs a new exception with a parameterized message.
     *
     * @param msg       exception message
     * @param params    message parameters
     */
    ServerException(String msg, Object... params)
    {
      super(msg, params);
    }
  }

}
