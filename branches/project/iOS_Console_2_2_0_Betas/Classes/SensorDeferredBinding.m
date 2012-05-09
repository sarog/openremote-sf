//
//  SensorDeferredBinding.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "SensorDeferredBinding.h"
#import "ControllerSlider.h"
#import "Definition.h"
#import "LocalController.h"

@implementation SensorDeferredBinding

- (void)bind
{
    [((ControllerSlider *)self.enclosingObject).sensor addObject:[self.definition.localController sensorForId:self.boundComponentId]];
}

@end