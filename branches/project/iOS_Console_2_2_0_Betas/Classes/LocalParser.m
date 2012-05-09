//
//  LocalParser.m
//  openremote
//
//  Created by Eric Bariaux on 03/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "LocalParser.h"
#import "LocalController.h"
#import "CommandParser.h"
#import "SensorParser.h"
#import "ControllerButtonParser.h"
#import "ControllerSwitchParser.h"
#import "ControllerButton.h"

@interface LocalParser ()

@property (nonatomic, retain, readwrite) LocalController *localController;

@end

@implementation LocalParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:@"ctrl:command"];
        [self addKnownTag:@"ctrl:sensor"];
        [self addKnownTag:@"ctrl:button"];
        [self addKnownTag:@"ctrl:switch"];
        LocalController *tmp = [[LocalController alloc] init];
        self.localController = tmp;
        [tmp release];
    }
    return self;
}

- (void)dealloc
{
    self.localController = nil;
    [super dealloc];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    NSLog(@"Parser didStartElement >%@< namespaceURI >%@<", elementName, namespaceURI);
    [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

- (void)endSensorElement:(SensorParser *)parser
{
    [self.localController addSensor:parser.sensor];
}

- (void)endCommandElement:(CommandParser *)parser
{
    [self.localController addCommand:parser.command];
}

- (void)endButtonElement:(ControllerButtonParser *)parser
{
    [self.localController addComponent:parser.button];
}

- (void)endSwitchElement:(ControllerSwitchParser *)parser
{
    [self.localController addComponent:parser.theSwitch];
}

@synthesize localController;

@end
