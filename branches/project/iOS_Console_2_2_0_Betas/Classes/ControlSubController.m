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
#import "ControlSubController.h"
#import "LocalController.h"
#import "LocalCommand.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"
#import "Component.h"
#import "AppDelegate.h"
#import "ClientSideRuntime.h"
#import "ORControllerProxy.h"

@implementation ControlSubController

- (void)sendCommandRequest:(NSString *)commandType {

    ORController *controller = [[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController;
    
    // This is a lookup based on the command id, not the component that references the command !!!!

	// Check for local command first
	NSArray *localCommands = [controller.definition.localController commandsForComponentId:self.component.componentId action:commandType];
	if (localCommands && ([localCommands count] > 0)) {
        [controller.clientSideRuntime executeCommands:localCommands];
	} else {
        [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController sendCommand:commandType forComponent:self.component delegate:nil];
	}
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
}

@end
