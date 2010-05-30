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

#import "ClippedUIImage.h"
#import	"XMLEntity.h"

@interface ClippedUIImage(Private)
- (CGPoint) clippedPointDependingOnUIView:(UIView *)uiView alignToViewPattern:(NSString *)align;
- (CGSize) clippedSizeDependingOnUIView:(UIView *)uiView;
- (void) clipWithRect:(CGRect)clipRect;
- (int) startXWithRelativeAlignPattern:(NSString *)align uiViewSize:(CGSize)uiViewSize;
- (int) startYWithRelativeAlignPattern:(NSString *)align uiViewSize:(CGSize)uiViewSize;
@end

@implementation ClippedUIImage

NSString *const IMAGE_ABSOLUTE_ALIGN_TO_VIEW = @"ABSOLUTE";

- (id) initWithUIImage:(UIImage *)uiImage dependingOnUIView:(UIView *)uiView imageAlignToView:(NSString *)align {
	if (self = [super initWithCGImage:[uiImage CGImage]]) {
		if (self && uiView) {
			CGPoint startAtImagePoint = [self clippedPointDependingOnUIView:uiView alignToViewPattern:align];
			CGSize clipImageSize = [self clippedSizeDependingOnUIView:uiView];
			[self clipWithRect:CGRectMake(startAtImagePoint.x, startAtImagePoint.y, clipImageSize.width, clipImageSize.height)];
		} else {
			return nil;
		}
	}
	return self;
}

- (CGPoint) clippedPointDependingOnUIView:(UIView *)uiView alignToViewPattern:(NSString *)align {

	align = [align uppercaseString];
	
	if ([@"ABSOLUTE" isEqualToString:align] || align == nil || [@"" isEqualToString:align]) {
		CGFloat left = uiView.frame.origin.x;
		CGFloat top = uiView.frame.origin.y;
		if (uiView.frame.origin.x > 0) {
			left = 0.0;
		}
		if (uiView.frame.origin.y > 0) {
			top = 0.0;
		}
		return CGPointMake(fabs(left), fabs(top));
	}	
	int startAtImageXCoordinate=0;
	int startAtImageYCoordinate=0;
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
	startAtImageXCoordinate = [self startXWithRelativeAlignPattern:align uiViewSize:uiViewSize];
	// Calculate startAtImageYCoordinate
	startAtImageYCoordinate = [self startYWithRelativeAlignPattern:align uiViewSize:uiViewSize];
	return CGPointMake(startAtImageXCoordinate, startAtImageYCoordinate);
}

// Calculate startAtImageXCoordinate
- (int) startXWithRelativeAlignPattern:(NSString *)align uiViewSize:(CGSize)uiViewSize {
	int x = 0;
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT]) {
		x = 0;
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		if (self.size.width > uiViewSize.width) {
			x = self.size.width - uiViewSize.width;
		} else {
			x = 0;
		}
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		if (self.size.width > uiViewSize.width) {
			x = (self.size.width - uiViewSize.width)/2;
		} else {
			x = 0;
		}
	}
	return x;
}

// Calculate startAtImageYCoordinate
- (int) startYWithRelativeAlignPattern:(NSString *)align uiViewSize:(CGSize)uiViewSize {
	int y = 0;
	if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_TOP_RIGHT]) {
		y = 0;
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_BOTTOM_RIGHT]) {
		if (self.size.height > uiViewSize.height) {
			y = self.size.height - uiViewSize.height;
		} else {
			y = 0;
		}
	} else if ([align isEqualToString:BG_IMAGE_RELATIVE_POSITION_LEFT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_RIGHT] || [align isEqualToString:BG_IMAGE_RELATIVE_POSITION_CENTER]) {
		if (self.size.height > uiViewSize.height) {
			y = (self.size.height - uiViewSize.height)/2;
		} else {
			y = 0;
		}
	}
	return y;
}

- (CGSize) clippedSizeDependingOnUIView:(UIView *)uiView {
	CGSize clipImageSize = self.size;
	CGSize uiViewSize = uiView.frame.size;
	if(self.size.width > uiViewSize.width && self.size.height > uiViewSize.height) {
		clipImageSize = uiViewSize;
	} else if (self.size.width > uiViewSize.width && self.size.height <= uiViewSize.height) {
		clipImageSize = CGSizeMake(uiViewSize.width, self.size.height);
	} else if (self.size.width <= uiViewSize.width && self.size.height > uiViewSize.height) {
		clipImageSize = CGSizeMake(self.size.width, uiViewSize.height);
	}
	return clipImageSize;
}

- (void) clipWithRect:(CGRect)clipRect {
	CGImageRef uiImageRef = CGImageCreateWithImageInRect([self CGImage], clipRect);
	[self initWithCGImage:uiImageRef];
}

@end
