//
//  ControllerSlider.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ControllerComponent.h"

@class LocalSensor;

@interface ControllerSlider : ControllerComponent

@property (nonatomic, retain) LocalSensor *sensor;

@end