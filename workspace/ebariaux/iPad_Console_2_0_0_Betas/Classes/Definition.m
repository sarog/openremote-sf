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


#import "Definition.h"
#import "FileUtils.h"
#import "ServerDefinition.h"
#import "DirectoryDefinition.h"
#import "StringUtils.h"
#import "Group.h"
#import "Screen.h"
#import "Control.h"
#import "LayoutContainer.h"
#import "AbsoluteLayoutContainer.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "CheckNetwork.h"
#import "LocalLogic.h"

@interface Definition (Private)
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

static Definition *myInstance = nil;

@implementation Definition


@synthesize isUpdating, lastUpdateTime, groups, screens, labels, tabBar, localLogic, imageNames, loading, username, password;

- (id)init {			
	if (myInstance != nil) {
		[self release];
		[NSException raise:@"singletonClassError" format:@" Don't init singleton class %@"];
	} else if (self = [super init]) {
		myInstance = self; 
		groups = [[NSMutableArray alloc] init];
		screens = [[NSMutableArray alloc] init];
		labels = [[NSMutableArray alloc] init];
		imageNames = [[NSMutableArray alloc] init];
		//		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(reachabilityChanged:) name:@"kNetworkReachabilityChangedNotification" object:nil];
		
	}
	return myInstance;
}

+ (Definition *)sharedDefinition {
	@synchronized (self) {
		if (myInstance == nil) {
			[[Definition alloc] init];
		}
	}
	return myInstance;
}

- (Group *)findGroupById:(int)groupId {
	for (Group *g in groups) {
		if (g.groupId == groupId) {
			return [g retain];			
		}
	}
	return nil;
}

- (Screen *)findScreenById:(int)screenId {
	for (Screen *tempScreen in self.screens) {
		if (tempScreen.screenId == screenId) {
			NSLog(@"find screen screenId %d", screenId);
			return [tempScreen retain];
		}
	}
	return nil;
}

- (void)addGroup:(Group *)group {
	for (int i = 0; i < self.groups.count; i++) {
		Group *tempGroup = [self.groups objectAtIndex:i];
		if (tempGroup.groupId == group.groupId) {
			[self.groups replaceObjectAtIndex:i withObject:group];
			return;
		}
	}
	[self.groups addObject:[group retain]];
	[group release];
}

- (void)addScreen:(Screen *)screen {
	for (int i = 0; i < self.screens.count; i++) {
		Screen *tempScreen = [self.screens objectAtIndex:i];
		if (tempScreen.screenId == screen.screenId) {
			[self.screens replaceObjectAtIndex:i withObject:screen];
			return;
		}
	}
	[self.screens addObject:[screen retain]];
	[screen release];
}

- (void) addLabel:(Label *)label {
	for (int i = 0; i < self.labels.count; i++) {
		Label *tempLabel = [self.labels objectAtIndex:i];
		if (tempLabel.componentId == label.componentId) {
			[self.labels replaceObjectAtIndex:i withObject:label];
			return;
		}
	}
	[self.labels addObject:label];
	[label release];
}

- (Label *)findLabelById:(int)labelId {
	for (Label *tempLabel in self.labels) {
		if (tempLabel.componentId == labelId) {
			return [tempLabel retain];
		}
	}
	return nil;
}

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
	[imageName release];
}

- (void)parseXml {
	NSLog(@"start parse xml");
	
	[self clearPanelXMLData];
	
	for(int i = 1; i <= TWICE; i++) {
		NSData *data = [[NSData alloc] initWithContentsOfFile:[[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition panelXmlRESTUrl]]]];
		NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
		NSString *dataStr = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
		NSLog(@"%@",dataStr);
		[dataStr release];
		
		//Set delegate to self in order to parse next elements by itself
		[xmlParser setDelegate:self];
		
		
		//Calls parse method to start parse xml
		[xmlParser parse];
		

		NSLog(@"groups count = %d",[groups count]);
		NSLog(@"screens count = %d",[screens count]);
		NSLog(@"xml parse done");
		[data release];
		[xmlParser release];
	}
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

#pragma mark delegate method of NSXMLParser
//Delegate method when find a element start
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"screen"]) {
		NSLog(@"start at screen");
		Screen *screen = [[Screen alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
//		[screens addObject:[screen retain]];
//		[screen release];
		[self addScreen:screen];
	} else if ([elementName isEqualToString:@"group"]) {
		NSLog(@"start at group");
		Group *group = [[Group alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
//		[groups addObject:group];
//		[group release];
		[self addGroup:group];
	} else if ([elementName isEqualToString:@"tabbar"]) {
		NSLog(@"start at tabbar");
		tabBar = [[TabBar alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	} else if ([elementName isEqualToString:@"locallogic"]) {
		NSLog(@"start at locallogic");
		localLogic = [[LocalLogic alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	}
}

/**
 * When we find an openremote end element, restore the original XML parser delegate.
 */
- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName {
	if ([elementName isEqualToString:@"openremote"]) {
		NSLog(@"End parse openremote");
		//		[parser setDelegate:nil];
		// 		[parser setDelegate:xmlParserParentDelegate];
		
	}
}


- (void)addImageName:(NSString *)imageName {
	for (NSString *name in imageNames) {
		// avoid duplicated
		if ([name isEqualToString:imageName]) {
			return;
		}
	}
	if (imageName) {
		[[self imageNames] addObject:imageName];	
	}
}

- (void)downloadImages {
	@try {
		[CheckNetwork checkWhetherNetworkAvailable];
		
		for (NSString *imageName in imageNames) {
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

- (void)clearPanelXMLData {
	if (groups) {
		[groups removeAllObjects];
	}
	if (screens) {
		[screens removeAllObjects];
	}
	if (labels) {
		[labels removeAllObjects];
	}
	if (imageNames) {
		[imageNames removeAllObjects];
	}
	if (tabBar) {
		tabBar = nil;
	}
}

#pragma mark override methods to keep this class instance
- (id)retain {
	return self;
}

- (unsigned)retainCount
{
	return UINT_MAX;  //denotes an object that cannot be released	
}

- (void)release {
	//do nothing
}

- (id)autorelease
{
	return self;
}


@end
