/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2012, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a full listing of individual contributors.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU Affero General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Affero General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>.
 */
package org.openremote.controller.protocol.mpd;

/**
 * These are all allowed command values for the MPD protocol
 */
public enum MpdCmdEnum {
   
   PLAY,
   PAUSE,
   STOP,
   MUTE,
   UNMUTE,
   NEXT,
   PREV,
   SET_VOLUME,
   GET_VOLUME,
   GET_PLAYER_STATUS,
   GET_SONG_NAME,
   GET_SONG_LENGTH,
   GET_ALBUM_NAME,
   GET_ARTIST_NAME,
   GET_ELAPSED_TIME;

   private MpdCmdEnum() {
      this.value = null;
   }

   private MpdCmdEnum(String value) {
      this.value = value;
   }

   private MpdCmdEnum(MpdCmdEnum otherKey) {
      this(otherKey.getValue());
   }

   private String value;

   public String getValue() {
      if (value == null) {
         return this.name();
      }
      return value;
   }
   

}