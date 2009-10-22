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


#import "ScreenView.h"
#import "Control.h"
#import "ControlView.h"
#import "LayoutContainer.h"
#import "LayoutContainerView.h"


@interface ScreenView (Private) 
- (void)createLayout;
@end

@implementation ScreenView

@synthesize screen, layoutContainerViews;

//override the constractor
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
			layoutContainerViews = [[NSMutableArray alloc] init];
    }
    return self;
}

//Set screen *and* render its layout 
- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	[self createLayout];
}
	
//create each layout container in screen
- (void)createLayout {
//	if (layoutContainerViews.count != 0) {
//		[layoutContainerViews release];
//		layoutContainerViews = [[NSMutableArray alloc] init];
//	}


	for (LayoutContainer *layout in screen.layouts) { 
		LayoutContainerView *layoutView = [LayoutContainerView buildWithLayoutContainer:layout];
		[self addSubview:layoutView];
		[layoutContainerViews addObject:layoutView];
		[layoutView release];
	}
	
}


//override layoutSubviews method of UIView, In order to resize the ControlView when add subview
// Only in this time we can know this view's size
- (void)layoutSubviews {
	//[screenNameLabel setFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y, self.bounds.size.width, 20)];

	//int h = self.bounds.size.height/screen.rows;	
//	//int h = (self.bounds.size.height-20)/screen.rows;
//	int w = self.bounds.size.width/screen.cols;
//	
//	
//	for (ControlView *controlView in controlViews) {
//		Control *control = [controlView control];
//		[controlView setFrame:CGRectInset(CGRectMake(control.x*w, control.y*h, w*control.width, h*control.height),roundf(w*0.1),  roundf(h*0.1))];
//		//[controlView setFrame:CGRectInset(CGRectMake(control.x*w, (control.y*h +20), w*control.width, h*control.height),roundf(w*0.1), roundf(h*0.1))];
//		[controlView layoutSubviews];
	//}
}

- (void)dealloc {
	[screen release];
	[layoutContainerViews release];
	
	[super dealloc];
}


@end
