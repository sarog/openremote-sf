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

#import <Foundation/Foundation.h>

/**
 * Provide the way to access some special directories.
 */
@interface DirectoryDefinition : NSObject {
}

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

/**
 * Get the directory of appSettings.plist in handset.
 * It's in the directory of document directory in handset.
 */
+ (NSString *)appSettingsFilePath;

/**
 * Get the directory of info.plist .
 */
+ (NSString *)infoFilePath;

@end
