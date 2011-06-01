/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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
#import "GridLayoutContainerView.h"
#import "GridLayoutContainer.h"
#import "GridCellView.h"
#import "GridCell.h"

@implementation GridLayoutContainerView

// Override method of UIView and be called automatically.
- (void)layoutSubviews
{
	GridLayoutContainer *grid = (GridLayoutContainer *)layout;
	int h = self.bounds.size.height / grid.rows;				
	int w = self.bounds.size.width / grid.cols;
	
	if (grid) {
		for (GridCell *cell in grid.cells){
			GridCellView *cellView = [[GridCellView alloc] initWithGridCell:cell frame:CGRectMake(cell.x * w, cell.y * h, w * cell.colspan, h * cell.rowspan)];
			[self addSubview:cellView];
            [cellView release];
		}
	}
}

@end
