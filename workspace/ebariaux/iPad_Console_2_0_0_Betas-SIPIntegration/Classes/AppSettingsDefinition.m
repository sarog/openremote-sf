/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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

#import "AppSettingsDefinition.h"
#import "DirectoryDefinition.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "AppSettingController.h"
#import "ServerAutoDiscoveryController.h"
#import "NotificationConstant.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"


@interface AppSettingsDefinition (Private)

@end

@implementation AppSettingsDefinition

static NSString *currentServerUrl = nil;
static NSMutableArray *settingsData = nil;
static NSString *unsavedChosenServerUrl = nil;

// Read appSettings infomation from file appSettings.plist in array.
+ (NSMutableArray *)getAppSettings {
	if (!settingsData) {
			settingsData = [[NSMutableArray alloc] initWithContentsOfFile:[DirectoryDefinition appSettingsFilePath]];
	}
	return settingsData;
}

// Refresh settingsData from appSettings.plist .
+ (void)reloadData {
	if (settingsData) {
		[settingsData release];
		settingsData = nil;
	}
	settingsData = [[NSMutableArray alloc] initWithContentsOfFile:[DirectoryDefinition appSettingsFilePath]];
}

// Reload settingsData from appSettings.plist for the test.
+ (void)reloadDataForTest {
	NSBundle *thisBundle = [NSBundle bundleForClass:[self class]];
	NSString *settingFilePath = [thisBundle pathForResource:@"appSettings" ofType:@"plist"];
	if (settingsData) {
		[settingsData release];
		settingsData = nil;
	}
	settingsData = [[NSMutableArray alloc] initWithContentsOfFile:settingFilePath];
}

// Get the specified section with index from appSettings.plist .
+(NSMutableDictionary *)getSectionWithIndex:(int)index {
	return [[self getAppSettings] objectAtIndex:index];
}

// Get the specified section's header with index from appSettings.plist .
+ (NSString *)getSectionHeaderWithIndex:(int)index{
	return [[[self getAppSettings] objectAtIndex:index] valueForKey:@"header"];
}

// Get the specified section's footer with index from appSettings.plist .
+ (NSString *)getSectionFooterWithIndex:(int)index{
	return [[[self getAppSettings] objectAtIndex:index] valueForKey:@"footer"];
}

// Get the map value of auto discovery boolean value.
+ (NSMutableDictionary *)getAutoDiscoveryDic {
	return (NSMutableDictionary *)[[self getSectionWithIndex:AUTO_DISCOVERY_SWITCH_INDEX] objectForKey:@"item"];
}

// Check if function of auto discovery is enabled.
+ (BOOL)isAutoDiscoveryEnable {
	return [[[self getAutoDiscoveryDic] objectForKey:@"value"] boolValue];
}

// Enable or disable auto discovery function.
+ (void)setAutoDiscovery:(BOOL)on {
	[[self getAutoDiscoveryDic] setValue:[NSNumber numberWithBool:on] forKey:@"value"];
}

// Get servers by auto discovery from appSettings.plist .
+ (NSMutableArray *)getAutoServers {
	return (NSMutableArray *)[[self getSectionWithIndex:AUTO_DISCOVERY_URLS_INDEX] objectForKey:@"servers"];
}

// Get servers by user input from appSettings.plist .
+ (NSMutableArray *)getCustomServers {
	return (NSMutableArray *)[[self getSectionWithIndex:CUSOMIZED_URLS_INDEX] objectForKey:@"servers"];
}

// Add specified server into array of auto servers.
+ (void)addAutoServer:(NSDictionary *)server {
	[[self getAutoServers] addObject:server];
}

// Clear the auto servers from appSettings.plist .
+ (void)removeAllAutoServer {
	[[self getAutoServers] removeAllObjects];
	[self writeToFile];
	NSLog(@"remove all auto server ,now auto server count is %d",[self getAutoServers].count);
}

// chosen panel identity
+ (NSMutableDictionary *)getPanelIdentityDic {
	return (NSMutableDictionary *)[[self getSectionWithIndex:PANEL_IDENTITY_INDEX] objectForKey:@"item"];
}

// Get security infomation from appSettings.plist .
+ (NSMutableDictionary *)getSecurityDic {
	return (NSMutableDictionary *)[[self getSectionWithIndex:SECURITY_INDEX] objectForKey:@"item"];
}

// use SSL or not
+ (BOOL)useSSL {
	return [[[self getSecurityDic] objectForKey:@"useSSL"] boolValue];
}

// Set use SSL or not
+ (void)setUseSSL:(BOOL)on {
	[[self getSecurityDic] setValue:[NSNumber numberWithBool:on] forKey:@"useSSL"];
}

// SSL port
+ (int)sslPort {
	return [[[self getSecurityDic] objectForKey:@"port"] intValue];
}

// Set SSL port
+ (void)setSslPort:(int)port {
	if (port <0 && port != DEFAULT_SSL_PORT){
		NSLog(@"negative port number");
		return;
	}
	if (port > 65535) {
		NSLog(@"port number too large");
		return;
	}
	[[self getSecurityDic] setValue:[NSNumber numberWithInt:port] forKey:@"port"];
}

// Save the appSettings infomation into appSettings.plist .
+ (void)writeToFile {
	if ([settingsData writeToFile:[DirectoryDefinition appSettingsFilePath] atomically:NO]) {	
		[self readServerUrlFromFile];
	}
}

// Read server url from file, if find it will set currentServerUrl value and return NO else return NO.
// after read you can get all the details through AppSettingsDefinition api.
+ (BOOL)readServerUrlFromFile {
	[self reloadData];
	
	NSString *serverUrl = nil;
	if ([self isAutoDiscoveryEnable]) {
		NSLog(@"auto enable");
		if ([self getAutoServers].count == 0) {
			NSLog(@"auto 0");
			return NO;
		} 
		
		NSLog(@"auto count = %d",[self getAutoServers].count);
		
		for (int i=0; i < [self getAutoServers].count; i++) {
			if ([[[[self getAutoServers] objectAtIndex:i] valueForKey:@"choose"] boolValue]) {
				serverUrl =  [[[self getAutoServers] objectAtIndex:i] valueForKey:@"url"];
				break;
			} 
		}		
		serverUrl = (serverUrl?serverUrl: [[[self getAutoServers] objectAtIndex:0] valueForKey:@"url"]);
	} else {
        // No autodiscovery, check the list of custom defined server, return the one selected or the first one if none selected
		if ([self getCustomServers].count == 0) {
			return NO;
		}
		
		for (int i=0; i < [self getCustomServers].count; i++) {
			if ([[[[self getCustomServers] objectAtIndex:i] valueForKey:@"choose"] boolValue]) {
				serverUrl =  [[[self getCustomServers] objectAtIndex:i] valueForKey:@"url"];
				break;
			}
		}
        // TODO - EBR : breaks if array empty
		serverUrl = (serverUrl?serverUrl: [[[self getCustomServers] objectAtIndex:0] valueForKey:@"url"]);
	}
	if (serverUrl) {
		[self setCurrentServerUrl:serverUrl];
		return YES;
	} else {
		currentServerUrl = nil;
		return NO;
	}
}

// Get current server url panel client use from appSettings.plist .
+ (NSString *)getCurrentServerUrl {
	if (currentServerUrl) {
		return currentServerUrl;
	} else {
		if ( [AppSettingsDefinition readServerUrlFromFile]) {
			return currentServerUrl;
		} 
	}
	return nil;
}

// Change current server url panel client use to specified url .
+ (void)setCurrentServerUrl:(NSString *)url {
	if(url) {
		[url retain];
		[currentServerUrl release];
		currentServerUrl = url;
	} else {
		[currentServerUrl release];
		currentServerUrl = @"";
	}
	
}

// Get panel identity current panel client use from appSettings.plist .
+ (NSString *)getCurrentPanelIdentity {
	return [[self getPanelIdentityDic] objectForKey:@"identity"];
}

// Get current conroller server url as unsavedChosenServerUrl
// or return value method setUnsavedChosenServerUrl set.
+ (NSString *)getUnsavedChosenServerUrl {
	if (!unsavedChosenServerUrl) {
		unsavedChosenServerUrl = [currentServerUrl copy];
	}
	return unsavedChosenServerUrl;
}

// Set server url as choosed controller server url but don't save into appSettings.plist .
+ (void)setUnsavedChosenServerUrl:(NSString *)url {
	[url retain];
	[unsavedChosenServerUrl release];
	unsavedChosenServerUrl = url;
}

@end
