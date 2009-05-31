//
//  AppSettingsDefinition.m
//  openremote
//
//  Created by finalist on 5/15/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

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

static NSString *currentServerUrl;
static NSMutableArray *settingsData;
static ServerAutoDiscoveryController *autoDiscovery;

static int retryTimes = 0;

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
	NSLog(@"remove all auto server ,now auto server count is %d",[self getAutoServers].count);
}
+ (void)writeToFile {
	if ([settingsData writeToFile:[DirectoryDefinition appSettingsFilePath] atomically:NO]) {	
		[self readServerUrlFromFile:NULL];
	}
}

//Read server url from file, if find it will set currentServerUrl value and return NO else return NO.
+ (BOOL)readServerUrlFromFile:(NSError **)error {
	[self reloadData];
	
	NSString *serverUrl = nil;
	if ([self isAutoDiscoveryEnable]) {
		NSLog(@"auto enable");
		if ([self getAutoServers].count == 0) {
			NSLog(@"auto 0");
			*error =  [[[NSError alloc] initWithDomain:@"readServerUrlFromFileError" code:0 userInfo:nil] autorelease];
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
			*error =  [[[NSError alloc] initWithDomain:@"readServerUrlFromFileError" code:0 userInfo:nil] autorelease];
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
		*error =  [[[NSError alloc] initWithDomain:@"readServerUrlFromFileError" code:0 userInfo:nil] autorelease];
		return NO;
	}
}


+ (void)checkConfigAndUpdate {
	NSLog(@"check config");
	NSError *readServerUrlError;
	@try {
		[AppSettingsDefinition readServerUrlFromFile:&readServerUrlError];
		if (readServerUrlError) {
			if ([self isAutoDiscoveryEnable]) {
				[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(afterFindServer) name:NotificationAfterFindServer object:nil];
				[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(findServerFail) name:NotificationFindServerFail object:nil];
				if (autoDiscovery) {
					[autoDiscovery release];
					autoDiscovery = nil;
				}
				autoDiscovery = [[ServerAutoDiscoveryController alloc] init];
				[autoDiscovery findServer];
				retryTimes = retryTimes + 1;
			} else {
				[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't find server url configuration. You can turn on auto-discovery or specify a server url in settings."];
				[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowSettingsView object:nil];
			}			
		} else {
			[CheckNetworkStaff checkAll];
			[[Definition sharedDefinition] update];
		}
	}
	@catch (CheckNetworkStaffException *e) {
		NSLog(@"CheckNetworkStaffException %@",e.message);
		if (retryTimes == 0) {
			if ([self isAutoDiscoveryEnable]) {
					NSLog(@"retry 1 time.");
					[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(afterFindServer) name:NotificationAfterFindServer object:nil];
					[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(findServerFail) name:NotificationFindServerFail object:nil];
					if (autoDiscovery) {
						[autoDiscovery release];
						autoDiscovery = nil;
					}
					autoDiscovery = [[ServerAutoDiscoveryController alloc] init];
					[autoDiscovery findServer];
					retryTimes = retryTimes + 1;
			}
		} else {
			[ViewHelper showAlertViewWithTitle:e.title Message:e.message];
			[[NSNotificationCenter defaultCenter] postNotificationName:DefinationNeedNotUpdate object:nil];
		}
		
	}	
}

+ (void)afterFindServer {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:NotificationAfterFindServer object:nil];
	[self reloadData];
	if (autoDiscovery) {
		[autoDiscovery release];
		autoDiscovery = nil;
	}
	
	NSLog([AppSettingsDefinition getCurrentServerUrl]);
	NSLog(@"after find server, find auto server %d",[AppSettingsDefinition getAutoServers].count);
	if ([AppSettingsDefinition getAutoServers].count > 0) {
		[self checkConfigAndUpdate];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't find Server, please make sure you are under the same network service with Server Or make sure you have been started your server."];
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowSettingsView object:nil];
	}

}

+ (void)findServerFail {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:NotificationFindServerFail object:nil];
	if (autoDiscovery) {
		[autoDiscovery release];
		autoDiscovery = nil;
	}
	if (retryTimes != 0) {
		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't discover the server, may be your server haven't been started or your iphone is not under the the same network service with Server."];
		[[Definition sharedDefinition] useLocalCacheDirectly];
	}
		
}

+ (NSString *)getCurrentServerUrl {
	return currentServerUrl;
}

+ (void)setCurrentServerUrl:(NSString *)url {
	[url retain];
	[currentServerUrl release];
	currentServerUrl = url;
}

@end
