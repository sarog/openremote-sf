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


#import "FileUtils.h"
#import "DirectoryDefinition.h"
#import "StringUtils.h"

@interface FileUtils (Private)
	
+ (void)makeSurePathExists:(NSString *)path;

@end


@implementation FileUtils

NSFileManager *fileManager;

+ (void)initialize
{
	if (self == [FileUtils class]) {
		fileManager = [NSFileManager defaultManager];
	}
}

+ (void)downloadFromURL:(NSString *) url  path:(NSString *)p {
	[self makeSurePathExists:p];
	NSError *error = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[[NSURL alloc]initWithString:url] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:NULL error:&error];
	
	if (error) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alert show];
		[alert release];
		return;
	}
	
	NSString *fileName = [StringUtils parsefileNameFromString:url];
	NSString *filePathToSave = [p stringByAppendingPathComponent:fileName];
	
	//delete the file
	[fileManager removeItemAtPath:filePathToSave error:NULL];
	
	[fileManager createFileAtPath:filePathToSave contents:data attributes:nil];
	[request release];
}

+ (void)makeSurePathExists:(NSString *)path {
	if (![fileManager fileExistsAtPath:path]) {
		[fileManager createDirectoryAtPath:path  withIntermediateDirectories:YES attributes:nil error:NULL];
	}
}

+ (void)deleteFolderWithPath:(NSString *) path {
	if ([fileManager fileExistsAtPath:path]) {
		[fileManager removeItemAtPath:path error:NULL];
	}
}

@end
