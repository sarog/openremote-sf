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