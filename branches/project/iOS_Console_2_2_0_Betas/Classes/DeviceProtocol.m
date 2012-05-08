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
#import "DeviceProtocol.h"
#import "LocalSensor.h"
#import "LocalCommand.h"
#import "DeviceProtocolDateTimeCommand.h"
#import "ClientSideRuntime.h"

@interface DeviceProtocol()

@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;

@end

@implementation DeviceProtocol

- (id)initWithRuntime:(ClientSideRuntime *)runtime
{
    self = [super init];
    if (self) {
        self.clientSideRuntime = runtime;
    }
    return self;
}

- (void)dealloc
{
    self.clientSideRuntime = nil;
    [super dealloc];
}

- (void)executeCommand:(LocalCommand *)command
{
    NSLog(@"Device executing command %@", command);
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    LocalCommand *command = sensor.command;
    if ([@"DATE_TIME" isEqualToString:[command propertyValueForKey:@"command"]]) {
        
        // TODO : should keep handle on command -> reuse + ensure does not go away while sensor is being updated
        
        DeviceProtocolDateTimeCommand *cmd = [[DeviceProtocolDateTimeCommand alloc] initWithRuntime:self.clientSideRuntime];
        [cmd startUpdatingSensor:sensor];
        [cmd release];
    } else if ([@"BATTERY_LEVEL" isEqualToString:[command propertyValueForKey:@"command"]]) {
        // Register with system for battery level notifications
    }
    NSLog(@"Device start update sensor %@", sensor);

    // I should have a list of sensors registered with each command
    // A list of already registered commands
    
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    // TODO: implement -> then test adding a second page to design -> navigate away should stop updates
    
    NSLog(@"Device stop update sensor %@", sensor);
}

@synthesize clientSideRuntime;

@end