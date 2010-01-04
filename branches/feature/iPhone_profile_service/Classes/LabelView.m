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

@interface LabelView(Private)
-(void) initLabel;
@end

@implementation LabelView

#pragma mark Overridden methods

// This method is abstract method of direct superclass ControlView's.
// So, this method must be overridden in subclass.
- (void)setPollingStatus:(NSNotification *)notification {
}

// This method is abstract method of indirect superclass UIView's.
- (void)layoutSubviews {
	NSLog(@"layoutSubviews of LabelView.");
	[self initLabel];
}

#pragma mark Private methods implementation

-(void) initLabel {
	UILabel *uiLabel = [[UILabel alloc] initWithFrame:[self bounds]];
	//[uiLabel backgroundColor:[UIColor blackColor]];
	[uiLabel setBackgroundColor:[UIColor colorWithRed:0.0 green:0.0 blue:0.0 alpha:0.0]];
	[uiLabel setTextAlignment:UITextAlignmentCenter];
	Label *labelModel = (Label *)component;
	uiLabel.text = labelModel.value;
	uiLabel.textColor = [UIColor grayColor];
	[self addSubview:uiLabel];
}

@end
