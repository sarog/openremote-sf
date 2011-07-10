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
#import "PollingHelper.h"
#import "AppDelegate.h"
#import "URLConnectionHelper.h"
#import "LocalLogic.h"
#import "LocalSensor.h"
#import "PollingStatusParserDelegate.h"
#import "ORConsoleSettingsManager.h"
#import "ORControllerProxy.h"

//retry polling after half a second
#define POLLING_RETRY_DELAY 0.5

@interface PollingHelper ()
    
@property (nonatomic, retain) ORControllerPollingSender *pollingSender;
    
@end
    
@implementation PollingHelper

@synthesize isPolling, pollingStatusIds, isError, pollingSender;

- (void)setPollingSender:(ORControllerPollingSender *)aPollingSender
{
    if (pollingSender != aPollingSender) {
        pollingSender.delegate = nil;
        [pollingSender release];
        pollingSender = [aPollingSender retain];
    }
}

- (id) initWithComponentIds:(NSString *)ids
{
    self = [super init];
	if (self) {
		isPolling = NO;
		isError = NO;
		
		NSMutableArray *remoteSensors = [NSMutableArray array];
		NSMutableArray *tempLocalSensors = [NSMutableArray array];
		for (NSString *anId in [ids componentsSeparatedByString:@","]) {
			LocalSensor *sensor = [[Definition sharedDefinition].localLogic sensorForId:[anId intValue]];
			if (sensor) {
				[tempLocalSensors addObject:sensor];
			} else {
				[remoteSensors addObject:anId];
			}
		}
		if ([remoteSensors count] > 0) {
			pollingStatusIds = [[remoteSensors componentsJoinedByString:@","] retain];
		}
		localSensors = [[NSArray arrayWithArray:tempLocalSensors] retain];
		NSLog(@"pollingStatusIds %@", pollingStatusIds);
	}
	
	return self;
}

- (void)requestCurrentStatusAndStartPolling {
	if (isPolling) {
		return;
	}
	isPolling = YES;
	
	// Only if remote sensors
	if (pollingStatusIds) {
        self.pollingSender = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController requestStatusForIds:pollingStatusIds delegate:self];
	}
	
	// For local sensors, schedule timers to handle calling the required method
	localSensorTimers = [[NSMutableDictionary dictionaryWithCapacity:[localSensors count]] retain];
	for (LocalSensor *sensor in localSensors) {
		[localSensorTimers setObject:[NSTimer scheduledTimerWithTimeInterval:(sensor.refreshRate / 1000.0) target:self selector:@selector(handleLocalSensorForTimer:) userInfo:sensor repeats:YES]
							  forKey:[NSNumber numberWithInt:sensor.componentId]];
	}
}

- (void)doPolling {
    self.pollingSender = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController requestPollingForIds:pollingStatusIds delegate:self];
}

- (void)cancelLocalSensors {
	// Cancel local sensors
	for (NSTimer *timer in [localSensorTimers allValues]) {
		[timer invalidate];
	}
	[localSensorTimers removeAllObjects];	
}

- (void)cancelPolling {
	isPolling = NO;
    [self.pollingSender cancel];
    [self cancelLocalSensors];
}

- (void)handleLocalSensorForTimer:(NSTimer*)theTimer {
	LocalSensor *sensor = (LocalSensor *)[theTimer userInfo];

	Class clazz = NSClassFromString(sensor.className);
	SEL selector = NSSelectorFromString([NSString stringWithFormat:@"%@:", sensor.methodName]);
	NSString *retValue = [clazz performSelector:selector withObject:((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext];

    if (retValue) {
        PollingStatusParserDelegate *delegate = [[PollingStatusParserDelegate alloc] init];
        [delegate publishNewValue:retValue forSensorId:[NSString stringWithFormat:@"%d", sensor.componentId]];
        [delegate release];
    }	
}

#pragma mark ORControllerPollingSenderDelegate implementation

- (void)pollingDidFailWithError:(NSError *)error;
{
    
	//if iphone is in sleep mode, retry polling after a while.
	if (![URLConnectionHelper isWifiActive]) {
		[NSTimer scheduledTimerWithTimeInterval:POLLING_RETRY_DELAY 
                                         target:self 
                                       selector:@selector(doPolling) 
                                       userInfo:nil 
                                        repeats:NO];
	} else if (!isError) {
		NSLog(@"Polling failed, %@",[error localizedDescription]);
		isError = YES;
	}    
}

- (void)pollingDidSucceed
{
    [URLConnectionHelper setWifiActive:YES];
    isError = NO;
    if (isPolling == YES) {
        [self doPolling];
    }    
}

- (void)pollingDidTimeout
{
    // Polling timed out, need to refresh
    isError = NO;				
    if (isPolling == YES) {
        [self doPolling];
    }
}

- (void)pollingDidReceiveErrorResponse
{
    isError = YES;
    isPolling = NO;
}



- (void)dealloc
{
	[pollingSender release];
	[pollingStatusIds release];
	[self cancelLocalSensors];
	[localSensors release];
	[localSensorTimers release];
	[super dealloc];
}

@end
