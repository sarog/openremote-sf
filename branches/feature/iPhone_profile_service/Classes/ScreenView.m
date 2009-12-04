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
#import "BackgroundRelativePositionConstant.h"


@interface ScreenView (Private) 
- (void)createLayout;
-(UIImage *)clipUIImage:(UIImage *)uiImage dependingOnUIView:(UIView *)uiView;
-(UIImage *)doClipWithUIImage:(UIImage *)uiImage withCGSize:(CGSize)cgSize;
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
	
	if ([[[screen background] backgroundImage] src] && [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[screen background] backgroundImage] src]]]) {
		UIImage *backgroundImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:[[[screen background] backgroundImage] src]]];
		
		int screenBackgroundImageViewWidth = IPHONE_SCREEN_WIDTH;
		int screenBackgroundImageViewHeight = IPHONE_SCREEN_HEIGHT - IPHONE_SCREEN_STATUS_BAR_HEIGHT - IPHONE_SCREEN_BOTTOM_PAGE_SWITCH_CONTROL_HEIGHT;
		
		// fullscreen is false
		if (![[screen background] fullScreen]) {
			UIImageView *backgroundImageView = [[UIImageView alloc] init];		
			//[backgroundImageView setContentMode:UIViewContentModeScaleAspectFill];
			NSLog(@"BackgroundImage's original width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
			
			// ablosute position of screen background.
			if ([[screen background] isBackgroundImageAbsolutePosition]) {
				int left = [[screen background] backgroundImageAbsolutePositionLeft];
				int top = [[screen background] backgroundImageAbsolutePositionTop];
				[backgroundImageView setFrame:CGRectMake(left, top, screenBackgroundImageViewWidth-left, screenBackgroundImageViewHeight-top)];
				if (backgroundImage && backgroundImageView) {
					CGSize uiImageSize = backgroundImage.size;
					CGSize uiViewSize = backgroundImageView.bounds.size;
					CGSize clipCGSize;
					if(uiImageSize.width > uiViewSize.width && uiImageSize.height > uiViewSize.height) {
						clipCGSize = uiViewSize;
					} else if (uiImageSize.width > uiViewSize.width && uiImageSize.height <= uiViewSize.height) {
						clipCGSize = CGSizeMake(uiViewSize.width, uiImageSize.height);
					} else if (uiImageSize.width <= uiViewSize.width && uiImageSize.height > uiViewSize.height) {
						clipCGSize = CGSizeMake(uiImageSize.width, uiViewSize.height);
					} else {
						clipCGSize = uiImageSize;
					}
					UIGraphicsBeginImageContext(clipCGSize);
					[backgroundImage drawInRect:CGRectMake(0, 0, clipCGSize.width, clipCGSize.height)];
					backgroundImage = UIGraphicsGetImageFromCurrentImageContext();
					[backgroundImage retain];
					UIGraphicsEndImageContext();
				}
				[backgroundImageView setImage:backgroundImage];
				[backgroundImageView sizeToFit];
				//UIColor *color = [[UIColor alloc] initWithPatternImage:backgroundImage];				
				//[backgroundImageView setBackgroundColor:color];
				//[color release];
				NSLog(@"Clipped BackgroundImage's width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
				NSLog(@"BackgroundImageView's width:%f, height:%f", backgroundImageView.bounds.size.width, backgroundImageView.bounds.size.height);
				NSLog(@"BackgroundImageView's left is %d, top is %d", left, top);
			}
			// relative position of screen background.
			else {
				// relative position is LEFT
				NSString *backgroundImageRelativePosition = screen.background.backgroundImageRelativePosition;
				//if ([backgroundImageRelativePosition isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT]) {
//					
//				}
			}
			[self addSubview:backgroundImageView];
		}
		// fullscreen is true
		else {
			UIImageView *uiImageView = [[UIImageView alloc] init];
			[uiImageView setContentMode:UIViewContentModeScaleAspectFill];
			[uiImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
			// Begin clip image
			if (backgroundImage && uiImageView) {
				CGSize uiImageSize = backgroundImage.size;
				CGSize uiImageViewSize = uiImageView.bounds.size;
				CGSize clipCGSize;
				if(uiImageSize.width > uiImageViewSize.width && uiImageSize.height > uiImageViewSize.height) {
					clipCGSize = uiImageViewSize;
				} else if (uiImageSize.width > uiImageViewSize.width && uiImageSize.height <= uiImageViewSize.height) {
					clipCGSize = CGSizeMake(uiImageViewSize.width, uiImageSize.height);
				} else if (uiImageSize.width <= uiImageViewSize.width && uiImageSize.height > uiImageViewSize.height) {
					clipCGSize = CGSizeMake(uiImageSize.width, uiImageViewSize.height);
				} else {
					clipCGSize = uiImageSize;
				}
				UIGraphicsBeginImageContext(clipCGSize);
				[backgroundImage drawInRect:CGRectMake(0, 0, clipCGSize.width, clipCGSize.height)];
				backgroundImage = UIGraphicsGetImageFromCurrentImageContext();
				[backgroundImage retain];
				UIGraphicsEndImageContext();
			}
			// End clip image
			[uiImageView setImage:backgroundImage];
			[uiImageView sizeToFit];
			//[uiImageView setContentMode:UIViewContentModeScaleAspectFill];
			[self addSubview:uiImageView];
		}
		NSLog(@"Added width: %d, height: %d backgroundImageView", screenBackgroundImageViewWidth, screenBackgroundImageViewHeight);
		[backgroundImage release];
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
