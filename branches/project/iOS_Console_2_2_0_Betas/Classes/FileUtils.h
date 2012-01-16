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
#import <UIKit/UIKit.h>

// DENNIS: Maybe you can add these helper methods as an Objective-C category to the NSFileManager class? (Make sure to use a precise and unique name for your methods.)
/**
 * Uitls about file operations.
 */
@interface FileUtils : NSObject {
}

/**
 * Download resouce file from specified url and save into the specified path.
 */
+ (void) downloadFromURL:(NSString *)URLString  path:(NSString *)p;

/**
 * Delete folder with specified path.
 */
+ (void)deleteFolderWithPath:(NSString *) path;

/**
 * Check if the file path specified exists.
 */
+ (BOOL)checkFileExistsWithPath:(NSString *)path;

@end
