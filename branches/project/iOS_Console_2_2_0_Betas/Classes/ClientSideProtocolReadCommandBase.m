//
//  ClientSideProtocolReadCommandBase.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ClientSideProtocolReadCommandBase.h"
#import "ClientSideRuntime.h"
#import "SensorStatusCache.h"
#import "LocalSensor.h"

@interface ClientSideProtocolReadCommandBase ()

@property (nonatomic, retain) NSMutableSet *sensorIds;
@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;

- (void)startUpdating;
- (void)stopUpdating;
- (NSString *)sensorValue;

@end

@implementation ClientSideProtocolReadCommandBase

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
    self.clientSideRuntime = nil;
    [super dealloc];
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    if (![self.sensorIds count]) {
        [self startUpdating];
    }
    [self.clientSideRuntime.sensorStatusCache publishNewValue:[self sensorValue] forSensorId:sensor.componentId];
    [self.sensorIds addObject:[NSNumber numberWithInt:sensor.componentId]];
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    [self.sensorIds removeObject:[NSNumber numberWithInt:sensor.componentId]];
    if (![self.sensorIds count]) {
        [self stopUpdating];
    }
}

- (void)publishValue
{
    NSString *sensorValue = [self sensorValue];
    for (NSNumber *sensorId in self.sensorIds) {
        [self.clientSideRuntime.sensorStatusCache publishNewValue:sensorValue forSensorId:[sensorId intValue]];
    }
}

- (void)startUpdating
{
    // To be implemented by subclasses
}

- (void)stopUpdating
{
    // To be implemented by subclasses
}

- (NSString *)sensorValue
{
    // To be implemented by subclasses
    return @"";
}

@synthesize sensorIds;
@synthesize clientSideRuntime;

@end