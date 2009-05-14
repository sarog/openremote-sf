/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.openremote.beehive.file;

/**
 * The Class ScraperProgress.
 * 
 * @author Tomsky
 */
public class ScraperProgress {
   
   /** The progress. */
   private String progress;
   
   /** The status. */
   private String status;
   
   /** The percent. */
   private double percent;
   
   public ScraperProgress() {
      status = "";
   }
   /**
    * Gets the progress.
    * 
    * @return the progress
    */
   public String getProgress() {
      return progress;
   }
   
   /**
    * Gets the status.
    * 
    * @return the status
    */
   public String getStatus() {
      return status;
   }
   
   /**
    * Gets the percent.
    * 
    * @return the percent
    */
   public String getPercentString() {
      percent = percent > 1 ? 1 : percent;
      return String.valueOf(percent * 100).replaceAll("\\.\\d+", "") + "%";
   }
   
   /**
    * Sets the progress.
    * 
    * @param progress the new progress
    */
   public void setProgress(String progress) {
      this.progress = progress;
   }
   
   /**
    * Sets the status.
    * 
    * @param status the new status
    */
   public void setStatus(String status) {
      this.status = status;
   }
   
   /**
    * Sets the percent.
    * 
    * @param percent the new percent
    */
   public void setPercent(double percent) {
      this.percent = percent;
   }
}
