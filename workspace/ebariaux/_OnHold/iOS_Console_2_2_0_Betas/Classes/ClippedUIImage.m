/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "ClippedUIImage.h"
#import	"XMLEntity.h"

// MyCreateBitmapContext: Source based on Apple Sample Code
CGContextRef MyCreateBitmapContext (int pixelsWide,	int pixelsHigh) {
    CGContextRef    context = NULL;
    CGColorSpaceRef colorSpace;
    void *          bitmapData;
    int             bitmapByteCount;
    int             bitmapBytesPerRow;
	
    bitmapBytesPerRow   = (pixelsWide * 4);
    bitmapByteCount     = (bitmapBytesPerRow * pixelsHigh);
	
    colorSpace = CGColorSpaceCreateDeviceRGB();
    bitmapData = malloc( bitmapByteCount );
    if (bitmapData == NULL) {
        CGColorSpaceRelease( colorSpace );
        fprintf (stderr, "Memory not allocated!");
        return NULL;
    }
    context = CGBitmapContextCreate (bitmapData,
									 pixelsWide,
									 pixelsHigh,
									 8,
									 bitmapBytesPerRow,
									 colorSpace,
									 kCGImageAlphaPremultipliedLast);
    if (context== NULL) {
        free (bitmapData);
        CGColorSpaceRelease( colorSpace );
        fprintf (stderr, "Context not created!");
        return NULL;
    }
    CGColorSpaceRelease( colorSpace );
	
    return context;
}

@interface ClippedUIImage(Private)
- (CGPoint) clippedPointDependingOnUIView:(UIView *)uiView alignToViewPattern:(NSString *)align;
- (CGSize) clippedSizeDependingOnUIView:(UIView *)uiView;
- (void) clipWithRect:(CGRect)clipRect;
- (int) startXWithRelativeAlignPattern:(NSString *)align uiViewSize:(CGSize)uiViewSize;
- (int) startYWithRelativeAlignPattern:(NSString *)align uiViewSize:(CGSize)uiViewSize;
@end

@implementation ClippedUIImage

// Constant value : Absolute way of image aligning to parent view.
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

/**
 * In addition to clipping the image within the view, also ensures that its "drawing area" has the same size as the view.
 * Ensures that the image is not streched on display.
 */
- (id) initWithUIImage:(UIImage *)uiImage withinUIView:(UIView *)uiView imageAlignToView:(NSString *)align {
	if (self = [super initWithCGImage:[uiImage CGImage]]) {
		if (self && uiView) {
            // Clip image first
			CGPoint startAtImagePoint = [self clippedPointDependingOnUIView:uiView alignToViewPattern:align];
			CGSize clipImageSize = [self clippedSizeDependingOnUIView:uiView];
            CGRect aRect = CGRectMake(startAtImagePoint.x, startAtImagePoint.y, clipImageSize.width, clipImageSize.height);
            CGImageRef image = CGImageCreateWithImageInRect([self CGImage], aRect);            

            // Then draw within same region as view
            CGContextRef context = MyCreateBitmapContext(uiView.bounds.size.width, uiView.bounds.size.height);
            CGContextClearRect(context, uiView.bounds);
            CGContextDrawImage(context, aRect, image);
            CGImageRelease(image);
            
            CGImageRef myRef = CGBitmapContextCreateImage (context);
            free(CGBitmapContextGetData(context));
            CGContextRelease(context);
            
            [self initWithCGImage:myRef];
            CGImageRelease(myRef);
		} else {
			return nil;
		}
	}
	return self;
}


// Get the point position where to start clipping.
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

// Get size how much image will be clipped depending on its parent view.
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

// Clip UIImage with rect value.
- (void) clipWithRect:(CGRect)clipRect {
	CGImageRef uiImageRef = CGImageCreateWithImageInRect([self CGImage], clipRect);
	[self initWithCGImage:uiImageRef];
    CGImageRelease(uiImageRef);
}

@end