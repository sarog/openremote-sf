/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
 * Call previous checking method.
 */
+ (void)checkAllUsingTimeout:(NSTimeInterval)timeoutInterval;

@end
