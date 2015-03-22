/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2011, OpenRemote Inc.
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

#import "ORControllerPanelsFetcher.h"
#import "ServerDefinition.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "NotificationConstant.h"

@implementation ORControllerPanelsFetcher

@synthesize delegate;

- (id)init
{
    self = [super init];
    if (self) {
        panels = [[NSMutableArray alloc] init];
    }
    return self;
}

- (void)dealloc
{
    [controllerRequest release];
    [panels release];
    [super dealloc];
}

- (void)fetch
{
    NSAssert(!controllerRequest, @"ORControllerPanelsFetcher can only be used to send a request once");

    controllerRequest = [[ControllerRequest alloc] init];
    controllerRequest.delegate = self;
    [controllerRequest getRequestWithPath:kControllerFetchPanelsPath];
}

#pragma mark NSXMLParserDelegate implementation

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
	if ([elementName isEqualToString:@"panel"]) {
		NSLog(@"panel logical id : %@",[attributeDict valueForKey:@"name"]);
		[panels addObject:[attributeDict valueForKey:@"name"]]; 
	}
}

#pragma mark ControllerRequestDelegate implementation

- (void)controllerRequestDidFinishLoading:(NSData *)data
{
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[xmlParser release];
    [delegate fetchPanelsDidSucceedWithPanels:[NSArray arrayWithArray:panels]];
}

// optional TODO EBR is it required
- (void)controllerRequestDidFailWithError:(NSError *)error
{
    if ([delegate respondsToSelector:@selector(fetchPanelsDidFailWithError:)]) {
        [delegate fetchPanelsDidFailWithError:error];
    }
}

- (void)controllerRequestDidReceiveResponse:(NSURLResponse *)response
{
    int statusCode = ((NSHTTPURLResponse *)response).statusCode;
	if (statusCode != 200) {
		if (statusCode == UNAUTHORIZED) {
			[Definition sharedDefinition].password = nil;
			[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];            
		} else {
			[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
		}
	} 
}

@end
