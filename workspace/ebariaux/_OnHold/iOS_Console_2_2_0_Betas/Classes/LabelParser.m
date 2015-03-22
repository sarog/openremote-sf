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
#import "LabelParser.h"
#import "Label.h"
#import "SensorLinkParser.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"
#import "SensorState.h"
#import "XMLEntity.h"
#import "Sensor.h"

@interface LabelParser ()

@property (nonatomic, retain, readwrite) Label *label;

@end
/**
 * Stores text, textcolor, font size and parsed from element label in panel.xml.
 * XML fragment example:
 * <label id="59" fontSize="14" color="#AAAAAA" text="AWaiting">
 *    <link type="sensor" ref="1001">
 *       <state name="on" value="LAMP_ON" />
 *       <state name="off" value="LAMP_OFF" />
 *    </link>
 * </label>
 */
@implementation LabelParser

- (void)dealloc
{
    self.label = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK];
        Label *tmp = [[Label alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                 fontSize:[[attributeDict objectForKey:FONT_SIZE] intValue]
                                    color:[attributeDict objectForKey:COLOR]
                                     text:[attributeDict objectForKey:TEXT]];
        self.label = tmp;
        [tmp release];
    }
    return self;
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        self.label.sensor = parser.sensor;
        
        
        // TODO: why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in self.label.sensor.states) {
			[self.depRegister.definition addImageName:state.value];
		}
    }
}

@synthesize label;

@end