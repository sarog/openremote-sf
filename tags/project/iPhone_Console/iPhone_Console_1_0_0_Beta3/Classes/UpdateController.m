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


#import "UpdateController.h"
#import "ServerAutoDiscoveryController.h"
#import "AppSettingsDefinition.h"
#import "CheckNetworkStaff.h"
#import "CheckNetworkStaffException.h"
#import "Definition.h"
#import "NotificationConstant.h"

#define MAX_RETRY_TIMES 1

@interface UpdateController (private)
- (void)checkNetworkAndUpdate;
@end


@implementation UpdateController

- (id)init {
	if (self = [super init]) {
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


- (void)checkConfigAndUpdate {
	NSLog(@"check config");

	
	if ([AppSettingsDefinition readServerUrlFromFile]) {
		[self checkNetworkAndUpdate];
	} else {
		if ([AppSettingsDefinition isAutoDiscoveryEnable]) {
			if (serverAutoDiscoveryController) {
				[serverAutoDiscoveryController release];
				serverAutoDiscoveryController = nil;
			}	
			
			serverAutoDiscoveryController = [[ServerAutoDiscoveryController alloc] initWithDelegate:self];
		} else {
			[self didUseLocalCache:@"Can't find server url configuration. You can turn on auto-discovery or specify a server url in settings."];
		}
	}
}

- (void)checkNetworkAndUpdate {
	@try {	
		[CheckNetworkStaff checkAll];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didUpadted) name:DefinationUpdateDidFinishedNotification object:nil];
		[[Definition sharedDefinition] update];
	}
	@catch (CheckNetworkStaffException *e) {
		NSLog(@"CheckNetworkStaffException %@",e.message);
		NSLog(@"retry %d time.",retryTimes);
		if (retryTimes <= MAX_RETRY_TIMES && [AppSettingsDefinition isAutoDiscoveryEnable]) {
			NSLog(@"retry %d time.",retryTimes);
			retryTimes = retryTimes + 1;
			if (serverAutoDiscoveryController) {
				[serverAutoDiscoveryController release];
				serverAutoDiscoveryController = nil;
			}	
			serverAutoDiscoveryController = [[ServerAutoDiscoveryController alloc] initWithDelegate:self];
		} else {
			[self didUseLocalCache:e.message];
		}
	}	
}

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


#pragma mark delegate method of ServerAutoDiscoveryController
- (void)onFindServer:(NSString *)serverUrl {
	NSLog(@"onFindServer %@",serverUrl);
	NSLog(@"after find server, find auto server %d",[AppSettingsDefinition getAutoServers].count);
	[self checkNetworkAndUpdate];
}

- (void)onFindServerFail:(NSString *)errorMessage {
		[self checkNetworkAndUpdate];
}

-(void)dealloc {
	[theDelegate release];
	[serverAutoDiscoveryController release];
	[super dealloc];
}

@end
