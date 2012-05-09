//
//  ControllerSwitchParser.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ControllerSwitchParser.h"
#import "ControllerComponent.h"
#import "ControllerComponentCommandDeferredBinding.h"
#import "DefinitionElementParserRegister.h"
#import "XMLEntity.h"

@interface ControllerSwitchParser()

@property (nonatomic, retain) NSString *action;

@end

@implementation ControllerSwitchParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        ControllerComponent *tmp = [[ControllerComponent alloc] initWithId:[[attributeDict objectForKey:ID] intValue]];
        self.theSwitch = tmp;
        [tmp release];
    }
    return self;
}

- (void)dealloc
{
    self.theSwitch = nil;
    self.action = nil;
    [super dealloc];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:@"ctrl:on"]) {
        self.action = @"on";
    } else if ([elementName isEqualToString:@"ctrl:off"]) {
        self.action = @"off";
    } else if ([elementName isEqualToString:@"ctrl:include"] && [@"command" isEqualToString:[attributeDict objectForKey:TYPE]]) {
        // This is a reference to another element, will be resolved later, put a standby in place for now
        ControllerComponentCommandDeferredBinding *standby = [[ControllerComponentCommandDeferredBinding alloc] initWithBoundComponentId:[[attributeDict objectForKey:REF] intValue] enclosingObject:self.theSwitch action:self.action];
        standby.definition = self.depRegister.definition;
        [self.depRegister addDeferredBinding:standby];
        [standby release];
	}
    [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

@synthesize theSwitch;
@synthesize action;

@end