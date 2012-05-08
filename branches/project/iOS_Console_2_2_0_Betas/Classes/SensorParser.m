/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import "SensorParser.h"
#import "LocalSensor.h"
#import "CommandDeferredBinding.h"
#import "DefinitionElementParserRegister.h"
#import "XMLEntity.h"

@interface SensorParser ()

@property (nonatomic, retain, readwrite) LocalSensor *sensor;

@end

/**
 * Stores model data about sensor parsed from "include" element in panel.xml.
 * XML fragment example:
 * <link type="sensor" ref="575">
 * ......
 * </link>
 */
@implementation SensorParser

- (void)dealloc
{
    self.sensor = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        LocalSensor *tmp = [[LocalSensor alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                                      name:[attributeDict objectForKey:@"name"]
                                                      type:[attributeDict objectForKey:@"type"]];
        self.sensor = tmp;
        [tmp release];
    }
    return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	if ([elementName isEqualToString:@"ctrl:include"] && [@"command" isEqualToString:[attributeDict objectForKey:TYPE]]) {
        // This is a reference to another element, will be resolved later, put a standby in place for now
        CommandDeferredBinding *standby = [[CommandDeferredBinding alloc] initWithBoundComponentId:[[attributeDict objectForKey:REF] intValue] enclosingObject:self.sensor];
        standby.definition = self.depRegister.definition;
        [self.depRegister addDeferredBinding:standby];
        [standby release];
	}
    [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
}

@synthesize sensor;

@end