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


#import "ScreenViewController.h"
#import "ScreenView.h"
#import "ViewHelper.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "ServerDefinition.h"
#import "CredentialUtil.h"
#import "ControllerException.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORControllerProxy.h"

@interface ScreenViewController ()

- (void)sendCommandRequest:(Component *)component;
- (void)doNavigate:(Navigate *)navi;

@property (nonatomic, retain) ORControllerCommandSender *commandSender;

@end

@implementation ScreenViewController

@synthesize screen, polling;
@synthesize commandSender;

- (void)setCommandSender:(ORControllerCommandSender *)aCommandSender
{
    if (commandSender != aCommandSender) {
        commandSender.delegate = nil;
        [commandSender release];
        commandSender = [aCommandSender retain];
    }
}

/**
 * Assign parameter screen model data to screenViewController.
 */
- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	if ([[screen pollingComponentsIds] count] > 0 ) {
		polling = [[PollingHelper alloc] initWithComponentIds:[[screen pollingComponentsIds] componentsJoinedByString:@","]];
	}
}

/**
 * Perform gesture action. Currently, the gesture should be one action of sliding from left to right, 
 * sliding from right to left, sliding from top to bottom and sliding from bottom to top.
 */
- (void)performGesture:(Gesture *)gesture {
	Gesture * g = [screen getGestureIdByGestureSwipeType:gesture.swipeType];
	if (g) {
		if (g.hasControlCommand) {
			[self sendCommandRequest:g];
		} else if (g.navigate) {
			[self doNavigate:g.navigate];
		}
	}
}

// Implement loadView to create a view hierarchy programmatically.
- (void)loadView {
	ScreenView *v = [[ScreenView alloc] init];

	//set Screen in ScreenView
	[v setScreen:screen];
	
	[self setView:v];
	[v setBackgroundColor:[UIColor blackColor]];
	[v release];
}

- (void)startPolling {
	[polling requestCurrentStatusAndStartPolling];
}
- (void)stopPolling {
	[polling cancelPolling];
}

// Send control command for gesture actions.
- (void)sendCommandRequest:(Component *)component
{
    self.commandSender = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController sendCommand:@"swipe" forComponent:component delegate:self];
}

- (void)doNavigate:(Navigate *)navi {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:navi];
}

- (void)dealoc {
    self.commandSender = nil;
	[polling release];
	//[screen release];
	
	[super dealloc];
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
}

@end
