//
//  ControllerSlider.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ControllerSlider.h"

@implementation ControllerSlider

- (void)dealloc
{
    self.sensor = nil;
    [super dealloc];
}

@synthesize sensor;

@end