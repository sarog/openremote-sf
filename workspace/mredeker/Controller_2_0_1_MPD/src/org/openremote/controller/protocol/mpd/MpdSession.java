package org.openremote.controller.protocol.mpd;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.bff.javampd.MPD;
import org.bff.javampd.MPDPlayer;
import org.bff.javampd.MPDPlayer.PlayerStatus;
import org.bff.javampd.events.PlayerBasicChangeEvent;
import org.bff.javampd.events.PlayerBasicChangeListener;
import org.bff.javampd.events.PlaylistBasicChangeEvent;
import org.bff.javampd.events.PlaylistBasicChangeListener;
import org.bff.javampd.events.TrackPositionChangeEvent;
import org.bff.javampd.events.TrackPositionChangeListener;
import org.bff.javampd.events.VolumeChangeEvent;
import org.bff.javampd.events.VolumeChangeListener;
import org.bff.javampd.exception.MPDConnectionException;
import org.bff.javampd.monitor.MPDStandAloneMonitor;
import org.bff.javampd.objects.MPDSong;
import org.openremote.controller.model.sensor.Sensor;
import org.openremote.controller.utils.Logger;

public class MpdSession implements TrackPositionChangeListener, PlaylistBasicChangeListener, PlayerBasicChangeListener, VolumeChangeListener {

   // Class Members --------------------------------------------------------------------------------
   private final static Logger logger = Logger.getLogger(MpdCommandBuilder.MPD_PROTOCOL_LOG_CATEGORY);

   // Instance Fields ------------------------------------------------------------------------------
   private MPD mpd;
   private Map<String, Sensor> sensors = new HashMap<String, Sensor>();

   // Constructor ---------------------------------------------------------------------
   public MpdSession(String ip, int port) throws UnknownHostException, MPDConnectionException {
      mpd = new MPD(ip, port);
      logger.debug("Connected to MPD at: " + ip+":"+port);
      logger.debug("MPD Version:"+mpd.getVersion());
      MPDStandAloneMonitor mpdStandAloneMonitor = new MPDStandAloneMonitor(mpd);
      mpdStandAloneMonitor.addTrackPositionChangeListener(this);
      mpdStandAloneMonitor.addPlaylistChangeListener(this);
      mpdStandAloneMonitor.addPlayerChangeListener(this);
      mpdStandAloneMonitor.addVolumeChangeListener(this);
      Thread th = new Thread(mpdStandAloneMonitor);
      th.start();
   }

   public void addSensor(String command, Sensor sensor) {
      sensors.put(command, sensor);
      playlistBasicChange(null);
      trackPositionChanged(null);
      playerBasicChange(null);
      volumeChanged(null);
   }

   public void removeSensor(String command) {
      sensors.remove(command);
   }
   
   @Override
   public void trackPositionChanged(TrackPositionChangeEvent arg0) {
      try {
         if (mpd.getMPDPlayer().getStatus() == PlayerStatus.STATUS_PLAYING) {
            if (sensors.get("GET_ELAPSED_TIME") != null) {
               int elapsedTime = (int)mpd.getMPDPlayer().getElapsedTime(); 
               int min =  elapsedTime / 60;
               int sec = elapsedTime % 60;
               sensors.get("GET_ELAPSED_TIME").update(min+":"+String.format("%02d", sec));
            }
         }
      } catch (Exception e) {
         logger.error("trackPositionChanged error", e);
      }
   }

   @Override
   public void playlistBasicChange(PlaylistBasicChangeEvent arg0) {
      try {
         if (mpd.getMPDPlayer().getCurrentSong() != null) {
            MPDSong song = mpd.getMPDPlayer().getCurrentSong();
            if (sensors.get("GET_SONG_NAME") != null) {
               if (song.getName() != null) {
                  sensors.get("GET_SONG_NAME").update(song.getName());
               } else {
                  sensors.get("GET_SONG_NAME").update("unknown");
               }
            }
            if (sensors.get("GET_ALBUM_NAME") != null) {
               if ((song.getAlbum() != null) && (song.getAlbum().getName() != null)){
                  sensors.get("GET_ALBUM_NAME").update(song.getAlbum().getName());
               } else {
                  sensors.get("GET_ALBUM_NAME").update("unknown");
               }
            }
            if (sensors.get("GET_ARTIST_NAME") != null) {
               if ((song.getArtist() != null) && (song.getArtist().getName() != null)){
                  sensors.get("GET_ARTIST_NAME").update(song.getArtist().getName());
               } else {
                  sensors.get("GET_ARTIST_NAME").update("unknown");
               }
            }
            if (sensors.get("GET_SONG_LENGTH") != null) {
               int min= song.getLength() / 60;
               int sec = song.getLength() % 60;
               sensors.get("GET_SONG_LENGTH").update(min+":"+String.format("%02d", sec));
            }
         }
      } catch (Exception e) {
         logger.error("playlistBasicChange error", e);
      }
   }

   @Override
   public void playerBasicChange(PlayerBasicChangeEvent arg0) {
      try {
         if (sensors.get("GET_PLAYER_STATUS") != null) {
            sensors.get("GET_PLAYER_STATUS").update(mpd.getMPDPlayer().getStatus().toString());
         }
      } catch (Exception e) {
         logger.error("playerBasicChange error", e);
      }
   }

   @Override
   public void volumeChanged(VolumeChangeEvent arg0) {
      try {
         if (sensors.get("GET_VOLUME") != null) {
            sensors.get("GET_VOLUME").update(""+mpd.getMPDPlayer().getVolume());
         }
      } catch (Exception e) {
         logger.error("volumeChanged error", e);
      }
   }
   
   public MPDPlayer getPlayer() {
      return mpd.getMPDPlayer();
   }
}
