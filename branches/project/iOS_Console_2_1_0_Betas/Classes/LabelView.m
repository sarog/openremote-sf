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
#import "LabelView.h"
#import "Label.h"
#import "NotificationConstant.h"
#import "SensorState.h"
#import "PollingStatusParserDelegate.h"
#import "UIColor+ORAdditions.h"

@implementation LabelView

@synthesize uiLabel;

#pragma mark Overrided methods of superclass(SensoryView)

- (void) initView {
	uiLabel = [[UILabel alloc] initWithFrame:[self bounds]];
	[uiLabel setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
	[uiLabel setTextAlignment:UITextAlignmentCenter];
    uiLabel.adjustsFontSizeToFitWidth = NO;
    [uiLabel setLineBreakMode:UILineBreakModeWordWrap];
    [uiLabel setNumberOfLines:50];
	Label *labelModel = (Label *)component;
	
	uiLabel.text = labelModel.text;
	uiLabel.font = [UIFont fontWithName:@"Arial" size:labelModel.fontSize];
	uiLabel.textColor = [UIColor or_ColorWithRGBString:[labelModel.color substringFromIndex:1]];
	
	[self addSubview:uiLabel];
}

// Override method of sensory view.
- (void)setPollingStatus:(NSNotification *)notification {
    
    // TODO EBR : check / test, it seems this method is called multiple times for a single sensor update
    
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Label *)component).sensor.sensorId;
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]];
//	NSLog(@"new status is : %@", newStatus);
	
	Label *labelModel = ((Label *)component);
	BOOL changeText = NO;
	for (SensorState *sensorState in labelModel.sensor.states) {
		if ([[sensorState.name lowercaseString] isEqualToString:[newStatus lowercaseString]]) {
			uiLabel.text = sensorState.value;
			changeText = YES;
			break;
		}
	}
	if(!changeText && ![newStatus isEqualToString:@""] && newStatus != nil) {
		uiLabel.text = newStatus;
		changeText = YES;
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
