/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "LocalController.h"
#import "LocalSensor.h"
#import "SensorStatusCache.h"
#import "ClientSideRuntime.h"
#import "ORConsoleSettingsManager.h"
#import "ORControllerProxy.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

//retry polling after half a second
#define POLLING_RETRY_DELAY 0.5

@interface PollingHelper ()

@property(nonatomic, readwrite) BOOL isPolling;
@property(nonatomic, readwrite) BOOL isError;
@property(nonatomic, retain, readwrite) NSString *pollingStatusIds;
@property (nonatomic, retain) NSArray *localSensors;
@property (nonatomic, retain) ORControllerPollingSender *pollingSender;

@property (nonatomic, retain) UpdateController *updateController;

@property (nonatomic, assign) SensorStatusCache *sensorStatusCache;
@property (nonatomic, assign) ClientSideRuntime *clientSideRuntime;

@end
    
@implementation PollingHelper

- (id)initWithController:(ORController *)controller componentIds:(NSString *)ids;
{
    self = [super init];
	if (self) {
		self.isPolling = NO;
		self.isError = NO;
        
        self.sensorStatusCache = controller.sensorStatusCache;
        self.clientSideRuntime = controller.clientSideRuntime;
		
		NSMutableArray *remoteSensors = [NSMutableArray array];
		NSMutableArray *tempLocalSensors = [NSMutableArray array];
		for (NSString *anId in [ids componentsSeparatedByString:@","]) {
			LocalSensor *sensor = [controller.definition.localController sensorForId:[anId intValue]];
			if (sensor) {
				[tempLocalSensors addObject:sensor];
			} else {
				[remoteSensors addObject:anId];
			}
		}
		if ([remoteSensors count] > 0) {
			self.pollingStatusIds = [remoteSensors componentsJoinedByString:@","];
		}
		self.localSensors = [NSArray arrayWithArray:tempLocalSensors];
		NSLog(@"pollingStatusIds %@", self.pollingStatusIds);
	}
	
	return self;
}

- (void)requestCurrentStatusAndStartPolling {
	if (self.isPolling) {
		return;
	}
	self.isPolling = YES;
	
	// Only if remote sensors
	if (self.pollingStatusIds) {
        self.pollingSender = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController requestStatusForIds:self.pollingStatusIds delegate:self];
	}
	
	for (LocalSensor *sensor in self.localSensors) {
        [self.clientSideRuntime startUpdatingSensor:sensor];
	}
}

- (void)doPolling {
    self.pollingSender = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController requestPollingForIds:self.pollingStatusIds delegate:self];
}

- (void)cancelLocalSensors {
    for (LocalSensor *sensor in self.localSensors) {
        [self.clientSideRuntime stopUpdatingSensor:sensor];
    }
}

- (void)cancelPolling {
	self.isPolling = NO;
    [self.pollingSender cancel];
    [self cancelLocalSensors];
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
	} else if (!self.isError) {
		NSLog(@"Polling failed, %@",[error localizedDescription]);
		self.isError = YES;
	}    
}

- (void)pollingDidSucceed
{
    [URLConnectionHelper setWifiActive:YES];
    self.isError = NO;
    if (self.isPolling == YES) {
        [self doPolling];
    }    
}

- (void)pollingDidTimeout
{
    // Polling timed out, need to refresh
    self.isError = NO;				
    if (self.isPolling == YES) {
        [self doPolling];
    }
}

- (void)pollingDidReceiveErrorResponse
{
    self.isError = YES;
    self.isPolling = NO;
}

- (void)controllerConfigurationUpdated:(ORController *)aController
{
    if (!self.updateController) {
        UpdateController *tmpController = [[UpdateController alloc] initWithDelegate:self];
        self.updateController = tmpController;
        [tmpController release];
    }
    [self.updateController checkConfigAndUpdate];
}

- (void)dealloc
{
    self.sensorStatusCache = nil;
    self.clientSideRuntime = nil;
	self.pollingSender = nil;
    self.pollingStatusIds = nil;
	[self cancelLocalSensors];
    self.localSensors = nil;
    self.updateController = nil;
	[super dealloc];
}

#pragma mark Delegate method of UpdateController

- (void)didUpdate
{
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage
{
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Use Local Cache" Message:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage
{
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Update Failed" Message:errorMessage];
	}
}

@synthesize isPolling, pollingStatusIds, isError, pollingSender, localSensors, updateController;
@synthesize sensorStatusCache;
@synthesize clientSideRuntime;

- (void)setPollingSender:(ORControllerPollingSender *)aPollingSender
{
    if (pollingSender != aPollingSender) {
        pollingSender.delegate = nil;
        [pollingSender release];
        pollingSender = [aPollingSender retain];
    }
}

@end