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
 * This class represent a Status response command in the OpenWebNet
 * For further details check the OpenWebNet reference
 *
 * @author Flavio Crisciani
 */
public class StatusResponseCmd extends CommandOPEN
{
    // ---- MEMBERS ---- //
    String what = null;			// What field of the standard

    // ---- METHODS ---- //
    /**
     * Create a new instance of a Status response command
     * @param commandString command as a string
     * @param who who field
     * @param what what field
     * @param where where field
     */
    public StatusResponseCmd(String commandString, String who, String what, String where)
    {
        super(commandString, 1, who, where);
        this.what = what;
    }
    /**
     * Generate automatically a new Status Response Command passing basic argument
     * @param who who field
     * @param what what field
     * @param where where field
     */
    public StatusResponseCmd(String who, String what, String where)
    {
        super("*" + who + "*" + what + "*" + where + "##", 1, who, where);
        this.what = what;
    }
    /**
     * Generate automatically a new Status Response Command passing command as string
     * @param commandString command as a string
     */
    public StatusResponseCmd(String commandString)
    {
        super(commandString, 1, "", "");
        String string = commandString.substring(1, commandString.length() - 2);
        String[] par = string.split("\\*");
        who = par[0];
        what = par[1];
        where = par[2];
    }

    /**
     * Returns what field
     * @return what field
     */
    public String getWhat()
    {
        return what;
    }
}
