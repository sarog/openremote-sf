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
#import <Foundation/Foundation.h>
#import "ControllerException.h"

#define ROUNDROBIN_TCP_SERVER_START_FAIL       450 //The round-robin TCP server of controller didn't startup.
#define ROUNDROBIN_UDP_SERVER_START_FAIL       451 //The round-robin UDP server of controller didn't startup.
#define ROUNDROBIN_UDP_CLIENT_START_FAIL       452 //The round-robin UDP client of controller didn't startup.


/*
 Exception while round-robin happens.
 
 A round robin is an arrangement of choosing all elements in a group equally in some rational order, 
 usually from the top to the bottom of a list and then starting again at the top of the list and so on. 
 A simple way to think of round robin is that it is about "taking turns." 
 Used as an adjective, round robin becomes "round-robin."
 
 failover is the capability to switch over automatically to a redundant 
 or standby server in the same group upon the failure.
 
 when current Controller is not available, 
 console should switch to another Controller in the same group.
 */

@interface RoundRobinException : ControllerException {

}

//convenient method to find concrete exception message by error code.
+ (NSString *)exceptionMessageOfCode:(int)code;

@end
