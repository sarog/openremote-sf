//
//  Capability.m
//  openremote
//
//  Created by Eric Bariaux on 24/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "Capability.h"

@interface Capability()

@property (nonatomic, copy, readwrite) NSString *name;
@property (nonatomic, copy, readwrite) NSDictionary *properties;

@end

@implementation Capability

- (id)initWithName:(NSString *)aName properties:(NSDictionary *)someProperties
{
    self = [super init];
    if (self) {
        self.name = aName;
        self.properties = someProperties;
    }
    return self;
}

- (void)dealloc
{
    self.name = nil;
    self.properties = nil;
    [super dealloc];
}

@synthesize name;
@synthesize properties;

@end