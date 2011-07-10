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

- (void)controlButtonDown:(id)sender {
	[self cancelTimers];
	self.longPress = NO;
    
	Button *button = (Button *)self.component;
	if (button.hasPressCommand == YES) {
		[self sendPressCommand:nil];
	 	if (button.repeat == YES ) {			
			self.buttonRepeatTimer = [NSTimer scheduledTimerWithTimeInterval:(button.repeatDelay / 1000.0) target:self selector:@selector(sendPressCommand:) userInfo:nil repeats:YES];
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