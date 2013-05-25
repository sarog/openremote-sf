/*
 * OpenRemote, the Home of the Digital Home. Copyright 2008-2013, OpenRemote Inc.
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
package org.openremote.controller.protocol.freetts;

import javax.speech.synthesis.Synthesizer;

import org.openremote.controller.Constants;
import org.openremote.controller.command.ExecutableCommand;
import org.openremote.controller.utils.Logger;

/**
 * Text to Speech command using freeTTS.
 * 
 * The only parameter is the text to speak. 
 * Speaks the supplied text on the OpenRemote server.
 * 
 * @author Lawrie Griffiths
 *
 */
public class FreeTTSCommand implements ExecutableCommand {
   
   // Constants ------------------------------------------------------------------------------------
   
   public final static String FREETTS_PROTOCOL_LOG_CATEGORY = Constants.CONTROLLER_PROTOCOL_LOG_CATEGORY + "freetts";

   // Class Members --------------------------------------------------------------------------------

   private final static Logger logger = Logger.getLogger(FREETTS_PROTOCOL_LOG_CATEGORY);
   
   // Instance Fields
   // ----------------------------------------------------------

   private String text;
   private Synthesizer synth;
   
   // Implements ExecutableCommand ---------------------------------------------------------------------

   public FreeTTSCommand(Synthesizer synth, String text) {
      this.synth = synth;
      this.text = text;
   }

   @Override
   public void send() {
      if (synth != null) {
         try {
            // Speak the message
            logger.debug("FreeTTS: Saying '" + text + "'");
            synth.speakPlainText(text, null);

            // Wait till speaking is done
            synth.waitEngineState(Synthesizer.QUEUE_EMPTY);
         } catch (Exception e) {
            logger.error("FreeTTS: failed to synthesize text");
         }
      } else {
         logger.error("FreeTTS: The synthesizer is null");
      }
   }
}
