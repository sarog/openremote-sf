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
package org.openremote.controller.protocol.onewire;

import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.protocol.onewire.command.OneWireAlarmingCommand;
import org.openremote.controller.protocol.onewire.command.OneWireCommand;
import org.openremote.controller.protocol.onewire.command.OneWireReadCommand;
import org.openremote.controller.protocol.onewire.command.OneWireSwitchableCommand;
import org.openremote.controller.protocol.onewire.command.OneWireWriteCommand;
import org.openremote.controller.utils.Logger;

/**
 * OneWire command builder.
 *
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireCommandBuilder implements CommandBuilder {

	private final static Logger logger = OneWireLoggerFactory.getLogger();

	/**
	 * Command repository cache. Commands are created once and reused by id property corelation.
	 */
	private static Map<String, OneWireCommand> commandRepository = new HashMap<String, OneWireCommand>();

	/**
	 * Connection repository that holds owfs servers connection factories. Each server has its own connection factory;
	 */
	private static OneWireHostFactory connectionFactoryRepository = new OneWireHostFactory();

	public OneWireCommandBuilder() {
		logger.info("Creating new OneWireCommandBuilder with clean repositories");
	}

	/**
	 * Main factory method building proper command. It tries to load existing command from repository based on its id.
	 * This method is run every time new command is going to be send or defined. As a result dynamic value is always set
	 *
	 * @param element contains JDOM instances of the command XML snippet to parse
	 * @return
	 */
	@Override
	public Command build(Element element) {
		OneWireConfigurationReader reader = new OneWireConfigurationReader(element);
		return loadCommand(reader);
	}

	private OneWireCommand loadCommand(OneWireConfigurationReader reader) {
		OneWireCommand oneWireCommand = commandRepository.get(reader.getCommandId());
		if (oneWireCommand == null) {
			oneWireCommand = createProperTypeOfCommand(reader);
			configureAndValidate(reader, oneWireCommand);
			commandRepository.put(reader.getCommandId(), oneWireCommand);
			logger.info("OneWire new command created: " + oneWireCommand);
		} else {
			configureAndValidate(reader,oneWireCommand);
		}
		return oneWireCommand;
	}

	private void configureAndValidate(OneWireConfigurationReader reader, OneWireCommand oneWireCommand) {
		oneWireCommand.setOwfsConnectorFactory(connectionFactoryRepository.loadOrCreate(reader.getOneWireHost()));
		oneWireCommand.configure(reader);
		oneWireCommand.validate();
	}

	/**
	 * Creates command based on command configuration
	 * @param configuration configuration reader
	 * @return
	 */
	private OneWireCommand createProperTypeOfCommand(OneWireConfigurationReader configuration) {
		if (OneWireConfigurationReader.ALARMING.equals(configuration.getFilenameProperty())) {
			return new OneWireAlarmingCommand();
		} else if (OneWireConfigurationReader.SWITCHABLE.equals(configuration.getDataProperty())) {
			return new OneWireSwitchableCommand();
		} else if (configuration.getDataProperty() != null) {
			return new OneWireWriteCommand();
		} else {
			return new OneWireReadCommand();
		}
	}
}
