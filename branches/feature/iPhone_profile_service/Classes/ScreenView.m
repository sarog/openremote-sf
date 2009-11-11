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
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "DirectoryDefinition.h"


@interface ScreenView (Private) 
- (void)createLayout;

@end

@implementation ScreenView

@synthesize screen;

//override the constractor
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {

			
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
	
	if (screen.background && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:screen.background]]) {
		UIImage *background = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:screen.background]];
		UIColor *color = [[UIColor alloc] initWithPatternImage:background];
		[self setBackgroundColor:color];
		[color release];
		[background release];
	}
	
	for (LayoutContainer *layout in screen.layouts) { 
		LayoutContainerView *layoutView = [LayoutContainerView buildWithLayoutContainer:layout];
		[self addSubview:layoutView];
	}
	

}

//override layoutSubviews method of UIView, In order to resize the ControlView when add subview
// Only in this time we can know this view's size
- (void)layoutSubviews {
	
	
	
}


- (void)dealloc {
	[screen release];

	[super dealloc];
}


@end
