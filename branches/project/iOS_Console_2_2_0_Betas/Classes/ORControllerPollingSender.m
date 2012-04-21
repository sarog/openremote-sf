/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "ORControllerPollingSender.h"
#import "Definition.h"
#import "ServerDefinition.h"
#import "PollingStatusParserDelegate.h"
#import "ORController.h"
#import "ControllerException.h"

@interface ORControllerPollingSender ()

@property (nonatomic, retain) ORController *controller;

@end

@implementation ORControllerPollingSender

- (id)initWithController:(ORController *)aController ids:(NSString *)someIds
{
    self = [super init];
    if (self) {
        ids = [someIds retain];
        self.controller = aController;
    }
    return self;
}

- (void)dealloc
{
    [ids release];
    [controllerRequest release];
    self.controller = nil;
    [super dealloc];
}

- (void)requestStatus
{
    NSAssert(!controllerRequest, @"ORControllerPollingSender can only be used to send a request once");

    NSString *urlPath = [[ServerDefinition controllerStatusPathForController:self.controller] stringByAppendingFormat:@"/%@", ids];
    controllerRequest = [[ControllerRequest alloc] initWithController:self.controller];
    controllerRequest.delegate = self;
    [controllerRequest getRequestWithPath:urlPath];
}

- (void)poll
{
    NSAssert(!controllerRequest, @"ORControllerPollingSender can only be used to send a request once");
    
    NSString *deviceId = [[UIDevice currentDevice] uniqueIdentifier];
    NSString *urlPath = [[ServerDefinition controllerPollingPathForController:self.controller] stringByAppendingFormat:@"/%@/%@", deviceId, ids];
    controllerRequest = [[ControllerRequest alloc] initWithController:self.controller];
    controllerRequest.delegate = self;
    [controllerRequest getRequestWithPath:urlPath];
}

- (void)cancel
{
    [controllerRequest cancel];
}

- (void)handleServerResponseWithStatusCode:(int)statusCode
{
	if (statusCode != 200) {
		switch (statusCode) {
			case POLLING_TIMEOUT:
            {
                if ([delegate respondsToSelector:@selector(pollingDidTimeout)]) {
                    [delegate pollingDidTimeout];
                }
				return;
            }
		}		
        if ([delegate respondsToSelector:@selector(pollingDidReceiveErrorResponse)]) {
            [delegate pollingDidReceiveErrorResponse];
        }
        // [ViewHelper showAlertViewWithTitle:@"Polling Failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];
        // Don't bother user with this, for now simply log
        NSLog(@"Polling failed %@", [ControllerException exceptionMessageOfCode:statusCode]);
        // TODO: user should be notified in an unobstrusive way that the polling did stop and there should be a way to restart it
	} else {
        if ([delegate respondsToSelector:@selector(pollingDidSucceed)]) {
            [delegate pollingDidSucceed];
        }
	} 
}

#pragma mark ControllerRequestDelegate implementation

- (void)controllerRequestDidFinishLoading:(NSData *)data
{
	NSString *result = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	
	NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	PollingStatusParserDelegate *parserDelegate = [[PollingStatusParserDelegate alloc] init];
	[xmlParser setDelegate:parserDelegate];
	[xmlParser parse];
	
	[xmlParser release];
	[result release];
	[parserDelegate release];
}

- (void)controllerRequestDidReceiveResponse:(NSURLResponse *)response
{
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	NSLog(@"polling[%@]statusCode is %d", ids, [httpResp statusCode]);
	
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}

- (void)controllerRequestDidFailWithError:(NSError *)error
{
	if ([delegate respondsToSelector:@selector(pollingDidFailWithError:)]) {
        [delegate pollingDidFailWithError:error];
    }
}

- (void)controllerRequestConfigurationUpdated:(ControllerRequest *)request
{
    if ([delegate respondsToSelector:@selector(controllerConfigurationUpdated:)]) {
        [delegate controllerConfigurationUpdated:request.controller];
    }    
}

@synthesize controller;
@synthesize delegate;

@end