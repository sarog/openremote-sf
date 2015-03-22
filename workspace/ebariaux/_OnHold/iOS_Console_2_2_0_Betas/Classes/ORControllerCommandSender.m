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
#import "ORControllerCommandSender.h"
#import "ORController.h"
#import "Component.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "NotificationConstant.h"
#import "ServerDefinition.h"

@interface ORControllerCommandSender ()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) ControllerRequest *controllerRequest;
@property (nonatomic, retain) NSString *command;
@property (nonatomic, retain) Component *component;

@end

@implementation ORControllerCommandSender

- (id)initWithController:(ORController *)aController command:(NSString *)aCommand component:(Component *)aComponent
{
    self = [super initWithController:aController];
    if (self) {
        self.command = aCommand;
        self.component = aComponent;
    }
    return self;
}

- (void)dealloc
{
    self.command = nil;
    self.component = nil;
    [super dealloc];
}

#pragma mark -

- (BOOL)shouldExecuteNow
{
    return [self.controller hasPanelIdentities];
}

- (void)send
{  
    NSAssert(!self.controllerRequest, @"ORControllerCommandSender can only be used to send a request once");
    
    NSString *commandURLPath = [[ServerDefinition controllerControlPathForController:self.controller] stringByAppendingFormat:@"/%d/%@", self.component.componentId, self.command];
    self.controllerRequest = [[[ControllerRequest alloc] initWithController:self.controller] autorelease];
    self.controllerRequest.delegate = self;
    [self.controllerRequest postRequestWithPath:commandURLPath];
}

// TODO EBR : things like UNAUTHORIZED should be moved down to ControllerRequest code, not handled in each command -> test this authorization stuff

- (void)handleServerResponseWithStatusCode:(int)statusCode
{
	if (statusCode != 200) {
		[ViewHelper showAlertViewWithTitle:@"Command failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];

        // TODO EBR we should sure pass some params e.g. for handling multiple command send ...
        if ([self.delegate respondsToSelector:@selector(commandSendFailed)]) {
            [self.delegate commandSendFailed];
        }
	}
}

#pragma mark ControllerRequestDelegate implementation

- (void)controllerRequestDidFinishLoading:(NSData *)data
{
    // This method is intentionally left empty
}

- (void)controllerRequestDidReceiveResponse:(NSURLResponse *)response
{
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
    NSLog(@"Command response for component %d, statusCode is %d", self.component.componentId, [httpResp statusCode]);
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}

- (void) controllerRequestDidFailWithError:(NSError *)error
{
    if ([self.delegate respondsToSelector:@selector(commandSendFailed)]) {
        [self.delegate commandSendFailed];
    }
}

@synthesize controller;
@synthesize delegate;
@synthesize controllerRequest;
@synthesize command;
@synthesize component;

@end