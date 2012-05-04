//
//  ControllerComponentParser.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ControllerButtonParser.h"
#import "ControllerButton.h"
#import "IncludeParser.h"
#import "XMLEntity.h"

@implementation ControllerButtonParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:@"ctrl:include"];
        ControllerButton *tmp = [[ControllerButton alloc] initWithId:[[attributeDict objectForKey:ID] intValue]];
        self.button = tmp;
        [tmp release];
    }
    return self;
}

- (void)endIncludeElement:(IncludeParser *)parser
{
    if ([@"command" isEqualToString:parser.type]) {
        [self.button addCommandRef:parser.ref];
    }
}

@synthesize button;

@end