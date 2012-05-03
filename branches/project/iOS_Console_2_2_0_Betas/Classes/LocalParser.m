//
//  LocalParser.m
//  openremote
//
//  Created by Eric Bariaux on 03/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "LocalParser.h"
#import "LocalController.h"

@interface LocalParser ()

@property (nonatomic, retain, readwrite) LocalController *localController;

@end

@implementation LocalParser

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
//        [self addKnownTag:SENSOR];
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
/*
- (void)endSensorElement:(SensorParser *)parser
{
    [self.localLogic addSensor:parser.sensor];
}

- (void)endCommandElement:(CommandParser *)parser
{
    [self.localLogic addCommand:parser.command];
}

- (void)endTaskElement:(TaskParser *)parser
{
    [self.localLogic addTask:parser.task];
}
 */

@synthesize localController;

@end
