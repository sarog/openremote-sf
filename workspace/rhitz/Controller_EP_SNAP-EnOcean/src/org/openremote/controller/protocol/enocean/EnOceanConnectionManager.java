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
package org.openremote.controller.protocol.enocean;

import org.openremote.controller.protocol.enocean.packet.Esp3Processor;
import org.openremote.controller.protocol.enocean.port.Esp3ComPortAdapter;
import org.openremote.controller.protocol.enocean.port.EspPortConfiguration;
import org.openremote.controller.utils.Logger;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

/**
 * EnOcean connection manager for providing an established connection to the EnOcean module.
 *
 * @author Rainer Hitz
 */
public class EnOceanConnectionManager
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * EnOcean logger. Uses a common category for all EnOcean related logging.
   */
  private final Logger log = Logger.getLogger(EnOceanCommandBuilder.ENOCEAN_LOG_CATEGORY);


  // Private Instance Fields ----------------------------------------------------------------------

  /**
   * Map contains all established connections using the COM port as the key.
   */
  HashMap<String, EnOceanConnection> connections = new HashMap<String, EnOceanConnection>();


  // Constructors ---------------------------------------------------------------------------------

  /**
   * Constructs an EnOcean connection manager instance.
   */
  public EnOceanConnectionManager()
  {
    // add shutdown hook in an attempt to avoid leaving open connections behind
    log.debug("Adding shutdown hook to manage unclosed EnOcean connections in case of controller exit.");
    addShutdownHook();
  }


  // Public Instance Methods ----------------------------------------------------------------------

  /**
   * Returns an already established connection if available or creates a new one.
   *
   * @param   configuration  EnOcean serial protocol (ESP) port configuration
   *
   * @param   listener       listener which will be notified from the returned connection
   *                         if a radio telegram has been received
   *
   * @return  established connection to the EnOcean module
   *
   * @throws ConfigurationException
   *           if connection cannot be established because of an configuration error
   *
   * @throws ConnectionException
   *           if connection cannot be established because of an connection error
   */
  public synchronized EnOceanConnection getConnection(EspPortConfiguration configuration, RadioTelegramListener listener)
      throws ConfigurationException, ConnectionException
  {
    if(configuration == null)
    {
      throw new IllegalArgumentException("null configuration");
    }

    if(configuration.getComPort() == null)
    {
      throw new ConfigurationException("Missing COM port configuration.");
    }

    if(connections.containsKey(configuration.getComPort()))
    {
      return connections.get(configuration.getComPort());
    }

    EnOceanConnection connection = createEsp3Connection(configuration, listener);

    connections.put(configuration.getComPort(), connection);

    return connection;
  }


  // Protected Instance Methods -------------------------------------------------------------------

  /**
   * Returns a new ESP3 connection instance.
   *
   * @param   configuration  EnOcean serial protocol (ESP) port configuration
   *
   * @param   listener       listener which will be notified from the returned connection
   *                         if a radio telegram has been received
   *
   * @return  new ESP3 connection instance
   *
   * @throws ConfigurationException
   *           if connection cannot be established because of an configuration error
   *
   * @throws ConnectionException
   *           if connection cannot be established because of an connection error
   */
  protected EnOceanConnection createEsp3Connection(EspPortConfiguration configuration, RadioTelegramListener listener)
      throws ConfigurationException, ConnectionException
  {
    Esp3ComPortAdapter port = new Esp3ComPortAdapter(configuration);
    Esp3Processor processor = new Esp3Processor(port);
    Esp3Connection connection = new Esp3Connection(processor, listener);
    processor.setProcessorListener(connection);

    connection.connect();

    return connection;
  }

  // Private Instance Methods ---------------------------------------------------------------------


  /**
   * Registers a shutdown hook in the JVM to attempt to close any open EnOcean
   * connections when JVM process is killed. <p>
   *
   * Adding shutdown hook in a privileged code block -- as long as calling code
   * has sufficient security permissions, we don't require additional
   * permissions for this operation.
   */
  private void addShutdownHook()
  {
    try
    {
      final Thread shutdown = new Thread(new Shutdown());

      // BEGIN PRIVILEGED CODE BLOCK
      // --------------------------------------------------------------
      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {
        public Void run()
        {
          Runtime.getRuntime().addShutdownHook(shutdown);

          return null;
        }
      });
      // END PRIVILEGED CODE BLOCK
      // ----------------------------------------------------------------

    }
    catch (SecurityException exception)
    {
      log.warn(
          "Cannot register shutdown hook. Most likely due to lack of security permissions " +
          "in the JVM security manager. DSC IT100 connection manager service will operate normally " +
          "but may be unable to clean up all the connection resources in case of an unexpected " +
          "shutdown (security exception: " + exception.getMessage() + ")", exception
      );
    }
    catch (IllegalStateException exception)
    {
      log.error(
          "Unable to register shutdown hook due to illegal state exception (" +
          exception.getMessage() + "). This may be due to the JVM already starting the " +
          "shutdown process.", exception
      );
    }
  }

  /**
   * Implements shutdown hook for the EnOcean connection manager.
   */
  private class Shutdown implements Runnable
  {
    public void run()
    {
      for (String key : connections.keySet())
      {
        try
        {
          log.debug("Executing JVM shutdown hook to close EnOcean connections...");

          EnOceanConnection connection = connections.get(key);
          if (connection != null)
          {
            connection.disconnect();
          }
        }
        catch (Throwable t)
        {
          log.error("Closing connection failed: " + t.getMessage(), t);
        }
      }
    }
  }
}
