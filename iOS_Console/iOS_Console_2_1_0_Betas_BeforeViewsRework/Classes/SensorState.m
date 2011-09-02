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
#import "SensorState.h"

@implementation SensorState

@synthesize name, value;

- (id)initWithName:(NSString *)sensorName value:(NSString *)sensorValue
{
    self = [super init];
    if (self) {
        name = [sensorName copy];
        value = [sensorValue copy];
    }
    return self;
}

- (void)dealloc {
	[name release];
	[value release];
	
	[super dealloc];
}

@end
