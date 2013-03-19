//
//  DeferredBinding.m
//  openremote
//
//  Created by Eric Bariaux on 26/08/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "DeferredBinding.h"

@interface DeferredBinding()

@property (nonatomic, retain, readwrite) id enclosingObject;
@property (nonatomic, assign, readwrite) int boundComponentId;

@end

@implementation DeferredBinding

- (id)initWithBoundComponentId:(int)anId enclosingObject:(id)anEnclosingObject
{
    self = [super init];
    if (self) {
        self.boundComponentId = anId;
        self.enclosingObject = [anEnclosingObject retain];
    }
    return self;
}

- (void)dealloc
{
    self.enclosingObject = nil;
    self.definition = nil;
    [super dealloc];
}

- (void)bind
{
    [self doesNotRecognizeSelector:_cmd];
}

@synthesize boundComponentId;
@synthesize enclosingObject;
@synthesize definition;

@end