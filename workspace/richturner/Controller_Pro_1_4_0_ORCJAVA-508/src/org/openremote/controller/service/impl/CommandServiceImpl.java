/*
 * OpenRemote, the Home of the Digital Home.
 *
 * Copyright 2008-2014, OpenRemote Inc. All rights reserved.
 *
 * The content of this file is available under commercial licensing only. It is *not* distributed
 * with any open source license. Please contact www.openremote.com for licensing.
 */
package org.openremote.controller.service.impl;

import org.openremote.controller.Constants;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.Command;
import org.openremote.controller.service.CommandService;
import org.openremote.controller.service.DeployerCommandListener;
import org.openremote.controller.utils.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Service interface implementation that is used to execute OpenRemote controller commands
 * by command name.
 *
 * @author <a href="mailto:rainer@openremote.org">Rainer Hitz</a>
 */
public class CommandServiceImpl implements CommandService, DeployerCommandListener
{

  // Class Members --------------------------------------------------------------------------------

  /**
   * Logging category for runtime execution of commands.
   */
  private final static Logger log = Logger.getLogger(
      Constants.RUNTIME_COMMAND_EXECUTION_LOG_CATEGORY
  );


  // Private Instance Fields ----------------------------------------------------------------------

  private Map<String, Command> commandMap = new HashMap<String, Command>();


  // Implements CommandService --------------------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public synchronized void execute(String commandName, String commandParam)
      throws NoSuchCommandException
  {
    Command cmd = null;

    if (commandName != null)
    {
      synchronized (commandMap)
      {
        cmd = commandMap.get(commandName.trim());
      }
    }

    if (cmd != null)
    {
      if (commandParam != null)
      {
        cmd.execute(commandParam);
      }

      else
      {
        cmd.execute();
      }
    }

    else
    {
      throw new NoSuchCommandException("Unknown command '" + commandName + "'.");
    }
  }


  // Implements DeployerCommandListener -----------------------------------------------------------

  /**
   * {@inheritDoc}
   */
  @Override public void processNewCommands(Set<Command> commands)
  {
    synchronized (commandMap)
    {
      commandMap.clear();

      for (Command curCmd : commands)
      {
        String cmdName = curCmd.getName();

        if (cmdName != null)
        {
          commandMap.put(cmdName.trim(), curCmd);
        }
      }
    }
  }
}
