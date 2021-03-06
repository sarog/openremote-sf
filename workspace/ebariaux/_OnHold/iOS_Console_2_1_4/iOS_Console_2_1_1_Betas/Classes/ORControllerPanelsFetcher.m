/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import "ORControllerPanelsFetcher.h"
#import "ServerDefinition.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "ControllerException.h"

@interface ORControllerPanelsFetcher ()

@property (nonatomic, retain) ORController *controller;

@end

@implementation ORControllerPanelsFetcher

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        panels = [[NSMutableArray alloc] init];
        self.controller = aController;
    }
    return self;
}

- (void)dealloc
{
    [controllerRequest release];
    [panels release];
    self.controller = nil;
    [super dealloc];
}

- (void)fetch
{
    NSAssert(!controllerRequest, @"ORControllerPanelsFetcher can only be used to send a request once");

    controllerRequest = [[ControllerRequest alloc] initWithController:self.controller];
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
		[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
	} 
}

- (void)controllerRequestRequiresAuthentication:(ControllerRequest *)request
{
    if ([delegate respondsToSelector:@selector(fetchPanelsRequiresAuthenticationForControllerRequest:)]) {
        [delegate fetchPanelsRequiresAuthenticationForControllerRequest:request];
    }
}

@synthesize controller;
@synthesize delegate;

@end