//
//  DeferredBinding.m
//  openremote
//
//  Created by Eric Bariaux on 26/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "DeferredBinding.h"

@implementation DeferredBinding

@synthesize boundComponentId;
@synthesize enclosingObject;
@synthesize definition;

- (id)initWithBoundComponentId:(int)anId enclosingObject:(id)anEnclosingObject
{
    self = [super init];
    if (self) {
        boundComponentId = anId;
        enclosingObject = [anEnclosingObject retain];
    }
    return self;
}

- (void)dealloc
{
    [enclosingObject release];
    self.definition = nil;
    [super dealloc];
}

- (void)bind
{
    [self doesNotRecognizeSelector:_cmd];
}

@end
