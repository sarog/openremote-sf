//
//  DirectoryDefinition.m
//  openremote
//
//  Created by finalist on 2/24/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "DirectoryDefinition.h"


@implementation DirectoryDefinition

+ (NSString *)cacheFolder {
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
	return [[paths objectAtIndex:0] retain];
		
}
+ (NSString *)imageCacheFolder{
	return [[self cacheFolder] stringByAppendingPathComponent:@"image"];
	
}
+ (NSString *)xmlCacheFolder {
	return [[self cacheFolder] stringByAppendingPathComponent:@"xml"];
}

+ (NSString *)appSettingsFilePath{
	// Check for data in Documents directory. Copy default appData.plist to Documents if not found.
	NSFileManager *fileManager = [NSFileManager defaultManager];
	NSError *error;
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex:0];
	NSString *pathToUserCopyOfPlist = [documentsDirectory stringByAppendingPathComponent:@"appSettings.plist"];
	NSLog(pathToUserCopyOfPlist);
	if ([fileManager fileExistsAtPath:pathToUserCopyOfPlist] == NO) {
		NSLog(@"don't found the app settings");
		NSString *pathToDefaultPlist = [[NSBundle mainBundle] pathForResource:@"appSettings" ofType:@"plist"];
		NSLog(@"pathToDefaultPlist");
		if ([fileManager copyItemAtPath:pathToDefaultPlist toPath:pathToUserCopyOfPlist error:&error] == NO) {
			NSAssert1(0, @"Failed to copy data with error message '%@'.", [error localizedDescription]);
			NSLog(@"can't find it");
		}
	}
	return pathToUserCopyOfPlist;
}

@end
