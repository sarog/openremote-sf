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
