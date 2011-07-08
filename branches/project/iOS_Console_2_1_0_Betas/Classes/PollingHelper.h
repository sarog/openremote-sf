/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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

#import <Foundation/Foundation.h>
#import "ORControllerPollingSender.h"

/**
 * Setup a polling connection to detect the component status changes and 
 * notificate the new status to component on screen.
 */

@interface PollingHelper : NSObject <ORControllerPollingSenderDelegate> {
	
	NSString *pollingStatusIds;
	NSArray *localSensors;
	NSMutableDictionary *localSensorTimers;
	BOOL isPolling;
	BOOL isError;
}

/**
 * Construct polling helper with sensor ids.
 */
- (id) initWithComponentIds:(NSString *)ids;

/**
 * Request the latest status of device and then start polling.
 */
- (void)requestCurrentStatusAndStartPolling;

/**
 * Cancel polling to controller server.
 */
- (void)cancelPolling;

@property(nonatomic,readonly) BOOL isPolling;
@property(nonatomic,readonly) BOOL isError;
@property(nonatomic,readonly) NSString *pollingStatusIds;

@end
