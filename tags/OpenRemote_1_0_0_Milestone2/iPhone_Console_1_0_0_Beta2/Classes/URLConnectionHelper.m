//
//  URLConnectionHelper.m
//  openremote
//
//  Created by wei allen on 09-2-19.
//  Copyright 2009 finalist. All rights reserved.
//

#import "URLConnectionHelper.h"

@implementation URLConnectionHelper 

@synthesize delegate;

#pragma mark constructor
- (id)initWithURL:(NSURL *)url delegate:(id <URLConnectionHelperDelegate>)d  {
	if (self = [super init]) {
		[self setDelegate:d];
		receivedData = [[NSMutableData alloc] init];
		
		NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:15];
		
		//the initWithRequest constractor will invoke the request
		[[[NSURLConnection alloc] initWithRequest:request delegate:self] release];
		[request release];
	}
	return self;
}

- (id)initWithRequest:(NSURLRequest *)request delegate:(id <URLConnectionHelperDelegate>)d  {
	if (self = [super init]) {
		[self setDelegate:d];
		receivedData = [[NSMutableData alloc] init];
		
		[[[NSURLConnection alloc] initWithRequest:request delegate:self] release];
	}
	return self;
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
	NSLog(@"Finished loading");
	//[delegate performSelector:@selector(definitionURLConnectionDidFinishLoading:) withObject:receivedData afterDelay:5];
	[delegate definitionURLConnectionDidFinishLoading:receivedData];
}
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
	[alert show];
	[alert release];
}

- (void)dealloc {
	[receivedData release];
	
	[super dealloc];
}

@end
