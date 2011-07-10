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
#import "GridCellView.h"
#import "GridCell.h"
#import "ComponentView.h"

@implementation GridCellView

@synthesize componentView, cell;

- (id)initWithGridCell:(GridCell *)gridCell frame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
			cell = gridCell;
			//transparent background 
			[self setBackgroundColor:[UIColor clearColor]]; 
    }
    return self;
}

// Override metho of UIView and be called automatically.
- (void)layoutSubviews
{
	if (cell.component) {
		//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
		//Otherwise, UI widget inside will not work in nested UIViews
		componentView = [[ComponentView componentViewWithComponent:cell.component frame:CGRectMake(0, 0, self.bounds.size.width, self.bounds.size.height)] retain];
	}
	
	[self addSubview:componentView];
}

- (void)dealloc
{
    [componentView release];
    [super dealloc];
}


@end
