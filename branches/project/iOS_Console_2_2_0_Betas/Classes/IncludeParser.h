//
//  IncludeParser.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "DefinitionElementParser.h"

@interface IncludeParser : DefinitionElementParser

@property (nonatomic, retain, readonly) NSString *type;
@property (nonatomic, assign, readonly) NSUInteger ref;

@end