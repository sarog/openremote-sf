/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import "FileUtils.h"
#import "DirectoryDefinition.h"
#import "StringUtils.h"
#import "CredentialUtil.h"
#import "URLConnectionHelper.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

#define DOWNLOAD_TIMEOUT_INTERVAL 60

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
    [encodedUrl release];
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:DOWNLOAD_TIMEOUT_INTERVAL];
    [url release];
    ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
	[CredentialUtil addCredentialToNSMutableURLRequest:request forController:activeController];
    
    URLConnectionHelper *connectionHelper = [[URLConnectionHelper alloc] init];
	NSData *data = [connectionHelper sendSynchronousRequest:request returningResponse:&response error:&error];
    [request release];	
    [connectionHelper release];

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
