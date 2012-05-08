//
//  SIPProtocol.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "SIPProtocol.h"

@implementation SIPProtocol

- (void)executeCommand:(LocalCommand *)command
{
    NSLog(@"SIP executing command %@", command);
}

- (void)startUpdatingSensor:(LocalSensor *)sensor
{
    NSLog(@"SIP start update sensor %@", sensor);
}

- (void)stopUpdatingSensor:(LocalSensor *)sensor
{
    NSLog(@"SIP stop update sensor %@", sensor);
}

@end
