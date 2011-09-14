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
#import "SensorStateParser.h"
#import "SensorState.h"
#import "XMLEntity.h"

@interface SensorStateParser ()

@property (nonatomic, retain, readwrite) SensorState *sensorState;

@end

/**
 * Stores model data about state parsed from element "state" in panel.xml.
 * XML fragment example:
 * ......
 * <state name="off" value="light is off" />
 * <state name="on" value="light is on" />
 * ......
 */
@implementation SensorStateParser

- (void)dealloc
{
    self.sensorState = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        SensorState *tmp = [[SensorState alloc] initWithName:[attributeDict objectForKey:NAME] value:[attributeDict objectForKey:VALUE]];
        self.sensorState = tmp;
        [tmp release];
    }
    return self;
}

@synthesize sensorState;

@end