package org.openremote.controller.protocol.onewire;

import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;
import org.openremote.controller.command.Command;
import org.openremote.controller.command.CommandBuilder;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.protocol.onewire.command.OneWireAlarmingCommand;
import org.openremote.controller.protocol.onewire.command.OneWireCommand;
import org.openremote.controller.protocol.onewire.command.OneWireExecutableCommand;
import org.openremote.controller.protocol.onewire.command.OneWireIntervalReadCommand;
import org.openremote.controller.protocol.onewire.command.OneWireSwitchableCommand;
import org.openremote.controller.utils.Logger;


/**
 * OneWire command builder.
 * @author Tom Kucharski <kucharski.tom@gmail.com>
 */
public class OneWireCommandBuilder implements CommandBuilder {
	private final static Logger logger = OneWireLoggerFactory.getLogger();

	/**
	 * Command repository.
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
	 * @param element  contains JDOM instances of the command XML snippet to parse
	 * @return
	 */
	@Override
	public Command build(Element element) {
		OneWireConfigurationReader reader = new OneWireConfigurationReader(element);
		OneWireCommand oneWireCommand = loadOrCreateCommand(reader);
		oneWireCommand.setDynamicValue(reader.getDynamicValue());
		return oneWireCommand;
	}

	private OneWireCommand loadOrCreateCommand(OneWireConfigurationReader reader) {
		OneWireCommand oneWireCommand = commandRepository.get(reader.getCommandId());
		if (oneWireCommand == null) {
			oneWireCommand = createAndValidateCommand(reader);
			logger.info("OneWire new command created: " + oneWireCommand);
			commandRepository.put(reader.getCommandId(),oneWireCommand);
		}
		return oneWireCommand;
	}

	/**
	 * Creates new command
	 * @param configuration command configuration
	 * @return
	 */
	private OneWireCommand createAndValidateCommand(OneWireConfigurationReader configuration) {
		OneWireCommand command = createProperTypeOfCommand(configuration);
		command.validate();
		return command;
	}

	/**
	 * Creates command based on its command type
	 * @param configuration
	 * @return
	 */
	private OneWireCommand createProperTypeOfCommand(OneWireConfigurationReader configuration) {
		switch (configuration.getCommandType()) {
			case ALARMING:
				 return buildOneWireAlarmingCommand(configuration);
			case EXECUTABLE:
				return buildOneWireExecutableCommand(configuration);
			case SWITCHABLE:
				return buildOneWireSwitchableCommand(configuration);
			case INTERVAL:
				return buildOneWireIntervalReadCommand(configuration);
		}
		throw new NoSuchCommandException("Cannot create OneWireCommand for command id: '"+ configuration.getCommandId()+"'.");
	}

	private OneWireCommand buildOneWireSwitchableCommand(OneWireConfigurationReader configuration) {
		OneWireSwitchableCommand command = new OneWireSwitchableCommand();
		configureWithGeneralProperties(command, configuration);
		return command;
	}

	private OneWireCommand buildOneWireAlarmingCommand(OneWireConfigurationReader configuration) {
		OneWireAlarmingCommand command = new OneWireAlarmingCommand();
		configureWithGeneralProperties(command, configuration);
		return command;
	}

	private OneWireCommand buildOneWireExecutableCommand(OneWireConfigurationReader configuration) {
		OneWireExecutableCommand command = new OneWireExecutableCommand();
		configureWithGeneralProperties(command, configuration);
		return command;
	}

	private OneWireCommand buildOneWireIntervalReadCommand(OneWireConfigurationReader configuration) {
		OneWireIntervalReadCommand command = new OneWireIntervalReadCommand();
		configureWithGeneralProperties(command, configuration);
		command.setPollingIntervalInMiliseconds(configuration.getPollingInterval());
		return command;
	}

	private void configureWithGeneralProperties(OneWireCommand command, OneWireConfigurationReader configuration) {
		OneWireHost oneWireHost = configuration.getOneWireHost();
		command.setHost(oneWireHost);
		command.setOwfsConnectorFactory(connectionFactoryRepository.loadOrCreate(oneWireHost));
		command.setDeviceName(configuration.getDeviceName());
		command.setDevicePropertyName(configuration.getDevicePropertyName());
	}
}
