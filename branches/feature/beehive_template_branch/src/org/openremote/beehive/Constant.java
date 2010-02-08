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
package org.openremote.beehive;

/**
 * The Class Constant.
 * 
 * @author Tomsky
 */
public final class Constant {
   
   /**
    * Instantiates a new constant.
    */
   private Constant(){
   }
   
   /** Compatibility for MySQL 5.0.x and HSQLDB (UTF-8) */
   public static final String TEXT_COLUMN_DEFINITION = "VARCHAR(10000)";
   
   /** The value for the progress file. */
   public static final String SYNC_PROGRESS_FILE = "syncProgress.txt";
   
   /** The Constant COMMIT_PROGRESS_FILE. */
   public static final String COMMIT_PROGRESS_FILE = "commitProgress.txt";
   
   /** The Constant LIRC_ROOT_URL. */
   public static final String LIRC_ROOT_URL = "http://lirc.sourceforge.net/remotes/";
   
   /** The value for svn repo and workCopy root directory. */
   public static final String ROOT_PATH = "";
   
   /** The Constant WORK_COPY. */
   public static final String WORK_COPY = "workCopy";
   
   /** The Constant SYNC_HISTORY. */
   public static final String SYNC_HISTORY = "syncHistory";
   
   public static final String HTTP_BASIC_AUTH_HEADER_NAME= "Authorization";
   
   public static final String HTTP_BASIC_AUTH_HEADER_VALUE_PREFIX= "Basic ";
   
   
   
   
}
