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

#import "LabelView.h"
#import "Label.h"
#import "NotificationConstant.h"
#import "SensorState.h"
#import "PollingStatusParserDelegate.h"
#import "ColorUtil.h"

@implementation LabelView

@synthesize uiLabel;

#pragma mark Overrided methods of superclass(SensoryView)

- (void) initView {
	uiLabel = [[UILabel alloc] initWithFrame:[self bounds]];
	[uiLabel setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
	[uiLabel setTextAlignment:UITextAlignmentCenter];
	Label *labelModel = (Label *)component;
	
	uiLabel.text = labelModel.text;
	uiLabel.font = [UIFont fontWithName:@"Arial" size:labelModel.fontSize];
	uiLabel.textColor = [ColorUtil colorWithRGBString:[labelModel.color substringFromIndex:1]];
	
	[self addSubview:uiLabel];
}

- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Label *)component).sensor.sensorId;
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]];
	NSLog(@"new status is : %@", newStatus);
	
	Label *labelModel = ((Label *)component);
	BOOL changeText = NO;
	for (SensorState *sensorState in labelModel.sensor.states) {
		if ([[sensorState.name lowercaseString] isEqualToString:[newStatus lowercaseString]]) {
			uiLabel.text = sensorState.value;
			changeText = YES;
			break;
		}
	}
	if (!changeText) {
		uiLabel.text = labelModel.text;
	}
}

#pragma mark dealloc

- (void)dealloc {
	[uiLabel release];
	[super dealloc];
}

@end
