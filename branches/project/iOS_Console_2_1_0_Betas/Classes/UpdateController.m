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

/*
  * For the update behavior.  
  * If you need know the update result and do something, you must set delegate and implement three delegate methods
  * - (void)didUpdate;
  * - (void)didUseLocalCache:(NSString *)errorMessage;
  * - (void)didUpdateFail:(NSString *)errorMessage;
  */
#import "UpdateController.h"
#import "AppSettingsDefinition.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "StringUtils.h"
#import "ServerDefinition.h"
#import "DirectoryDefinition.h"
#import "RoundRobinException.h"
#import "URLConnectionHelper.h"
#import "CredentialUtil.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "ORGroupMember.h"

//Define the default max retry times. It should be set by user in later version.
#define MAX_RETRY_TIMES 0
#define TIMEOUT_INTERVAL 5

@interface UpdateController ()

- (void)checkNetworkAndUpdate;
- (void)findServer;
- (void)updateFailOrUseLocalCache:(NSString *)errorMessage;
- (void)didUseLocalCache:(NSString *)errorMessage;
- (void)didUpdateFail:(NSString *)errorMessage;

@end


@implementation UpdateController

@synthesize delegate;

- (id)init
{
    self = [super init];
	if (self) {
		// Set retryTime to 1
		retryTimes = 1;
	}
	return self;
}

- (id)initWithDelegate:(id)aDelegate
{
    self = [super init];
	if (self) {
		self.delegate = aDelegate;
	}
	return self;
}


// Read Application settings from appSettings.plist.
// If there have an defined server url. It will call checkNetworkAndUpdate method
// else if auto discovery is enable it will try to find another server url using auto discovery,
// else it will check local cache or call didUpdateFail method.
- (void)checkConfigAndUpdate {
	if ([Definition sharedDefinition].groups.count > 0) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
	}
	NSLog(@"check config");

    // If there is a selected controller (auto-discovered or configured), try to use it
	if ([[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].selectedConfiguredController) {
		[self checkNetworkAndUpdate];
	} else {
		NSLog(@"No selected controller found in configuration");
		if ([[ORConsoleSettingsManager sharedORConsoleSettingsManager] consoleSettings].autoDiscovery) {
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

// Check if network is available. If network is available, then update client.
- (void)checkNetworkAndUpdate {
	NSLog(@"checkNetworkAndUpdate");
	@try {
		// this method will throw CheckNetworkException if the check failed.
		[CheckNetwork checkAll];

		// TODO: check what we really want to do 
//		[self getRoundRobinGroupMembers];

		//Add an Observer to listern Definition's update behavior
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didUpdate) name:DefinitionUpdateDidFinishNotification object:nil];
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

// Use local cache if update fail and local cache exists.
- (void)updateFailOrUseLocalCache:(NSString *)errorMessage {
	NSLog(@"updateFailOrUseLocalCache");
	NSString *path = [[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition panelXmlRESTUrl]]];
	if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
		[self didUseLocalCache:errorMessage];
	} else {
		[self didUpdateFail:errorMessage];
	}
}

#pragma mark call the delegate method which the the delegate implemented.
- (void)didUpdate {
    NSLog(@">>UpdateController.didUpdate");
	[[NSNotificationCenter defaultCenter] removeObserver:self name:DefinitionUpdateDidFinishNotification object:nil];
    NSLog(@"theDelegate %@", delegate);
	if (delegate && [delegate respondsToSelector:@selector(didUpdate)]) {
		[delegate performSelector:@selector(didUpdate)];
	}
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	[[Definition sharedDefinition] useLocalCacheDirectly];
	if (delegate && [delegate respondsToSelector:@selector(didUseLocalCache:)]) {
		[delegate performSelector:@selector(didUseLocalCache:) withObject:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage {
	NSLog(@"didUpdateFail");
	if (delegate && [delegate respondsToSelector:@selector(didUpdateFail:)]) {
		[delegate performSelector:@selector(didUpdateFail:) withObject:errorMessage];
	}
}


#pragma mark delegate method of ServerAutoDiscoveryController
- (void)onFindServer:(ORController *)aController {
	NSLog(@"onFindServer %@", aController.primaryURL);
	[self checkNetworkAndUpdate];
}

- (void)onFindServerFail:(NSString *)errorMessage {
	NSLog(@"onFindServerFail %@",errorMessage);
		[self findServer];
}

-(void)dealloc
{
	[delegate release];
	[serverAutoDiscoveryController release];
	[super dealloc];
}

@end
