//
//  IncludeParser.m
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "IncludeParser.h"

@interface IncludeParser()

@property (nonatomic, retain, readwrite) NSString *type;
@property (nonatomic, assign, readwrite) NSUInteger ref;

@end

@implementation IncludeParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        self.type = [attributeDict objectForKey:@"type"];
        self.ref = [[attributeDict objectForKey:@"ref"] intValue];
    }
    return self;
}

- (void)dealloc
{
    self.type = nil;
    [super dealloc];
}

@synthesize type;
@synthesize ref;

@end