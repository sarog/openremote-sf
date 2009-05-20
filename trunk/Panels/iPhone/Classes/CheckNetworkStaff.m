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


@implementation CheckNetworkStaff
+(int)checkWhetherNetworkAvailable {
	[[Reachability sharedReachability] setHostName:@"www.google.com"];
	if ([[Reachability sharedReachability] remoteHostStatus] == NotReachable) {
		return kNoNetwork;
	}
	return kCheckNetworkStepOK;
}

+ (int)checkIPAddress {
	[[Reachability sharedReachability] setHostName:[ServerDefinition serverUrl]];
	
	if ([[Reachability sharedReachability] internetConnectionStatus] == NotReachable) {
		NSLog(@"checkIPAddress status is ",[[Reachability sharedReachability] internetConnectionStatus]);
		return kIPAddressIsWrong;
	}
	return kCheckNetworkStepOK;
}

+ (int)checkControllerAvailable {
	NSError *error = nil;
	NSHTTPURLResponse *resp = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[ServerDefinition serverUrl]] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	[NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:&error];
	[request release];
	if (error ) {
		return kControllerNotStarted;
	} else if ([resp statusCode] != 200){
		NSLog(@"checkControllerAvailable statusCode %@",[resp statusCode] );
		return kControllerNotFindApp;
	}
	return kCheckNetworkStepOK;
}

+ (int)checkXmlExist {
	NSHTTPURLResponse *resp = nil;
	NSURLRequest *request = [[NSURLRequest alloc] initWithURL:[NSURL URLWithString:[ServerDefinition sampleXmlUrl]] cachePolicy:NSURLRequestReloadIgnoringLocalCacheData timeoutInterval:5];
	[NSURLConnection sendSynchronousRequest:request returningResponse:&resp error:NULL];
	
	[request release];
	if ([resp statusCode] != 200 ){
		return kControllerNotFindXml;
	}
	return kCheckNetworkStepOK;
}

@end
