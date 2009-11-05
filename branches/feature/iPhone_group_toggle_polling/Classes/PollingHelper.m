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
	isPolling = YES;
	NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition statusRESTUrl]];
	NSURL *url = [[NSURL alloc]initWithString:[location stringByAppendingFormat:@"/%@",pollingStatusIds]];
	NSLog([location stringByAppendingFormat:@"/%@",pollingStatusIds]);
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
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
	NSLog([location stringByAppendingFormat:@"/%@/%@",deviceId,pollingStatusIds]);
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];
	
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
		NSString *errorMessage = nil;
		switch (statusCode) {
			case 404:
				errorMessage = [NSString stringWithString:@"The command was sent to an invalid URL."];
				break;
			case 500:
				errorMessage = [NSString stringWithString:@"Error in controller. Please check controller log."];
				break;
			case 503:
				errorMessage = [NSString stringWithString:@"Controller is not currently available."];
				break;
			case 504://polling timeout, need to refresh
				isError = NO;

				
				if (isPolling == YES) {
					[self doPolling];
				}
				
				return;
		} 
		
		if (!errorMessage) {
			errorMessage = [NSString stringWithFormat:@"Unknown error occured , satus code is %d",statusCode];
		}
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:errorMessage];
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
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
		[alert show];
		[alert release];
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
	NSLog(@"statusCode is %d", [httpResp statusCode]);
	
	[self handleServerErrorWithStatusCode:[httpResp statusCode]];
}

- (void)dealloc {
	[connection release];
	[pollingStatusIds release];
	[super dealloc];
}

@end
