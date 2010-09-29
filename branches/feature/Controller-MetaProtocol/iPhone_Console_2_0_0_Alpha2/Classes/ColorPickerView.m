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

#import "ColorPickerView.h"
#import "ColorPicker.h"
#import "DirectoryDefinition.h"
#import "AbsoluteLayoutContainerView.h"
#import "GridCellView.h"

@interface ColorPickerView (Private)
- (void) enableScrollView:(BOOL) booleanValue;
- (CGFloat)distanceBetweenTwoPoints:(CGPoint)fromPoint toPoint:(CGPoint)toPoint;
@end

@implementation ColorPickerView

- (void)initView {	
	ColorPicker *colorPicker = (ColorPicker *)component;
	uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:colorPicker.image.src]];
	imageView = [[ColorPickerImageView alloc] initWithImage:uiImage];
	imageView.pickedColorDelegate = self;
	[imageView setFrame:CGRectMake(self.bounds.origin.x, self.bounds.origin.y, uiImage.size.width, uiImage.size.height)];
	[self addSubview:imageView];
}

// Send picker command with color value to controller server.
- (void) pickedColor:(UIColor*)color {
	const CGFloat *c = CGColorGetComponents(color.CGColor);
	NSLog(@"color=%@",color);
	NSLog(@"color R=%0.0f",c[0]*255);
	NSLog(@"color G=%0.0f",c[1]*255);
	NSLog(@"color B=%0.0f",c[2]*255);
	[self sendCommandRequest:[NSString stringWithFormat:@"%02x%02x%02x", (int)(c[0]*255), (int)(c[1]*255), (int)(c[2]*255)]];
}

- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
	NSLog(@"ColorPikcerView began event......");
	[self enableScrollView:NO];
	
	UITouch* touch = [touches anyObject];
	touchBeginPoint = [touch previousLocationInView:self];
}

- (void) touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event {
	NSLog(@"ColorPikcerView moving event......");
	
	movingTag = YES;
	UITouch* touch = [touches anyObject];
	CGPoint currentPoint = [touch locationInView:self];
	CGFloat distance = [self distanceBetweenTwoPoints:touchBeginPoint toPoint:currentPoint];
	if (distance > MIN_VALID_MOVE_DISTANCE) {
		[imageView touchesMoved:touches withEvent:event];
		touchBeginPoint = currentPoint;
		NSLog(@"The distance %f of moving is greater than %d, so command will be sent.", distance, MIN_VALID_MOVE_DISTANCE);
	} else {
		NSLog(@"The distance %f of moving is less than %d, so command won't be sent.", distance, MIN_VALID_MOVE_DISTANCE);
	}
}

- (void) touchesEnded:(NSSet*)touches withEvent:(UIEvent*)event {
	NSLog(@"ColorPikcerView end event......");
	if (movingTag == NO) {
		[imageView touchesEnded:touches withEvent:event];
	}
	movingTag = NO;
	[self enableScrollView:YES];
}

- (CGFloat)distanceBetweenTwoPoints:(CGPoint)fromPoint toPoint:(CGPoint)toPoint {
	float x = toPoint.x - fromPoint.x;
	float y = toPoint.y - fromPoint.y;
	return sqrt(x * x + y * y);
}

- (void) enableScrollView:(BOOL) booleanValue {
	UIScrollView *scrollViewOfPaginationController = nil;
	if ([self.superview isMemberOfClass:[AbsoluteLayoutContainerView class]]) {
		scrollViewOfPaginationController = (UIScrollView *)self.superview.superview.superview;
	} else if ([self.superview isMemberOfClass:[GridCellView class]]) {
		scrollViewOfPaginationController = (UIScrollView *)self.superview.superview.superview.superview;
	}
	if (scrollViewOfPaginationController != nil) {
		[scrollViewOfPaginationController setScrollEnabled:booleanValue];
	}
}

- (void)dealloc {
	[imageView release];
	[uiImage release];
	
	[super dealloc];
}


@end
