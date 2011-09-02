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
#import "LayoutContainerView.h"
#import "LayoutContainer.h"
#import "AbsoluteLayoutContainerView.h"
#import "AbsoluteLayoutContainer.h"
#import "GridLayoutContainer.h"
#import	"GridLayoutContainerView.h"

@implementation LayoutContainerView

@synthesize layout;

+ (LayoutContainerView *)layoutContainerViewWithLayoutContainer:(LayoutContainer *)layoutContainer
{
	LayoutContainerView* layoutView = nil;
	if ([layoutContainer isKindOfClass:[AbsoluteLayoutContainer class]]) {
		layoutView = [AbsoluteLayoutContainerView alloc];
	} else if ([layoutContainer isKindOfClass:[GridLayoutContainer class]]) {
		layoutView = [GridLayoutContainerView alloc];
	} else {
		layoutView = [LayoutContainerView alloc];
	}

	//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
	//Otherwise, UI widget inside will not work in nested UIViews
	return [[layoutView initWithLayoutContainer:layoutContainer frame:CGRectMake(layoutContainer.left, layoutContainer.top, layoutContainer.width, layoutContainer.height)] autorelease];
}


//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
//Otherwise, UI widget inside will not work in nested UIViews
- (id)initWithLayoutContainer:(LayoutContainer *)layoutContainer frame:(CGRect)frame
{
    self = [super initWithFrame:frame];
	if (self) {
		layout = [layoutContainer retain];
        
		// Transparent background
        self.backgroundColor = [UIColor clearColor];
		
		/* If you need to create a rectangle that is either larger or smaller than an existing rectangle, 
		 * centered on the same point, try CGRectInset: (use negative values for a larger rectangle or
		 * use positive values for a smaller rectangle)
		 */ 
		//[self setFrame:CGRectInset(CGRectMake(layout.left, layout.top, layout.width, layout.height),-2.0f, -2.0f)];		
		//[self setFrame:CGRectMake(layout.left, layout.top, layout.width, layout.height)];
		
	}
	return self;
}

- (void)dealloc
{
	[layout release];
	[super dealloc];
}

@end