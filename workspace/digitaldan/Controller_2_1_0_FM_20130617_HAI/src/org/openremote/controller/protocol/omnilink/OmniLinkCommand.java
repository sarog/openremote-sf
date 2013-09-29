package org.openremote.controller.protocol.omnilink;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

import com.digitaldan.jomnilinkII.MessageUtils;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;

public class OmniLinkCommand implements EventListener, ExecutableCommand{
   private final static Logger logger = Logger.getLogger(OmnilinkCommandBuilder.OMNILINK_PROTOCOL_LOG_CATEGORY);
   
   OmniLinkCmd command;
   int parameter1;
   int parameter2;
   OmnilinkClient client;
   
   public OmniLinkCommand(OmniLinkCmd command, int parameter1, int parameter2, OmnilinkClient client) {
      super();
      this.command = command;
      this.parameter1 = parameter1;
      this.parameter2 = parameter2;
      this.client = client;
   }
   
   @Override
   public void send() {
      try {
         switch(command){
         case CMD_THERMO_SET_COOL_POINTC:
         case CMD_THERMO_SET_HEAT_POINTC:
            parameter1 = MessageUtils.CToOmni(parameter1);
            break;
         case CMD_THERMO_SET_COOL_POINTF:
         case CMD_THERMO_SET_HEAT_POINTF:
            parameter1 = MessageUtils.FtoOmni(parameter1);
            break;  
         case CMD_AUDIO_ZONE_SET_MUTE:
            parameter1 += 2;
            break;
         }
         logger.info("Sending command " + command.toString() 
               + "(" + command.getNumber() + ")" + " with param1: " 
               + parameter1 + " and param2: " + parameter2);
         client.connection().controllerCommand(command.getNumber(), parameter1, parameter2);
      } catch (Exception e) {
         logger.error("could not send command " + command, e);
      } 
   }

   @Override
   public void setSensor(Sensor sensor) {
      logger.info("Attempting to add sensor for " + command.toString() + " with number " + parameter2); 
      sensor.getProperties().put("OmniCommand", command.toString());
      try {
      switch(command) {
         case SENSOR_UNIT_POWER:
         case SENSOR_UNIT_LEVEL:
            client.addSensor(ObjectProperties.OBJ_TYPE_UNIT, parameter2, sensor, command);
            break;
         case SENSOR_THERMO_HEAT_POINTC:
         case SENSOR_THERMO_HEAT_POINTF:
         case SENSOR_THERMO_COOL_POINTC:
         case SENSOR_THERMO_COOL_POINTF:
         case SENSOR_THERMO_SYSTEM_MODE:
         case SENSOR_THERMO_FAN_MODE:
         case SENSOR_THERMO_HOLD_MODE:
         case SENSOR_THERMO_TEMPC:
         case SENSOR_THERMO_TEMPF:
            client.addSensor(ObjectProperties.OBJ_TYPE_THERMO, parameter2, sensor, command);
            break;
         case SENSOR_ZONE_STATUS:
            client.addSensor(ObjectProperties.OBJ_TYPE_ZONE, parameter2, sensor, command);
            break;
         case SENSOR_AREA_STATUS:
            client.addSensor(ObjectProperties.OBJ_TYPE_AREA, parameter2, sensor, command);
            break;
         case SENSOR_AUX_STATUS:
         case SENSOR_AUX_CURRENTC:
         case SENSOR_AUX_CURRENTF:
         case SENSOR_AUX_LOWC:
         case SENSOR_AUX_LOWF:
         case SENSOR_AUX_HIGHC:
         case SENSOR_AUX_HIGHF:
            client.addSensor(ObjectProperties.OBJ_TYPE_AUX_SENSOR, parameter2, sensor, command);
            break;
         case SENSOR_AUDIOZONE_POWER:
         case SENSOR_AUDIOZONE_SOURCE:
         case SENSOR_AUDIOZONE_VOLUME:
         case SENSOR_AUDIOZONE_MUTE:
         case SENSOR_AUDIOZONE_TEXT:
            logger.info("calling addSensor for command " + command.toString() + " with number " + parameter2); 
            client.addSensor(ObjectProperties.OBJ_TYPE_AUDIO_ZONE, parameter2, sensor, command);
            break;
         case SENSOR_AUDIOSOURCE_TEXT:
            client.addSensor(ObjectProperties.OBJ_TYPE_AUDIO_SOURCE, parameter2, sensor, command);
            break;
            default:
               //Log.warn("command " + command + " is not a valid sensor command");
         }
      }catch(Exception e){
         logger.error("Could not add sensor for cmd " + command.toString(), e);
      }
   }

   @Override
   public void stop(Sensor sensor) {
     
      
   }

}
