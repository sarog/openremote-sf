//
//  ControllerButton.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ControllerButton.h"
#import "LocalController.h"

@interface ControllerButton()

@property (nonatomic, retain) NSMutableDictionary *commandsPerActionRegistry;

@end

@implementation ControllerButton

- (id)initWithId:(int)anId
{
    self = [super initWithId:anId];
    if (self) {
        self.commandsPerActionRegistry = [NSMutableDictionary dictionaryWithCapacity:1];
    }
    return self;
}

- (void)dealloc
{
    self.commandsPerActionRegistry = nil;
    [super dealloc];
}

- (void)addCommand:(LocalCommand *)aCommand forAction:(NSString *)anAction
{
    NSMutableArray *commands = [self.commandsPerAction objectForKey:anAction];
    if (!commands) {
        commands = [NSMutableArray array];
        [self.commandsPerActionRegistry setObject:commands forKey:anAction];
    }
    [commands addObject:aCommand];
}

// TODO: this is only valid for 2.0 API, must check for 2.1 (long button press support)
- (NSDictionary *)commandsPerAction
{
    return [NSDictionary dictionaryWithDictionary:self.commandsPerActionRegistry];
}

@synthesize commandsPerActionRegistry;

@end