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

@interface ControlSubController ()

- (NSArray *)localCommandsForCommandType:(NSString *)commandType;

@end

@implementation ControlSubController

- (void)sendCommandRequest:(NSString *)commandType {
    NSLog(@"commandType %@", commandType);

	// Check for local command first
	NSArray *localCommands = [self localCommandsForCommandType:commandType];
    if (localCommands && ([localCommands count] > 0)) {
        [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.clientSideRuntime executeCommands:localCommands]; // TODO: pass command type
	} else {
        [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController sendCommand:commandType forComponent:self.component delegate:nil];
	}
}

- (NSArray *)localCommandsForCommandType:(NSString *)commandType
{
	return [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition.localController commandsForComponentId:self.component.componentId action:commandType];
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
}

@end