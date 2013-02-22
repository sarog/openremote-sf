/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import "GridCellView.h"
#import "GridCell.h"

@implementation GridCellView

@synthesize componentView, cell;

- (id)initWithGridCell:(GridCell *)gridCell frame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
			cell = gridCell;
			//transparent background 
			[self setBackgroundColor:[UIColor clearColor]]; 
    }
    return self;
}

// Override metho of UIView and be called automatically.
- (void)layoutSubviews {
	if (cell.component) {
		//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
		//Otherwise, UI widget inside will not work in nested UIViews
		componentView = [ComponentView buildWithComponent:cell.component frame:CGRectMake(0, 0, self.bounds.size.width, self.bounds.size.height)];
	}
	
	[self addSubview:componentView];
	
	
}

- (void)drawRect:(CGRect)rect {
    // Drawing code
}


- (void)dealloc {
    [super dealloc];
}


@end
