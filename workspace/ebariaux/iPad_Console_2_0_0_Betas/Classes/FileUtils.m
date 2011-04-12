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
#import "CredentialUtil.h"
#import "URLConnectionHelper.h"

#define DOWNLOAD_TIMEOUT_INTERVAL 20

@interface FileUtils (Private)
	
+ (void)makeSurePathExists:(NSString *)path;

@end


@implementation FileUtils

NSFileManager *fileManager;

+ (void)initialize {
	if (self == [FileUtils class]) {
		fileManager = [NSFileManager defaultManager];
	}
}

+ (void)downloadFromURL:(NSString *)URLString  path:(NSString *)p {
	[self makeSurePathExists:p];
	NSError *error = nil;
	NSURLResponse *response = nil;
	NSString *encodedUrl = (NSString *)CFURLCreateStringByAddingPercentEscapes(NULL, (CFStringRef)URLString, NULL, (CFStringRef)@"", kCFStringEncodingUTF8);
    NSURL *url = [[NSURL alloc] initWithString:encodedUrl];
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:DOWNLOAD_TIMEOUT_INTERVAL];
    [url release];
	[CredentialUtil addCredentialToNSMutableURLRequest:request];
	NSData *data = [[[URLConnectionHelper alloc] init] sendSynchronousRequest:request returningResponse:&response error:&error];
	
	if (error) {
		NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:[NSString stringWithFormat:@"[%d]%@",[httpResp statusCode], [error localizedDescription]] message:URLString delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alert show];
		[alert release];
		return;
	}
	
	NSString *fileName = [StringUtils parsefileNameFromString:URLString];
	NSString *filePathToSave = [p stringByAppendingPathComponent:fileName];
	
	//delete the file
	[fileManager removeItemAtPath:filePathToSave error:NULL];
	
	[fileManager createFileAtPath:filePathToSave contents:data attributes:nil];
	[request release];	
}

+ (BOOL)checkFileExistsWithPath:(NSString *)path {
	return [fileManager fileExistsAtPath:path];
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
