//
//  ControllerSliderParser.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DefinitionElementParser.h"

@class ControllerComponent;

@interface ControllerSliderParser : DefinitionElementParser

@property (nonatomic, retain) ControllerComponent *slider;

@end