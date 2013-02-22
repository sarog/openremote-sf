//
//  CommandDeferredBinding.m
//  openremote
//
//  Created by Eric Bariaux on 08/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "CommandDeferredBinding.h"
#import "Definition.h"
#import "LocalSensor.h"
#import "LocalController.h"

@implementation CommandDeferredBinding

- (void)bind
{
    ((LocalSensor *)self.enclosingObject).command = [self.definition.localController commandForId:self.boundComponentId];
}

@end