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

#import "GetPanelsController.h"
#import "URLConnectionHelper.h"
#import "ServerDefinition.h"
#import "CredentialUtil.h"

// Interval of get panels timer.
#define GET_PANELS_TIMER_INTERVAL 1

@implementation GetPanelsController

- (id) initWithDelegate:(id<GetPanelsDelegate>)delegateParam {
	self = [super init];
	if (self != nil) {	
		
		_delegate = delegateParam;
		
		panels = [[NSMutableArray alloc] init];
		NSString *location = [[NSString alloc] initWithFormat:[ServerDefinition panelsRESTUrl]];
		NSURL *url = [[NSURL alloc]initWithString:location];
		NSLog(@"panels:%@",location);
		
		//assemble put request 
		NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
		[request setURL:url];
		[request setHTTPMethod:@"GET"];
		[CredentialUtil addCredentialToNSMutableURLRequest:request];
		
		URLConnectionHelper *connection = [[URLConnectionHelper alloc]initWithRequest:request  delegate:self];
		
		// Cancel the connection after GET_PANELS_TIMER_INTERVAL
		NSTimer *getAutoServersTimer = [[NSTimer scheduledTimerWithTimeInterval:GET_PANELS_TIMER_INTERVAL target:connection selector:@selector(cancelConnection) userInfo:nil repeats:NO] retain];
		
		[location release];
		[url	 release];
		[request release];
		[connection autorelease];
		[getAutoServersTimer autorelease];
	}
	return self;
}

#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {

}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[_delegate onGetPanels:panels];
	[xmlParser release];
	[result release];
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {

}

#pragma mark delegate method of NSXMLParser
//when find a panel start we get its *name* attribute as logical identity
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:@"panel"]) {
		NSLog(@"panel logical id : %@",[attributeDict valueForKey:@"name"]);
		[panels addObject:[attributeDict valueForKey:@"name"]]; 
	}
}

- (void)dealloc {
	[panels release];
	
	[super dealloc];
}

@end
