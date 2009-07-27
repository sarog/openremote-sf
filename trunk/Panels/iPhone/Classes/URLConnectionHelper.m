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

@implementation URLConnectionHelper 

@synthesize delegate;

#pragma mark constructor
- (id)initWithURL:(NSURL *)url delegate:(id <URLConnectionHelperDelegate>)d  {
	if (self = [super init]) {
		[self setDelegate:d];
		receivedData = [[NSMutableData alloc] init];
		
		NSURLRequest *request = [[NSURLRequest alloc] initWithURL:url cachePolicy:NSURLRequestReloadIgnoringCacheData timeoutInterval:15];
		
		//the initWithRequest constractor will invoke the request
		[[[NSURLConnection alloc] initWithRequest:request delegate:self] autorelease];
		[request release];
	}
	return self;
}

- (id)initWithRequest:(NSURLRequest *)request delegate:(id <URLConnectionHelperDelegate>)d  {
	if (self = [super init]) {
		[self setDelegate:d];
		receivedData = [[NSMutableData alloc] init];
		
		[[[NSURLConnection alloc] initWithRequest:request delegate:self] autorelease];
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
	//[delegate performSelector:@selector(definitionURLConnectionDidFinishLoading:) withObject:receivedData afterDelay:5];
	[delegate definitionURLConnectionDidFinishLoading:receivedData];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
	if ([delegate respondsToSelector:@selector(definitionURLConnectionDidFailWithError:)]) {
		[delegate definitionURLConnectionDidFailWithError:error];
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
			



- (void)dealloc {
	[receivedData release];
	
	[super dealloc];
}

@end
