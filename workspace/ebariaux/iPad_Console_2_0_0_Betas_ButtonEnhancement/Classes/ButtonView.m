/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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
#import "ClippedUIImage.h"

@interface ButtonView ()

@property (nonatomic, retain) NSTimer *buttonRepeatTimer;
@property (nonatomic, retain) NSTimer *longPressTimer;
@property (nonatomic, getter=isLongPress, setter=setLongPress:) BOOL longPress;

- (void)cancelTimers;

- (void)createButton;
- (void)controlButtonUp:(id)sender;
- (void)controlButtonDown:(id)sender;
- (void)longPress:(NSTimer *)timer;

- (void)sendPressCommand:(id)sender;
- (void)sendShortReleaseCommand:(id)sender;
- (void)sendLongPressCommand:(id)sender;
- (void)sendLongReleaseCommand:(id)sender;

@end

@implementation ButtonView

@synthesize uiButton, uiImage, uiImagePressed;
@synthesize buttonRepeatTimer, longPressTimer, longPress;

#pragma mark Private methods

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

// Event handler for button up.
- (void)controlButtonUp:(id)sender {
	[self cancelTimers];
	Button *button = (Button *)self.component;
    
    if (button.hasShortReleaseCommand && !self.isLongPress) {
        [self sendShortReleaseCommand:nil];
    }
    if (button.hasLongReleaseCommand && self.isLongPress) {
        [self sendLongReleaseCommand:nil];        
    }
    
	if (button.navigate) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:button.navigate];
	}
}

// Event handler for button down.
- (void)controlButtonDown:(id)sender {
	[self cancelTimers];
	self.longPress = NO;
    
	Button *button = (Button *)self.component;
	if (button.hasPressCommand == YES) {
		[self sendPressCommand:nil];
	 	if (button.repeat == YES ) {			
			self.buttonRepeatTimer = [NSTimer scheduledTimerWithTimeInterval:(button.repeatDelay / 1000.0) target:self selector:@selector(sendCommand:) userInfo:nil repeats:YES];			
		}
	}
    if (button.hasLongPressCommand || button.hasLongReleaseCommand) {
        // Set-up timer to detect when this becomes a long press
        self.longPressTimer = [NSTimer scheduledTimerWithTimeInterval:(button.longPressDelay / 1000.0) target:self selector:@selector(longPress:) userInfo:nil repeats:NO];
    }
}

- (void)longPress:(NSTimer *)timer
{
    self.longPress = YES;
    [self sendLongPressCommand:nil];
}

- (void)sendPressCommand:(id)sender {
	[self sendCommandRequest:@"press"];
}

- (void)sendShortReleaseCommand:(id)sender {
    [self sendCommandRequest:@"shortRelease"];
}

- (void)sendLongPressCommand:(id)sender {
    [self sendCommandRequest:@"longPress"];
}

- (void)sendLongReleaseCommand:(id)sender {
    [self sendCommandRequest:@"longRelease"];
}

- (void)cancelTimers {
	if (self.buttonRepeatTimer) {
		[self.buttonRepeatTimer invalidate];
	}
	self.buttonRepeatTimer = nil;
	if (self.longPressTimer) {
		[self.longPressTimer invalidate];
	}
	self.longPressTimer = nil;
}

#pragma mark Override the methods of superclass(ComponentView)

- (void)initView {
	[self createButton];
	
	Button *button = (Button *)self.component;
	if (button.defaultImage) {
		uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:button.defaultImage.src]];
		uiImagePressed = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:button.pressedImage.src]];	
		ClippedUIImage *clippedUIImage = [[ClippedUIImage alloc] initWithUIImage:uiImage dependingOnUIView:self imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];		
		[uiButton setBackgroundImage:clippedUIImage forState:UIControlStateNormal];
        [clippedUIImage release];
		if (uiImagePressed) {
			ClippedUIImage *clippedUIImagePressed = [[ClippedUIImage alloc] initWithUIImage:uiImagePressed dependingOnUIView:self imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];
			[uiButton setBackgroundImage:clippedUIImagePressed forState:UIControlStateHighlighted];
            [clippedUIImagePressed release];
		}
		//use top-left alignment
		[uiButton setFrame:CGRectMake(0, 0, clippedUIImage.size.width, clippedUIImage.size.height)];
	} else {
		[uiButton setFrame:[self bounds]];
		UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:29];
		[uiButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
	}
	
	uiButton.titleLabel.font = [UIFont boldSystemFontOfSize:13];
	uiButton.titleLabel.lineBreakMode = UILineBreakModeTailTruncation;
	//[uiButton setTitleShadowColor:[UIColor grayColor] forState:UIControlStateNormal];
	//uiButton.titleLabel.shadowOffset = CGSizeMake(0, -2);
	[uiButton setTitle:button.name forState:UIControlStateNormal];	
}

#pragma mark dealloc

- (void)dealloc {
	[uiImage  release];
	[uiImagePressed release];
	[uiButton release];
    [self cancelTimers];

    [super dealloc];
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
    [super commandSendFailed];
    [self cancelTimers];
}

@end