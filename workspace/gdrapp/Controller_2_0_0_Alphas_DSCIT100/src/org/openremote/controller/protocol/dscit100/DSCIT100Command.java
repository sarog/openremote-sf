/**
 * 
 */
package org.openremote.controller.protocol.dscit100;

import org.apache.log4j.Logger;
import org.openremote.controller.command.Command;
import org.openremote.controller.exception.NoSuchCommandException;

/**
 * This class is an abstract superclass for KNX protocol read/write commands.
 * 
 * @see ExecuteCommand
 * 
 * @author Greg Rapp
 * 
 */
public abstract class DSCIT100Command implements Command
{

  // Constants
  // ------------------------------------------------------------------------------------

  // Class Members
  // --------------------------------------------------------------------------------

  /**
   * DSCIT100 logger. Uses a common category for all KNX related logging.
   */
  private final static Logger log = Logger
      .getLogger(DSCIT100CommandBuilder.DSCIT100_LOG_CATEGORY);

  /**
   * Factory method for creating DSCIT100 command instances
   * {@link ExecuteCommand} and {@link ReadCommand} based on a human-readable
   * configuration strings.
   * <p>
   * 
   * Each DSCIT100 command instance is associated with a link to a connection
   * manager and a destination address.
   * 
   * @param name
   *          Command lookup name. This is usually a human-readable string used
   *          in configuration and tools. Note that multiple lookup names can be
   *          used to return Java equal() (but not same instance) commands.
   * @param mgr
   *          DSCIT100 connection manager used to transmit this command
   * @param address
   *          DSCIT100 destination address.
   * 
   * @throws NoSuchCommandException
   *           if command cannot be created by its lookup name
   * 
   * @return new DSCIT100 command instance
   */
  public static DSCIT100Command createCommand(String name, String address,
      String code, String target, DSCIT100ConnectionManager mgr)
  {
    name = name.trim().toUpperCase();

    ExecuteCommand execCommand = ExecuteCommand.createCommand(name, address,
        code, target, mgr);
    if (execCommand != null)
      return execCommand;

    ReadCommand readCommand = ReadCommand.createCommand(name, address, target,
        mgr);
    if (readCommand != null)
      return readCommand;

    throw new NoSuchCommandException("Unknown command '" + name + "'.");
  }

  // Private Instance Fields
  // ----------------------------------------------------------------------

  /**
   * Destination address for this command.
   */
  protected String address;

  /**
   * Target partition or zone for this command.
   */
  protected String target;

  /**
   * Connection manager to be used to transmit this command.
   */
  private DSCIT100ConnectionManager connectionManager;

  // Constructors
  // ---------------------------------------------------------------------------------

  /**
   * @param address
   *          IT100 address
   * @param target
   *          Target partition or zone
   * @param connectionManager
   */
  public DSCIT100Command(String address, String target,
      DSCIT100ConnectionManager connectionManager)
  {
    this.address = address;
    this.target = target;
    this.connectionManager = connectionManager;
  }

  // Package-Private Instance Methods
  // -------------------------------------------------------------

  protected void write(ExecuteCommand command)
  {
    try
    {
      DSCIT100Connection connection = connectionManager.getConnection(address);
      if (connection != null)
        connection.send(command);
    }
    catch (Exception e)
    {
      log.error("Unable to send " + this + " : " + e.getMessage(), e);
    }
  }

  /**
   * Read the internal state map of the associated connection.
   * 
   * TODO : call semantics on return value
   * 
   * @param command
   *          DSCIT100 read command
   * 
   * @return Returns a State object
   * 
   *         NOTE: may return <code>null</code> in case there's a connection
   *         exception or the state map is not populated.
   */
  protected PanelState.State read(ReadCommand command)
  {
    try
    {
      DSCIT100Connection connection = connectionManager.getConnection(address);
      if (connection != null)
        return connection.getState(command.getStateDefinition());
      else
      {
        log.error("Could not get connection instance for address : " + address);
        return null;
      }
    }
    catch (Exception e)
    {
      log.error("Unable to read " + this + " : " + e.getMessage(), e);
    }
    return null;
  }

}
