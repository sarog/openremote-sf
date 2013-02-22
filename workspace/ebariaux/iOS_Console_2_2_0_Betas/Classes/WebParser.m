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
#import "WebParser.h"
#import "Web.h"
#import "SensorLinkParser.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"
#import "SensorState.h"
#import "Sensor.h"
#import "XMLEntity.h"

@interface WebParser ()

@property (nonatomic, retain, readwrite) Web *web;

@end

@implementation WebParser

- (void)dealloc
{
    self.web = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK];
        Web *tmp = [[Web alloc] initWithId:[[attributeDict objectForKey:ID] intValue] src:[attributeDict objectForKey:SRC] username:[attributeDict objectForKey:USERNAME] password:[attributeDict objectForKey:PASSWORD]];
        self.web = tmp;
        [tmp release];
    }
    return self;
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        self.web.sensor = parser.sensor;
        
        
        // TODO: why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in self.web.sensor.states) {
			[self.depRegister.definition addImageName:state.value];
		}
    }
}

@synthesize web;

@end