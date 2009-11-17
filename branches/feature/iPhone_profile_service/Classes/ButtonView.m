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

#import "ButtonView.h"
#import "Control.h"
#import "DirectoryDefinition.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"

//defines the interval (seconds) of command when pressing a repeat button
#define REPEAT_CMD_INTERVAL 0.3

@interface ButtonView (Private) 
- (void)createButton;
- (void)controlButtonUp:(id)sender;
- (void)controlButtonDown:(id)sender;
- (void)sendCommand:(id)sender;

@end

@implementation ButtonView

@synthesize uiButton, uiImage, uiImagePressed;



//Create button according to control and add tap event
- (void)createButton {
	if (uiButton != nil) {
		[uiButton release];
		uiButton = nil;
	}
	uiButton = [[UIButton buttonWithType:UIButtonTypeCustom] retain];
	
	[uiButton addTarget:self action:@selector(controlButtonDown:) forControlEvents:UIControlEventTouchDown];	
	[uiButton addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpOutside];	
	[uiButton addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpInside];

	
	[self addSubview:uiButton];
	
}

- (void) controlButtonUp:(id)sender {
	[self cancelTimer];
	
}

- (void) controlButtonDown:(id)sender {
	
	[self cancelTimer];
	
	Button *button = (Button *)control;
	if (button.hasCommand == YES) {
		[self sendCommand:nil];
	 	if (button.repeat == YES ) {			
			controlTimer = [NSTimer scheduledTimerWithTimeInterval:REPEAT_CMD_INTERVAL	target:self selector:@selector(sendCommand:) userInfo:nil repeats:YES];			
		} 
	}
	if (button.navigate) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:button.navigate];
	}
	
}

- (void) sendCommand:(id)sender {
	[self	sendCommandRequest:@"click"];
}


//override layoutSubviews method of UIView 
- (void)layoutSubviews {	
	[self createButton];
	
	Button *button = (Button *)control;
	if (button.image) {
		uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:button.image.src]];
		uiImagePressed = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:button.imagePressed.src]];	
		[uiButton setImage:uiImage forState:UIControlStateNormal];
		[uiButton setImage:uiImagePressed forState:UIControlStateHighlighted];
		//use top-left alignment
		[uiButton setFrame:CGRectMake(0, 0, uiImage.size.width, uiImage.size.height)];
	} else {
		[uiButton setFrame:[self bounds]];
		UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		[uiButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
		
		//buttonImage = [[UIImage imageNamed:@"buttonHighlighted.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:20];
		//[uiButton setBackgroundImage:buttonImage forState:UIControlStateHighlighted];
		
		uiButton.titleLabel.font = [UIFont boldSystemFontOfSize:18];
		[uiButton setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
		uiButton.titleLabel.shadowOffset = CGSizeMake(0, -2);
		[uiButton setTitle:button.name forState:UIControlStateNormal];
	}
	
}




- (void)dealloc {
	[uiImage  release];
	[uiImagePressed release];
	[uiButton release];
	
  [super dealloc];
}


@end
