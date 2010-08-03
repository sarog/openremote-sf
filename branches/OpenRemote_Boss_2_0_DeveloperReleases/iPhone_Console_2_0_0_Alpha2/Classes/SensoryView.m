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

#import "SensoryView.h"
#import "NotificationConstant.h"
#import "Label.h"
#import "Image.h"

@implementation SensoryView

#pragma mark instance methods.

- (void) addPollingNotificationObserver {
	int sensorId;
	if ([component isKindOfClass:[Label class]]) {
		sensorId = ((Label *)component).sensor.sensorId;
	} else if ([component isKindOfClass:[Image class]]) {
		sensorId = ((Image *)component).sensor.sensorId;
		if (sensorId <= 0) {
			sensorId = ((Image *)component).label.sensor.sensorId;
		}
	}
	if (sensorId > 0 ) {
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,sensorId] object:nil];
	}
}

#pragma mark Delegate method of protocol SensoryDelegate.
/**
 * The implementation of this method is empty.<br /> 
 * That means this method must be override in it's subclasses or runtime error.
 */
- (void)setPollingStatus:(NSNotification *)notification; {
	[self doesNotRecognizeSelector:_cmd];
}

#pragma mark Methods of UIView

// This method is abstract method of indirect superclass UIView's.
- (void)layoutSubviews {
	[self initView];
	[self addPollingNotificationObserver];
}
@end
