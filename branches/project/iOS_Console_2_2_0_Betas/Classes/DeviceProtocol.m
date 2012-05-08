//
//  DeviceProtocol.m
//  openremote
//
//  Created by Eric Bariaux on 08/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DeviceProtocol.h"

@implementation DeviceProtocol

- (void)executeCommand:(LocalCommand *)command
{
    NSLog(@"Device executing command %@", command);
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    NSLog(@"Device start update sensor %@", sensor);
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    NSLog(@"Device stop update sensor %@", sensor);
}

@end
