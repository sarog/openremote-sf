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

#import "SensoryControlView.h"
#import "Slider.h"
#import "Switch.h"
#import "NotificationConstant.h"

@implementation SensoryControlView

#pragma mark instance methods
// Implement the delegate method for adding notification observer of polling.
- (void) addPollingNotificationObserver {
	int sensorId;
	if ([component isKindOfClass:[Slider class]]) {
		sensorId = ((Slider *)component).sensor.sensorId;
	} else if ([component isKindOfClass:[Switch class]]) {
		sensorId = ((Switch *)component).sensor.sensorId;
	}
	if (sensorId > 0 ) {
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,sensorId] object:nil];
	}
}

#pragma mark delegate methods of SensoryDelegate.
// Implement the delegate method of dealing polling notification.
- (void)setPollingStatus:(NSNotification *)notification {
	[self doesNotRecognizeSelector:_cmd];
}

#pragma mark Override mehtods of UIView
- (void)layoutSubviews {
	[self initView];
	[self addPollingNotificationObserver];
}

@end
