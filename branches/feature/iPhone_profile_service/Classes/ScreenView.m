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


@interface ScreenView (Private) 
- (void)createLayout;
- (void)layoutBackground;
- (void)backgroundImageRelativeAlign:(NSString *)align withUIImage:(UIImage *)uiImage withUIView:(UIView *)uiView;
- (UIImage *)clipUIImage:(UIImage *)uiImage dependingOnUIView:(UIView *)uiView;
- (UIImage *)clipUIImage:(UIImage *)uiImage startAtImageXCoordinate:(int)x startAtImageYCoordinate:(int)y dependingOnUIView:(UIView *)uiView;
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
		// fullscreen is false
		if (![[screen background] fullScreen]) {
			NSLog(@"BackgroundImage isn't fullScreen");
			NSLog(@"BackgroundImage's original width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
			
			// ablosute position of screen background.
			if ([[screen background] isBackgroundImageAbsolutePosition]) {
				int left = [[screen background] backgroundImageAbsolutePositionLeft];
				int top = [[screen background] backgroundImageAbsolutePositionTop];
				[backgroundImageView setFrame:CGRectMake(left, top, screenBackgroundImageViewWidth-left, screenBackgroundImageViewHeight-top)];
				backgroundImage = [self clipUIImage:backgroundImage dependingOnUIView:backgroundImageView];
				[backgroundImageView setImage:backgroundImage];
				[backgroundImageView sizeToFit];
				NSLog(@"Clipped BackgroundImage's width:%f, height:%f", backgroundImage.size.width, backgroundImage.size.height);
				NSLog(@"BackgroundImageView's left is %d, top is %d", left, top);
				NSLog(@"BackgroundImageView's width:%f, height:%f", backgroundImageView.bounds.size.width, backgroundImageView.bounds.size.height);
			}
			// relative position of screen background.
			else {
				// relative position is LEFT
				[backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
				NSString *backgroundImageRelativePosition = [[screen background] backgroundImageRelativePosition];
				[self backgroundImageRelativeAlign:backgroundImageRelativePosition withUIImage:backgroundImage withUIView:backgroundImageView];
			}
		}
		// fullscreen is true
		else {
			[backgroundImageView setFrame:CGRectMake(0, 0, screenBackgroundImageViewWidth, screenBackgroundImageViewHeight)];
			backgroundImage = [self clipUIImage:backgroundImage dependingOnUIView:backgroundImageView];
			[backgroundImageView setImage:backgroundImage];
			// Let imageView fit the size of image. This can avoid small/big size image fit imageview.
			[backgroundImageView sizeToFit];
		}
		NSLog(@"Added width: %d, height: %d backgroundImageView", screenBackgroundImageViewWidth, screenBackgroundImageViewHeight);
		[self addSubview:backgroundImageView];
		[backgroundImageView release];
		[backgroundImage release];
	}
}

// Parameter 'backgroundImageRelativeAlign' must be the value in BackgroundImageRelativePositionConstant class declared.
- (void)backgroundImageRelativeAlign:(NSString *)align withUIImage:(UIImage *)uiImage withUIView:(UIView *)uiView {
	
	int startAtImageXCoordinate=0;
	int startAtImageYCoordinate=0;
	CGSize uiImageSize = uiImage.size;
	CGSize uiViewSize = uiView.bounds.size;
	
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP]) {
		[uiView setContentMode:UIViewContentModeTop];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM]) {
		[uiView setContentMode:UIViewContentModeBottom];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT]) {
		[uiView setContentMode:UIViewContentModeLeft];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT]) {
		[uiView setContentMode:UIViewContentModeRight];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT]) {
		[uiView setContentMode:UIViewContentModeTopLeft];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT]) {
		[uiView setContentMode:UIViewContentModeTopRight];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT]) {
		[uiView setContentMode:UIViewContentModeBottomLeft];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		[uiView setContentMode:UIViewContentModeBottomRight];
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		[uiView setContentMode:UIViewContentModeCenter];
	} else {
		[uiView setContentMode:UIViewContentModeTopLeft];
	}
	// Calculate startAtImageXCoordinate
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT]) {
		startAtImageXCoordinate = 0;
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		if (uiImageSize.width > uiViewSize.width) {
			startAtImageXCoordinate = uiImageSize.width - uiViewSize.width;
		} else {
			startAtImageXCoordinate = 0;
		}
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		if (uiImageSize.width > uiViewSize.width) {
			startAtImageXCoordinate = (uiImageSize.width - uiViewSize.width)/2;
		} else {
			startAtImageXCoordinate = 0;
		}
	}
	// Calculate startAtImageYCoordinate
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT]) {
		startAtImageYCoordinate = 0;
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		if (uiImageSize.height > uiViewSize.height) {
			startAtImageYCoordinate = uiImageSize.height - uiViewSize.height;
		} else {
			startAtImageYCoordinate = 0;
		}
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		if (uiImageSize.height > uiViewSize.height) {
			startAtImageYCoordinate = (uiImageSize.height - uiViewSize.height)/2;
		} else {
			startAtImageYCoordinate = 0;
		}
	}
	
	uiImage = [self clipUIImage:uiImage startAtImageXCoordinate:startAtImageXCoordinate startAtImageYCoordinate:startAtImageYCoordinate dependingOnUIView:uiView];
	[(UIImageView *)uiView setImage:uiImage];
}

- (UIImage *)clipUIImage:(UIImage *)uiImage dependingOnUIView:(UIView *)uiView {
	if (uiImage && uiView) {
		CGSize uiImageSize = uiImage.size;
		CGSize uiViewSize = uiView.bounds.size;
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
		CGImageRef uiImageRef = CGImageCreateWithImageInRect([uiImage CGImage], CGRectMake(0, 0, clipCGSize.width, clipCGSize.height));
		uiImage = [UIImage imageWithCGImage:uiImageRef];
		NSLog(@"clipped uiImage size is %f,%f", uiImage.size.width, uiImage.size.height);
		return uiImage;
	} else {
		return nil;
	}
}

- (UIImage *)clipUIImage:(UIImage *)uiImage startAtImageXCoordinate:(int)x startAtImageYCoordinate:(int)y dependingOnUIView:(UIView *)uiView {
	if (uiImage && uiView) {
		CGSize uiImageSize = uiImage.size;
		CGSize uiViewSize = uiView.bounds.size;
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
		CGImageRef uiImageRef = CGImageCreateWithImageInRect([uiImage CGImage], CGRectMake(x, y, clipCGSize.width, clipCGSize.height));
		uiImage = [UIImage imageWithCGImage:uiImageRef];
		NSLog(@"clipped uiImage size is %f,%f", uiImage.size.width, uiImage.size.height);
		return uiImage;
	} else {
		return nil;
	}
}

- (void)dealloc {
	[screen release];
	[super dealloc];
}


@end
