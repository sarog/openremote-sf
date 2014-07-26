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
package org.openremote.controller.protocol.openwebnet.datastructure.command;

/**
 * This is the base class of each type of command.
 * It has all the base arguments of each type of message.
 * Keep in mind to extend this class everytime you decide to create a new command class.
 *
 * @author Flavio Crisciani
 */
public class CommandOPEN
{
    // ---- MEMBERS ---- //
    String commandString = null;			// Command as a string
    int commandType = -1;					/* Type of the command:
                                                - 1 = status response command
                                                - 2 = status request command
                                                - 3 = dimension request command
                                                - 4 = dimension write command
                                                - 5 = dimension response command
                                                - 6 = delay command
                                            */
    String who = null;						// Who field
    String where = null;					// Where field

    // ---- METHODS ---- //
    /**
     * Create a new command open instance
     * @param commandString string representing the command
     * @param commandType type of the command
     * @param who who field of the command
     * @param where where field of the command
     */
    public CommandOPEN(String commandString, int commandType, String who, String where)
    {
        super();
        this.commandString = commandString;
        this.commandType = commandType;
        this.who = who;
        this.where = where;
    }

    /**
     * Creates a command open from a string
     * @param commandString string representing the command
     * @return the corresponding command object
     */
    public static CommandOPEN getCommandByString(String commandString)
    {
        CommandOPEN cmd = null;
        String[] par = commandString.substring(1).split("\\*");
        if (par.length > 3)
        {
            if (par[2].charAt(0) != '#')
                cmd = new DimensionResponseCmd(commandString);
            else
                cmd = new DimensionWriteCmd(commandString);
        }
        else if (par.length == 3)
        {
            if (commandString.charAt(1) != '#')
                cmd = new StatusResponseCmd(commandString);
            else
                cmd = new DimensionRequestCmd(commandString);
        }
        else if (par.length == 2)
            cmd = new StatusRequestCmd(commandString);
        return cmd;
    }

    /**
     * Compares a Status Request Command with a Status Response Command
     * @param requestCmd a Status Request Command
     * @param responseCmd a Status Response Command
     * @return true if who and where of the two commands are equal
     */
    public static Boolean statusEquals(StatusRequestCmd requestCmd, StatusResponseCmd responseCmd)
    {
        return (requestCmd.getWho().equals(responseCmd.getWho())
                && requestCmd.getWhere().equals(responseCmd.getWhere()));
    }
    /**
     * Compares a Dimension Request Command with a Dimension Response Command
     * @param requestCmd a Dimension Request Command
     * @param responseCmd a Dimension Response Command
     * @return true if who, where and dimension of the two commands are equal
     */
    public static Boolean dimensionEquals(DimensionRequestCmd requestCmd, DimensionResponseCmd responseCmd)
    {
        return (requestCmd.getWho().equals(responseCmd.getWho())
                && requestCmd.getWhere().equals(responseCmd.getWhere())
                && requestCmd.getDimension().equals(responseCmd.getDimension()));
    }

    @Override
    public String toString()
    {
        return "CommandOPEN [commandString=" + commandString + "]";
    }

    /**
     * Get the command as a string that can be executed
     * @return the command as a string
     */
    public String getCommandString()
    {
        return commandString;
    }

    /**
     * Get the who field
     * @return who
     */
    public int getType()
    {
        return commandType;
    }

    /**
     * Get the who field
     * @return who
     */
    public String getWho()
    {
        return who;
    }

    /**
     * Get the where field
     * @return where
     */
    public String getWhere()
    {
        return where;
    }
}
