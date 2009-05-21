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

@interface AppSettingsDefinition (Private)
- (void)startUpdate;
@end

@implementation AppSettingsDefinition

static NSString *currentServerUrl;
static NSMutableArray *settingsData;
static ServerAutoDiscoveryController *autoDiscovery;
static NSMutableArray *errors;


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
		[self reloadData];
		[self readServerUrlFromFile];
	}
}

//Read server url from file, if find it will set currentServerUrl value and return NO else return NO.
+ (BOOL)readServerUrlFromFile {
	if ([self isAutoDiscoveryEnable]) {
		NSLog(@"auto enable");
		if ([self getAutoServers].count == 0) {
			NSLog(@"auto 0");
			return NO;
		} 
		NSLog(@"auto count = %d",[self getAutoServers].count);
		if ( [self getAutoServers].count == 1) {
			NSString *url =  [[[self getAutoServers] objectAtIndex:0] valueForKey:@"url"];
			[self setCurrentServerUrl:url];
			return YES;
		} else {
			for (int i=0; i < [self getAutoServers].count; i++) {
				if ([[[[self getAutoServers] objectAtIndex:i] valueForKey:@"choose"] boolValue]) {
					NSString *url =  [[[self getAutoServers] objectAtIndex:i] valueForKey:@"url"];
					[self setCurrentServerUrl:url];
					return YES;
				} 
			}		
			[self setCurrentServerUrl:[[[self getAutoServers] objectAtIndex:0] valueForKey:@"url"]];
			return YES;
			NSLog(@"Find more than one Server....");
		}
	} else {
		if ([self getCustomServers].count == 0) {
			return NO;
		} else {
			for (int i=0; i < [self getCustomServers].count; i++) {
				if ([[[[self getCustomServers] objectAtIndex:i] valueForKey:@"choose"] boolValue]) {
					NSString *url =  [[[self getCustomServers] objectAtIndex:i] valueForKey:@"url"];
					[self setCurrentServerUrl:url];
					return YES;
				}
			}
			return NO;
		}
	}
}


+ (void)checkConfigAndUpdate {
	if (errors) {
		[errors release];
		errors = nil;
	} 
	errors = [[NSMutableArray alloc] init];
	NSLog(@"check config");
	
	[self reloadData];
	if ([AppSettingsDefinition readServerUrlFromFile]) {
		if ([CheckNetworkStaff checkWhetherNetworkAvailable] == kCheckNetworkStepOK) {
			if ([CheckNetworkStaff checkIPAddress] == kCheckNetworkStepOK) {
				int checkControllerStatus = [CheckNetworkStaff checkControllerAvailable];
				if ( checkControllerStatus == kCheckNetworkStepOK) {
					if ([CheckNetworkStaff checkXmlExist] != kCheckNetworkStepOK) {
						[errors addObject:@"Make sure the xml config file is in you server."];
					} else {
						[[Definition sharedDefinition] update];
						return;
					}
				} else
					if (checkControllerStatus == kControllerNotStarted) {
						if ([self isAutoDiscoveryEnable]) {
							[errors addObject:@"Make sure your server have been started."];
						} else {
							[errors addObject:@"Your server is not start or the server url which you configed is wrong."];
						}
					} else {
						[errors addObject:@"Can't find controller appliaction on your server."];
					}
			} else {
				[errors addObject:@"Your server address is wrong please check your settings"];
			}
		}  else {
			[errors addObject:@"Please connect your device to network"];
		}
		if (errors.count > 0) {
			[ViewHelper showAlertViewWithTitle:@"Error Occured" Message:[errors objectAtIndex:0]];
		}
		[[NSNotificationCenter defaultCenter] postNotificationName:DefinationNeedNotUpdate object:nil];
	} else {
		if ([self isAutoDiscoveryEnable]) {
			[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(afterFindServer) name:NotificationAfterFindServer object:nil];
			autoDiscovery = [[ServerAutoDiscoveryController alloc] init];
			[autoDiscovery findServer];
		} else {
			[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't find server url configuration. You can turn on auto-discovery or specify a server url in settings."];
			[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowSettingsView object:nil];
		}
	}
}

+ (void)afterFindServer {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:NotificationAfterFindServer object:nil];
	[self reloadData];
	if (autoDiscovery) {
		[autoDiscovery release];
	}
	
	NSLog([AppSettingsDefinition getCurrentServerUrl]);
	NSLog(@"after find server, find auto server %d",[AppSettingsDefinition getAutoServers].count);
	if ([AppSettingsDefinition getAutoServers].count > 0) {
		[self checkConfigAndUpdate];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't discovery the server, please make sure your server have been started or make sure iphone is in the save network service."];
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowSettingsView object:nil];
	}

}

- (void)startUpdate{
	
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
