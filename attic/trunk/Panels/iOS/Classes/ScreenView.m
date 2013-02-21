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
#import "ScreenView.h"
#import "Control.h"
#import "ControlView.h"
#import "LayoutContainer.h"
#import "LayoutContainerView.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "DirectoryDefinition.h"
#import "XMLEntity.h"
#import "ClippedUIImage.h"
#import "UIViewUtil.h"

@interface ScreenView (Private) 
- (void)createLayout;
- (void)layoutBackground;
@end

@implementation ScreenView

@synthesize screen;

//override the constractor
- (id)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
			[self setBackgroundColor:[UIColor blackColor]];
    }
    return self;
}

//Set screen *and* render its layout 
- (void)setScreen:(Screen *)s{
	[s retain];
	[screen release];
	screen = s;
	[self createLayout];
}
	
//create each layout container in screen
- (void)createLayout {
	[self layoutBackground];
	
	for (LayoutContainer *layout in screen.layouts) {
		[self addSubview:[LayoutContainerView layoutContainerViewWithLayoutContainer:layout]];
	}

}

//override layoutSubviews method of UIView, In order to resize the ControlView when add subview
// Only in this time we can know this view's size
- (void)layoutSubviews {
}

#pragma mark Private method

// Render backgound of ScreenView.
- (void)layoutBackground {
	if ([[[screen background] backgroundImage] src] && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[screen background] backgroundImage] src]]]) {
		UIImage *backgroundImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[screen background] backgroundImage] src]]];
		int screenBackgroundImageViewWidth = 0;
		int screenBackgroundImageViewHeight = 0;
		
		if (screen.landscape) {
			screenBackgroundImageViewWidth = [UIScreen mainScreen].bounds.size.height;
			screenBackgroundImageViewHeight = [UIScreen mainScreen].bounds.size.width;
		} else {
			screenBackgroundImageViewWidth = [UIScreen mainScreen].bounds.size.width;
			screenBackgroundImageViewHeight = [UIScreen mainScreen].bounds.size.height;
		}
		//screenBackgroundImageViewHeight -= IPHONE_SCREEN_BOTTOM_PAGE_CONTROL_HEIGHT;

		UIImageView *backgroundImageView = [[[UIImageView alloc] init] autorelease];
		// fillscreen is false
		if (![[screen background] fillScreen]) {
			NSLog(@"BackgroundImage isn't fillScreen");
			NSLog(@"BackgroundImage's original width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
			
			// ablosute position of screen background.
			if ([[screen background] isBackgroundImageAbsolutePosition]) {
				int left = [[screen background] backgroundImageAbsolutePositionLeft];
				int top = [[screen background] backgroundImageAbsolutePositionTop];
				if (left > 0) {
					screenBackgroundImageViewWidth = screenBackgroundImageViewWidth-left;
				}
				if (top > 0) {
					screenBackgroundImageViewHeight = screenBackgroundImageViewHeight-top;
				}
				[backgroundImageView setFrame:CGRectMake(left, top, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
				backgroundImageView = [UIViewUtil clippedUIImageViewWith:backgroundImage dependingOnUIView:backgroundImageView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:YES];
				if (left < 0) {
					left = 0;
				}
				if (top < 0) {
					top = 0;
				}
				[backgroundImageView setFrame:CGRectMake(left, top, backgroundImageView.frame.size.width, backgroundImageView.frame.size.height)];
				NSLog(@"Clipped BackgroundImage's width:%f, height:%f", backgroundImageView.image.size.width, backgroundImageView.image.size.height);
				NSLog(@"BackgroundImageView's left is %d, top is %d", left, top);
				NSLog(@"BackgroundImageView's width:%f, height:%f", backgroundImageView.frame.size.width, backgroundImageView.frame.size.height);
			}
			// relative position of screen background.
			else {
				// relative position
				[backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
				NSString *backgroundImageRelativePosition = [[screen background] backgroundImageRelativePosition];
				backgroundImageView = [UIViewUtil clippedUIImageViewWith:backgroundImage dependingOnUIView:backgroundImageView uiImageAlignToUIViewPattern:backgroundImageRelativePosition isUIImageFillUIView:NO];
			}
		}
		// fillscreen is true
		else {
			[backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
			backgroundImageView = [UIViewUtil clippedUIImageViewWith:backgroundImage dependingOnUIView:backgroundImageView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:YES];
		}
		NSLog(@"Added width: %d, height: %d backgroundImageView", screenBackgroundImageViewWidth, screenBackgroundImageViewHeight);
		[self addSubview:backgroundImageView];
		[backgroundImage release];
	}
}

- (void)dealloc {
	[screen release];
	[super dealloc];
}


@end
