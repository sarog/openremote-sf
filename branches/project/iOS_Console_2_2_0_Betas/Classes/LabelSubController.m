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
#import "LabelSubController.h"
#import "Component.h"
#import "Label.h"
#import "UIColor+ORAdditions.h"
#import "PollingStatusParserDelegate.h"
#import "SensorState.h"
#import "Sensor.h"

@interface LabelSubController()

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, readonly) Label *label;

@end

@implementation LabelSubController

- (id)initWithComponent:(Component *)aComponent
{
    self = [super initWithComponent:aComponent];
    if (self) {
        UILabel *uiLabel = [[UILabel alloc] initWithFrame:CGRectZero];
        uiLabel.backgroundColor = [UIColor clearColor];
        uiLabel.adjustsFontSizeToFitWidth = NO;
        uiLabel.textAlignment = UITextAlignmentCenter;
        uiLabel.lineBreakMode = UILineBreakModeWordWrap;
        uiLabel.numberOfLines = 50;
        uiLabel.text = self.label.text;
        uiLabel.font = [UIFont fontWithName:@"Arial" size:self.label.fontSize];
        uiLabel.textColor = [UIColor or_ColorWithRGBString:[self.label.color substringFromIndex:1]];
        self.view = uiLabel;
        [uiLabel release];
    }
    return self;
}

- (Label *)label
{
    return (Label *)self.component;
}

- (void)setPollingStatus:(NSNotification *)notification
{
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = self.label.sensorId;
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d", sensorId]];
	
    UILabel *uiLabel = (UILabel *)self.view;
    NSString *stateValue = [self.label.sensor stateValueForName:newStatus];
    if (stateValue) {
        uiLabel.text = stateValue;
    } else {
        if (newStatus && ![newStatus isEqualToString:@""]) {
            uiLabel.text = newStatus;
        } else {
            uiLabel.text = self.label.text;
        }
    }
}

@synthesize view;

@end