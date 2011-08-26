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
#import "SwitchParser.h"
#import "Switch.h"
#import "SensorLinkParser.h"
#import "SensorState.h"
#import "DefinitionElementParserRegister.h"
#import "Definition.h"

@implementation SwitchParser

@synthesize sswitch;

- (void)dealloc
{
    [sswitch release];
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:LINK]; 
        sswitch = [[Switch alloc] initWithId:[[attributeDict objectForKey:ID] intValue]];
    }
    return self;
}

- (void)endSensorLinkElement:(SensorLinkParser *)parser
{
    if (parser.sensor) {
        sswitch.sensor = parser.sensor;
        
        
        // TODO: review that
        // - why is this done (here ? maybe in SensorState itself ?) 
        for (SensorState *state in sswitch.sensor.states) {
			Image *img = [[Image alloc] init];
			img.src = state.value;
            [self.depRegister.definition addImageName:state.value];
			if ([[state.name lowercaseString] isEqualToString:ON]) {
				sswitch.onImage = img;
			} else if ([[state.name lowercaseString] isEqualToString:OFF]) {
				sswitch.offImage = img;
			}
			[img release];
		}
    }
}

@end
