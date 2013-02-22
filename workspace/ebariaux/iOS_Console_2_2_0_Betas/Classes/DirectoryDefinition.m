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
#import "DirectoryDefinition.h"


@implementation DirectoryDefinition

// Get cache folder in apple handset.
+ (NSString *)cacheFolder {
	NSArray *paths = NSSearchPathForDirectoriesInDomains(NSCachesDirectory, NSUserDomainMask, YES);
	return [paths objectAtIndex:0];
}

+ (NSString *)applicationDocumentsDirectory {
	return [NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES) lastObject];
}

// Get image cache folder. It bases on cache folder of handset.
+ (NSString *)imageCacheFolder{
	return [[self cacheFolder] stringByAppendingPathComponent:@"image"];
}

// Get xml cache folder. It bases on cache folder of handset.
+ (NSString *)xmlCacheFolder {
	return [[self cacheFolder] stringByAppendingPathComponent:@"xml"];
}

+ (NSString *)settingsDefinitionFilePath
{
    return [[NSBundle mainBundle] pathForResource:@"appSettings" ofType:@"plist"];
}

@end
