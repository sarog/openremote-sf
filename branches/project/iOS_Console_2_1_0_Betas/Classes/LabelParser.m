/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import "LabelParser.h"
#import "Label.h"
#import "SensorLinkParser.h"

// TODO: should go later, see below
#import "Definition.h"
#import "SensorState.h"

@implementation LabelParser

@synthesize label;

- (void)dealloc
{
    [label release];
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK]; 
        label = [[Label alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                 fontSize:[[attributeDict objectForKey:FONT_SIZE] intValue]
                                    color:[attributeDict objectForKey:COLOR]
                                     text:[attributeDict objectForKey:TEXT]];
    }
    return self;
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        label.sensor = parser.sensor;
        
        
        // TODO: review that
        // - for reference to correct instance of Definition
        // - why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in label.sensor.states) {
			[[Definition sharedDefinition] addImageName:state.value];
		}
    }
}

@end
