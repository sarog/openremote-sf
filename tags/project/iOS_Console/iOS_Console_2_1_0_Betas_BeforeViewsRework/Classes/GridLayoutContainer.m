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
#import "GridLayoutContainer.h"
#import "GridCell.h"
#import "SensorComponent.h"
#import "Sensor.h"

@implementation GridLayoutContainer

@synthesize cells, rows, cols;

- (id)initWithLeft:(int)leftPos top:(int)topPos width:(int)widthDim height:(int)heightDim rows:(int)rowsNum cols:(int)colsNum
{
    self = [super init];
    if (self) {
        left = leftPos;
        top = topPos;
        width = widthDim;
        height = heightDim;
        rows = rowsNum;
        cols = colsNum;
		cells = [[NSMutableArray alloc] init];
    }
    return self;
}

- (NSArray *)pollingComponentsIds {
	NSMutableArray *ids = [[[NSMutableArray alloc] init] autorelease];
	for (GridCell *cell in cells) {
		if ([cell.component isKindOfClass:SensorComponent.class]){
			Sensor *sensor = ((SensorComponent *)cell.component).sensor;
			if (sensor) {
				[ids addObject:[NSString stringWithFormat:@"%d",sensor.sensorId]];
			}
			
		} 
	}
	return ids;
}


- (void)dealloc {
	[cells release];
	
	[super dealloc];
}

@end
