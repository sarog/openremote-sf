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
#import "AbsoluteLayoutContainer.h"
#import "SensorComponent.h"
#import "Sensor.h"

@implementation AbsoluteLayoutContainer

@synthesize component;

- (id)initWithLeft:(int)leftPos top:(int)topPos width:(int)widthDim height:(int)heightDim
{
    self = [super init];
    if (self) {
        left = leftPos;
        top = topPos;
        width = widthDim;
        height = heightDim;
    }
    return self;
}

/**
 * Get the polling ids of component in AbsoluteLayoutContainer.
 */
- (NSArray *)pollingComponentsIds {
	NSMutableArray *ids = [[[NSMutableArray alloc] init] autorelease];
	if ([component isKindOfClass:SensorComponent.class]){	
		Sensor *sensor = ((SensorComponent *)component).sensor;
		if (sensor) {
			[ids addObject:[NSString stringWithFormat:@"%d", sensor.sensorId]];
		}
		
	} 
	
	return ids;
}

- (void)dealloc
{
	[component release];	
	[super dealloc];
}


@end
