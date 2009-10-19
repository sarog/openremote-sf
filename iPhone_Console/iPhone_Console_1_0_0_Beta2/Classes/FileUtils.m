//
//  FileUtils.m
//  openremote
//
//  Created by finalist on 2/24/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

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
