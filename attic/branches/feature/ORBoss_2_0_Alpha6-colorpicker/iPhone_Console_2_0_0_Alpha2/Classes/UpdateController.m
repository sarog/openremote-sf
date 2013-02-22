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

/*
  * For the update behavior.  
  * If you need know the update result and do something, you must set delegate and implement three delegate methods
  * - (void)didUpadted;
  * - (void)didUseLocalCache:(NSString *)errorMessage;
  * - (void)didUpdateFail:(NSString *)errorMessage;
  */
#import "UpdateController.h"
#import "ServerAutoDiscoveryController.h"
#import "AppSettingsDefinition.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "StringUtils.h"
#import "ServerDefinition.h"
#import "DirectoryDefinition.h"
#import "RoundRobinException.h"
#import "DataBaseService.h"
#import "GroupMember.h"
#import "URLConnectionHelper.h"

//Define the default max retry times. It should be set by user in later version.
#define MAX_RETRY_TIMES 0
#define TIMEOUT_INTERVAL 2

@interface UpdateController (private)
- (void)checkNetworkAndUpdate;
- (void)findServer;
- (void)updateFailOrUseLocalCache:(NSString *)errorMessage;
- (void)useCustomDefaultUrl;
- (void)getRoundRobinGroupMembers;
@end


@implementation UpdateController

- (id)init {
	if (self = [super init]) {
		// Set retryTime to 1
		retryTimes = 1;
	}
	return self;
}

- (id)initWithDelegate:(id)delegate {
	if (self = [self init]) {
		[self setDelegate:delegate];
	}
	return self;
}

- (void)setDelegate:(id)delegate {
	[delegate retain];
	[theDelegate release];
	theDelegate = delegate;
}


// Read Application settings from appSettings.plist.
// If there have an defined server url. It will call checkNetworkAndUpdate method
// else if auto discovery is enable it will try to find another server url using auto discovery,
//        elese it will check local cache or call didUpdateFail method.
- (void)checkConfigAndUpdate {
	if ([Definition sharedDefinition].groups.count > 0) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
	}
	NSLog(@"check config");

	if ([AppSettingsDefinition readServerUrlFromFile]) {
		NSLog(@"readServerUrlFromFile success.");
		[self checkNetworkAndUpdate];
	} else {
		NSLog(@"readServerUrlFromFile fail.");
		if ([AppSettingsDefinition isAutoDiscoveryEnable]) {
			[self findServer];
		} else {
			[self updateFailOrUseLocalCache:@"Can't find server url configuration. You can turn on auto-discovery or specify a server url in settings."];
		}
	}
}

// Try to find a server using auto discovery mechanism. 
- (void)findServer {
	NSLog(@"findServer");
	NSLog(@"retry time %d <= %d", retryTimes, MAX_RETRY_TIMES);
	if (retryTimes <= MAX_RETRY_TIMES) {		
		retryTimes++;
		if (serverAutoDiscoveryController) {
			[serverAutoDiscoveryController release];
			serverAutoDiscoveryController = nil;
		}
		//ServerAutoDiscoveryController have  tow delegate methods
		// - (void)onFindServer:(NSString *)serverUrl;
		// - (void)onFindServerFail:(NSString *)errorMessage;
		serverAutoDiscoveryController = [[ServerAutoDiscoveryController alloc] initWithDelegate:self];
	} else {
		[self updateFailOrUseLocalCache:@"Can't find OpenRemote controller automatically."];
	}	
}

- (void)checkNetworkAndUpdate {
	NSLog(@"checkNetworkAndUpdate");
	@try {	
		// this method will throw CheckNetworkException if the check failed.
		[CheckNetwork checkAll];
		
		[self getRoundRobinGroupMembers];

		//Add an Observer to listern Definition's update behavior
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didUpadted) name:DefinationUpdateDidFinishedNotification object:nil];
		// If all the check success, it will call Definition's update method to update resouces.
		[[Definition sharedDefinition] update];
	}
	@catch (CheckNetworkException *e) {
		NSLog(@"CheckNetworkException occured %@",e.message);
		if (retryTimes <= MAX_RETRY_TIMES) {
			NSLog(@"retry time %d <= %d", retryTimes, MAX_RETRY_TIMES);
			retryTimes++;			
			[self checkNetworkAndUpdate];
		} else {
			[self updateFailOrUseLocalCache:e.message];
		}
		
	}	
}

- (void)updateFailOrUseLocalCache:(NSString *)errorMessage {
	NSLog(@"updateFailOrUseLocalCache");
	NSString *path = [[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition panelXmlRESTUrl]]];
	if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
		[self didUseLocalCache:errorMessage];
	} else {
		//[self useCustomDefaultUrl];
		[self didUpdateFail:errorMessage];
	}
}

- (void)useCustomDefaultUrl {
	NSLog(@"useCustomDefaultUrl");
	if ([[AppSettingsDefinition getCustomServers] count] > 0) {
		[AppSettingsDefinition setAutoDiscovery:NO];
		// Begin: Reset all customized server to unchoose
		for(NSMutableDictionary *toBeResetCustomServer in [AppSettingsDefinition getCustomServers]) {
			[toBeResetCustomServer setValue:[NSNumber numberWithBool:NO] forKey:@"choose"]; 
		}
		// End
		NSMutableDictionary *customServer = [[AppSettingsDefinition getCustomServers] objectAtIndex:0];
		[customServer setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
		[AppSettingsDefinition setCurrentServerUrl:[customServer valueForKey:@"url"]];
		[AppSettingsDefinition writeToFile];
		@try {
			[CheckNetwork checkAll];			
			[self checkNetworkAndUpdate];
		}
		@catch (CheckNetworkException *e) {
			[self didUpdateFail:e.message];
		}
	} else {
		[self didUpdateFail:@"There is no customized default Controller server."];
	}
}

- (void)getRoundRobinGroupMembers {
	NSError *error = nil;
	NSHTTPURLResponse *resp = nil;
	NSURL *url = [NSURL URLWithString:[ServerDefinition serversXmlRESTUrl]]; 
	NSLog(@"serversXmlRESTUrl %@", [ServerDefinition serversXmlRESTUrl]);
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:TIMEOUT_INTERVAL];
	NSData *data = [[[URLConnectionHelper alloc] init] sendSynchronousRequest:request returningResponse:&resp error:&error];
	NSLog(@"Servers Xml REST url is : %@", [ServerDefinition serversXmlRESTUrl]);
	[request release];
	if (error ) {
		NSLog(@"getRoundRobinGroupMembers failed %@",[error localizedDescription]);
		@throw [CheckNetworkException exceptionWithTitle:@"Servers request fail" 
												 message:@"Could not find OpenRemote Controller. It may not be running or the connection URL in Settings is invalid."];
	} else if ([resp statusCode] != 200) {	
		NSLog(@"getRoundRobinGroupMembers statusCode %d",[resp statusCode] );
		@throw [CheckNetworkException exceptionWithTitle:@"Servers request fail" message:[RoundRobinException exceptionMessageOfCode:[resp statusCode]]];
	}
	[[DataBaseService sharedDataBaseService] deleteAllGroupMembers];
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[xmlParser release];
}

#pragma mark delegate method of NSXMLParser
//when find a servers, gets their *url* attribute.
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"server"]) {
		GroupMember *groupMember = [[GroupMember alloc] initWithUrl:[attributeDict valueForKey:@"url"]];
		[[DataBaseService sharedDataBaseService] insertGroupMember:groupMember];
	}
}

#pragma mark call the delegate method which the the delegate implemented.
- (void)didUpadted {
	[[NSNotificationCenter defaultCenter] removeObserver:self name:DefinationUpdateDidFinishedNotification object:nil];
	if (theDelegate && [theDelegate respondsToSelector:@selector(didUpadted)]) {
		[theDelegate performSelector:@selector(didUpadted)];
	}
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	[[Definition sharedDefinition] useLocalCacheDirectly];
	if (theDelegate && [theDelegate respondsToSelector:@selector(didUseLocalCache:)]) {
		[theDelegate performSelector:@selector(didUseLocalCache:) withObject:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage {
	NSLog(@"didUpdateFail");
	if (theDelegate && [theDelegate respondsToSelector:@selector(didUpdateFail:)]) {
		[theDelegate performSelector:@selector(didUpdateFail:) withObject:errorMessage];
	}
}


#pragma mark delegate method of ServerAutoDiscoveryController
- (void)onFindServer:(NSString *)serverUrl {
	NSLog(@"onFindServer %@",serverUrl);
	NSLog(@"after find server, find auto server %d",[AppSettingsDefinition getAutoServers].count);
	[self checkNetworkAndUpdate];
}

- (void)onFindServerFail:(NSString *)errorMessage {
	NSLog(@"onFindServerFail %@",errorMessage);
		[self findServer];
}

-(void)dealloc {
	[theDelegate release];
	[serverAutoDiscoveryController release];
	[super dealloc];
}

@end
