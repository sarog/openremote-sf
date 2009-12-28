/* OpenRemote, the Home of the Digital Home.
* Copyright 2008-2009, OpenRemote Inc.
*
* See the contributors.txt file in the distribution for a
* full listing of individual contributors.
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/
package org.openremote.controller.event;



/**
 * The common Event and it's executable ,the exec() method MUST be overridden, the default one does nothing.
 * 
 * @author Dan 2009-4-20
 */
public class Event implements Executable{
   
   /** The label. */
   private String label;
   
   private long delay;
   
   /**
    * Gets the label.
    * 
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * Sets the label.
    * 
    * @param label the new label
    */
   public void setLabel(String label) {
      this.label = label;
   }
   

   /**
    * Gets the delay.
    * 
    * @return the delay
    */
   public long getDelay() {
      return delay;
   }

   /**
    * Sets the delay.
    * 
    * @param delay the new delay
    */
   public void setDelay(long delay) {
      this.delay = delay;
   }

   /**
    * {@inheritDoc}
    */
   public void exec() {
      ;//nothing
   }

   /**
    * {@inheritDoc}
    */
   public void start() {
      ;//nothing
   }

   /**
    * {@inheritDoc}
    */
   public void stop() {
      ;//nothing
   }
   

}
