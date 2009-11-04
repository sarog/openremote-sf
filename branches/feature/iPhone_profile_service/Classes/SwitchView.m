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

#import "SwitchView.h"
#import "DirectoryDefinition.h"
#import "ViewHelper.h"
#import "PollingStatusParserDelegate.h"
#import "NotificationConstant.h"


@interface SwitchView (Private)


- (void)setOn:(BOOL)on;
- (void)stateChanged:(id)sender;

@end



@implementation SwitchView

@synthesize button, onUIImage, offUIImage;

- (void)stateChanged:(id)sender {
	if (isOn) {
		[self sendCommandRequest:@"OFF"];
	} else {		
		[self sendCommandRequest:@"ON"];
	} 
}

- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *delegate = (PollingStatusParserDelegate *)[notification object];
	NSString *newStatus = [delegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",control.controlId]];
	if ([[newStatus uppercaseString] isEqualToString:@"ON"]) {
		[self setOn:YES];
	} else if ([[newStatus uppercaseString] isEqualToString:@"OFF"]) {
		[self setOn:NO];
	} 
}


- (void)setOn:(BOOL)on {
	if (on) {
		isOn = YES;
		if (canUseImage) {
			[button setImage:onUIImage forState:UIControlStateNormal];		
		} else {
			[button setTitle:@"ON" forState:UIControlStateNormal];			
		}

	} else {
		isOn = NO;
		if (canUseImage) {
			[button setImage:offUIImage forState:UIControlStateNormal];			
		} else {
			[button setTitle:@"OFF" forState:UIControlStateNormal];
		}		
	}

}



//Create button according to control and add tap event
- (void)createButton {
	
	//avoid overlap the same button by mistake
	if (button) {
		[button removeFromSuperview];
		[button release];
	}
	
	button = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
	[button addTarget:self action:@selector(stateChanged:) forControlEvents:UIControlEventTouchUpInside];
	
	[self addSubview:button];
	Switch *theSwitch = (Switch *)control;
	NSString *onImage = theSwitch.onImage.src;
	NSString *offImage = theSwitch.offImage.src;
	
	//assign YES if have both on image and off image
	canUseImage = onImage && offImage;
}

//NOTE:You should init all nested views with *initWithFrame* and you should pass in valid frame rects.
//Otherwise, UI widget inside will not work in nested UIViews
- (void)layoutSubviews {
	[self createButton];
	Switch *theSwitch = (Switch *)control;
	NSString *onImage = theSwitch.onImage.src;
	NSString *offImage = theSwitch.offImage.src;
	[button setFrame:[self bounds]];
	if (canUseImage) {
		 onUIImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:onImage]];
		 offUIImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:offImage]];
	 } else {
		UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[button setBackgroundImage:buttonImage forState:UIControlStateNormal];

		buttonImage = [[UIImage imageNamed:@"buttonHighlighted.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[button setBackgroundImage:buttonImage forState:UIControlStateHighlighted];

		button.titleLabel.font = [UIFont boldSystemFontOfSize:18];
		[button setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
		button.titleLabel.shadowOffset = CGSizeMake(0, -2);
	}
	[self setOn:NO];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(setPollingStatus:) name:[NSString stringWithFormat:NotificationPollingStatusIdFormat,control.controlId] object:nil];
}



- (void)dealloc {
	[button release];
	[onUIImage release];
	[offUIImage release];
	[super dealloc];
}


@end
