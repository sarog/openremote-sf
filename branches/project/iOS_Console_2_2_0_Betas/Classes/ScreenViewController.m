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
#import "ScreenViewController.h"
#import "ViewHelper.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "ServerDefinition.h"
#import "CredentialUtil.h"
#import "ControllerException.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORControllerProxy.h"
#import "ScreenSubController.h"

@interface ScreenViewController ()

- (void)sendCommandRequest:(Component *)component;
- (void)doNavigate:(Navigate *)navi;

@property (nonatomic, retain) ScreenSubController *screenSubController;

@end

@implementation ScreenViewController

/**
 * Assign parameter screen model data to screenViewController.
 */
- (void)setScreen:(Screen *)s {
	[s retain];
	[screen release];
	screen = s;
	if ([[screen pollingComponentsIds] count] > 0 ) {
		polling = [[PollingHelper alloc] initWithController:[ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController
                                               componentIds:[[screen pollingComponentsIds] componentsJoinedByString:@","]];
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
    self.screenSubController = [[[ScreenSubController alloc] initWithScreen:screen] autorelease];
    self.view = self.screenSubController.view;    
}

- (void)viewDidUnload
{
    self.screenSubController = nil;
    self.view = nil;
    [super viewDidUnload];
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
    [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController sendCommand:@"swipe" forComponent:component delegate:nil];
}

- (void)doNavigate:(Navigate *)navi {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationNavigateTo object:navi];
}

- (void)dealoc {
    [polling release];
	//[screen release];
	self.screenSubController = nil;
	[super dealloc];
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
}

@synthesize screen, polling;
@synthesize screenSubController;

@end
