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
#import "NotificationConstant.h"
#import "CheckNetworkStaff.h"

@interface Definition (Private)
- (void) postNotificationToMainThread:(NSString *)notificationName;
- (void)downloadXml;
- (void)parseXMLData;
- (void)downloadImages;
- (void)downloadImageWithName:(NSString *)imageName;
- (void)addDownloadImageOperationWithImageName:(NSString *)imageName;
@end

static Definition *myInstance = nil;

@implementation Definition


@synthesize isUpdating,lastUpdateTime,activities;

- (id)init {			
	if (myInstance != nil) {
		[self release];
		[NSException raise:@"singletonClassError" format:@" Don't init singleton class %@"];
	} else if (self == [super init]) {
		myInstance = self; 
				
				
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



- (void)update {
	if (updateOperationQueue) {
		[updateOperationQueue release];
	}
	updateOperationQueue = [[NSOperationQueue alloc] init];
	if (updateOperation) {
		[updateOperation release];
	}
	updateOperation = [[NSInvocationOperation alloc] initWithTarget:self selector:@selector(postNotificationToMainThread:) object:DefinationUpdateDidFinishedNotification];
	isUpdating = NO;
	if (activities) {
		[activities release];
	}
	activities = [[NSMutableArray alloc] init];

	
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
}

- (void)useLocalCacheDirectly {
	if ([[NSFileManager defaultManager] fileExistsAtPath:[[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition sampleXmlUrl]]]]) {
		[self parseXMLData];
	} else {
//		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't find local cache, you need to connect network and retry."];
		[[NSNotificationCenter defaultCenter] postNotificationName:DefinationNeedNotUpdate object:nil];
	}
	
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
	@try {
		[CheckNetworkStaff checkWhetherNetworkAvailable];
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
	@catch (NSException * e) {
		[ViewHelper showAlertViewWithTitle:@"Error" Message:@"Can't download image from Server, there is not network."];
	}		for (Activity *myActivity in activities) {
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
