package org.openremote.controller.protocol.mpd;

import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.protocol.EventListener;
import org.openremote.controller.utils.Logger;

public class MpdCommand implements EventListener, ExecutableCommand {

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(MpdCommandBuilder.MPD_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private MpdCmdEnum command;
   private MpdSession mpdSession;
   private String paramValue;


   // Constructor ---------------------------------------------------------------------
   public MpdCommand(MpdSession session, MpdCmdEnum cmd, String paramValue) {
      this.mpdSession = session;
      this.command = cmd;
      this.paramValue = paramValue;
   }

   
   @Override
   public void send() {
      try {
         switch (command) {
            case PLAY:
               mpdSession.getPlayer().play();
               break;
            case PAUSE:
               mpdSession.getPlayer().pause();
               break;
            case STOP:
               mpdSession.getPlayer().stop();
               break;
            case MUTE:
               mpdSession.getPlayer().mute();
               break;
            case UNMUTE:
               mpdSession.getPlayer().unMute();
               break;
            case NEXT:
               mpdSession.getPlayer().playNext();
               break;
            case PREV:
               mpdSession.getPlayer().playPrev();
               break;
            case SET_VOLUME:
               mpdSession.getPlayer().setVolume(Integer.parseInt(paramValue));
               break;
         }
      } catch (Exception e) {
         logger.error("Could not execute MPD command: " + command, e);
      }
   }

   @Override
   public void setSensor(Sensor sensor) {
      mpdSession.addSensor(command.getValue(), sensor);
   }


   @Override
   public void stop(Sensor sensor) {
      mpdSession.removeSensor(command.getValue());
   }

}

