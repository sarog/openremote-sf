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
#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"
#import "NSString+ORAdditions.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

NSString *const kControllerFetchCapabilitiesPath = @"rest/capabilities";
NSString *const kControllerFetchGroupMembersPath = @"rest/servers";

#define NO_VERSION_IN_REST_VERSION  @"2.0"

@implementation ServerDefinition

+ (NSString *)controllerControlPathForController:(ORController *)aController
{
    if ([aController.controllerAPIVersion isEqualToString:NO_VERSION_IN_REST_VERSION]) {
        return @"rest/control";
    }
    return [NSString stringWithFormat:@"rest/%@/control", aController.controllerAPIVersion];
}

+ (NSString *)controllerStatusPathForController:(ORController *)aController
{
    if ([aController.controllerAPIVersion isEqualToString:NO_VERSION_IN_REST_VERSION]) {
        return @"rest/status";
    }
    return [NSString stringWithFormat:@"rest/%@/status", aController.controllerAPIVersion];
}

+ (NSString *)controllerPollingPathForController:(ORController *)aController
{
    if ([aController.controllerAPIVersion isEqualToString:NO_VERSION_IN_REST_VERSION]) {
        return @"rest/polling";
    }
    return [NSString stringWithFormat:@"rest/%@/polling", aController.controllerAPIVersion];
}

+ (NSString *)controllerFetchPanelsPathForController:(ORController *)aController
{
    if ([aController.controllerAPIVersion isEqualToString:NO_VERSION_IN_REST_VERSION]) {
        return @"rest/panels";
    }
    return [NSString stringWithFormat:@"rest/%@/panels", aController.controllerAPIVersion];
}

+ (NSString *)serverUrl {
    return ((ORController *)[ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController).primaryURL;
}

+ (NSString *)panelXmlRESTUrlForController:(ORController *)aController {
	NSString *panelUrl;

    if ([aController.controllerAPIVersion isEqualToString:NO_VERSION_IN_REST_VERSION]) {
        panelUrl = [NSString stringWithFormat:@"rest/panel/%@", aController.selectedPanelIdentity];
    } else {
        panelUrl = [NSString stringWithFormat:@"rest/%@/panel/%@", aController.controllerAPIVersion, aController.selectedPanelIdentity];
    }
    
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
