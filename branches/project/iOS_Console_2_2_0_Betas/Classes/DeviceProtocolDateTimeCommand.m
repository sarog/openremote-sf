//
//  DeviceProtocolDateTimeCommand.m
//  openremote
//
//  Created by Eric Bariaux on 08/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DeviceProtocolDateTimeCommand.h"
#import "LocalSensor.h"
#import "ClientSideRuntime.h"
#import "SensorStatusCache.h"

@interface DeviceProtocolDateTimeCommand()

@property (nonatomic, retain) NSTimer *refreshTimer;
@property (nonatomic, retain) NSMutableSet *sensorIds;
@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;

- (void)refresh:(NSTimer *)timer;

@end

@implementation DeviceProtocolDateTimeCommand

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super init];
    if (self) {
        self.sensorIds = [NSMutableSet set];
        self.clientSideRuntime = runtime;
    }
    return self;
}

- (void)dealloc
{
    self.sensorIds = nil;
    [self.refreshTimer invalidate];
    self.refreshTimer = nil;
    self.clientSideRuntime = nil;
    [super dealloc];
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    if (![self.sensorIds count]) {
        self.refreshTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(refresh:) userInfo:nil repeats:YES];
    }
    [self.sensorIds addObject:[NSNumber numberWithInt:sensor.componentId]];
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    [self.sensorIds removeObject:[NSNumber numberWithInt:sensor.componentId]];
    if (![self.sensorIds count]) {
        [self.refreshTimer invalidate];
        self.refreshTimer = nil;
    }
}

- (void)refresh:(NSTimer *)timer
{
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
	[dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
	[dateFormatter setDateStyle:NSDateFormatterShortStyle];
	NSString *dateTimeString = [dateFormatter stringFromDate:[NSDate date]];
	[dateFormatter release];

    for (NSNumber *sensorId in self.sensorIds) {
        [self.clientSideRuntime.sensorStatusCache publishNewValue:dateTimeString forSensorId:[sensorId intValue]];
    }
}

@synthesize refreshTimer;
@synthesize sensorIds;
@synthesize clientSideRuntime;

@end