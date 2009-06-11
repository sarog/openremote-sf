//
//  UpdateController.m
//  openremote
//
//  Created by finalist on 6/7/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

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
		
	if (!serverAutoDiscoveryController) {
		serverAutoDiscoveryController = [[ServerAutoDiscoveryController alloc] init];
	}
	
	if ([AppSettingsDefinition readServerUrlFromFile]) {
		[self checkNetworkAndUpdate];
	} else {
		if ([AppSettingsDefinition isAutoDiscoveryEnable]) {
			[serverAutoDiscoveryController findServerWithDelegate:self];
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
		if (retryTimes == 0 && [AppSettingsDefinition isAutoDiscoveryEnable]) {
			NSLog(@"retry @d time.",retryTimes);
			[serverAutoDiscoveryController findServerWithDelegate:self];
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
	if (retryTimes == MAX_RETRY_TIMES) {
		[self didUseLocalCache:@"Can't discover the server, maybe your server hasn't been started or your iPhone is not under the same LAN as Server."];
	} else {
		retryTimes = retryTimes + 1;
		[self checkConfigAndUpdate];
	}
}

-(void)dealloc {
	[theDelegate release];
	[serverAutoDiscoveryController release];
	[super dealloc];
}

@end
