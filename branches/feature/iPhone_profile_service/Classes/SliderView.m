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

#import "SliderView.h"
#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"
#import "Slider.h"
#import "DirectoryDefinition.h"

@interface SliderView(Private)
- (void) afterSlide:(UISlider *)sender;
-(void) releaseSlider;
-(void) touchDownSlider:(UISlider *)sender;
-(void) showTip:(UIImageView *)tip ofSlider:(UISlider *)uiSliderParam withSender:(UISlider *)sender;
-(void) clearSliderTipSubviews:(UIImageView *)sliderTipParam;
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
	if (sliderModel.vertical) {
		uiSlider.transform = CGAffineTransformMakeRotation(270.0/180*M_PI);
		uiSlider.frame = CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width, self.frame.size.height);
	}

	uiSlider.minimumValue = sliderModel.minValue;
	NSString *minimumValueImageSrc = sliderModel.minImage.src;
	UIImage *minimumValueImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:minimumValueImageSrc]];
	uiSlider.minimumValueImage = minimumValueImage;
	
	uiSlider.maximumValue = sliderModel.maxValue;
	NSString *maximumValueImageSrc = sliderModel.maxImage.src;
	UIImage *maximumValueImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:maximumValueImageSrc]];
	uiSlider.maximumValueImage = maximumValueImage;
	
	// TrackImages, thumbImage
	uiSlider.backgroundColor = [UIColor clearColor];	
	NSString *minTrackImageSrc = sliderModel.minTrackImage.src;
	NSString *maxTrackImageSrc = sliderModel.maxTrackImage.src;
	NSString *thumbImageSrc = sliderModel.thumbImage.src;
	
	UIImage *stetchLeftTrack = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:minTrackImageSrc]] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
	UIImage *stetchRightTrack = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:maxTrackImageSrc]] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
	UIImage *thumbImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:thumbImageSrc]];
	if (stetchRightTrack) {
		[uiSlider setMaximumTrackImage:stetchRightTrack forState:UIControlStateNormal];
	}
	if (stetchLeftTrack) {
		[uiSlider setMinimumTrackImage:stetchLeftTrack forState:UIControlStateNormal];
	}
	if (thumbImage) {
		[uiSlider setThumbImage: thumbImage forState:UIControlStateNormal];
	}
	
	sliderTip = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"slider_tip.png"]];
	[sliderTip setFrame:CGRectMake(uiSlider.frame.origin.x, uiSlider.frame.origin.y - 50, 80, 80)];
	sliderTip.hidden = YES;
	[self addSubview:sliderTip];
	
//	uiSlider.continuous = YES;
	uiSlider.value = 0.0;
	currentValue = 0.0;
	
	[self addSubview:uiSlider];
	
	if (!sliderModel.passive) {
		[uiSlider addTarget:self action:@selector(afterSlide:) forControlEvents:UIControlEventValueChanged];
		[uiSlider addTarget:self action:@selector(touchDownSlider:) forControlEvents:UIControlEventTouchDown];
		[uiSlider addTarget:self action:@selector(releaseSlider) forControlEvents:UIControlEventTouchUpInside];
	} else {
		UIView *cover = [[UIView alloc] initWithFrame:self.bounds];
		[cover setBackgroundColor:[UIColor colorWithRed:255.0 green:255.0 blue:255.0 alpha:0.0]];
		[self addSubview:cover]; 
	}
}

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
		NSLog(@"The value sent is : %d", afterSlideValue);
		[self showTip:sliderTip ofSlider:uiSlider withSender:sender];
		[self sendCommandRequest: [NSString stringWithFormat:@"%d", afterSlideValue]];
	} else {
		NSLog(@"The min slide variant value less than %d", MIN_SLIDE_VARIANT);
	}
}

-(void) releaseSlider {
	sliderTip.hidden = YES;
	[self clearSliderTipSubviews:sliderTip];
	
}

-(void) touchDownSlider:(UISlider *)sender {
	[self showTip:sliderTip ofSlider:uiSlider withSender:sender];
}

-(void) showTip:(UIImageView *)tip ofSlider:(UISlider *)uiSliderParam withSender:(UISlider *)sender {
	tip.hidden = NO;
	[self clearSliderTipSubviews:tip];
	CGFloat x = ((sender.value - uiSliderParam.minimumValue)/(uiSliderParam.maximumValue - uiSliderParam.minimumValue)) * (uiSliderParam.frame.size.width) + uiSliderParam.frame.origin.x;
	CGFloat y = uiSliderParam.frame.origin.y + uiSliderParam.frame.size.height / 2;	
	tip.frame = CGRectMake(x - 40, y - 100, 80, 80);
	UILabel *tipText = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 80)];
	tipText.font = [UIFont systemFontOfSize:40];
	tipText.backgroundColor = [UIColor clearColor];
	tipText.textAlignment = UITextAlignmentCenter;
	tipText.text = [NSString stringWithFormat:@"%d",(int)[sender value]];
	[tip addSubview:tipText];
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
