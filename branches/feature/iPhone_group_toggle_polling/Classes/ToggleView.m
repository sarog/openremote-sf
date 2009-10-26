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

#import "ToggleView.h"
#import "Toggle.h"

@interface ToggleView (Private) 

- (void)stateChanged:(id)sender;

@end

@implementation ToggleView

@synthesize switchToggle;

- (void)stateChanged:(id)sender {

	
	NSLog(@"state = %@", [switchToggle isOn]);
}

//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
//Otherwise, UI widget inside will not work in nested UIViews
- (void)layoutSubviews {
	Toggle *toggle = (Toggle *)control;
	
	BOOL useNativeStyle = YES;
	BOOL currentState = YES;
	// TODO: render states, here use UISwitch for mockup
	if (toggle.states.count == 2 && useNativeStyle) {
		switchToggle = [[UISwitch alloc] initWithFrame:CGRectMake(0,0,0,0)];
		
		[switchToggle setOn:currentState];
		[switchToggle addTarget:self action:@selector(stateChanged:) forControlEvents:UIControlEventValueChanged];

	} else {		
		//UIImage *firstStateImage = [UIImage imageNamed:[[[toggle.states objectAtIndex:0] image] src]];
	}
	[self addSubview:switchToggle];
}




- (void)dealloc {
	[switchToggle release];
	
  [super dealloc];
}


@end
