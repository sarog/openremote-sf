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
 * This command represent an OpenWebNet dimension write
 * See OpenWebNet manual for further documentation
 *
 * @author Flavio Crisciani
 */
public class DimensionWriteCmd extends CommandOPEN
{
    // ---- MEMBERS ---- //
    String dimension = null;                    // Dimension type (see documentation for possible values)
    String[] values = null;                     // List of values in the message

    // ---- METHODS ---- //
    /**
     * Create a new Dimension write instance
     * @param commandString command in string format
     * @param who who field
     * @param where where field
     * @param dimension dimension type
     * @param values values of the specified dimension
     */
    public DimensionWriteCmd(String commandString, String who, String where, String dimension, String values)
    {
        super(commandString, 4, who, where);
        this.dimension = dimension;
        this.values = values.split("\\*");
    }
    /**
     * Create a new Dimension write instance
     * @param who who field
     * @param where where field
     * @param dimension dimension type
     * @param values values of the specified dimension
     */
    public DimensionWriteCmd(String who, String where, String dimension, String values)
    {
        super("*#" + who + "*" + where + "*#" + dimension + values + "##", 4, who, where);
        this.dimension = dimension;
        this.values = values.split("\\*");
    }
    /**
     * Generate a new Dimension Response Command passing command as string
     * @param commandString command as a string
     */
    public DimensionWriteCmd(String commandString)
    {
        super(commandString, 4, "", "");
        String string = commandString.substring(2, commandString.length() - 2);
        String[] par = string.split("\\*");
        who = par[0];
        where = par[1];
        dimension = par[2].substring(1);
        String[] array_values = new String[par.length - 3];
        for (int i = 3; i < par.length; i++)
            array_values[i - 3] = par[i];
        values = array_values;
    }

    /**
     * Get dimension type
     * @return dimension type
     */
    public String getDimension()
    {
        return dimension;
    }
    /**
     * Get list of values
     * @return
     */
    public String[] getValues()
    {
        return values;
    }

}
