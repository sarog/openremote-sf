//
//  DeviceProtocol.m
//  openremote
//
//  Created by Eric Bariaux on 08/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

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