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
#import "GridCell.h"

@interface GridCell ()

@property (nonatomic, readwrite) int x;
@property (nonatomic, readwrite) int y;
@property (nonatomic, readwrite) int rowspan;
@property (nonatomic, readwrite) int colspan;

@end

@implementation GridCell

- (id)initWithX:(int)xPos y:(int)yPos rowspan:(int)rowspanValue colspan:(int)colspanValue
{
    self = [super init];
    if (self) {
        self.x = xPos;
        self.y = yPos;
        self.rowspan = MAX(1, rowspanValue);
        self.colspan = MAX(1, colspanValue);
    }
    return self;    
}

- (void)dealloc
{
	self.component = nil;
	[super dealloc];
}

@synthesize x,y,rowspan,colspan,component;

@end