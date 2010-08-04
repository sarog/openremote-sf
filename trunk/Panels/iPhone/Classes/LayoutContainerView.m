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

#import "LayoutContainerView.h"
#import "LayoutContainer.h"
#import "AbsoluteLayoutContainerView.h"
#import "AbsoluteLayoutContainer.h"
#import "GridLayoutContainer.h"
#import	"GridLayoutContainerView.h"


@implementation LayoutContainerView

@synthesize layout;


+ (LayoutContainerView *)buildWithLayoutContainer:(LayoutContainer *)layoutContainer {
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
	return [layoutView initWithLayoutContainer:layoutContainer frame:CGRectMake(layoutContainer.left, layoutContainer.top, layoutContainer.width, layoutContainer.height)];
}


//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
//Otherwise, UI widget inside will not work in nested UIViews
- (id)initWithLayoutContainer:(LayoutContainer *)layoutContainer frame:(CGRect)frame{
	if (self = [super initWithFrame:frame]) {
		layout = layoutContainer;	
		//transparent background 
		[self setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]]; 
		//[self setBackgroundColor:[UIColor whiteColor]];
		
		/* If you need to create a rectangle that is either larger or smaller than an existing rectangle, 
		 * centered on the same point, try CGRectInset: (use negative values for a larger rectangle or
		 * use positive values for a smaller rectangle)
		 */ 
		//[self setFrame:CGRectInset(CGRectMake(layout.left, layout.top, layout.width, layout.height),-2.0f, -2.0f)];		
		//[self setFrame:CGRectMake(layout.left, layout.top, layout.width, layout.height)];
		
	}
	return self;
}


- (void)dealloc {
	[layout release];
	[super dealloc];
}


@end