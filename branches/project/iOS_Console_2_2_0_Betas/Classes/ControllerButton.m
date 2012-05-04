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

@property (nonatomic, retain) NSMutableArray *commandRefs;

@end

@implementation ControllerButton

- (id)initWithId:(int)anId
{
    self = [super initWithId:anId];
    if (self) {
        self.commandRefs = [NSMutableArray array];
    }
    return self;
}

- (void)dealloc
{
    self.commandRefs = nil;
    [super dealloc];
}

- (void)addCommandRef:(NSUInteger)ref
{
    [commandRefs addObject:[NSNumber numberWithInt:ref]];
}

// TODO: this is only valid for 2.0 API, must check for 2.1 (long button press support)
- (NSDictionary *)commandsPerAction:(LocalController *)localController;
{
    NSMutableArray *tmpArray = [NSMutableArray array];
    
    // Collect only local commands;
    for (NSNumber *commandId in commandRefs) {
        if ([localController commandForId:[commandId intValue]]) {
            [tmpArray addObject:commandId];
        }
    }
    return [NSDictionary dictionaryWithObject:[NSArray arrayWithArray:tmpArray] forKey:@"click"];
}

@synthesize commandRefs;

@end