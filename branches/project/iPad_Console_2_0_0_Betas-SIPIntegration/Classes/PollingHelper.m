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

#import "PollingHelper.h"
#import "PollingStatusParserDelegate.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "ControllerException.h"
#import "AppDelegate.h"
#import "Definition.h"
#import "LocalLogic.h"
#import "LocalSensor.h"

//retry polling after half a second
#define POLLING_RETRY_DELAY 0.5

@implementation PollingHelper

@synthesize isPolling, pollingStatusIds, isError, connection;

- (id) initWithComponentIds:(NSString *)ids {
	if (self = [super init]) {
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
		NSString *location = [[NSString alloc] initWithFormat:@"%@/%@", [ServerDefinition statusRESTUrl], pollingStatusIds];
		NSURL *url = [[NSURL alloc] initWithString:location];
		//assemble put request 
		NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
		[request setURL:url];
		[request setHTTPMethod:@"GET"];
		
		connection = [[URLConnectionHelper alloc] initWithRequest:request delegate:self];
		
		[location release];
		[url	 release];
		[request release];
		//[connection autorelease];

		// TODO: EBR - connection will leak the way it's implemented, to check
	}
	
	// For local sensors, schedule timers to handle calling the required method
	localSensorTimers = [[NSMutableDictionary dictionaryWithCapacity:[localSensors count]] retain];
	for (LocalSensor *sensor in localSensors) {
		[localSensorTimers setObject:[NSTimer scheduledTimerWithTimeInterval:(sensor.refreshRate / 1000.0) target:self selector:@selector(handleLocalSensorForTimer:) userInfo:sensor repeats:YES]
							  forKey:[NSNumber numberWithInt:sensor.componentId]];
	}
}

- (void)doPolling {
	NSString *deviceId = [[UIDevice currentDevice] uniqueIdentifier];
	NSString *location = [[NSString alloc] initWithFormat:@"%@/%@/%@", [ServerDefinition pollingRESTUrl], deviceId, pollingStatusIds];
	NSURL *url = [[NSURL alloc] initWithString:location];
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"GET"];
	
	connection = [[URLConnectionHelper alloc] initWithRequest:request delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	//[connection autorelease];	
	
	// TODO: EBR - connection will leak the way it's implemented, to check
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
	if (connection) {
		[connection cancelConnection];
	}
	[self cancelLocalSensors];
}

- (void)handleLocalSensorForTimer:(NSTimer*)theTimer {
	LocalSensor *sensor = (LocalSensor *)[theTimer userInfo];

	Class clazz = NSClassFromString(sensor.className);
	SEL selector = NSSelectorFromString([NSString stringWithFormat:@"%@:", sensor.methodName]);
	NSString *retValue = [clazz performSelector:selector withObject:((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext];

	PollingStatusParserDelegate *delegate = [[PollingStatusParserDelegate alloc] init];
	[delegate publishNewValue:retValue forSensorId:[NSString stringWithFormat:@"%d", sensor.componentId]];
	[delegate release];
	
}

- (void)handleServerResponseWithStatusCode:(int) statusCode {

	if (statusCode != 200) {
		isError = YES;
		switch (statusCode) {

			case CONTROLLER_CONFIG_CHANGED://controller config changed
				updateController = [[UpdateController alloc] initWithDelegate:self];
				[updateController checkConfigAndUpdate];
				return;
			case POLLING_TIMEOUT://polling timeout, need to refresh
				isError = NO;				
				if (isPolling == YES) {
					[self doPolling];
				}
				
				return;
		} 
		
		[ViewHelper showAlertViewWithTitle:@"Polling Failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
		isPolling = NO;
	} else {
		[URLConnectionHelper setWifiActive:YES];
		isError = NO;
		if (isPolling == YES) {
			[self doPolling];
		}
	} 
	
}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
	
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

//Do polling when the request successful
- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	PollingStatusParserDelegate *delegate = [[PollingStatusParserDelegate alloc] init];
	[xmlParser setDelegate:delegate];
	[xmlParser parse];
	
	[xmlParser release];
	[result release];
	[delegate release];
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	NSLog(@"polling[%@]statusCode is %d",pollingStatusIds, [httpResp statusCode]);
	
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}

#pragma mark Delegate method of UpdateController
- (void)didUpadted {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Use Local Cache" Message:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage {
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Update Failed" Message:errorMessage];
	}
}

- (void)dealloc {
	[connection release];
	[pollingStatusIds release];
	[self cancelLocalSensors];
	[localSensors release];
	[localSensorTimers release];
	[updateController release];
	[super dealloc];
}

@end
