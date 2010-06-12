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
#import "ClippedUIImage.h"


@interface SwitchView (Private)
- (void)createButton;
- (void)setOn:(BOOL)on;
- (void)stateChanged:(id)sender;
@end

@implementation SwitchView

@synthesize button, onUIImage, offUIImage;

#pragma mark Override methods of SensoryControlView.

- (void)initView {
	[self createButton];
	Switch *theSwitch = (Switch *)component;
	NSString *onImage = theSwitch.onImage.src;
	NSString *offImage = theSwitch.offImage.src;
	if (canUseImage) {		
		onUIImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:onImage]];
		offUIImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:offImage]];
		onUIImage = [[ClippedUIImage alloc] initWithUIImage:onUIImage dependingOnUIView:self imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];
		offUIImage = [[ClippedUIImage alloc] initWithUIImage:offUIImage dependingOnUIView:self imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];
		//use top-left alignment
		[button setFrame:CGRectMake(0, 0, onUIImage.size.width, onUIImage.size.height)];
	} else {
		[button setFrame:[self bounds]];
		UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:29];
		[button setBackgroundImage:buttonImage forState:UIControlStateNormal];
		
		button.titleLabel.font = [UIFont boldSystemFontOfSize:18];
		[button setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
		button.titleLabel.shadowOffset = CGSizeMake(0, -2);
	}
	[self setOn:NO];
}

- (void)setPollingStatus:(NSNotification *)notification {
	PollingStatusParserDelegate *pollingDelegate = (PollingStatusParserDelegate *)[notification object];
	int sensorId = ((Switch *)component).sensor.sensorId;
	NSString *newStatus = [pollingDelegate.statusMap objectForKey:[NSString stringWithFormat:@"%d",sensorId]];
	if ([[newStatus uppercaseString] isEqualToString:@"ON"]) {
		[self setOn:YES];
	} else if ([[newStatus uppercaseString] isEqualToString:@"OFF"]) {
		[self setOn:NO];
	} 
}

#pragma mark Private methods

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
	Switch *theSwitch = (Switch *)component;
	NSString *onImage = theSwitch.onImage.src;
	NSString *offImage = theSwitch.offImage.src;
	
	//assign YES if have both on image and off image
	canUseImage = onImage && offImage;
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

- (void)stateChanged:(id)sender {
	if (isOn) {
		[self sendCommandRequest:@"OFF"];
	} else {		
		[self sendCommandRequest:@"ON"];
	} 
}

#pragma mark dealloc method

- (void)dealloc {
	[button release];
	[onUIImage release];
	[offUIImage release];
	[super dealloc];
}


@end
