package org.openremote.controller.protocol.onkyoeiscp;

import java.util.HashMap;
import java.util.Map;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.lutron.LutronHomeWorksDeviceException;
import org.openremote.controller.protocol.lutron.model.Dimmer;
import org.openremote.controller.protocol.lutron.model.HomeWorksDevice;
import org.openremote.controller.protocol.onkyoeiscp.OnkyoeISCPGateway.OnkyoResponse;
import org.openremote.controller.utils.Logger;

public class SimpleCommand extends OnkyoeISCPCommand implements ExecutableCommand, EventListener {

   private final static Map<String, CommandConfig> knownCommands = new HashMap<String, CommandConfig>();

   static {
      CommandConfig cfg = new CommandConfig("PWR");
      cfg.addParameter("ON", "01");
      cfg.addParameter("OFF", "00");
      cfg.addParameter("STATUS", "QSTN");
      knownCommands.put("POWER", cfg);
      cfg = new CommandConfig("AMT");
      cfg.addParameter("ON", "01");
      cfg.addParameter("OFF", "00");
      cfg.addParameter("TOGGLE", "TG");
      cfg.addParameter("STATUS", "QSTN");
      knownCommands.put("MUTE", cfg);
   }
   
   /**
    * Onkyo eISCP logger. Uses a common category for all Onkyo eISCP related logging.
    */
   protected final static Logger log = Logger.getLogger(OnkyoeISCPCommandBuilder.ONKYO_EISCP_LOG_CATEGORY);

   public static SimpleCommand createCommand(String name, OnkyoeISCPGateway gateway, String parameter) {
      // Check for mandatory attributes
/*      if (parameter == null) {
        throw new NoSuchCommandException("DeviceIndex is required for any AMX command");
      }*/

      return new SimpleCommand(name, gateway, parameter);
    }

   public SimpleCommand(String name, OnkyoeISCPGateway gateway, String parameter) {
      super(name, gateway);
      this.parameter = parameter;
   }

   // Private Instance Fields ----------------------------------------------------------------------

   /**
    * Parameter used by this command.
    */
   private String parameter;

   // Implements ExecutableCommand -----------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   public void send() {
      
      // TODO: add check for valid command / parameter
      
     CommandConfig cfg = knownCommands.get(name);
     gateway.sendCommand(cfg.getCommand(), cfg.getParameter(parameter));
   }

   // Implements EventListener -------------------------------------------------------------------

   @Override
   public void setSensor(Sensor sensor) {
       if (sensors.isEmpty()) {
          
          // First sensor registered, we also need to register ourself with the gateway
          gateway.registerCommand(name, this);
          addSensor(sensor);

          // Trigger a query to get the initial value
          send();
       } else {
          addSensor(sensor);
       }
   }
   
   @Override
   public void stop(Sensor sensor) {
      removeSensor(sensor);
      if (sensors.isEmpty()) {
         // Last sensor removed, we may unregister ourself from gateway
         gateway.unregisterCommand(name, this);
      }
   }
   
   @Override
   protected void updateWithResponse(OnkyoResponse response)
   {
      // TODO: have generic, this is just for testing
      if ("00".equals(response.parameter)) {
         updateSensorsWithValue("off");
      } else {
         updateSensorsWithValue("on");
      }
   }
}

class CommandConfig {
   
   private String command;
   private Map<String, String> knownParameters;
   
   public CommandConfig(String command) {
      super();
      this.command = command;
      this.knownParameters = new HashMap<String, String>();
   }
   
   public void addParameter(String orParam, String onkyoParam) {
      knownParameters.put(orParam, onkyoParam);
   }

   public String getCommand() {
      return command;
   }
   
   public String getParameter(String orParam) {
      return knownParameters.get(orParam);
   }
}

 