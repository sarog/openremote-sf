//
//  Definition.m
//  openremote
//
//  Created by wei allen on 09-2-20.
//  Copyright 2009 finalist. All rights reserved.
//

#import "Definition.h"
#import "FileUtils.h"
#import "ServerDefinition.h"
#import "DirectoryDefinition.h"
#import "StringUtils.h"
#import "Activity.h"
#import "Screen.h"
#import "Control.h"
#import "ViewHelper.h"
#import "Reachability.h"

@interface Definition (Private)
- (void) postNotificationToMainThread:(NSString *)notificationName;
- (void)downloadXml;
- (void)parseXMLData;
- (void)downloadImages;
- (void)downloadImageWithName:(NSString *)imageName;
- (BOOL)checkWhetherNetworkAvailable;
- (BOOL)checkWhetherControllerAvailable;
- (BOOL)checkWhetherXmlExist;
- (void)useLocalCacheDirectly;
- (void)addDownloadImageOperationWithImageName:(NSString *)imageName;
@end

static Definition *myInstance = nil;

@implementation Definition

NSString *const DefinationUpdateDidFinishedNotification = @"updateDidFinishedNotification";
NSString *const DefinationNeedNotUpdate = @"needNotUpdateNotification";

@synthesize isUpdating,lastUpdateTime,activities;

- (id)init {			
	if (myInstance != nil) {
		[self release];
		[NSException raise:@"singletonClassError" format:@" Don't init singleton class %@"];
	} else if (self == [super init]) {
		myInstance = self; 
		updateOperationQueue = [[NSOperationQueue alloc] init];
		updateOperation = [[[NSInvocationOperation alloc] initWithTarget:self selector:@selector(postNotificationToMainThread:) object:DefinationUpdateDidFinishedNotification] autorelease];
		isUpdating = NO;
		activities = [[NSMutableArray alloc] init];
		
		[[Reachability sharedReachability] setHostName:@"www.google.com"];
		
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


- (BOOL)isDataReady {
	//need to implement
	return YES;
}


-(BOOL)checkWhetherNetworkAvailable {
	if ([[Reachability sharedReachability] remoteHostStatus] == NotReachable) {
		return NO;
	}
	return YES;
}

- (BOOL)checkWhetherControllerAvailable {
	
	NSError *error = nil;
	NSHTTPURLResponse *resp = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[ServerDefinition serverUrl]] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	[NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:&error];
	
	[request release];
	if (error ) {
		[ViewHelper showAlertViewWithTitle:@"Can't Connect to Controller" Message:@"Make sure your server has been started and your configuration of server url is correct."];
		return NO;
	} else if ([resp statusCode] != 200){
		[ViewHelper showAlertViewWithTitle:@"Can't Connect to Controller" Message:@"We detect your server has been started, but can't find controller application on the server, Make sure your url is correct."];
		return NO;
	}
	return YES;
}

- (BOOL)checkWhetherXmlExist {
	NSHTTPURLResponse *resp = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[ServerDefinition sampleXmlUrl]] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	[NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:NULL];
	
	[request release];
	if ([resp statusCode] != 200 ){
		[ViewHelper showAlertViewWithTitle:@"Can't Find iphone.xml" Message:@"Make sure you have already put the iphone.xml into the controller."];
		return NO;
	}
	return YES;
}


- (void)update {
	if ([ServerDefinition serverUrl] == nil) {
		[ViewHelper showAlertViewWithTitle:@"No Config Information" Message:@"There is no config information, You can modify it in your iphone 'Settings'."];
		[self postNotificationToMainThread:DefinationNeedNotUpdate];
		return;
	}
	if (isUpdating) {
		return;
	}
	
	isUpdating = YES;
	
	if (![self checkWhetherNetworkAvailable]) {
		if ([self isDataReady]) {
			[ViewHelper showAlertViewWithTitle:@"No Network" Message:@"There is no network, you can use local cache." ];
			[self	useLocalCacheDirectly];
		} else {
			[ViewHelper showAlertViewWithTitle:@"No Network" Message:@"You can't start up. Because application can't connect to server and you don't have local cache.So you can check your network and restart the application." ];
		}
		[self postNotificationToMainThread:DefinationNeedNotUpdate];
	} else {
		
		if ([self checkWhetherControllerAvailable] && [self checkWhetherXmlExist]) {
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
		} else {
			[self postNotificationToMainThread:DefinationNeedNotUpdate];
		}
	}
}

- (void)useLocalCacheDirectly {
	[self parseXMLData];
}

#pragma mark Operation Tasks
- (void)downloadXml {
	NSLog(@"start download xml");
	[FileUtils downloadFromURL:[ServerDefinition sampleXmlUrl]  path:[DirectoryDefinition xmlCacheFolder]];
	NSLog(@"xml file downloaded.");
}

- (void)downloadImageWithName:(NSString *)imageName {
	NSLog([@"start download image " stringByAppendingString:imageName]);
	[FileUtils downloadFromURL:[[ServerDefinition imageUrl] stringByAppendingPathComponent:imageName]  path:[DirectoryDefinition imageCacheFolder]];
}

//Parses xml
- (void)parseXMLData {	
	NSLog(@"start parse xml");
	NSData *data = [[NSData alloc] initWithContentsOfFile:[[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition sampleXmlUrl]]]];
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	NSLog(@"%@",data);
	
	//Set delegate to self in order to parse next elements by itself
	[xmlParser setDelegate:self];

	//Calls parse method to start parse xml
	[xmlParser parse];
	NSLog(@"xml parse done");
	
	[self downloadImages];
	NSLog(@"images download done");
	
	[data release];
	[xmlParser release];
	
	//after parse the xml all the Operation have already added to OperationQuere and addDependency to updateOperation
	[updateOperationQueue addOperation:updateOperation];
	NSLog(@"parse xml end element screens and add updateOperation to queue");
}

#pragma mark delegate method of NSXMLParser
//Delegate method when find a element start
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	
	if ([elementName isEqualToString:@"activity"]) {		
		//To let Activity to parse the
		NSLog(@"Start at activity");
		Activity *activity = [[Activity alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		[activities addObject:activity];
		[activity release];
	}
}

- (void)downloadImages {
	if  ([self checkWhetherNetworkAvailable]) {
		for (Activity *myActivity in activities) {
			if (myActivity.icon) {
				[self addDownloadImageOperationWithImageName:myActivity.icon];
			}
			for (Screen *myScreen in myActivity.screens) {
				if (myScreen.icon) {
					[self addDownloadImageOperationWithImageName:myScreen.icon];
				}				
				for (Control *myControl in myScreen.controls) {
					if (myControl.icon) {
						[self addDownloadImageOperationWithImageName:myControl.icon];
					}
				}
				
			}
		}
	}
}

- (void)addDownloadImageOperationWithImageName:(NSString *)imageName {
	NSInvocationOperation *downloadControlIconOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(downloadImageWithName:) object:imageName];
	[updateOperation addDependency:downloadControlIconOperation];
	[updateOperationQueue addOperation:downloadControlIconOperation];
	NSLog(@"add download control image operation");
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
