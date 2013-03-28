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
#import "SensorLinkParser.h"
#import "Sensor.h"
#import "SensorStateParser.h"
#import "XMLEntity.h"

@interface SensorLinkParser ()

@property (nonatomic, retain, readwrite) Sensor *sensor;

@end

@implementation SensorLinkParser

- (void)dealloc
{
    self.sensor = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:STATE];
        if ([SENSOR isEqualToString:[attributeDict objectForKey:TYPE]]) {
            Sensor *tmp = [[Sensor alloc] initWithId:[[attributeDict objectForKey:REF] intValue]];
            self.sensor = tmp;
            [tmp release];
            
            // TODO: check semantic on this, this is a duplication of the sensor, not a link
        }
    }
    return self;
}

- (void)endSensorStateElement:(SensorStateParser *)parser
{
    [self.sensor.states addObject:parser.sensorState];
}

@synthesize sensor;

@end