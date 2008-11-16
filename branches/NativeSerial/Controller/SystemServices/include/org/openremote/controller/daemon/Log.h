/*
* OpenRemote, the Home of the Digital Home.
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

//--------------------------------------------------------------------------------------------------
//
// TODO
//
//
// Author: Juha Lindfors (juha@juhalindfors.com)
//
//--------------------------------------------------------------------------------------------------

/**
 * Some logging macros. These need to go to /var/logs eventually but for now just print on
 * standard out.
 */
#define loginfo(content, args...)           \
    printf("[INFO] " content "\n", args);   \
    fflush(stdout);

#define logerror(content, args...)          \
    printf("[ERROR] " content "\n", args);  \
    fflush(stdout);

#define logdebug(content, args...)          \
    printf("[DEBUG] " content "\n", args);  \
    fflush(stdout);

#define logwarn(content, args...)           \
    printf("[WARN] " content "\n", args);   \
    fflush(stdout);

#define logtrace(content, args...)           \
    printf("[TRACE] " content "\n", args);   \
    fflush(stdout);

