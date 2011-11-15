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
#import "DefinitionManager.h"
#import "FileUtils.h"
#import "ServerDefinition.h"
#import "DirectoryDefinition.h"
#import "StringUtils.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "CheckNetwork.h"
#import "PanelDefinitionParser.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "Definition.h"

@interface DefinitionManager ()

- (void)postNotificationToMainThread:(NSString *)notificationName;
- (void)downloadXml;
- (void)parseXMLData;
- (void)downloadImages;
- (void)downloadImageWithName:(NSString *)imageName;
- (void)addDownloadImageOperationWithImageName:(NSString *)imageName;
- (BOOL)canUseLocalCache;
- (void)parseXml;
- (void)changeLoadingMessage:(NSString *)msg;

@end


@implementation DefinitionManager

// For now, take all the functionality that is about loading / triggering parsing / caching ... the definition
// from the Definition class and bring it here

- (BOOL)isDataReady {
	//need to implement
	return YES;
}

- (BOOL)canUseLocalCache {
	return [[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition panelXmlRESTUrl]]]];
}


- (void)update {
	if (updateOperationQueue) {
		[updateOperationQueue release];
	}
	updateOperationQueue = [[NSOperationQueue alloc] init];
	if (updateOperation) {
		[updateOperation release];
	}
	updateOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(postNotificationToMainThread:) object:DefinitionUpdateDidFinishNotification];
	isUpdating = NO;
	
	
	if (isUpdating) {
		return;
	}
	isUpdating = YES;
	
	if (lastUpdateTime) {
		[lastUpdateTime release];
	}
	
	lastUpdateTime = [[NSDate date] retain];
	
	//define Operations
	NSInvocationOperation *downloadXmlOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(downloadXml) object:nil];
	NSInvocationOperation *parseXmlOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(parseXMLData) object:nil];
	
	//define Operation dependency and add it to OperationQueue
	[parseXmlOperation addDependency:downloadXmlOperation];
	[updateOperationQueue addOperation:downloadXmlOperation];
	[updateOperationQueue addOperation:parseXmlOperation];
	
	[updateOperation addDependency:parseXmlOperation];
	
	[downloadXmlOperation release];
	[parseXmlOperation release];
    
    // TODO - EBR : check what needs to be added to queue e.g. updateOperation is not here
    // updateOperation added to queue in parseXMLData method, why ?
}

- (void)changeLoadingMessage:(NSString *)msg {
	if (loading) {
		//[loading setText:msg];
	}
}

- (void)useLocalCacheDirectly {
	if ([self canUseLocalCache]) {
		[self parseXml];
	} else {
		//		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't find local cache, you need to connect network and retry."];
		[[NSNotificationCenter defaultCenter] postNotificationName:DefinationNeedNotUpdate object:nil];
	}
	
}

#pragma mark Operation Tasks
- (void)downloadXml {
	NSLog(@"start download xml");
	[self changeLoadingMessage:@"download panel.xml ..."];
	NSLog(@"download panel.xml from %@",[ServerDefinition panelXmlRESTUrl]);
	[FileUtils downloadFromURL:[ServerDefinition panelXmlRESTUrl]  path:[DirectoryDefinition xmlCacheFolder]];
	NSLog(@"xml file downloaded.");
}


- (void)downloadImageIgnoreCacheWithName:(NSString *)imageName {
	NSString *msg = [[NSMutableString alloc] initWithFormat:@"download %@...", imageName];
	[self changeLoadingMessage:msg];
	NSLog(@"%@", msg);
	[FileUtils downloadFromURL:[[ServerDefinition imageUrl] stringByAppendingPathComponent:imageName]  path:[DirectoryDefinition imageCacheFolder]];
	[imageName release];
	[msg release];
}

- (void)downloadImageWithName:(NSString *)imageName {
	NSString *path = [[DirectoryDefinition imageCacheFolder] stringByAppendingPathComponent:imageName];
	if ([FileUtils checkFileExistsWithPath:path] == NO) {
		NSString *msg = [[NSMutableString alloc] initWithFormat:@"download %@...", imageName];
		[self changeLoadingMessage:msg];
		NSLog(@"%@", msg);
		[FileUtils downloadFromURL:[[ServerDefinition imageUrl] stringByAppendingPathComponent:imageName] path:[DirectoryDefinition imageCacheFolder]];
		[msg release];
	}
}

- (void)parsePanelConfigurationFileAtPath:(NSString *)configurationFilePath
{
    PanelDefinitionParser *parser = [[PanelDefinitionParser alloc] init];
    NSData *data = [[NSData alloc] initWithContentsOfFile:configurationFilePath];
    [[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition = [parser parseDefinitionFromXML:data];
    [data release];
    [parser release];
}

- (void)parseXml
{
    [self parsePanelConfigurationFileAtPath:[[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition panelXmlRESTUrl]]]];
}

//Parses xml
- (void)parseXMLData {	
	[self parseXml];
	[self downloadImages];
	NSLog(@"images download done");
	
	//after parse the xml all the Operation have already added to OperationQuere and addDependency to updateOperation
	[updateOperationQueue addOperation:updateOperation];
	NSLog(@"parse xml end element screens and add updateOperation to queue");
}

- (void)downloadImages {
	@try {
        // TODO: why that check at this stage
		[CheckNetwork checkWhetherNetworkAvailable];
		
		for (NSString *imageName in [[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedController.definition.imageNames) {
			if (imageName) {
				[self addDownloadImageOperationWithImageName:imageName];
			}				
		}
		
	}
	@catch (NSException * e) {
		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't download image from Server, there is no network."];
	}		
}

- (void)addDownloadImageOperationWithImageName:(NSString *)imageName {
	NSInvocationOperation *downloadControlIconOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(downloadImageWithName:) object:imageName];
	[updateOperation addDependency:downloadControlIconOperation];
	[updateOperationQueue addOperation:downloadControlIconOperation];
	[downloadControlIconOperation release];
}


//Shows alertView when url connection failtrue
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"ERROR OCCUR" message:error.localizedDescription  delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert show];
	[alert release];
}

#pragma mark post notification 
//post the notification to Main thread
- (void) postNotificationToMainThread:(NSString *)notificationName {
	NSLog(@"start post notification to main thread");
	[self performSelectorOnMainThread:@selector(postNotification:) withObject:notificationName waitUntilDone:NO];
	
	isUpdating = NO;
}

//create a NSNotification and add it to NSNotificationQueue
- (void) postNotification:(NSString *)notificationName {
	[[NSNotificationCenter defaultCenter] postNotificationName:notificationName object:self ];
	NSLog(@"post nofication done");
}

@synthesize isUpdating, lastUpdateTime, loading;

@end