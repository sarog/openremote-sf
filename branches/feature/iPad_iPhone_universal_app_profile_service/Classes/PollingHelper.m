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

#import "PollingHelper.h"
#import "PollingStatusParserDelegate.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "NotificationConstant.h"
#import "ControllerException.h"

@implementation PollingHelper

@synthesize isPolling, pollingStatusIds, isError, connection;

- (id) initWithComponentIds:(NSString *)ids {
	if (self = [super init]) {
		isPolling = NO;
		isError = NO;
		[ids retain];
		pollingStatusIds = ids;
	}
	
	return self;
}


- (void)requestCurrentStatusAndStartPolling {
	if (isPolling) {
		return;
	}
	isPolling = YES;
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition statusRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%@",pollingStatusIds]];
	NSLog(@"%@", [location stringByAppendingFormat:@"/%@",pollingStatusIds]);
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"GET"];
	
	connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	//[connection autorelease];	
}

- (void)doPolling {
	NSString *deviceId = [[UIDevice currentDevice] uniqueIdentifier];
	//NSString *deviceId = @"96e79218965eb72c92a549dd5a330112";
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition pollingRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%@/%@",deviceId,pollingStatusIds]];
	NSLog(@"%@", [location stringByAppendingFormat:@"/%@/%@",deviceId,pollingStatusIds]);
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"GET"];
	
	connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
	
	[location release];
	[url	 release];
	[request release];
	//[connection autorelease];	
}

- (void)cancelPolling {
	isPolling = NO;
	if (connection) {
		[connection cancelConnection];
	}
}


- (void)handleServerErrorWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		isError = YES;
		switch (statusCode) {

			case 506://controller config changed
				updateController = [[UpdateController alloc] initWithDelegate:self];
				[updateController checkConfigAndUpdate];
				return;
			case 504://polling timeout, need to refresh
				isError = NO;

				
				if (isPolling == YES) {
					[self doPolling];
				}
				
				return;
		} 
		
		[ViewHelper showAlertViewWithTitle:@"Polling Failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
		isPolling = NO;
	} else {
		isError = NO;
		if (isPolling == YES) {
			[self doPolling];
		}
		

	} 
	
}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {
	if (!isError) {
		NSLog(@"Polling failed, %@",[error localizedDescription]);
		isError = YES;
	} 
}

//Do polling when the request successful
- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	PollingStatusParserDelegate *delegate = [[PollingStatusParserDelegate alloc] init];
	[xmlParser setDelegate:delegate];
	[xmlParser parse];
	
	[xmlParser release];
	[result release];
	[delegate release];
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	NSLog(@"polling[%@]statusCode is %d",pollingStatusIds, [httpResp statusCode]);
	
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}

#pragma mark Delegate method of UpdateController
- (void)didUpadted {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Use Local Cache" Message:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage {
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Update Failed" Message:errorMessage];
	}
}

- (void)dealloc {
	[connection release];
	[pollingStatusIds release];
	[updateController release];
	[super dealloc];
}

@end
