package org.openremote.controller.protocol.omnilink;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.omnilink.model.Area;
import org.openremote.controller.protocol.omnilink.model.AudioSource;
import org.openremote.controller.protocol.omnilink.model.AudioZone;
import org.openremote.controller.protocol.omnilink.model.Aux;
import org.openremote.controller.protocol.omnilink.model.Thermostat;
import org.openremote.controller.protocol.omnilink.model.Unit;
import org.openremote.controller.protocol.omnilink.model.Zone;
import org.openremote.controller.utils.Logger;

import com.digitaldan.jomnilinkII.Connection;
import com.digitaldan.jomnilinkII.DisconnectListener;
import com.digitaldan.jomnilinkII.Message;
import com.digitaldan.jomnilinkII.MessageUtils;
import com.digitaldan.jomnilinkII.NotificationListener;
import com.digitaldan.jomnilinkII.OmniInvalidResponseException;
import com.digitaldan.jomnilinkII.OmniNotConnectedException;
import com.digitaldan.jomnilinkII.OmniUnknownMessageTypeException;
import com.digitaldan.jomnilinkII.MessageTypes.AudioSourceStatus;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectProperties;
import com.digitaldan.jomnilinkII.MessageTypes.ObjectStatus;
import com.digitaldan.jomnilinkII.MessageTypes.OtherEventNotifications;
import com.digitaldan.jomnilinkII.MessageTypes.SystemStatus;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AreaProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioSourceProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AudioZoneProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.AuxSensorProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ButtonProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.CodeProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ThermostatProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.UnitProperties;
import com.digitaldan.jomnilinkII.MessageTypes.properties.ZoneProperties;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.AreaStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.AudioZoneStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.AuxSensorStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.ThermostatStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.UnitStatus;
import com.digitaldan.jomnilinkII.MessageTypes.statuses.ZoneStatus;

public class OmnilinkClient extends Thread {

   private final static Logger log = Logger.getLogger(OmnilinkCommandBuilder.OMNILINK_PROTOCOL_LOG_CATEGORY);

   String host;
   int port;
   String key;

   ConcurrentHashMap<Integer, Unit> units;
   ConcurrentHashMap<Integer, Thermostat> thermos;
   ConcurrentHashMap<Integer, Zone> zones;
   ConcurrentHashMap<Integer, Area> areas;
   //ConcurrentHashMap<Integer, CodeProperties> codes;
   ConcurrentHashMap<Integer, Aux> auxs;
   ConcurrentHashMap<Integer, ButtonProperties> buttons;
   ConcurrentHashMap<Integer, AudioZone> audioZones;
   ConcurrentHashMap<Integer, AudioSource> audioSources;

   
   private Connection c;

   boolean connected = false;
   boolean loaded = false;
   boolean running = true;
   
   private Object audioUpdateLock = new Object();
   private boolean omni;
   
   public OmnilinkClient(String address, int port, String key1, String key2) throws Exception {
      this.host = address;
      this.port = port;
      this.key = key1 + ":" + key2;
      units = new ConcurrentHashMap<Integer, Unit>();
      thermos = new ConcurrentHashMap<Integer, Thermostat>();
      zones = new ConcurrentHashMap<Integer, Zone>();
      areas = new ConcurrentHashMap<Integer, Area>();
      //codes = new ConcurrentHashMap<Integer, CodeProperties>();
      auxs = new ConcurrentHashMap<Integer, Aux>();
      buttons = new ConcurrentHashMap<Integer, ButtonProperties>();
      audioZones = new ConcurrentHashMap<Integer, AudioZone>();
      audioSources = new ConcurrentHashMap<Integer, AudioSource>();
      start();
   }
   
   public Map<Integer,Unit> getUnits(){
      return units;
   }
   
   /**
    * Main processing loop
    */
   public void run() {
      while (running) {
         loaded = false;
         connected = false;
         /*
          * Connect to the system
          */
         try {
            c = new Connection(host, port, key);
            connected = true;
         } catch (Exception e) {
            log.error("", e);
         }
         
         /*
          * If we fail to connect sleep a bit before attemping again
          */
         if (!connected) {
            try {
               Thread.currentThread().sleep(10 * 1000);
            } catch (InterruptedException ignored) {
            }

         } else {

            /*
             * If we get disconnected then do nothing
             */
            c.addDisconnectListener(new DisconnectListener() {
               public void notConnectedEvent(Exception e) {
                  log.error("", e);
               }
            });
            
            /*
             * Real time device changes get processed here
             */
            c.addNotificationListener(new NotificationListener() {
               public void objectStausNotification(ObjectStatus s) {
                  try {
                     switch (s.getStatusType()) {
                     case Message.OBJ_TYPE_AREA: {
                        log.info("STATUS_AREA changed");
                        AreaStatus[] u = (AreaStatus[]) s.getStatuses();
                        for (AreaStatus as : u) {
                           //updateArea(u[i]);
                           Area area = areas.get(new Integer(as.getNumber()));
                           if(area != null){
                              area.getProperties().updateArea(as);
                              area.updateSensors();
                           }
                        }
                     }
                        break;
                     case Message.OBJ_TYPE_AUDIO_ZONE: {
                        log.info("STATUS_AUDIO_ZONE changed");
                        AudioZoneStatus[] u = (AudioZoneStatus[]) s.getStatuses();
                        for (AudioZoneStatus as : u) {
                           AudioZone audioZone = audioZones.get(new Integer(as.getNumber()));
                           if(audioZone != null){
                              log.info("STATUS_AUDIO_ZONE updating sensor for zone " + as.getNumber());
                              audioZone.getProperties().updateAudioZone(as);
                              AudioSource audioSrc = audioSources.get(new Integer(as.getSource()));
                              //make sure we have the right audio src text. 
                              if(audioSrc != null){
                                 audioZone.setAudioSourceText(audioSrc.formatAudioText());
                              }
                              audioZone.getProperties().updateAudioZone(as);
                              audioZone.updateSensors();
                           }
                        }
                     }
                        break;
                     case Message.OBJ_TYPE_AUX_SENSOR: {
                        log.info("STATUS_AUX changed");
                        AuxSensorStatus[] u = (AuxSensorStatus[]) s.getStatuses();
                        for (AuxSensorStatus as : u) {
                           Aux aux = auxs.get(new Integer(as.getNumber()));
                           if(aux != null){
                              aux.getProperties().updateAuxSensor(as);
                              aux.updateSensors();
                           }
                        }
                     }
                        break;
                     case Message.OBJ_TYPE_EXP:
                        log.info("STATUS_EXP changed");
                        break;
                     case Message.OBJ_TYPE_MESG:
                        log.info("STATUS_MESG changed");
                        break;
                     case Message.OBJ_TYPE_THERMO: {
                        log.info("STATUS_THERMO changed " + s.getStatuses().length);
                        ThermostatStatus[] u = (ThermostatStatus[]) s.getStatuses();
                        for (ThermostatStatus ts : u) {
                           Thermostat thermo = thermos.get(new Integer(ts.getNumber()));
                           if(thermo != null){
                              thermo.getProperties().updateThermostat(ts);
                              thermo.updateSensors();
                           }
                        }
                     }
                        break;
                     case Message.OBJ_TYPE_UNIT: {
                        log.info("STATUS_UNIT changed " + s.getStatuses().length);
                        UnitStatus[] u = (UnitStatus[]) s.getStatuses();
                        log.info("STATUS_UNIT changed looping statuses");
                        for (UnitStatus us : u) {
                           log.info("STATUS_UNIT changed for unit " + us.getNumber());
                           Unit unit = units.get(new Integer(us.getNumber()));
                           if(unit != null){
                              unit.getProperties().updateUnit(us);
                              unit.updateSensors();
                           }

                        }
                     }
                        break;
                     case Message.OBJ_TYPE_ZONE: {
                        log.info("STATUS_ZONE changed " + s.getStatuses().length);
                        ZoneStatus[] u = (ZoneStatus[]) s.getStatuses();
                        for (ZoneStatus zs : u) {
                           Zone zone = zones.get(new Integer(zs.getNumber()));
                           if(zone != null){
                              zone.getProperties().updateZone(zs);
                              zone.updateSensors();
                           }
                        }
                     }
                        break;
                     default:
                        log.info("Unknown type " + s.getStatusType());
                        break;
                     }
                  } catch (Exception ex) {
                     log.error("", ex);
                     c.disconnect();
                  }
               } //End real time device changes

               /*
                * We currently don't do anything with these
                */
               public void otherEventNotification(OtherEventNotifications o) {
                  log.info("Other Event");
                  for (int k = 0; k < o.getNotifications().length; k++) {
                     log.info("Event bits " + MessageUtils.getBits(o.getNotifications()[k]));
                  }
               }
            });

            /*
             * Load everything and main audio source text loop
             */
            try {
               SystemStatus sysstatus = c.reqSystemStatus();
               log.info("System: " + sysstatus.toString());
               omni = c.reqSystemInformation().getModel() < 36;
               /*
                * We need to explicitly tell the controller to send us real time
                * notifications
                */
               c.enableNotifications();
               
               //audio sources are not pushed, just load all of them here
               loadAudioSources();
               
               loaded = true;
               
               /*
                * if we get disconnected then refresh any devices that we have
                * to keep them up to date.
                */
               refreshDevices();
               
               while (running && c.connected()) {
                  /*
                   * Audio source text is not pushed in real time, so we poll 
                   * for it
                   */
                  updateAudioSourceTexts();
                  
                  try {
                     synchronized (audioUpdateLock) {
                        audioUpdateLock.wait(5000);
                     }
                  } catch (InterruptedException ignored) {
                  }
               }
            } catch (IOException ex) {
               log.error("", ex);
            } catch (OmniNotConnectedException ex) {
               log.error("", ex.getNotConnectedReason());
            } catch (OmniInvalidResponseException ex) {
               log.error("", ex);
            } catch (OmniUnknownMessageTypeException ex) {
               log.error("", ex);
               // is this needed? I just added this without looking at the code for 2 years
            } catch (Exception ex) {
               log.error("", ex);
            } finally {
               c.disconnect();
               c = null;
               try {
                  sleep(1000 * 30);
               } catch (InterruptedException ignored) {

               }
            }
         }
      }
   }
   

   public void shutdown() {
      running = false;
   }

   public void updateAudioSourceStatus() {
      synchronized (audioUpdateLock) {
         audioUpdateLock.notifyAll();
      }
   }
   
   public void addSensor(int objectType, int number, Sensor sensor, OmniLinkCmd cmd) throws Exception{
      switch(objectType){
      case Message.OBJ_TYPE_ZONE:
         Zone zone = zones.get(new Integer(number));
         if(zone == null){
            ZoneProperties properties = readZoneProperties(number);
            if(properties != null){
               zone = new Zone(properties);
               zones.put(new Integer(number), zone);
            }
         }
         if(zone != null){
            zone.addSensor(cmd, sensor);
            zone.updateSensors();
         }   
         break;
      case Message.OBJ_TYPE_AUX_SENSOR:
         Aux aux = auxs.get(new Integer(number));
         if(aux == null){
            AuxSensorProperties properties = readAuxProperties(number);
            if(properties != null){
               aux = new Aux(properties);
               auxs.put(new Integer(number), aux);
            }
         }
         if(aux != null){
            aux.addSensor(cmd, sensor);
            aux.updateSensors();
         }   
         break;
      case Message.OBJ_TYPE_AUDIO_SOURCE:
         AudioSource as = audioSources.get(new Integer(number));
         if(as == null){
            AudioSourceProperties properties = readAudioSourceProperties(number);
            if(properties != null){
               as = new AudioSource(properties);
               audioSources.put(new Integer(number), as);
            }
         }
         if(as != null){
            as.addSensor(cmd, sensor);
            as.updateSensors();
         }  
         break;
      case Message.OBJ_TYPE_AUDIO_ZONE:
         AudioZone az = audioZones.get(new Integer(number));
         if(az == null){
            AudioZoneProperties properties = readAudioZoneProperties(number);
            if(properties != null){
               az = new AudioZone(properties);
               AudioSource src = audioSources.get(properties.getSource());
               if(src !=null)
                  az.setAudioSourceText(src.formatAudioText());
               audioZones.put(new Integer(number), az);
            }
         }
         log.info("About to add audioZone, az null ? " + (az== null));
         if(az != null){
            az.addSensor(cmd, sensor);
            az.updateSensors();
         }    
         break;
      case Message.OBJ_TYPE_AREA:
         Area area = areas.get(new Integer(number));
         if(area == null){
            AreaProperties properties = readAreaProperties(number);
            if(properties != null){
               area = new Area(properties,omni);
               areas.put(new Integer(number), area);
            }
         }
         if(area != null){
            area.addSensor(cmd, sensor);
            area.updateSensors();
         }    
         break;
      case Message.OBJ_TYPE_UNIT:
         Unit unit = units.get(new Integer(number));
         if(unit == null){
            UnitProperties properties = readUnitProperties(number);
            if(properties != null){
               unit = new Unit(properties);
               units.put(new Integer(number), unit);
            }
         }
         if(unit != null){
            unit.addSensor(cmd, sensor);
            unit.updateSensors();
         }
         break;
      case Message.OBJ_TYPE_THERMO:
         Thermostat thermo = thermos.get(new Integer(number));
         if(thermo == null){
            ThermostatProperties properties = readThermoProperties(number);
            if(properties != null){
               thermo = new Thermostat(properties);
               thermos.put(new Integer(number), thermo);
            }
         }
         if(thermo != null){
            thermo.addSensor(cmd, sensor);
            thermo.updateSensors();
         } 
         break;
   }
   }
   
   private ZoneProperties readZoneProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_ZONE, number, 0, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((ZoneProperties) m);
      }
      return null;
   }
   private AuxSensorProperties readAuxProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_AUX_SENSOR, number, 0, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((AuxSensorProperties) m);
      }
      return null;
   }
   
   private AudioSourceProperties readAudioSourceProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_AUDIO_SOURCE, number, 0, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((AudioSourceProperties) m);
      }
      return null;
   }
   private AudioZoneProperties readAudioZoneProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_AUDIO_ZONE, number, 0, 
            ObjectProperties.FILTER_1_NAMED, ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((AudioZoneProperties) m);
      }
      return null;
   }
   private AreaProperties readAreaProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_AREA, number, 0, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((AreaProperties) m);
      }
      return null;
   }
   private UnitProperties readUnitProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_UNIT, number, 0, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((UnitProperties) m);
      }
      return null;
   }
   private ThermostatProperties readThermoProperties(int number) throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      Message m = c.reqObjectProperties(Message.OBJ_TYPE_THERMO, number, 0, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_AREA_ALL, ObjectProperties.FILTER_3_ANY_LOAD);
      if(m.getMessageType() == Message.MESG_TYPE_OBJ_PROP){
         return ((ThermostatProperties) m);
      }
      return null;
   }
   
   private void refreshDevices() throws IOException, OmniNotConnectedException, OmniInvalidResponseException, OmniUnknownMessageTypeException{
      
      for(Unit u : units.values()){
         UnitProperties properties = readUnitProperties(u.getProperties().getNumber());
         u.setProperties(properties);
         u.updateSensors();
      }
      for(Thermostat t : thermos.values()){
         ThermostatProperties properties = readThermoProperties(t.getProperties().getNumber());
         t.setProperties(properties);
         t.updateSensors();
      }
      for(Zone z : zones.values()){
         ZoneProperties properties = readZoneProperties(z.getProperties().getNumber());
         z.setProperties(properties);
         z.updateSensors();
      }
      for(Area a : areas.values()){
         AreaProperties properties = readAreaProperties(a.getProperties().getNumber());
         a.setProperties(properties);
         a.updateSensors();
      }
      for(Aux a : auxs.values()){
         AuxSensorProperties properties = readAuxProperties(a.getProperties().getNumber());
         a.setProperties(properties);
         a.updateSensors();
      }
      for(AudioZone a : audioZones.values()){
         AudioZoneProperties properties = readAudioZoneProperties(a.getProperties().getNumber());
         a.setProperties(properties);
         a.updateSensors();
      }
      for(AudioSource a : audioSources.values()){
         AudioSourceProperties properties = readAudioSourceProperties(a.getProperties().getNumber());
         a.setProperties(properties);
         a.updateSensors();
      }
   }

   private void loadAudioSources() throws IOException, OmniNotConnectedException, OmniInvalidResponseException,
         OmniUnknownMessageTypeException {
      int objnum = 0;
      Message m;
      while ((m = c.reqObjectProperties(Message.OBJ_TYPE_AUDIO_SOURCE, objnum, 1, ObjectProperties.FILTER_1_NAMED,
            ObjectProperties.FILTER_2_NONE, ObjectProperties.FILTER_3_NONE)).getMessageType() == Message.MESG_TYPE_OBJ_PROP) {
         log.info(m.toString());
         AudioSourceProperties o = ((AudioSourceProperties) m);
         objnum = ((ObjectProperties) m).getNumber();
         Vector<String> text = new Vector<String>();
         int pos = 0;
         try {
            while ((m = c.reqAudioSourceStatus(o.getNumber(), pos)).getMessageType() == Message.MESG_TYPE_AUDIO_SOURCE_STATUS) {
               AudioSourceStatus a = (AudioSourceStatus) m;
               log.info(a.toString());
               text.add(a.getSourceData());
               pos = a.getPosition();
            }
         } catch (Exception e) {
            log.error("", e);
            log.info("Bug in audio source code, continuing");
         }
         AudioSource as = new AudioSource(o);
         as.setAudioText((String[]) text.toArray(new String[0]));
         audioSources.put(new Integer(o.getNumber()), as);
         updateAudioZoneText(as);

      }

   }

   private void updateAudioSourceTexts() throws IOException, OmniNotConnectedException,
         OmniInvalidResponseException, OmniUnknownMessageTypeException {
      Iterator<Integer> it = audioSources.keySet().iterator();
      while (it.hasNext()) {
         Integer source = it.next();
         int pos = 0;
         Message m;
         boolean updated = false;
         Vector<String> text = new Vector<String>();
         while ((m = c.reqAudioSourceStatus(source.intValue(), pos)).getMessageType() == Message.MESG_TYPE_AUDIO_SOURCE_STATUS) {
            AudioSourceStatus a = (AudioSourceStatus) m;
            // log.info(a.toString());
            text.add(a.getSourceData());
            pos = a.getPosition();
         }
         
         AudioSource as = audioSources.get(source);
         
         String text2[] = as.getAudioText();
         
         if (text.size() == text2.length) {
            for (int i = 0; i < text.size(); i++) {
               if (!text2[i].equals((String) text.get(i))) {
                  updated = true;
               }
            }
         } else {
            updated = true;
         }
         
         if(updated){
            as.setAudioText((String[]) text.toArray(new String[0]));
            as.updateSensors();
            updateAudioZoneText(as);
         }
      }
   }
   
   /**
    * This formats the audio source text for zones as a helper for openrmote designers
    * @param as
    */
   private void updateAudioZoneText(AudioSource as){
      System.out.println("updateAudioZoneText  text: " + as.formatAudioText());
      for(AudioZone az : audioZones.values()){
         if(az != null && az.getProperties().getSource() == as.getProperties().getNumber()){
            log.info("STATUS_AUDIO_ZONE updating sensor for zone " + az.getProperties().getNumber());
            System.out.println("updateAudioSourceTexts updating zone " + az.getProperties().getNumber() + " with text " + as.formatAudioText());
            az.setAudioSourceText(as.formatAudioText());
            az.updateSensors();
         }
      }
   }
   
   public Connection connection() {
      return c;
   }

   public boolean isRunning() {
      return running;
   }

   public boolean isLoaded() {
      return loaded;
   }
   
   public boolean isConnected() {
      return connected;
   }

}
