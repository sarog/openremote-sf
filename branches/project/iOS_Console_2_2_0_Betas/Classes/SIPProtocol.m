//
//  SIPProtocol.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "SIPProtocol.h"
#import "ClientSideRuntime.h"

@interface SIPProtocol()

@property (nonatomic, retain) ClientSideRuntime *clientSideRuntime;

@end

@implementation SIPProtocol

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

@synthesize clientSideRuntime;

@end