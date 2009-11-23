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
#import "SwitchServerAlertHelper.h"
#import "CheckNetwork.h"
#import "CheckNetworkException.h"

//allows self-signed cert
@interface NSURLRequest(HTTPSCertificate) 
+ (BOOL)allowsAnyHTTPSCertificateForHost:(NSString *)host;
@end


@implementation NSURLRequest(HTTPSCertificate)
+ (BOOL)allowsAnyHTTPSCertificateForHost:(NSString *)host {
	return YES; // Should probably return YES only for a specific host
}
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

#pragma mark instance method

/**
 * Switch to a autoServer depends on whether there are available autoServers.
 * If there are, then alert user switching to a server whose index is 0 in autoServers array.
 * If there aren't, then alert user adding server manually.
 */
- (void)switchToAvailableAutoServer {
	NSString *alertInfo = [errorMsg localizedDescription];
	NSMutableArray *availableAutoServers = [AppSettingsDefinition getAutoServers];
	NSString *availableAutoServerURL;
	NSLog(@"Found %d autoServer(s) before switch to a available autoServer.", availableAutoServers.count);
	if (availableAutoServers.count > 0) {
		availableAutoServerURL = [[availableAutoServers objectAtIndex:0] objectForKey:@"url"];
		NSString *tempAlertInfo = [[[@"! \nSwitch to another server: [" stringByAppendingString:availableAutoServerURL] stringByAppendingString:@"]"] stringByAppendingString:@" or setting?"];
		alertInfo = [alertInfo stringByAppendingString: tempAlertInfo];
		[[[SwitchServerAlertHelper alloc] init] showAlertViewWithTitleDiscorveredServerAndSettingNavigation:@"Command failed" Message:alertInfo];
	} else {
		NSLog(@"No auto servers found, then alert uesr adding server manually.");
		alertInfo = [alertInfo stringByAppendingString:@"\n There's no autodiscovered server available. Add a server manually?"];
		[[[SwitchServerAlertHelper alloc] init] showAlertViewWithTitleOnlyNoAndSettingNavigation:@"Command failed" Message:alertInfo];
	}
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
		
		if ([AppSettingsDefinition isAutoDiscoveryEnable]) {
			// AutoDiscovery was enabled.
			NSLog(@"Switch to a available server in automatical mode when connection fail.");
			self.errorMsg = error;
			[AppSettingsDefinition removeAllAutoServer];
			[AppSettingsDefinition writeToFile];
			if (autoDiscoverController) {
				[autoDiscoverController setDelegate:nil];
				[autoDiscoverController release];
				autoDiscoverController = nil;
			}
			autoDiscoverController = [[ServerAutoDiscoveryController alloc]initWithDelegate:self];
			getAutoServersTimer = [[NSTimer scheduledTimerWithTimeInterval:0.5 target:self selector:@selector(switchToAvailableAutoServer) userInfo:nil repeats:NO] retain];
		} else {
			// AutoDiscovery wasn't enabled.
			NSLog(@"Switch to a available server in manual mode when connection fail.");			
			NSMutableArray *availableCustomServers = [AppSettingsDefinition getCustomServers];
			NSString *alertInfo = [error localizedDescription];
			
			if (availableCustomServers.count > 0) {
			// If there are customized servers then alert user switching to a available one.
				NSString *oldCurrentServerUrl = [AppSettingsDefinition getCurrentServerUrl];
				NSString *availableCustomServerURL = @"";
				// check if there a customizedURL is available. 
				for(int i=0; i < availableCustomServers.count; i++) {
					NSString *toBeCheckedCustomizedURL = [[availableCustomServers objectAtIndex:i] objectForKey:@"url"];
					NSLog(@"toBeCheckedCustomizedURL is : %@", toBeCheckedCustomizedURL);
					@try {
						[AppSettingsDefinition setCurrentServerUrl:toBeCheckedCustomizedURL];
						// this method will throw CheckNetworkException if the check failed.
						[CheckNetwork checkAll];
					}
					@catch (CheckNetworkException *e) {
						NSLog(@"-------CheckNetworkException------- %@",e.message);
						[AppSettingsDefinition setCurrentServerUrl:oldCurrentServerUrl];
						continue;
					}
					availableCustomServerURL = toBeCheckedCustomizedURL;
					[[availableCustomServers objectAtIndex:i] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
					[AppSettingsDefinition writeToFile];
					break;
				}
				if(![availableCustomServerURL isEqualToString:@""]) {
					NSLog(@"After checking customized serverURL, a availableCustomServerURL is %@", availableCustomServerURL);
					// Temporarily, I just select the first customized url, even if it isn't available.
					//*availableCustomServerURL = [[availableCustomServers objectAtIndex:0] objectForKey:@"url"];
					NSString *tempAlertInfo = [[[@"! \nSwitch to available customized server: [" stringByAppendingString:availableCustomServerURL] stringByAppendingString:@"]"] stringByAppendingString:@" or setting?"];
					alertInfo = [alertInfo stringByAppendingString:tempAlertInfo];
					[[[SwitchServerAlertHelper alloc] init] showAlertViewWithTitleCustomizedServerAndSettingNavigation:@"Command failed" Message:alertInfo];
				} else {
				// If there aren't any customized servers available then alert user customizing a new server.
					alertInfo = [alertInfo stringByAppendingString:@"\n There's no cumtomized server available. Add a server manually?"];
					[[[SwitchServerAlertHelper alloc] init] showAlertViewWithTitleOnlyNoAndSettingNavigation:@"Command failed" Message:alertInfo];
				}
			} else {
			// If there aren't any customized servers then alert user customizing a new server.
				alertInfo = [alertInfo stringByAppendingString:@"\n There's no cumtomized server available. Add a server manually?"];
				[[[SwitchServerAlertHelper alloc] init] showAlertViewWithTitleOnlyNoAndSettingNavigation:@"Command failed" Message:alertInfo];
			}
		}
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

#pragma mark Delegate method of ServerAutoDiscoveryController
- (void)onFindServer:(NSString *)serverUrl {
	//TODO: donothing
}

- (void)onFindServerFail:(NSString *)errorMessage {
	NSLog(@"Find Server Error in class URLConnectionHelper. %@", errorMessage);
	[ViewHelper showAlertViewWithTitle:@"Find Server Error" Message:errorMessage];
}

- (void)dealloc {
	[receivedData release];
	[connection release];
	[autoDiscoverController release];
	[getAutoServersTimer release];
	[super dealloc];
}

@end
