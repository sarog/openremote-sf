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


#import "URLConnectionHelper.h"
#import "AppSettingsDefinition.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"
#import "DataBaseService.h"
#import "UpdateController.h"
#import "NotificationConstant.h"

@interface URLConnectionHelper (Private)
- (void) removeBadCurrentServerURL;
- (void) swithToGroupMemberServer;
- (NSString *) checkGroupMemberServers;
- (void) updateControllerWith:(NSString *)groupMemberUrl;
@end

@implementation URLConnectionHelper 

@synthesize delegate, connection, errorMsg, autoDiscoverController, getAutoServersTimer;

#pragma mark constructor
- (id)initWithURL:(NSURL *)url delegate:(id <URLConnectionHelperDelegate>)d  {
	if (self = [super init]) {
		[self setDelegate:d];
		
		receivedData = [[NSMutableData alloc] init];
		
		NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:15];
		
		//the initWithRequest constractor will invoke the request
		connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
		[request release];
	}
	return self;
}

- (id)initWithRequest:(NSURLRequest *)request delegate:(id <URLConnectionHelperDelegate>)d  {
	if (self = [super init]) {
		[self setDelegate:d];
		
		receivedData = [[NSMutableData alloc] init];
		
		connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
	}
	return self;
}

- (void)cancelConnection {
	if (connection) {
		NSLog(@"cancel url connection");
		[connection cancel];
		if (connection) {
			[connection release];
			connection = nil;
		}
		
	}
}

#pragma mark Instance method

// Swith to groupmember controller.
- (void) swithToGroupMemberServer {
	[self removeBadCurrentServerURL];
	NSString *aAvailableGroupMemberUrl = [self checkGroupMemberServers];
	
	if (aAvailableGroupMemberUrl != nil && ![@"" isEqualToString:aAvailableGroupMemberUrl]) {
		[self updateControllerWith:aAvailableGroupMemberUrl];
	} else {
		ViewHelper *viewHelper = [[ViewHelper alloc] init];
		[viewHelper showAlertViewWithTitleAndSettingNavigation:@"Command failed" Message:@"There's no server available. Leave this problem?"];
	}
}

- (void) removeBadCurrentServerURL {
	NSString *tempCurrentServerUrl = [AppSettingsDefinition getCurrentServerUrl];
	if ([AppSettingsDefinition isAutoDiscoveryEnable]) {
		NSMutableArray *autoServers = [AppSettingsDefinition getAutoServers];
		for (int i=0; i < [autoServers count]; i++) {
			[[autoServers objectAtIndex:i] setValue:[NSNumber numberWithBool:NO] forKey:@"choose"];
			if ([tempCurrentServerUrl isEqualToString:[[autoServers objectAtIndex:i] objectForKey:@"url"]]) {
				[[AppSettingsDefinition getAutoServers] removeObjectAtIndex:i];
			}
		}
	} else {
		NSMutableArray *customServers = [AppSettingsDefinition getCustomServers];
		for (int i=0; i < [customServers count]; i++) {
			[[customServers objectAtIndex:i] setValue:[NSNumber numberWithBool:NO] forKey:@"choose"];
			if ([tempCurrentServerUrl isEqualToString:[[customServers objectAtIndex:i] objectForKey:@"url"]]) {
				[[AppSettingsDefinition getCustomServers] removeObjectAtIndex:i];
			}
		}
	}
}

// Check whether the url of groupmember is available.
- (NSString *) checkGroupMemberServers {
	NSMutableArray *groupMembers = [[DataBaseService sharedDataBaseService] findAllGroupMembers];
	for (GroupMember *gm in groupMembers) {
		@try {
			[AppSettingsDefinition setCurrentServerUrl:gm.url];
			[CheckNetwork checkAll];
			
			NSMutableDictionary *groupMemberServer = [NSMutableDictionary dictionaryWithObject:gm.url forKey:@"url"];
			[groupMemberServer setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
			
			BOOL hadSameUrlBefore = NO;
			if ([AppSettingsDefinition isAutoDiscoveryEnable]) {
				for (int i=0; i<[[AppSettingsDefinition getAutoServers] count]; i++) {
					if ([gm.url isEqualToString:[[[AppSettingsDefinition getAutoServers] objectAtIndex:i] objectForKey:@"url"]]) {
						[[[AppSettingsDefinition getAutoServers] objectAtIndex:i] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
						hadSameUrlBefore = YES;
					}
				}
				if (!hadSameUrlBefore) {
					[AppSettingsDefinition addAutoServer:groupMemberServer];
				}
			} else {
				for (int i=0; i<[[AppSettingsDefinition getCustomServers] count]; i++) {
					if ([gm.url isEqualToString:[[[AppSettingsDefinition getCustomServers] objectAtIndex:i] objectForKey:@"url"]]) {
						[[[AppSettingsDefinition getCustomServers] objectAtIndex:i] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
						hadSameUrlBefore = YES;
					}
				}
				if (!hadSameUrlBefore) {
					[[AppSettingsDefinition getCustomServers] addObject:groupMemberServer];
				}
			}
			[AppSettingsDefinition writeToFile];
			return gm.url;
		}
		@catch (NSException * e) {
			continue;
		}
	}
	return nil;
}

- (void) updateControllerWith:(NSString *)groupMemberUrl {
	NSLog(@"Switching to groupmember controller server %@, please wait...", groupMemberUrl);
	UpdateController *updateController = [[UpdateController alloc] initWithDelegate:self];
	[updateController checkConfigAndUpdate];
}

#pragma mark delegate method of NSURLConnection

//Called we connection receive data
- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
	// Append data
	[receivedData appendData:data];
}

// When finished the connection invoke the deleget method
- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
	// Send data to delegate
	//[delegate performSelector:@selector(definitionURLConnectionDidFinishLoading:) withObject:receivedData afterDelay:5];
	[delegate definitionURLConnectionDidFinishLoading:receivedData];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	
	if ([delegate respondsToSelector:@selector(definitionURLConnectionDidFailWithError:)]) {
		[delegate definitionURLConnectionDidFailWithError:error];
		self.errorMsg = error;
		[self swithToGroupMemberServer];
	} else {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alert show];
		[alert release];
	}
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
	if (delegate && [delegate respondsToSelector:@selector(definitionURLConnectionDidReceiveResponse:)]) {
		[delegate definitionURLConnectionDidReceiveResponse:response];
	}
}

// Added by handy.wang
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace {
	NSLog(@"protectionSpace.authenticationMethod is %@", protectionSpace.authenticationMethod);
	return [protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust];
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
	NSLog(@"challenge.protectionSpace.authenticationMethod is %@", challenge.protectionSpace.authenticationMethod);
	if ([challenge.protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust]) {
		[challenge.sender useCredential:[NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust] forAuthenticationChallenge:challenge];
	}
}


#pragma mark Delegate method of ServerAutoDiscoveryController
- (void)onFindServer:(NSString *)serverUrl {
	//TODO: donothing
}

- (void)onFindServerFail:(NSString *)errorMessage {
	NSLog(@"Find Server Error in class URLConnectionHelper. %@", errorMessage);
	[ViewHelper showAlertViewWithTitle:@"Auto Discovery" Message:errorMessage];
}

#pragma mark Delegate method of UpdateController
- (void)didUpadted {
	NSLog(@"----------DidUpdated in URLConnectionHelper------------");
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	NSLog(@"------------didUseLocalCache in URLConnectionHelper------------");
	
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Use Local Cache" Message:errorMessage];
	}
}

- (void)dealloc {
	[receivedData release];
	[connection release];
	[autoDiscoverController release];
	[getAutoServersTimer release];
	[super dealloc];
}

@end
