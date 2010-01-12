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
@end

@implementation SliderView

@synthesize slider, currentValue;

#pragma mark Override methods of SensoryControlView.

- (void) initView {
	if (slider) {
		[slider removeFromSuperview];
		[slider release];
	}
	Slider *theSlider = (Slider *)component;
	
	slider = [[UISlider alloc] initWithFrame:[self bounds]];
	if (theSlider.vertical) {
		//slider.frame = CGRectMake(slider.frame.origin.x, slider.frame.origin.y, self.frame.size.height, slider.frame.size.width);
		//[self setContentMode:UIViewContentModeCenter];
		slider.transform = CGAffineTransformMakeRotation(270.0/180*M_PI);
	}

	slider.minimumValue = theSlider.minValue;
	NSString *minimumValueImageSrc = theSlider.minImage.src;
	UIImage *minimumValueImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:minimumValueImageSrc]];
	slider.minimumValueImage = minimumValueImage;
	
	slider.maximumValue = theSlider.maxValue;
	NSString *maximumValueImageSrc = theSlider.maxImage.src;
	UIImage *maximumValueImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:maximumValueImageSrc]];
	slider.maximumValueImage = maximumValueImage;
	
	slider.backgroundColor = [UIColor clearColor];	
	NSString *minTrackImageSrc = theSlider.minTrackImage.src;
	NSString *maxTrackImageSrc = theSlider.maxTrackImage.src;
	NSString *thumbImageSrc = theSlider.thumbImage.src;
	
	UIImage *stetchLeftTrack = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:minTrackImageSrc]] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
	UIImage *stetchRightTrack = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:maxTrackImageSrc]] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
	UIImage *thumbImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:thumbImageSrc]];
	[slider setThumbImage: thumbImage forState:UIControlStateNormal];
	[slider setMinimumTrackImage:stetchLeftTrack forState:UIControlStateNormal];
	[slider setMaximumTrackImage:stetchRightTrack forState:UIControlStateNormal];
	
	//slider.continuous = NO;
	slider.value = 0;
	currentValue = 0;
	
	[self addSubview:slider];
	
	if (!theSlider.passive) {
		[slider addTarget:self action:@selector(afterSlide:) forControlEvents:UIControlEventValueChanged];
	}
	// If passive slider then cover a transparent view over the slider.
	if (theSlider.passive) {
		UIView *cover = [[UIView alloc] initWithFrame:self.bounds];
		[cover setBackgroundColor:[UIColor colorWithRed:255.0 green:255.0 blue:255.0 alpha:0.0]];
		[self addSubview:cover]; 
	}
}

- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Slider *)component).sensor.sensorId;
	float newStatus = [[pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]] floatValue];
	slider.value = newStatus;
	currentValue = (int)newStatus;
}

#pragma mark Private methods

// This method will be executed after slide action finished.
- (void) afterSlide:(UISlider *)sender {
	int afterSlideValue = (int)[sender value];
	if (currentValue >= 0 && abs(currentValue-afterSlideValue) > MIN_SLIDE_VARIANT) {
		NSLog(@"The value sent is : %d", afterSlideValue);
		[self sendCommandRequest: [NSString stringWithFormat:@"%d", afterSlideValue]];
	} else {
		NSLog(@"The min slide variant value less than %d", MIN_SLIDE_VARIANT);
	}
}

#pragma mark dealloc

-(void) dealloc{
	[slider release];
	[super dealloc];
}

@end
