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

#import "ClippedUIImageViewTest.h"
#import "ClippedUIImage.h"
#import "UIViewUtil.h"

static NSString *assertImageWidthString = @"expected image width is %f, but actual width is %f";
static NSString *assertImageHeightString = @"expected image height is %f, but actual height is %f";

@implementation ClippedUIImageViewTest

#if USE_APPLICATION_UNIT_TEST     // all code under test is in the iPhone Application

- (void) testAppDelegate {
    
    id yourApplicationDelegate = [[UIApplication sharedApplication] delegate];
    STAssertNotNil(yourApplicationDelegate, @"UIApplication failed to find the AppDelegate");
    
}

#else

// Add UnitTest methods

/* The setUp method is called automatically before each test-case method (methods whose name starts with 'test').
 */
- (void) setUp {
	if (!uiView) {
		uiView = [[UIView alloc] initWithFrame:CGRectMake(0, 0, IPHONE_SCREEN_WIDTH_FOR_TEST, IPHONE_SCREEN_HEIGHT_FOR_TEST)];
	}
}

/* The tearDown method is called automatically after each test-case method (methods whose name starts with 'test').
 */
- (void) tearDown {
	[uiView release];
}

/**
 * The width and height of uiImage are both great than uiView's.
 * uiImageWidth > uiViewWidth, uiImageHeight > uiViewHeight
 */
- (void) testUImageWidthAndHeightBothGreatThanUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"500X500.png"];
	float expectedOriginalImageWidth = 500.0;
	float expectedOriginalImageHeight = 500.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiView.frame.size.width, assertImageWidthString, uiView.frame.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiView.frame.size.height, assertImageHeightString, uiView.frame.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is great than uiView's, and the height of uiImage is equal to uiView's.
 * uiImageWidth > uiViewWidth, uiImageHeight = uiViewHeight
 */
- (void) testUImageWidthGreatThanAndHeightEqualToUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"500X480.png"];
	float expectedOriginalImageWidth = 500.0;
	float expectedOriginalImageHeight = 480.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiView.frame.size.width, assertImageWidthString, uiView.frame.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiView.frame.size.height, assertImageHeightString, uiView.frame.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is great than uiView's, and the height of uiImage is less than uiView's.
 * uiImageWidth > uiViewWidth, uiImageHeight < uiViewHeight
 */
- (void) testUImageWidthGreatThanAndHeightLessThanUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"500X320.png"];
	float expectedOriginalImageWidth = 500.0;
	float expectedOriginalImageHeight = 320.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiView.frame.size.width, assertImageWidthString, uiView.frame.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiImage.size.height, assertImageHeightString, uiImage.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is equal to uiView's, and the height of uiImage is great than uiView's.
 * uiImageWidth = uiViewWidth, uiImageHeight > uiViewHeight
 */
- (void) testUImageWidthEqualToAndHeightGreatThanUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"320X500.png"];
	float expectedOriginalImageWidth = 320.0;
	float expectedOriginalImageHeight = 500.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiView.frame.size.width, assertImageWidthString, uiView.frame.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiView.frame.size.height, assertImageHeightString, uiView.frame.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is equal to uiView's, and the height of uiImage is equal to uiView's.
 * uiImageWidth = uiViewWidth, uiImageHeight = uiViewHeight
 */
- (void) testUImageWidthAndHeightBothEqualToUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"320X480.png"];
	float expectedOriginalImageWidth = 320.0;
	float expectedOriginalImageHeight = 480.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiView.frame.size.width, assertImageWidthString, uiImage.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiView.frame.size.height, assertImageHeightString, uiImage.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is equal to uiView's, and the height of uiImage is less than uiView's.
 * uiImageWidth = uiViewWidth, uiImageHeight < uiViewHeight
 */
- (void) testUImageWidthEqualToAndHeightLessThanUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"320X320.png"];
	float expectedOriginalImageWidth = 320.0;
	float expectedOriginalImageHeight = 320.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiView.frame.size.width, assertImageWidthString, uiView.frame.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiImage.size.height, assertImageHeightString, uiImage.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is less than uiView's, and the height of uiImage is great than uiView's.
 * uiImageWidth < uiViewWidth, uiImageHeight > uiViewHeight
 */
- (void) testUImageWidthLessThanAndHeightGreatThanUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"300X500.png"];
	float expectedOriginalImageWidth = 300.0;
	float expectedOriginalImageHeight = 500.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiImage.size.width, assertImageWidthString, uiImage.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiView.frame.size.height, assertImageHeightString, uiView.frame.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is less than uiView's, and the height of uiImage is equal to uiView's.
 * uiImageWidth < uiViewWidth, uiImageHeight = uiViewHeight
 */
- (void	) testUImageWidthLessThanAndHeightEqualToUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"300X480.png"];
	float expectedOriginalImageWidth = 300.0;
	float expectedOriginalImageHeight = 480.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiImage.size.width, assertImageWidthString, uiImage.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiView.frame.size.height, assertImageHeightString, uiView.frame.size.height, newUIImageView.image.size.height);
}

/**
 * The width of uiImage is less than uiView's, and the height of uiImage is less than uiView's.
 * uiImageWidth < uiViewWidth, uiImageHeight < uiViewHeight
 */
- (void) testUImageWidthLessThanAndHeightLessThanUIView {
	uiImage = [[UIImage alloc] initWithContentsOfFile:@"300X300.png"];
	float expectedOriginalImageWidth = 300.0;
	float expectedOriginalImageHeight = 300.0;
	STAssertTrue(uiImage.size.width == expectedOriginalImageWidth, assertImageWidthString, expectedOriginalImageWidth, uiImage.size.width);
	STAssertTrue(uiImage.size.height == expectedOriginalImageHeight, assertImageHeightString, expectedOriginalImageHeight, uiImage.size.height);
	UIImageView *newUIImageView = [UIViewUtil clippedUIImageViewWith:uiImage dependingOnUIView:uiView uiImageAlignToUIViewPattern:IMAGE_ABSOLUTE_ALIGN_TO_VIEW isUIImageFillUIView:NO];
	STAssertTrue(newUIImageView.image.size.width == uiImage.size.width, assertImageWidthString, uiImage.size.width, newUIImageView.image.size.width);
	STAssertTrue(newUIImageView.image.size.height == uiImage.size.height, assertImageHeightString, uiImage.size.height, newUIImageView.image.size.height);
}

#endif


@end
