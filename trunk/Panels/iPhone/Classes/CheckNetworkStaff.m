//
//  CheckNetworkStaff.m
//  openremote
//
//  Created by finalist on 5/19/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "CheckNetworkStaff.h"
#import "Reachability.h"
#import "ServerDefinition.h"
#import "CheckNetworkStaffException.h"


@implementation CheckNetworkStaff
+(void)checkWhetherNetworkAvailable {
	[[Reachability sharedReachability] setHostName:@"www.google.com"];
	if ([[Reachability sharedReachability] remoteHostStatus] == NotReachable) {
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
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Check controller ip address Fail" message:@"Your server address is wrong please check your settings"];
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
	[request release];
	if (error ) {
		NSLog(@"checkControllerAvailable occur error %@",[error localizedDescription]);
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Server not Start" message:@"Your server is not start or the server url which you configed is wrong."];
	} else if ([resp statusCode] != 200){	
		NSLog(@"checkControllerAvailable statusCode %d",[resp statusCode] );
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Can't find controller Application" message:@"Can't find controller appliaction on your server."];
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
		@throw [CheckNetworkStaffException exceptionWithTitle:@"Can't find xml resource" message:@"Make sure the xml config file is in you server."];
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
