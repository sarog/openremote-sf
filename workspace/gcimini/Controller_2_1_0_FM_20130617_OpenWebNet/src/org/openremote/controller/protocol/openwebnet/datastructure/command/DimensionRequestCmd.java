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
 * This command represent an OpenWebNet dimension request
 * See OpenWebNet manual for further documentation
 *
 * @author Flavio Crisciani
 */
public class DimensionRequestCmd extends CommandOPEN
{
    // ---- MEMBERS ---- //
    String dimension = null;					// Dimension type (see documentation for possible values)

    // ---- METHODS ---- //
    /**
     * Create a new Dimension request instance
     * @param commandString command in string format
     * @param who who field
     * @param where where field
     * @param dimension dimension type
     */
    public DimensionRequestCmd(String commandString, String who, String where, String dimension)
    {
        super(commandString, 3, who, where);
        this.dimension = dimension;
    }
    /**
     * Create a new Dimension request instance
     * @param who who field
     * @param where where field
     * @param dimension dimension type
     */
    public DimensionRequestCmd(String who, String where, String dimension)
    {
        super("*#" + who + "*" + where + "*" + dimension + "##", 3, who, where);
        this.dimension = dimension;
    }
    /**
     * Generate automatically a new Dimension Request Command passing command as string
     * @param commandString command as a string
     */
    public DimensionRequestCmd(String commandString)
    {
        super(commandString, 3, "", "");
        String string = commandString.substring(2, commandString.length() - 2);
        String[] par = string.split("\\*");
        who = par[0];
        where = par[1];
        dimension = par[2];
    }

    /**
     * Get dimension type
     * @return dimension type
     */
    public String getDimension()
    {
        return dimension;
    }

}
