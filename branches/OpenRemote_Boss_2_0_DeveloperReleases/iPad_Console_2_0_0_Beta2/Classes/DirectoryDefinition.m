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

#import "DirectoryDefinition.h"


@implementation DirectoryDefinition

// Get cache folder in apple handset.
+ (NSString *)cacheFolder {
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
	return [paths objectAtIndex:0];
		
}

// Get image cache folder. It bases on cache folder of handset.
+ (NSString *)imageCacheFolder{
	return [[self cacheFolder] stringByAppendingPathComponent:@"image"];
	
}

// Get xml cache folder. It bases on cache folder of handset.
+ (NSString *)xmlCacheFolder {
	return [[self cacheFolder] stringByAppendingPathComponent:@"xml"];
}

// Get directory of appSettings.plist .
+ (NSString *)appSettingsFilePath{
	// Check for data in Documents directory. Copy default appData.plist to Documents if not found.
	NSFileManager *fileManager = [NSFileManager defaultManager];
	NSError *error;
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [paths objectAtIndex:0];
	NSString *pathToUserCopyOfPlist = [documentsDirectory stringByAppendingPathComponent:@"appSettings.plist"];
	NSLog(@"%@", pathToUserCopyOfPlist);
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

// Get directory of file info.plist .
+ (NSString *)infoFilePath {
	NSString *pathToDefaultPlist = [[NSBundle mainBundle] pathForResource:@"Info" ofType:@"plist"];
	return pathToDefaultPlist;
}

@end
