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
#import "ORControllerCapabilitiesFetcher.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "CapabilitiesParser.h"
#import "Capabilities.h"
#import "ORController.h"

@interface ORControllerCapabilitiesFetcher ()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) ControllerRequest *controllerRequest;

@end

@implementation ORControllerCapabilitiesFetcher

- (BOOL)shouldExecuteNow
{
    return [self.controller hasGroupMembers];
}

- (void)send
{
    NSAssert(!self.controllerRequest, @"ORControllerPanelsFetcher can only be used to send a request once");
    
    ControllerRequest *request = [[ControllerRequest alloc] initWithController:self.controller];
    request.delegate = self;
    self.controllerRequest = request;
    [request release];
    [self.controllerRequest getRequestWithPath:kControllerFetchCapabilitiesPath];
}

#pragma mark ControllerRequestDelegate implementation

- (void)controllerRequestDidFinishLoading:(NSData *)data
{
    CapabilitiesParser *parser = [[CapabilitiesParser alloc] init];
    [self.delegate fetchCapabilitiesDidSucceedWithCapabilities:[parser parseXMLData:data]];
    [parser release];
}

// optional TODO EBR is it required
- (void)controllerRequestDidFailWithError:(NSError *)error
{
    if ([self.delegate respondsToSelector:@selector(fetchCapabilitiesDidFailWithError:)]) {
        [self.delegate fetchCapabilitiesDidFailWithError:error];
    }
}

- (void)controllerRequestDidReceiveResponse:(NSURLResponse *)response
{
    int statusCode = ((NSHTTPURLResponse *)response).statusCode;
    if (statusCode == 404) {
        // Controller does not support /rest/capabilities call -> return nil capabilities
        [self.delegate fetchCapabilitiesDidSucceedWithCapabilities:nil];
    } else if (statusCode != 200) {
		[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
	}
}

- (void)controllerRequestRequiresAuthentication:(ControllerRequest *)request
{
    if ([self.delegate respondsToSelector:@selector(fetchCapabilitiesRequiresAuthenticationForControllerRequest:)]) {
        [self.delegate fetchCapabilitiesRequiresAuthenticationForControllerRequest:request];
    }
}

@synthesize controller;
@synthesize controllerRequest;
@synthesize delegate;

@end