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
#import "CheckNetworkStaff.h"
#import "CheckNetworkStaffException.h"
#import "Definition.h"
#import "NotificationConstant.h"
#import "StringUtils.h"
#import "ServerDefinition.h"
#import "DirectoryDefinition.h"

//Define the default max retry times. It should be set by user in later version.
#define MAX_RETRY_TIMES 1

@interface UpdateController (private)
- (void)checkNetworkAndUpdate;
- (void)findServer;
- (void)updateFailOrUseLocalCache:(NSString *)errorMessage;
- (void)useDefaultUrl;
@end


@implementation UpdateController

- (id)init {
	if (self = [super init]) {
		// Set retryTime to 0
		retryTimes = 0;
	}
	return self;
}

- (id)initWithDelegate:(id)delegate {
	if (self = [super init]) {
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
	if (retryTimes <= MAX_RETRY_TIMES) {
		retryTimes = retryTimes + 1;
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
	@try {	
		// this method will throw CheckNetworkStaffException if the check failed.
		[CheckNetworkStaff checkAll];
		
		//Add an Observer to listern Definition's update behavior
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didUpadted) name:DefinationUpdateDidFinishedNotification object:nil];
		// If all the check success, it will call Definition's update method to update resouces.
		[[Definition sharedDefinition] update];
	}
	@catch (CheckNetworkStaffException *e) {
		NSLog(@"CheckNetworkStaffException %@",e.message);

		if (retryTimes <= MAX_RETRY_TIMES) {
			retryTimes = retryTimes + 1;
			[self checkNetworkAndUpdate];
		} else {
			[self updateFailOrUseLocalCache:e.message];
		}
		
	}	
}

- (void)updateFailOrUseLocalCache:(NSString *)errorMessage {
	NSString *path = [[DirectoryDefinition xmlCacheFolder] stringByAppendingPathComponent:[StringUtils parsefileNameFromString:[ServerDefinition sampleXmlUrl]]];
	if ([[NSFileManager defaultManager] fileExistsAtPath:path]) {
		[self didUseLocalCache:errorMessage];
	} else {
		[self useDefaultUrl];
	}
}

- (void)useDefaultUrl {
	if ([[AppSettingsDefinition getCustomServers] count] > 0) {
		[AppSettingsDefinition setAutoDiscovery:NO];
		NSMutableDictionary *customServer = [[AppSettingsDefinition getCustomServers] objectAtIndex:0];
		[customServer setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
		[AppSettingsDefinition setCurrentServerUrl:[customServer valueForKey:@"url"]];
		[AppSettingsDefinition writeToFile];
		@try {
			[CheckNetworkStaff checkAll];
			[self checkNetworkAndUpdate];
		}
		@catch (CheckNetworkStaffException *e) {
			[self didUpdateFail:e.message];
		}
	} else {
		[self didUpdateFail:@"There is no default url.Application init error."];
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
