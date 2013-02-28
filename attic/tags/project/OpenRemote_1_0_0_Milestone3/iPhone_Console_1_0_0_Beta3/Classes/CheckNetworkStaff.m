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


#import "CheckNetworkStaff.h"
#import "Reachability.h"
#import "ServerDefinition.h"
#import "CheckNetworkStaffException.h"


@implementation CheckNetworkStaff
+(void)checkWhetherNetworkAvailable {
	if ([[Reachability sharedReachability] localWiFiConnectionStatus] == NotReachable) {
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Check Network Fail" message:@"Please connect your device to network"];
	}
}

+ (void)checkIPAddress {
	@try {
		[CheckNetworkStaff checkWhetherNetworkAvailable];
	}
	@catch (CheckNetworkStaffException * e) {
		@throw e;
	}
	
	[[Reachability sharedReachability] setHostName:[ServerDefinition serverUrl]];
	if ([[Reachability sharedReachability] internetConnectionStatus] == NotReachable) {
		NSLog(@"checkIPAddress status is ",[[Reachability sharedReachability] internetConnectionStatus]);
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Check controller ip address Fail" message:@"Your server address is wrong, please check your settings"];
	}
}

+ (void)checkControllerAvailable {
	@try {
		[CheckNetworkStaff checkIPAddress];
	}
	@catch (CheckNetworkStaffException * e) {
		@throw e;
	}
	
	NSError *error = nil;
	NSHTTPURLResponse *resp = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[ServerDefinition serverUrl]] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	[NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:&error];
	NSLog([ServerDefinition serverUrl]);
	[request release];
	if (error ) {
		NSLog(@"checkControllerAvailable occur error %@",[error localizedDescription]);
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Controller Not Started" 
													  message:@"Could not find OpenRemote Controller. It may not be running or the connection URL in Settings is invalid."];
	} else if ([resp statusCode] != 200) {	
		NSLog(@"checkControllerAvailable statusCode %d",[resp statusCode] );
		@throw [CheckNetworkStaffException exceptionWithTitle:@"OpenRemote Controller Not Found" 
													  message:@"OpenRemote Controller not found on the configured URL. See 'Settings' to reconfigure. "];
	}
}

+ (void)checkXmlExist {
	@try {
		[CheckNetworkStaff checkControllerAvailable];
	}
	@catch (NSException * e) {
		@throw e;
	}

	NSHTTPURLResponse *resp = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[ServerDefinition sampleXmlUrl]] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	[NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:NULL];
	
	[request release];
	if ([resp statusCode] != 200 ){
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Can't find xml resource" 
													  message:@"Please check that the iphone.xml file has been correctly deployed on the controller."];
	}
}

+ (void)checkAll {
	@try {
		[CheckNetworkStaff checkXmlExist];
	}
	@catch (NSException * e) {
		@throw e;
	}
}

@end
