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
#import "CheckNetworkStaff.h"
#import "CheckNetworkStaffException.h"

@interface AppSettingsDefinition (Private)

@end

@implementation AppSettingsDefinition

static NSString *currentServerUrl = nil;
static NSMutableArray *settingsData = nil;

+ (NSMutableArray *)getAppSettings {
	if (!settingsData) {
			settingsData = [[NSMutableArray alloc] initWithContentsOfFile:[DirectoryDefinition appSettingsFilePath]];
	}
	return settingsData;
}

+ (void)reloadData {
	if (settingsData) {
		[settingsData release];
		settingsData = nil;
	}
	settingsData = [[NSMutableArray alloc] initWithContentsOfFile:[DirectoryDefinition appSettingsFilePath]];
}


+(NSMutableDictionary *)getSectionWithIndex:(int)index {
	return [[self getAppSettings] objectAtIndex:index];
}
+ (NSString *)getSectionHeaderWithIndex:(int)index{
	return [[[self getAppSettings] objectAtIndex:index] valueForKey:@"header"];
}
+ (NSString *)getSectionFooterWithIndex:(int)index{
	return [[[self getAppSettings] objectAtIndex:index] valueForKey:@"footer"];
}
+ (NSMutableDictionary *)getAutoDiscoveryDic {
	return (NSMutableDictionary *)[[self getSectionWithIndex:0] objectForKey:@"item"];
}
+ (BOOL)isAutoDiscoveryEnable {
	return [[[self getAutoDiscoveryDic] objectForKey:@"value"] boolValue];
}

+ (void)setAutoDiscovery:(BOOL)on {
	[[self getAutoDiscoveryDic] setValue:[NSNumber numberWithBool:on] forKey:@"value"];
}

+ (NSMutableArray *)getAutoServers {
	return (NSMutableArray *)[[self getSectionWithIndex:1] objectForKey:@"servers"];
}

+ (NSMutableArray *)getCustomServers {
	return (NSMutableArray *)[[self getSectionWithIndex:2] objectForKey:@"servers"];
}

+ (void)addAutoServer:(NSDictionary *)server {
	[[self getAutoServers] addObject:server];
}

+ (void)removeAllAutoServer {
	[[self getAutoServers] removeAllObjects];
	[self writeToFile];
	NSLog(@"remove all auto server ,now auto server count is %d",[self getAutoServers].count);
}
+ (void)writeToFile {
	if ([settingsData writeToFile:[DirectoryDefinition appSettingsFilePath] atomically:NO]) {	
		[self readServerUrlFromFile];
	}
}

//Read server url from file, if find it will set currentServerUrl value and return NO else return NO.
// after read you can get all the details through AppSettingsDefinition api.
+ (BOOL)readServerUrlFromFile{
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
			} 
		}		
		serverUrl = (serverUrl?serverUrl: [[[self getAutoServers] objectAtIndex:0] valueForKey:@"url"]);
	} else {
		if ([self getCustomServers].count == 0) {
			return NO;
		}
		
		for (int i=0; i < [self getCustomServers].count; i++) {
			if ([[[[self getCustomServers] objectAtIndex:i] valueForKey:@"choose"] boolValue]) {
				serverUrl =  [[[self getCustomServers] objectAtIndex:i] valueForKey:@"url"];
			}
		}
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

+ (void)setCurrentServerUrl:(NSString *)url {
	[url retain];
	[currentServerUrl release];
	currentServerUrl = url;
}

@end
