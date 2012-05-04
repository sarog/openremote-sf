//
//  ControllerComponentParser.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DefinitionElementParser.h"

@class ControllerButton;

@interface ControllerButtonParser : DefinitionElementParser

@property (nonatomic, retain) ControllerButton *button;

@end