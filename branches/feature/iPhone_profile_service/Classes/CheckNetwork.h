/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

/**
 * Wifi Network checking util.
 */
#import <Foundation/Foundation.h>

@interface CheckNetwork : NSObject {
}

/**
 * Check if wifi network is available. If wifi network isn't available, this method will throw CheckNetworkException.
 */
+ (void)checkWhetherNetworkAvailable;

/**
 * Check if ip address of controller server is available. If it isn't, this method will throw CheckNetworkException.
 */
+ (void)checkIPAddress;

/**
 * Check if controller server's url is available. If it isn't, this method will throw CheckNetworkException.
 */
+ (void)checkControllerAvailable;

/**
 * Check if the url of panel RESTful request if available. If it isn't, this method will throw CheckNetworkException.
 */
+ (void)checkPanelXml;

/**
 * Call previous checking method.
 */
+ (void)checkAll;
@end
