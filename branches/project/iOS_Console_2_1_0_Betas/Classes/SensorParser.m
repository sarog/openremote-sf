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
#import "SensorParser.h"
#import "LocalSensor.h"
#import "XMLEntity.h"

@implementation SensorParser

@synthesize sensor;

- (void)dealloc
{
    [sensor release];
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        sensor = [[LocalSensor alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                       className:[attributeDict objectForKey:CLASS]
                                      methodName:[attributeDict objectForKey:METHOD]
                                     refreshRate:([attributeDict objectForKey:REFRESH_RATE]?[NSNumber numberWithInt:[[attributeDict objectForKey:REFRESH_RATE] intValue]]:nil)];
    }
    return self;
}

@end
