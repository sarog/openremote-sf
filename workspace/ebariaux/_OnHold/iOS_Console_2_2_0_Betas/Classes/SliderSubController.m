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
#import "SliderSubController.h"
#import "Slider.h"
#import "Image.h"
#import "DirectoryDefinition.h"
#import "SensorStatusCache.h"
#import "Sensor.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"
#import "LocalController.h"
#import "NotificationConstant.h"
#import "ORSlider.h"

#define MIN_SLIDE_VARIANT 1

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

@interface SliderSubController()

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, readonly) Slider *slider;
@property (nonatomic, assign) int currentValue;
@property (nonatomic, retain) UIImageView *sliderTip;

- (int)sliderValue:(ORSlider *)sender;
- (void)sliderValueChanged:(UISlider *)sender;
- (void)releaseSlider:(UISlider *)sender;
- (void)touchDownSlider:(UISlider *)sender;
- (void)clearSliderTipSubviews:(UIImageView *)sliderTipParam;
- (UIImage *)getImageFromCacheByName:(NSString *)name;
- (UIImage *)transformToHorizontalWhenVertical:(UIImage *)vImg;

- (void)refreshTip;

@end

@implementation SliderSubController

- (id)initWithComponent:(Component *)aComponent
{
    self = [super initWithComponent:aComponent];
    if (self) {
        ORSlider *uiSlider = [[ORSlider alloc] initWithFrame:CGRectZero];
        
        uiSlider.minimumValue = self.slider.minValue;
        NSString *minimumValueImageSrc = self.slider.minImage.src;
        UIImage *minimumValueImage = [self getImageFromCacheByName:minimumValueImageSrc];
        uiSlider.minimumValueImage = minimumValueImage;
        
        uiSlider.maximumValue = self.slider.maxValue;
        NSString *maximumValueImageSrc = self.slider.maxImage.src;
        UIImage *maximumValueImage = [self getImageFromCacheByName:maximumValueImageSrc];
        uiSlider.maximumValueImage = maximumValueImage;
        
        // TrackImages, thumbImage
        uiSlider.backgroundColor = [UIColor clearColor];
        NSString *minTrackImageSrc = self.slider.minTrackImage.src;
        NSString *maxTrackImageSrc = self.slider.maxTrackImage.src;
        NSString *thumbImageSrc = self.slider.thumbImage.src;
        
//        UIImage *stretchedLeftTrack = [[self getImageFromCacheByName:minTrackImageSrc] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
//        UIImage *stretchedRightTrack = [[self getImageFromCacheByName:maxTrackImageSrc] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
        UIImage *stretchedLeftTrack = [self getImageFromCacheByName:minTrackImageSrc];
        UIImage *stretchedRightTrack = [self getImageFromCacheByName:maxTrackImageSrc];
        UIImage *thumbImage = [self getImageFromCacheByName:thumbImageSrc];
        if (stretchedLeftTrack) {
            [uiSlider setMinimumTrackImage:stretchedLeftTrack]; //  forState:UIControlStateNormal];
        }/*
        // Default
        else {
            UIImage *sliderDefaultMinTrackImage = [[UIImage imageNamed:@"slider_default_min_track_image_.png"] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
            [uiSlider setMinimumTrackImage:sliderDefaultMinTrackImage]; // forState:UIControlStateNormal];
        }*/
        if (stretchedRightTrack) {
            [uiSlider setMaximumTrackImage:stretchedRightTrack]; //  forState:UIControlStateNormal];
        } /*
        // Default
        else {
            UIImage *sliderDefaultMaxTrackImage = [[UIImage imageNamed:@"slider_default_max_track_image_.png"] stretchableImageWithLeftCapWidth:10.0 topCapHeight:0.0];
            [uiSlider setMaximumTrackImage:sliderDefaultMaxTrackImage]; //  forState:UIControlStateNormal];		
        }*/
        
        if (thumbImage) {
            [uiSlider setThumbImage: thumbImage]; //  forState:UIControlStateNormal];
            [uiSlider setThumbImage: thumbImage]; //  forState:UIControlStateHighlighted];
        } /*
        // Default
        else {
            UIImage *sliderDefaultThumbImage = [UIImage imageNamed:@"slider_default_thumb_image_.png"];
            [uiSlider setThumbImage: sliderDefaultThumbImage]; //  forState:UIControlStateNormal];
            [uiSlider setThumbImage: sliderDefaultThumbImage]; //  forState:UIControlStateHighlighted];
        }*/
        
        //	uiSlider.continuous = YES;
        uiSlider.value = 0.0;
        self.currentValue = 0.0;
        
        if (self.slider.vertical) {
            uiSlider.transform = CGAffineTransformMakeRotation(270.0/180*M_PI);
//            uiSlider.frame = self.frame;
        }

        if (!self.slider.passive) {
            [uiSlider addTarget:self action:@selector(sliderValueChanged:) forControlEvents:UIControlEventValueChanged];
            [uiSlider addTarget:self action:@selector(touchDownSlider:) forControlEvents:UIControlEventTouchDown];
            [uiSlider addTarget:self action:@selector(releaseSlider:) forControlEvents:UIControlEventTouchUpInside];
            [uiSlider addTarget:self action:@selector(releaseSlider:) forControlEvents:UIControlEventTouchUpOutside];
        } else {
            uiSlider.userInteractionEnabled = NO;
        }
        
        self.view = uiSlider;
        [uiSlider release];
        
        int sensorId = ((SensorComponent *)self.component).sensorId;
        if (sensorId > 0 ) {
            [[NSNotificationCenter defaultCenter] removeObserver:self name:[NSString stringWithFormat:NotificationPollingStatusIdFormat, sensorId] object:nil];
            [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat, sensorId] object:nil];
        }
    }
    
    return self;
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    self.sliderTip = nil;
    [super dealloc];
}

- (Slider *)slider
{
    return (Slider *)self.component;
}

// Get Image from image cache directory with image name.
- (UIImage *)getImageFromCacheByName:(NSString *)name {
	UIImage *img = [[[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:name]] autorelease];
	return [self transformToHorizontalWhenVertical:img];
}

// Rotate the specified image from horizontal to vertical.
- (UIImage *)transformToHorizontalWhenVertical:(UIImage *)vImg {
	return self.slider.vertical ? [vImg imageRotatedByDegrees:90.0] : vImg;
}

// Override method of sensory view.
- (void)setPollingStatus:(NSNotification *)notification {
	SensorStatusCache *statusCache = (SensorStatusCache *)[notification object];
	int sensorId = self.slider.sensor.sensorId;
	float newStatus = [[statusCache valueForSensorId:sensorId] floatValue];
    
    NSLog(@"Slider - setPollingStatus %d to %f", sensorId, newStatus);
    
    ORSlider *uiSlider = ((ORSlider *)self.view);
	uiSlider.value = newStatus;
	self.currentValue = [self sliderValue:uiSlider];
}

- (NSArray *)localCommandsForCommandType:(NSString *)commandType
{
	return [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition.localController commandsForComponentId:self.component.componentId action:@"setValue"];
}

#pragma mark Private methods

- (int)sliderValue:(ORSlider *)sender
{
    return (int)roundf([sender value]);
}

- (void)sliderValueChanged:(ORSlider *)sender
{
    // During the user action, always refresh the tip display
	[self refreshTip];
}

-(void) releaseSlider:(ORSlider *)sender {
	int afterSlideValue = [self sliderValue:sender];
	if (self.currentValue >= 0 && abs(self.currentValue-afterSlideValue) >= MIN_SLIDE_VARIANT) {
		[self sendCommandRequest: [NSString stringWithFormat:@"%d", afterSlideValue]];
	} else {
        sender.value = self.currentValue;
    }
	self.sliderTip.hidden = YES;
	[self clearSliderTipSubviews:self.sliderTip];
}

-(void) touchDownSlider:(ORSlider *)sender {
    [self refreshTip]; // showTip:sliderTip ofSlider:uiSlider withSender:sender];
}

// Render the bubble tip while tapping slider thumb image.
-(void) refreshTip {
    if (!self.sliderTip) {
        self.sliderTip = [[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"slider_tip.png"]] autorelease];        
        [self.view.superview addSubview:self.sliderTip];
        // TODO: superview might clip us anyway, better to add to window or to top view in window
        //        self.view.window.
    }
    self.sliderTip.hidden = NO;
    
    // TODO: get rid of that and make sure label is only added and configured once
	[self clearSliderTipSubviews:self.sliderTip];
    
    // TODO: pre-calculate values that can be and cache them
    ORSlider *uiSlider = (ORSlider *)self.view;
	CGFloat x = 0;
	CGFloat y = 0;
	CGFloat span = uiSlider.minimumValue - uiSlider.maximumValue;
    CGFloat thumbHeight = [uiSlider thumbImage].size.height; //    ForState:UIControlStateNormal].size.height;
    CGFloat thumbWidth = [uiSlider thumbImage].size.width; // ForState:UIControlStateNormal].size.width;
    CGFloat trackWidth = uiSlider.frame.size.width;
    CGFloat trackHeight = uiSlider.frame.size.height;
    if (uiSlider.minimumValueImage) {
        trackWidth = trackWidth - uiSlider.minimumValueImage.size.width - 14;
        trackHeight = trackHeight - uiSlider.minimumValueImage.size.height - 14;
    }
    if (uiSlider.maximumValueImage) {
        trackWidth = trackWidth - uiSlider.maximumValueImage.size.width - 14;
        trackHeight = trackHeight - uiSlider.maximumValueImage.size.height - 14;
    }
    
	if (self.slider.vertical) {
		x = uiSlider.frame.origin.x + uiSlider.frame.size.width / 2;
		y = ((uiSlider.value - uiSlider.maximumValue)/span) * (trackHeight - thumbHeight) + uiSlider.frame.origin.y;
        if (uiSlider.maximumValueImage) {
            y = y + uiSlider.maximumValueImage.size.height + 14;
        }
	} else {
		x = ((uiSlider.minimumValue - uiSlider.value)/span) * (trackWidth - thumbWidth) + uiSlider.frame.origin.x + thumbWidth / 2;
		y = uiSlider.frame.origin.y + uiSlider.frame.size.height / 2;
        if (uiSlider.minimumValueImage) {
            x = x + uiSlider.minimumValueImage.size.width + 14;
        }
	}
	
	self.sliderTip.frame = CGRectMake(x - 40, y - 100, 80, 80);
	
	// SliderView is in the AbsoluteLayoutContainerView
    /*	if ([self.superview isMemberOfClass:[AbsoluteLayoutContainerView class]]) {
     [self.superview.superview bringSubviewToFront:[self superview]];
     }
     // SliderView is in the GridCellView
     else if ([self.superview isMemberOfClass:[GridCellView class]]) {
     [self.superview.superview.superview bringSubviewToFront:[self superview]];
     }
     */
    UILabel *tipText = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 80)];
	tipText.font = [UIFont systemFontOfSize:40];
	tipText.backgroundColor = [UIColor clearColor];
	tipText.textAlignment = UITextAlignmentCenter;
	tipText.text = [NSString stringWithFormat:@"%d", [self sliderValue:uiSlider]];
	[self.sliderTip addSubview:tipText];
    [tipText release];
}

- (void)clearSliderTipSubviews:(UIImageView *)sliderTipParam {
	for(UIView *aView in sliderTipParam.subviews) {
		[aView removeFromSuperview];
	}
}

@synthesize view;
@synthesize currentValue;
@synthesize sliderTip;

@end