//
//  ButtonCommandDeferredBinding.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ControllerComponentCommandDeferredBinding.h"
#import "Definition.h"
#import "LocalController.h"
#import "ControllerComponent.h"

@interface ControllerComponentCommandDeferredBinding()

@property (nonatomic, retain) NSString *action;

@end

@implementation ControllerComponentCommandDeferredBinding

- (id)initWithBoundComponentId:(int)anId enclosingObject:(id)anEnclosingObject action:(NSString *)anAction
{
    self = [super initWithBoundComponentId:anId enclosingObject:anEnclosingObject];
    if (self) {
        self.action = anAction;
    }
    return self;
}

- (void)dealloc
{
    self.action = nil;
    [super dealloc];
}

- (void)bind
{
    [((ControllerComponent *)self.enclosingObject) addCommand:[self.definition.localController commandForId:self.boundComponentId] forAction:self.action];
}

@synthesize action;

@end