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
#import <Foundation/Foundation.h>

/**
 * Provide the way to access some special directories.
 */
@interface DirectoryDefinition : NSObject {
}

/**
 * Returns path to documents directory.
 */
+ (NSString *)applicationDocumentsDirectory;


/**
 * Get the directory of cache folder in handset.
 */
+ (NSString *)cacheFolder;

/**
 * Get the directory of image cache folder in handset.
 * It's the sub directory of cacheFolder.
 */
+ (NSString *)imageCacheFolder;

/**
 * Get the directory of xml cache folder in handset.
 * It's the sub directory of cacheFolder.
 */
+ (NSString *)xmlCacheFolder;

+ (NSString *)settingsDefinitionFilePath;
@end
