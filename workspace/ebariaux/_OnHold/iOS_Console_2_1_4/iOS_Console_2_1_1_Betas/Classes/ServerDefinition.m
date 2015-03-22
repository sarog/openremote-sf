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
#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"
#import "NSString+ORAdditions.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

NSString *const kControllerControlPath = @"rest/control";
NSString *const kControllerStatusPath = @"rest/status";
NSString *const kControllerPollingPath = @"rest/polling";
NSString *const kControllerFetchPanelsPath = @"rest/panels";
NSString *const kControllerFetchGroupMembersPath = @"rest/servers";

@implementation ServerDefinition

+ (NSString *)serverUrl {
    return ((ORController *)[ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController).primaryURL;
}

+ (NSString *)panelXmlRESTUrl {
	NSString *panelUrl = [NSString stringWithFormat:@"rest/panel/%@",
                          [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.selectedPanelIdentity];
	NSString *panelXmlUrl = [[self securedOrRawServerUrl] stringByAppendingPathComponent:panelUrl];
	return panelXmlUrl;
}

//Round-Robin (failover) servers
+ (NSString *)serversXmlRESTUrl {
	NSString *serversXmlUrl = [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"rest/servers"];
	return serversXmlUrl;
}

+ (NSString *)imageUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"resources"];
}

//returns serverUrl, if SSL is enabled, use secured server url.
+ (NSString *)securedOrRawServerUrl {
	return [self serverUrl];
}

+ (NSString *)logoutUrl {
	return [[self securedOrRawServerUrl] stringByAppendingPathComponent:@"logout"];
}

+ (NSString *)panelsRESTUrl {
	NSString *url = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.primaryURL;
	return [url stringByAppendingPathComponent:@"rest/panels"];
}

+ (NSString *)hostName {
	return [[self serverUrl] hostOfURL];
}

@end
