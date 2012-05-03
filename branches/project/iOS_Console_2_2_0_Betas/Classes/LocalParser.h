//
//  LocalParser.h
//  openremote
//
//  Created by Eric Bariaux on 03/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DefinitionElementParser.h"

@class LocalController;

@interface LocalParser : DefinitionElementParser

@property (nonatomic, retain, readonly) LocalController *localController;

@end
