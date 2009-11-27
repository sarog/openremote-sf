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

@interface SliderView(Private)
- (void) initSlider;
- (void) afterDrag:(UISlider *)sender;
@end

@implementation SliderView

@synthesize slider;

#pragma mark Overridden methods

// This method is abstract method of direct superclass ControlView's.
// So, this method must be overridden in subclass.
- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *delegate = (PollingStatusParserDelegate *)[notification object];
	float newStatus = [[delegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",control.controlId]] floatValue];
	slider.value = newStatus;
}

// This method is abstract method of indirect superclass UIView's.
- (void)layoutSubviews {
	NSLog(@"layoutSubviews of SliderView.");
	[self initSlider];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,control.controlId] object:nil];
	
}

#pragma mark Private methods implementation

- (void) initSlider {
	if (slider) {
		[slider removeFromSuperview];
		[slider release];
	}
	slider = [[UISlider alloc] initWithFrame:CGRectMake(0, 0, 100, 100)];
	Slider *theSlider = (Slider *)control;
	slider.minimumValue = theSlider.minValue;
	slider.maximumValue = theSlider.maxValue;
	slider.continuous = NO;
	slider.value = 0;
	[slider addTarget:self action:@selector(afterDrag:) forControlEvents:UIControlEventValueChanged];
	[self addSubview:slider];
}

// This method will be executed after slide action finished.
- (void) afterDrag:(UISlider *)sender {
	NSLog(@"The current value is : %f", [sender value]);
	[self sendCommandRequest: [NSString stringWithFormat:@"%f", [sender value]]];
}

-(void) dealloc{
	[slider release];
	[super dealloc];
}

@end
