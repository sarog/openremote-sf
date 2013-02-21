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
#import "SliderView.h"
#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"
#import "Slider.h"
#import "DirectoryDefinition.h"
#import	"AbsoluteLayoutContainerView.h"
#import "GridCellView.h"


@interface UIImage (RotateAdditions)
- (UIImage *)imageRotatedByDegrees:(CGFloat)degrees;
@end;

CGFloat DegreesToRadians(CGFloat degrees) {return degrees * M_PI / 180;};

@implementation UIImage (RotateAdditions)

// Rotate image with specified degrees
- (UIImage *)imageRotatedByDegrees:(CGFloat)degrees {   
	// calculate the size of the rotated view's containing box for our drawing space
	UIView *rotatedViewBox = [[UIView alloc] initWithFrame:CGRectMake(0,0,self.size.width, self.size.height)];
	CGAffineTransform t = CGAffineTransformMakeRotation(DegreesToRadians(degrees));
	rotatedViewBox.transform = t;
	CGSize rotatedSize = rotatedViewBox.frame.size;
	[rotatedViewBox release];
	
	// Create the bitmap context
	UIGraphicsBeginImageContext(rotatedSize);
	CGContextRef bitmap = UIGraphicsGetCurrentContext();
	
	// Move the origin to the middle of the image so we will rotate and scale around the center.
	CGContextTranslateCTM(bitmap, rotatedSize.width/2, rotatedSize.height/2);
	
	//   // Rotate the image context
	CGContextRotateCTM(bitmap, DegreesToRadians(degrees));
	
	// Now, draw the rotated/scaled image into the context
	CGContextScaleCTM(bitmap, 1.0, -1.0);
	CGContextDrawImage(bitmap, CGRectMake(-self.size.width / 2, -self.size.height / 2, self.size.width, self.size.height), [self CGImage]);
	
	UIImage *newImage = UIGraphicsGetImageFromCurrentImageContext();
	UIGraphicsEndImageContext();
	return newImage;
	
}

@end

@interface SliderView(Private)
- (void) afterSlide:(UISlider *)sender;
-(void) releaseSlider:(UISlider *)sender;
-(void) touchDownSlider:(UISlider *)sender;
-(void) showTip:(UIImageView *)tip ofSlider:(UISlider *)uiSliderParam withSender:(UISlider *)sender;
-(void) clearSliderTipSubviews:(UIImageView *)sliderTipParam;
- (UIImage *)getImageFromCacheByName:(NSString *)name;
- (UIImage *)transformToHorizontalWhenVertical:(UIImage *)vImg;
@end

@implementation SliderView

@synthesize uiSlider, currentValue;

#pragma mark Override methods of SensoryControlView.

- (void) initView {
	if (uiSlider) {
		[uiSlider removeFromSuperview];
		[uiSlider release];
	}
	Slider *sliderModel = (Slider *)component;
	
	uiSlider = [[UISlider alloc] initWithFrame:[self bounds]];
	vertical = sliderModel.vertical;
	

	uiSlider.minimumValue = sliderModel.minValue;
	NSString *minimumValueImageSrc = sliderModel.minImage.src;
	UIImage *minimumValueImage = [self getImageFromCacheByName:minimumValueImageSrc];
	uiSlider.minimumValueImage = minimumValueImage;
	
	uiSlider.maximumValue = sliderModel.maxValue;
	NSString *maximumValueImageSrc = sliderModel.maxImage.src;
	UIImage *maximumValueImage = [self getImageFromCacheByName:maximumValueImageSrc];
	uiSlider.maximumValueImage = maximumValueImage;
	
	// TrackImages, thumbImage
	uiSlider.backgroundColor = [UIColor clearColor];
	NSString *minTrackImageSrc = sliderModel.minTrackImage.src;
	NSString *maxTrackImageSrc = sliderModel.maxTrackImage.src;
	NSString *thumbImageSrc = sliderModel.thumbImage.src;
	
	UIImage *stretchedLeftTrack = [[self getImageFromCacheByName:minTrackImageSrc] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
	UIImage *stretchedRightTrack = [[self getImageFromCacheByName:maxTrackImageSrc] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
	UIImage *thumbImage = [self getImageFromCacheByName:thumbImageSrc];
	if (stretchedLeftTrack) {
		[uiSlider setMinimumTrackImage:stretchedLeftTrack forState:UIControlStateNormal];
	}
	// Default
	else {
		UIImage *sliderDefaultMinTrackImage = [[UIImage imageNamed:@"slider_default_min_track_image_.png"] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
		[uiSlider setMinimumTrackImage:sliderDefaultMinTrackImage forState:UIControlStateNormal];
	}
	if (stretchedRightTrack) {
		[uiSlider setMaximumTrackImage:stretchedRightTrack forState:UIControlStateNormal];
	} 
	// Default
	else {
		UIImage *sliderDefaultMaxTrackImage = [[UIImage imageNamed:@"slider_default_max_track_image_.png"] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
		[uiSlider setMaximumTrackImage:sliderDefaultMaxTrackImage forState:UIControlStateNormal];		
	}
	
	if (thumbImage) {
		[uiSlider setThumbImage: thumbImage forState:UIControlStateNormal];
		[uiSlider setThumbImage: thumbImage forState:UIControlStateHighlighted];
	} 
	// Default
	else {
		UIImage *sliderDefaultThumbImage = [UIImage imageNamed:@"slider_default_thumb_image_.png"];
		[uiSlider setThumbImage: sliderDefaultThumbImage forState:UIControlStateNormal];
		[uiSlider setThumbImage: sliderDefaultThumbImage forState:UIControlStateHighlighted];
	}
	
	sliderTip = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"slider_tip.png"]];
	[sliderTip setFrame:CGRectMake(uiSlider.frame.origin.x, uiSlider.frame.origin.y - 50, 80, 80)];
	sliderTip.hidden = YES;
	[self.superview addSubview:sliderTip];
	
//	uiSlider.continuous = YES;
	uiSlider.value = 0.0;
	currentValue = 0.0;
	
	if (sliderModel.vertical) {
		uiSlider.transform = CGAffineTransformMakeRotation(270.0/180*M_PI);
		uiSlider.frame = self.frame;
	}
	
	[self addSubview:uiSlider];
	
	if (!sliderModel.passive) {
		[uiSlider addTarget:self action:@selector(afterSlide:) forControlEvents:UIControlEventValueChanged];
		[uiSlider addTarget:self action:@selector(touchDownSlider:) forControlEvents:UIControlEventTouchDown];
		[uiSlider addTarget:self action:@selector(releaseSlider:) forControlEvents:UIControlEventTouchUpInside];
		[uiSlider addTarget:self action:@selector(releaseSlider:) forControlEvents:UIControlEventTouchUpOutside];
	} else {
		UIView *cover = [[UIView alloc] initWithFrame:self.bounds];
		[cover setBackgroundColor:[UIColor clearColor]];
		[self addSubview:cover]; 
	}
}

// Get Image from image cache directory with image name.
- (UIImage *)getImageFromCacheByName:(NSString *)name {
	UIImage *img = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:name]];
	return [self transformToHorizontalWhenVertical:img];
}

// Rotate the specified image from horizontal to vertical.
- (UIImage *)transformToHorizontalWhenVertical:(UIImage *)vImg {
	return vertical ? [vImg imageRotatedByDegrees:90.0] : vImg;
}

// Override method of sensory view.
- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Slider *)component).sensor.sensorId;
	float newStatus = [[pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]] floatValue];
	uiSlider.value = newStatus;
	currentValue = (int)newStatus;
}

#pragma mark Private methods

// This method will be executed after slide action finished.
- (void) afterSlide:(UISlider *)sender {
	int afterSlideValue = (int)[sender value];
	if (currentValue >= 0 && abs(currentValue-afterSlideValue) >= MIN_SLIDE_VARIANT) {
		//NSLog(@"The value sent is : %d", afterSlideValue);
		[self showTip:sliderTip ofSlider:uiSlider withSender:sender];
		//[self sendCommandRequest: [NSString stringWithFormat:@"%d", afterSlideValue]];
	} else {
		NSLog(@"The min slide variant value less than %d", MIN_SLIDE_VARIANT);
	}
}

-(void) releaseSlider:(UISlider *)sender {
	int afterSlideValue = (int)[sender value];
	if (currentValue >= 0 && abs(currentValue-afterSlideValue) >= MIN_SLIDE_VARIANT) {
		[self sendCommandRequest: [NSString stringWithFormat:@"%d", afterSlideValue]];
	}
	sliderTip.hidden = YES;
	[self clearSliderTipSubviews:sliderTip];
}

-(void) touchDownSlider:(UISlider *)sender {
	[self showTip:sliderTip ofSlider:uiSlider withSender:sender];
}

// Render the bubble tip while tapping slider thumb image.
-(void) showTip:(UIImageView *)tip ofSlider:(UISlider *)uiSliderParam withSender:(UISlider *)sender {
	tip.hidden = NO;
	[self clearSliderTipSubviews:tip];
	CGFloat x = 0;
	CGFloat y = 0;
	CGFloat span = uiSliderParam.minimumValue - uiSliderParam.maximumValue;
	
	if (vertical) {
		span = uiSlider.maximumValueImage ? span - uiSlider.maximumValueImage.size.height : span;
		span = uiSlider.minimumValueImage ? span - uiSlider.minimumValueImage.size.height : span;
		x = uiSliderParam.frame.origin.x + uiSliderParam.frame.size.width / 2;
		y = ((sender.value - uiSliderParam.maximumValue)/span) * uiSliderParam.frame.size.height + uiSliderParam.frame.origin.y;		
	} else {
		x = ((uiSliderParam.minimumValue - sender.value)/span) * uiSliderParam.frame.size.width + uiSliderParam.frame.origin.x;
		y = uiSliderParam.frame.origin.y + uiSliderParam.frame.size.height / 2;
	}
	
	tip.frame = CGRectMake(x - 40, y - 100, 80, 80);
	
	// SliderView is in the AbsoluteLayoutContainerView
	if ([self.superview isMemberOfClass:[AbsoluteLayoutContainerView class]]) {
		[self.superview.superview bringSubviewToFront:[self superview]];
	}
	// SliderView is in the GridCellView
	else if ([self.superview isMemberOfClass:[GridCellView class]]) {
		[self.superview.superview.superview bringSubviewToFront:[self superview]];
	}
	UILabel *tipText = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 80)];
	tipText.font = [UIFont systemFontOfSize:40];
	tipText.backgroundColor = [UIColor clearColor];
	tipText.textAlignment = UITextAlignmentCenter;
	tipText.text = [NSString stringWithFormat:@"%d",(int)[sender value]];
	[tip addSubview:tipText];
    [tipText release];
}

-(void) clearSliderTipSubviews:(UIImageView *)sliderTipParam {
	for(UIView *view in sliderTipParam.subviews) {
		[view removeFromSuperview];
	}
}

#pragma mark dealloc

-(void) dealloc{
	[uiSlider release];
	[super dealloc];
}

@end
