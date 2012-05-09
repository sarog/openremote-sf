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