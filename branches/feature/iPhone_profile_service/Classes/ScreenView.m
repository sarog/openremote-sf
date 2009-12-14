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
#import "BackgroundImageRelativePositionConstant.h"
#import "ClippedUIImageView.h"


@interface ScreenView (Private) 
- (void)createLayout;
- (void)layoutBackground;
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
	[self layoutBackground];
	
	for (LayoutContainer *layout in screen.layouts) { 
		LayoutContainerView *layoutView = [LayoutContainerView buildWithLayoutContainer:layout];
		[self addSubview:layoutView];
	}
	

}

//override layoutSubviews method of UIView, In order to resize the ControlView when add subview
// Only in this time we can know this view's size
- (void)layoutSubviews {
}

#pragma mark Private method

- (void)layoutBackground {
	if ([[[screen background] backgroundImage] src] && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[screen background] backgroundImage] src]]]) {
		UIImage *backgroundImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[screen background] backgroundImage] src]]];
		int screenBackgroundImageViewWidth = IPHONE_SCREEN_WIDTH;
		int screenBackgroundImageViewHeight = IPHONE_SCREEN_HEIGHT - IPHONE_SCREEN_STATUS_BAR_HEIGHT - IPHONE_SCREEN_BOTTOM_PAGE_SWITCH_CONTROL_HEIGHT;
		
		UIImageView *backgroundImageView = [[UIImageView alloc] init];
		// fillscreen is false
		if (![[screen background] fillScreen]) {
			NSLog(@"BackgroundImage isn't fillScreen");
			NSLog(@"BackgroundImage's original width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
			
			// ablosute position of screen background.
			if ([[screen background] isBackgroundImageAbsolutePosition]) {
				int left = [[screen background] backgroundImageAbsolutePositionLeft];
				int top = [[screen background] backgroundImageAbsolutePositionTop];
				[backgroundImageView setFrame:CGRectMake(left, top, screenBackgroundImageViewWidth-left, screenBackgroundImageViewHeight-top)];
				backgroundImageView = [[ClippedUIImageView alloc] initWithClipUIImage:backgroundImage dependingOnView:backgroundImageView imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW imageFillView:NO];
				NSLog(@"Clipped BackgroundImage's width:%f, height:%f", backgroundImageView.image.size.width, backgroundImageView.image.size.height);
				NSLog(@"BackgroundImageView's left is %d, top is %d", left, top);
				NSLog(@"BackgroundImageView's width:%f, height:%f", backgroundImageView.frame.size.width, backgroundImageView.frame.size.height);
			}
			// relative position of screen background.
			else {
				// relative position
				[backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
				NSString *backgroundImageRelativePosition = [[screen background] backgroundImageRelativePosition];
				backgroundImageView = [[ClippedUIImageView alloc] initWithClipUIImage:backgroundImage dependingOnView:backgroundImageView imageAlignToView:backgroundImageRelativePosition imageFillView:NO];
			}
		}
		// fillscreen is true
		else {
			[backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
			backgroundImageView = [[ClippedUIImageView alloc] initWithClipUIImage:backgroundImage dependingOnView:backgroundImageView imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW imageFillView:NO];
		}
		NSLog(@"Added width: %d, height: %d backgroundImageView", screenBackgroundImageViewWidth, screenBackgroundImageViewHeight);
		[self addSubview:backgroundImageView];
		[backgroundImageView release];
		[backgroundImage release];
	}
}

- (void)dealloc {
	[screen release];
	[super dealloc];
}


@end
