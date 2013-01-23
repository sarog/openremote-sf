package org.openremote.controller.protocol.marantz_avr.commands;

import java.text.NumberFormat;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.component.LevelSensor;
import org.openremote.controller.component.RangeSensor;
import org.openremote.controller.exception.NoSuchCommandException;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.model.sensor.StateSensor;
import org.openremote.controller.model.sensor.SwitchSensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRCommand;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRCommandBuilder;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRGateway;
import org.openremote.controller.protocol.marantz_avr.MarantzAVRGateway.MarantzResponse;
import org.openremote.controller.utils.Logger;

public class VolumeCommand extends MarantzAVRCommand implements ExecutableCommand, EventListener {

   /**
    * Marantz AVR logger. Uses a common category for all Marantz AVR related logging.
    */
   protected final static Logger log = Logger.getLogger(MarantzAVRCommandBuilder.MARANTZ_AVR_LOG_CATEGORY);

   public static VolumeCommand createCommand(String name, MarantzAVRGateway gateway, String parameter) {
      // Check for mandatory attributes
      if (parameter == null) {
        throw new NoSuchCommandException("A parameter is always required for the VOLUME command.");
      }

      return new VolumeCommand(name, gateway, parameter);
    }

   public VolumeCommand(String name, MarantzAVRGateway gateway, String parameter) {
      super(name, gateway);
      this.parameter = parameter;
      volumeFormat = NumberFormat.getInstance();
      volumeFormat.setMaximumFractionDigits(0);
      volumeFormat.setMinimumIntegerDigits(3);
      volumeFormat.setMaximumIntegerDigits(3);
   }

   // Private Instance Fields ----------------------------------------------------------------------

   /**
    * Parameter used by this command.
    */
   private String parameter;
   
   private NumberFormat volumeFormat;

   // Implements ExecutableCommand -----------------------------------------------------------------

   /**
    * {@inheritDoc}
    */
   public void send() {
     if ("STATUS".equals(parameter)) {
        gateway.sendCommand("MV",  "?");
     } else if ("UP".equals(parameter) || "DOWN".equals(parameter)) {
        gateway.sendCommand("MV", parameter);
     } else {
        // This should then be a value, parse it and reformat appropriately
        try {
           float value = Float.parseFloat(parameter);
           value = Math.round(value * 2.0f) / 2.0f; // Round to closest .5 value
           gateway.sendCommand("MV", volumeFormat.format(value * 10.0f)); // Sent string is 3 digits without decimal point
        } catch (NumberFormatException e) {
           throw new NoSuchCommandException("Invalid volume parameter value (" + parameter + ")");
        }
     }
   }

   // Implements EventListener -------------------------------------------------------------------

   @Override
   public void setSensor(Sensor sensor) {
       if (sensors.isEmpty()) {
          
          // First sensor registered, we also need to register ourself with the gateway
          gateway.registerCommand("MV", this);
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
         gateway.unregisterCommand("MV", this);
      }
   }
   
   @Override
   protected void updateWithResponse(MarantzResponse response)
   {
      // MVMAX comes here also, don't handle it
      // TODO: in later version, better parsing of response should mean MVMAX command is not associated with this class
      if (!response.parameter.startsWith("MAX")) {
         float value = Float.parseFloat(response.parameter);
         if (response.parameter.length() == 3) {
            // 3 characters value such as 275 mean 27.5 volume.
            value = value / 10.0f;
         }
         
         updateSensorsWithValue(value);
      }
   }
   
   @Override
   protected void updateSensorWithValue(Sensor sensor, Object value) {
      Float sensorValue = (Float)value;
      if (sensor instanceof SwitchSensor) {
         sensor.update((sensorValue != 0)?"on":"off");
      }
      if (sensor instanceof StateSensor) { // Note this includes SwitchSensor
         sensor.update(Float.toString(sensorValue));
      } else if (sensor instanceof RangeSensor) {
         Integer parsedValue = sensorValue.intValue();
         if (sensor instanceof LevelSensor) {
            sensor.update(Integer.toString(Math.min(100, Math.max(0, parsedValue))));
         } else {
            sensor.update(Integer.toString(parsedValue));
         }
      } else{
         log.warn("Query level value for incompatible sensor type (" + sensor + ")");
      }
   }

}