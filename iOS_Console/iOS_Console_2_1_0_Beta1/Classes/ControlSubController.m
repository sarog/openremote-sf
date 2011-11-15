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
#import "ControlSubController.h"
#import "LocalLogic.h"
#import "LocalCommand.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"
#import "Component.h"
#import "AppDelegate.h"
#import "ORControllerProxy.h"

@implementation ControlSubController

- (void)sendCommandRequest:(NSString *)commandType {
	// Check for local command first
	LocalCommand *localCommand = [[[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition.localLogic commandForId:self.component.componentId];
	if (localCommand) {
		Class clazz = NSClassFromString(localCommand.className);
		SEL selector = NSSelectorFromString([NSString stringWithFormat:@"%@:", localCommand.methodName]);
		[clazz performSelector:selector withObject:((AppDelegate *)[[UIApplication sharedApplication] delegate]).localContext];
	} else {
        [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController sendCommand:commandType forComponent:self.component delegate:nil];
	}
}

#pragma mark ORControllerCommandSenderDelegate implementation

- (void)commandSendFailed
{
}

@end
