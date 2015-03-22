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
#import "ButtonSubController.h"
#import "DirectoryDefinition.h"
#import "Button.h"
#import "Image.h"
#import "ClippedUIImage.h"
#import "NotificationConstant.h"

#import "ControllerVersionSelectAPI.h"

@interface ButtonSubController()

@property (nonatomic, readwrite, retain) UIView *view;
@property (nonatomic, readonly) Button *button;

@property (nonatomic, retain) id<ControllerButtonAPI> controllerButtonAPI;

@property (nonatomic, retain) NSTimer *buttonRepeatTimer;
@property (nonatomic, retain) NSTimer *longPressTimer;
@property (nonatomic, getter=isLongPress, setter=setLongPress:) BOOL longPress;

- (void)cancelTimers;

- (void)controlButtonUp:(id)sender;
- (void)controlButtonDown:(id)sender;
- (void)longPress:(NSTimer *)timer;
- (void)press:(NSTimer *)timer;

@end

@implementation ButtonSubController

- (id)initWithComponent:(Component *)aComponent
{
    self = [super initWithComponent:aComponent];
    if (self) {
        UIButton *uiButton = [UIButton buttonWithType:UIButtonTypeCustom];
        [uiButton addTarget:self action:@selector(controlButtonDown:) forControlEvents:UIControlEventTouchDown];	
        [uiButton addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpOutside];	
        [uiButton addTarget:self action:@selector(controlButtonUp:) forControlEvents:UIControlEventTouchUpInside];
        
        /* Observing the frame so the displayed image can be resized to appear centered in the button */
        [uiButton addObserver:self forKeyPath:@"frame" options:NSKeyValueObservingOptionNew context:NULL];
        uiButton.titleLabel.font = [UIFont boldSystemFontOfSize:13];
        uiButton.titleLabel.lineBreakMode = UILineBreakModeTailTruncation;
        [uiButton setTitle:self.button.name forState:UIControlStateNormal];
        self.view = uiButton;
        

        // TODO/ comment
        self.controllerButtonAPI = (id <ControllerButtonAPI>)[[[ControllerVersionSelectAPI alloc] initWithAPIProtocol:@protocol(ControllerButtonAPI)] autorelease];
        
    }
    return self;
}

- (void)observeValueForKeyPath:(NSString *)keyPath ofObject:(id)object change:(NSDictionary *)change context:(void *)context
{
    /*
     * Resize the images appropriately for the new button dimensions, making sure they are centered, clipped and not resized.
     * Using imageEdgeInsets would be an easier solution to accomplish this but is not available for the background image.
     */
    if (object == self.view) {        
        UIButton *uiButton = (UIButton *)self.view;
        if (self.button.defaultImage) {
            UIImage *uiImage = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:self.button.defaultImage.src]];
            ClippedUIImage *clippedUIImage = [[ClippedUIImage alloc] initWithUIImage:uiImage withinUIView:uiButton imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];		
            [uiImage release];
            [uiButton setBackgroundImage:clippedUIImage forState:UIControlStateNormal];
            [clippedUIImage release];
            UIImage *uiImagePressed = [[UIImage alloc] initWithContentsOfFile:[[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:self.button.pressedImage.src]];
            if (uiImagePressed) {
                ClippedUIImage *clippedUIImagePressed = [[ClippedUIImage alloc] initWithUIImage:uiImagePressed withinUIView:uiButton imageAlignToView:IMAGE_ABSOLUTE_ALIGN_TO_VIEW];
                [uiButton setBackgroundImage:clippedUIImagePressed forState:UIControlStateHighlighted];
                [clippedUIImagePressed release];
            }
            [uiImagePressed release];
        } else {
            UIImage *buttonImage = [[UIImage imageNamed:@"button.png"] stretchableImageWithLeftCapWidth:20 topCapHeight:29];
            [uiButton setBackgroundImage:buttonImage forState:UIControlStateNormal];
        }
    }
}

- (void)dealloc
{
    [self.view removeObserver:self forKeyPath:@"frame"];
    self.controllerButtonAPI = nil;
    [self cancelTimers];
    [super dealloc];
}

- (Button *)button
{
    return (Button *)self.component;
}

- (void)controlButtonUp:(id)sender
{
	[self cancelTimers];
	Button *button = (Button *)self.component;
    
    if (button.hasShortReleaseCommand && !self.isLongPress) {
        [self.controllerButtonAPI sendShortReleaseCommand:self];
    }
    if (button.hasLongReleaseCommand && self.isLongPress) {
        [self.controllerButtonAPI sendLongReleaseCommand:self];        
    }
    
	if (button.navigate) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:button.navigate];
	}
}

- (void)controlButtonDown:(id)sender
{
	[self cancelTimers];
	self.longPress = NO;
    
	Button *button = (Button *)self.component;
	if (button.hasPressCommand == YES) {
		[self.controllerButtonAPI sendPressCommand:self];
	 	if (button.repeat == YES ) {			
			self.buttonRepeatTimer = [NSTimer scheduledTimerWithTimeInterval:(button.repeatDelay / 1000.0) target:self selector:@selector(press:) userInfo:nil repeats:YES];
		}
	}
    if (button.hasLongPressCommand || button.hasLongReleaseCommand) {
        // Set-up timer to detect when this becomes a long press
        self.longPressTimer = [NSTimer scheduledTimerWithTimeInterval:(button.longPressDelay / 1000.0) target:self selector:@selector(longPress:) userInfo:nil repeats:NO];
    }
}

- (void)press:(NSTimer *)timer
{
    [self.controllerButtonAPI sendPressCommand:self];
}

- (void)longPress:(NSTimer *)timer
{
    self.longPress = YES;
    [self.controllerButtonAPI sendLongPressCommand:self];
}

- (void)cancelTimers
{
	if (self.buttonRepeatTimer) {
		[self.buttonRepeatTimer invalidate];
	}
	self.buttonRepeatTimer = nil;
	if (self.longPressTimer) {
		[self.longPressTimer invalidate];
	}
	self.longPressTimer = nil;
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
    [super commandSendFailed];
    [self cancelTimers];
}

@synthesize view;
@synthesize controllerButtonAPI;
@synthesize buttonRepeatTimer, longPressTimer, longPress;

@end