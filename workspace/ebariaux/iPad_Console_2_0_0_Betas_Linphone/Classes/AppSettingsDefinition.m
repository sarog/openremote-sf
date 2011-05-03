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
#import "NotificationConstant.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"

#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface AppSettingsDefinition (Private)

@end

@implementation AppSettingsDefinition

static NSMutableArray *settingsData = nil;


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
	[settingsData writeToFile:[DirectoryDefinition appSettingsFilePath] atomically:NO];
}

@end
