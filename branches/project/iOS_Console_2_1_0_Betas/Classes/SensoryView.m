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
#import "SensoryView.h"
#import "NotificationConstant.h"
#import "Label.h"
#import "Image.h"
#import "Web.h"

@implementation SensoryView

#pragma mark instance methods.

// Add notification observer for polling
- (void) addPollingNotificationObserver {
	int sensorId = 0;
	if ([component isKindOfClass:[Label class]]) {
		sensorId = ((Label *)component).sensor.sensorId;
	} else if ([component isKindOfClass:[Image class]]) {
		sensorId = ((Image *)component).sensor.sensorId;
		if (sensorId <= 0) {
			sensorId = ((Image *)component).label.sensor.sensorId;
		}
	} else if ([component isKindOfClass:[Web class]]) {
		sensorId = ((Web *)component).sensor.sensorId;
    }
	if (sensorId > 0 ) {
        // EBR : remove ourself first as with current code this method might be called multiple times and we only need to process event once
        [[NSNotificationCenter defaultCenter] removeObserver:self name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,sensorId] object:nil];
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
	[self initView]; // TODO EBR : we should not create views in layoutSubviews !!!
	[self addPollingNotificationObserver]; // TODO EBR : we should not do this in layout subviews, might be called "at any time"
}

- (void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [super dealloc];
}

@end
