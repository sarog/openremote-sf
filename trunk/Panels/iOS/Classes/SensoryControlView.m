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
#import "SensoryControlView.h"
#import "Slider.h"
#import "Switch.h"
#import "NotificationConstant.h"

@implementation SensoryControlView

#pragma mark instance methods
// Implement the delegate method for adding notification observer of polling.
- (void) addPollingNotificationObserver {
	int sensorId = 0;
	if ([component isKindOfClass:[Slider class]]) {
		sensorId = ((Slider *)component).sensor.sensorId;
	} else if ([component isKindOfClass:[Switch class]]) {
		sensorId = ((Switch *)component).sensor.sensorId;
	}
    // EBR: what about other types of sensors (labels and images), could test for SensorComponent class and cast to that
    // EBR: Labels and images are taken care of in SensoryView class
    // TODO : anyway, avoid isKindOfClass: and implement correctly in each subclass
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

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

@end
