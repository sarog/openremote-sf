//
//  PropertyParser.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#import "DefinitionElementParser.h"

@interface PropertyParser : DefinitionElementParser

@property (nonatomic, retain, readonly) NSString *name;
@property (nonatomic, retain, readonly) NSString *value;

@end