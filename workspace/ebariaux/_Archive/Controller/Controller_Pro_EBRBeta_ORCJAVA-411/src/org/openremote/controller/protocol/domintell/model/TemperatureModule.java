package org.openremote.controller.protocol.domintell.model;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.openremote.controller.protocol.domintell.DomintellAddress;
import org.openremote.controller.protocol.domintell.DomintellCommandBuilder;
import org.openremote.controller.protocol.domintell.DomintellGateway;
import org.openremote.controller.protocol.domintell.TemperatureMode;

public class TemperatureModule extends DomintellModule implements Temperature {

   /**
    * Domintell logger. Uses a common category for all Domintell related logging.
    */
   private final static Logger log = Logger.getLogger(DomintellCommandBuilder.DOMINTELL_LOG_CATEGORY);

   private float currentTemperature;
   private float setPoint;
   private TemperatureMode mode;
   private float presetSetPoint;
   
   public TemperatureModule(DomintellGateway gateway, String moduleType, DomintellAddress address) {
      super(gateway, moduleType, address);
   }

   @Override
   public void setSetPoint(Float setPoint) {
      NumberFormat temperatureFormat = NumberFormat.getInstance(Locale.US);
      temperatureFormat.setMinimumIntegerDigits(2);
      temperatureFormat.setMaximumIntegerDigits(2);
      temperatureFormat.setMinimumFractionDigits(1);
      temperatureFormat.setMaximumFractionDigits(1);
      gateway.sendCommand(moduleType + address + "%T" + temperatureFormat.format(setPoint));      
   }
   
   @Override
   public void setMode(TemperatureMode mode) {
      gateway.sendCommand(moduleType + address + "%M" + mode.getValue());

   }


   @Override
   public void queryState() {
      gateway.sendCommand(moduleType + address + "%S");
   }

   // Feedback method from HomeWorksDevice ---------------------------------------------------------

   @Override
   public void processUpdate(String info) {
     try {
        // T 0.0 18.0 AUTO 18.0
        StringTokenizer st = new StringTokenizer(info.substring(1));
        currentTemperature = Float.parseFloat(st.nextToken());
        setPoint = Float.parseFloat(st.nextToken());
        mode = TemperatureMode.valueOf(st.nextToken());
        presetSetPoint = Float.parseFloat(st.nextToken());
                
        log.info("Current temperature read as >" + currentTemperature + "<");
     } catch (NumberFormatException e) {
       // Not understood as a scene, do not update ourself
       log.warn("Invalid feedback received " + info, e);
     }
       
     super.processUpdate(info);
   }

   public float getCurrentTemperature() {
      return currentTemperature;
   }

   public float getSetPoint() {
      return setPoint;
   }

   public TemperatureMode getMode() {
      return mode;
   }

   public float getPresetSetPoint() {
      return presetSetPoint;
   }
   
}
