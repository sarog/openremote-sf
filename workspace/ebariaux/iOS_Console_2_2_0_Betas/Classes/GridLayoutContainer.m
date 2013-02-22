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
#import "GridLayoutContainer.h"
#import "GridCell.h"
#import "SensorComponent.h"
#import "Sensor.h"

@interface GridLayoutContainer ()

@property (nonatomic, retain, readwrite) NSMutableArray *cells;
@property (nonatomic, readwrite) int rows;
@property (nonatomic, readwrite) int cols;

@property (nonatomic, readwrite) int left;
@property (nonatomic, readwrite) int top;
@property (nonatomic, readwrite) int width;
@property (nonatomic, readwrite) int height;

@end

@implementation GridLayoutContainer

- (id)initWithLeft:(int)leftPos top:(int)topPos width:(int)widthDim height:(int)heightDim rows:(int)rowsNum cols:(int)colsNum
{
    self = [super init];
    if (self) {
        self.left = leftPos;
        self.top = topPos;
        self.width = widthDim;
        self.height = heightDim;
        self.rows = rowsNum;
        self.cols = colsNum;
		self.cells = [NSMutableArray array];
    }
    return self;
}

- (NSArray *)pollingComponentsIds {
	NSMutableArray *ids = [[[NSMutableArray alloc] init] autorelease];
	for (GridCell *cell in self.cells) {
		if ([cell.component isKindOfClass:SensorComponent.class]){
			Sensor *sensor = ((SensorComponent *)cell.component).sensor;
			if (sensor) {
				[ids addObject:[NSString stringWithFormat:@"%d",sensor.sensorId]];
			}
			
		} 
	}
	return ids;
}

- (void)dealloc
{
	self.cells = nil;
	[super dealloc];
}

@synthesize cells, rows, cols;
@synthesize left,top,width,height;

@end