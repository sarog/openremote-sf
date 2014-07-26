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
package org.openremote.controller.protocol.openwebnet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import java.util.List;

import org.jdom.Element;
import org.openremote.controller.Constants;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.openwebnet.datastructure.command.*;
import org.openremote.controller.protocol.openwebnet.exception.MalformedCommandOPEN;
import org.openremote.controller.utils.CommandUtil;
import org.openremote.controller.utils.Logger;
import org.openremote.controller.utils.Strings;

/**
 * Builds OWN command from XML element. example:
 *
 * <pre>
 * {@code
 * <command id="xxx" protocol="openwebnet">
 *     <property name="url" value="http://127.0.0.1:20000" />
 * </command>
 * }
 * </pre>
 *
 * @author Marco Miccini
 */
public class OpenWebNetCommandBuilder implements CommandBuilder
{

    // Constants
    // ------------------------------------------------------------------------------------
    /**
     * Common log category name for all OWN protocol related logging.
     */
    public final static String OPENWEBNET_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "openwebnet";

    private final static String STR_ATTRIBUTE_NAME_URL = "url";
    private final static String STR_ATTRIBUTE_NAME_TYPE = "type";
    private final static String STR_ATTRIBUTE_NAME_WHO = "who";
    private final static String STR_ATTRIBUTE_NAME_WHAT = "what";
    private final static String STR_ATTRIBUTE_NAME_WHERE = "where";
    private final static String STR_ATTRIBUTE_NAME_DIMENSION = "dimension";
    private final static String STR_ATTRIBUTE_NAME_VALUES = "values";
    private final static String STR_ATTRIBUTE_NAME_POLLINGINTERVAL = "pollingInterval";
    private final static String STR_ATTRIBUTE_NAME_TIMEOUT = "timeout";
    private final static String STR_ATTRIBUTE_NAME_SENSORS_NAME_LIST = "sensorNamesList";

    // Class Members
    // --------------------------------------------------------------------------------
    /**
     * Logger for this OWN protocol implementation.
     */
    private final static Logger logger = Logger.getLogger(OPENWEBNET_PROTOCOL_LOG_CATEGORY);

    // Instance Fields ------------------------------------------------------------------------------
    /**
     * Avoids duplicate commands
     */
    private Hashtable<String, Command> cachedCommands = new Hashtable<String, Command>();

    // Implements CommandBuilder
    // --------------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public Command build(Element element)
    {
        String commandId = element.getAttribute("id").getValue();
        String commandParam = element.getAttributeValue(Command.DYNAMIC_VALUE_ATTR_NAME);
        if ((cachedCommands.get(commandId) != null) && (commandParam == null))      //only use cached command if we don't have dynamic values
        {
            logger.debug("Found cached OpenWebNet command with id: " + commandId);
            return cachedCommands.get(commandId);
        }

        logger.debug("Building OpenWebNetCommand");
        List<Element> propertyEles = element.getChildren("property", element.getNamespace());
        String urlAsString = null;
        String type = null;
        String who = null;
        String what = null;
        String where = null;
        String dimension = null;
        String values = null;
        String interval = null;
        String timeout = null;
        String sensorNamesList = null;
        Integer intervalInMillis = null;
        Integer timeoutInMillis = null;

        // read values from config xml

        for (Element ele : propertyEles)
        {
            String elementName = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_NAME);
            String elementValue = ele.getAttributeValue(CommandBuilder.XML_ATTRIBUTENAME_VALUE);

            if (STR_ATTRIBUTE_NAME_URL.equals(elementName))
            {
                urlAsString = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("OpenWebNetCommand: url = " + urlAsString);
            } else if (STR_ATTRIBUTE_NAME_TYPE.equals(elementName))
            {
                type = elementValue;
                logger.debug("OpenWebNetCommand: type = " + type);
            } else if (STR_ATTRIBUTE_NAME_WHO.equals(elementName))
            {
                who = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("OpenWebNetCommand: who = " + who);
            } else if (STR_ATTRIBUTE_NAME_WHAT.equals(elementName))
            {
                what = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("OpenWebNetCommand: what = " + what);
            } else if (STR_ATTRIBUTE_NAME_WHERE.equals(elementName))
            {
                where = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("OpenWebNetCommand: where = " + where);
            } else if (STR_ATTRIBUTE_NAME_DIMENSION.equals(elementName))
            {
                dimension = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("OpenWebNetCommand: dimension = " + dimension);
            } else if (STR_ATTRIBUTE_NAME_VALUES.equals(elementName))
            {
                values = CommandUtil.parseStringWithParam(element, elementValue);
                logger.debug("OpenWebNetCommand: values = " + values);
            } else if (STR_ATTRIBUTE_NAME_POLLINGINTERVAL.equals(elementName))
            {
                interval = elementValue;
                logger.debug("OpenWebNetCommand: pollingInterval = " + interval);
            } else if (STR_ATTRIBUTE_NAME_TIMEOUT.equals(elementName))
            {
                timeout = elementValue;
                logger.debug("OpenWebNetCommand: timeout = " + timeout);
            } else if (STR_ATTRIBUTE_NAME_SENSORS_NAME_LIST.equals(elementName))
            {
                sensorNamesList = elementValue;
                logger.debug("OpenWebNetCommand: sensorNamesList = " + sensorNamesList);
            }
        }

        CommandOPEN command = null;
        if (type.equals("Status Command"))
        {
            if (who.equals("") || what.equals("") || where.equals(""))
                throw new MalformedCommandOPEN("*" + who + "*" + what + "*" + where + "##");
            command = new StatusResponseCmd(who, what, where);
        } else if (type.equals("Status Request"))
        {
            if (who.equals("") || where.equals(""))
                throw new MalformedCommandOPEN("*#" + who + "*" + where + "##");
            command = new StatusRequestCmd(who, where);
        } else if (type.equals("Dimension Request"))
        {
            if (who.equals("") || where.equals("") || dimension.equals(""))
                throw new MalformedCommandOPEN("*#" + who + "*" + where + "*" + dimension + "##");
            command = new DimensionRequestCmd(who, where, dimension);
        } else if (type.equals("Dimension Write"))
        {
            String[] array_values = values.split(";");
            String string_values = "";
            for (String value : array_values)
                string_values += "*" + value;
            if (who.equals("") || where.equals("") || dimension.equals("") || values.equals(""))
                throw new MalformedCommandOPEN("*#" + who + "*" + where + "*#" + dimension + string_values + "##");
            command = new DimensionWriteCmd(who, where, dimension, string_values);
        }

        try
        {
            if (null != interval)
                intervalInMillis = Integer.valueOf(Strings.convertPollingIntervalString(interval));
        } catch (Exception e1)
        {
            throw new NoSuchCommandException("Unable to create OpenWebNet command, pollingInterval could not be converted into milliseconds");
        }

        try
        {
            if (null != timeout)
                timeoutInMillis = Integer.valueOf(Strings.convertPollingIntervalString(timeout)) * 250;
        } catch (Exception e1)
        {
            throw new NoSuchCommandException("Unable to create OpenWebNet command, timeout could not be converted into milliseconds");
        }

        if (intervalInMillis != null && timeoutInMillis != null && intervalInMillis <= timeoutInMillis)
            throw new NoSuchCommandException("Unable to create OpenWebNet command, timeout should be less than pollingInterval");

        URL url;
        try
        {
            url = new URL(urlAsString);
        }
        catch (MalformedURLException e)
        {
            throw new NoSuchCommandException("Invalid URL: " + e.getMessage(), e);
        }


        Command cmd = OpenWebNetCommand.createCommand(url.getHost(), url.getPort(), intervalInMillis, timeoutInMillis, sensorNamesList, command);
        cachedCommands.put(commandId, cmd);
        return cmd;
    }

}
