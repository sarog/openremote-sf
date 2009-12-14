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

#import "ClippedUIImageView.h"
#import "BackgroundImageRelativePositionConstant.h"

@interface ClippedUIImageView(Private)
- (CGSize) clippedUIImageSize:(CGSize)uiImageSize uiViewSize:(CGSize)uiViewSize;
- (UIImage *) doClipImage:(UIImage *)uiImage clipRect:(CGRect)clipRect;
- (CGPoint) calculateStartAtImagePoint:(UIImage *)uiImage imageAlignToView:(NSString *)align uiView:(UIView *)uiView;
- (int) startXWithRelativeAlignPattern:(NSString *)align uiImageSize:(CGSize)uiImageSize uiViewSize:(CGSize)uiViewSize;
- (int) startYWithRelativeAlignPattern:(NSString *)align uiImageSize:(CGSize)uiImageSize uiViewSize:(CGSize)uiViewSize;
@end


@implementation ClippedUIImageView

NSString *const IMAGE_ABSOLUTE_ALIGN_TO_VIEW = @"ABSOLUTE";

// NOTE: The value of imageAlignToView is BackgroundImageRelativePositionConstant or IMAGE_ABSOLUTE_ALIGN_TO_VIEW
- (id) initWithClipUIImage:(UIImage *)uiImage dependingOnView:(UIView *)uiView imageAlignToView:(NSString *)align imageFillView:(BOOL)imageFillView {
	if (self = [super init]) {
		if (uiImage && uiView) {
			CGPoint startAtImagePoint = [self calculateStartAtImagePoint:uiImage imageAlignToView:align uiView:uiView];			
			CGSize clipImageSize = [self clippedUIImageSize:uiImage.size uiViewSize:uiView.frame.size];
			UIImage *newUIImage = [self doClipImage:uiImage clipRect:CGRectMake(startAtImagePoint.x, startAtImagePoint.y, clipImageSize.width, clipImageSize.height)];
			
			[self setFrame:uiView.frame];
			[self setImage:newUIImage];
			if (!imageFillView) {
				[self sizeToFit];
			}
		} else {
			return nil;
		}
	}
	return self;
}

#pragma mark Private methods' implementations

// Calculate the clipped CGSize
- (CGSize) clippedUIImageSize:(CGSize)uiImageSize uiViewSize:(CGSize)uiViewSize {
	CGSize clipImageSize = uiImageSize;
	if(uiImageSize.width > uiViewSize.width && uiImageSize.height > uiViewSize.height) {
		clipImageSize = uiViewSize;
	} else if (uiImageSize.width > uiViewSize.width && uiImageSize.height <= uiViewSize.height) {
		clipImageSize = CGSizeMake(uiViewSize.width, uiImageSize.height);
	} else if (uiImageSize.width <= uiViewSize.width && uiImageSize.height > uiViewSize.height) {
		clipImageSize = CGSizeMake(uiImageSize.width, uiViewSize.height);
	}
	NSLog(@"clipImageSize width is %f, height is %f", clipImageSize.width, clipImageSize.height);
	return clipImageSize;
}

// Actually clip image with CGRect.
- (UIImage *) doClipImage:(UIImage *)uiImage clipRect:(CGRect)clipRect {
	CGImageRef uiImageRef = CGImageCreateWithImageInRect([uiImage CGImage], clipRect);
	return [UIImage imageWithCGImage:uiImageRef];
}

// Calcute where clip from.
- (CGPoint) calculateStartAtImagePoint:(UIImage *)uiImage imageAlignToView:(NSString *)align uiView:(UIView *)uiView {
	
	if ([IMAGE_ABSOLUTE_ALIGN_TO_VIEW isEqualToString:align] || align == nil || [@"" isEqualToString:align]) {
		return CGPointMake(0, 0);
	}
	
	int startAtImageXCoordinate=0;
	int startAtImageYCoordinate=0;
	CGSize uiImageSize = uiImage.size;
	CGSize uiViewSize = uiView.frame.size;
	
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
		align = BG_IMAGE_RELATIVE_POSITION_TOP;
	}
	// Calculate startAtImageXCoordinate
	startAtImageXCoordinate = [self startXWithRelativeAlignPattern:align uiImageSize:uiImageSize uiViewSize:uiViewSize];
	// Calculate startAtImageYCoordinate
	startAtImageYCoordinate = [self startYWithRelativeAlignPattern:align uiImageSize:uiImageSize uiViewSize:uiViewSize];
	return CGPointMake(startAtImageXCoordinate, startAtImageYCoordinate);
}

// Calculate startAtImageXCoordinate
- (int) startXWithRelativeAlignPattern:(NSString *)align uiImageSize:(CGSize)uiImageSize uiViewSize:(CGSize)uiViewSize {
	int x = 0;
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT]) {
		x = 0;
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		if (uiImageSize.width > uiViewSize.width) {
			x = uiImageSize.width - uiViewSize.width;
		} else {
			x = 0;
		}
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		if (uiImageSize.width > uiViewSize.width) {
			x = (uiImageSize.width - uiViewSize.width)/2;
		} else {
			x = 0;
		}
	}
	return x;
}

// Calculate startAtImageYCoordinate
- (int) startYWithRelativeAlignPattern:(NSString *)align uiImageSize:(CGSize)uiImageSize uiViewSize:(CGSize)uiViewSize {
	int y = 0;
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT]) {
		y = 0;
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		if (uiImageSize.height > uiViewSize.height) {
			y = uiImageSize.height - uiViewSize.height;
		} else {
			y = 0;
		}
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		if (uiImageSize.height > uiViewSize.height) {
			y = (uiImageSize.height - uiViewSize.height)/2;
		} else {
			y = 0;
		}
	}
	return y;
}

@end
