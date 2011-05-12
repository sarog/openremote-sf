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

#import "ORControllerCommandSender.h"
#import "Component.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "NotificationConstant.h"
#import "ServerDefinition.h"

@implementation ORControllerCommandSender

@synthesize delegate;

- (id)initWithCommand:(NSString *)aCommand component:(Component *)aComponent
{
    self = [super init];
    if (self) {
        command = [aCommand retain];
        component = [aComponent retain];
    }
    return self;
}

- (void)dealloc
{
    [command release];
    [component release];
    [controllerRequest release];
    [super dealloc];
}

- (void)send
{  
    NSAssert(!controllerRequest, @"ORControllerCommandSender can only be used to send a request once");
    
    NSString *commandURLPath = [kControllerControlPath stringByAppendingFormat:@"/%d/%@", component.componentId, command];
    controllerRequest = [[ControllerRequest alloc] init];
    controllerRequest.delegate = self;
    [controllerRequest postRequestWithPath:commandURLPath];
}

// TODO EBR : things like UNAUTHORIZED should be moved down to ControllerRequest code, not handled in each command -> test this authorization stuff

- (void)handleServerResponseWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		if (statusCode == UNAUTHORIZED) {
			[Definition sharedDefinition].password = nil;
			[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
		} else {
			[ViewHelper showAlertViewWithTitle:@"Command failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];
		}
        
        // TODO EBR we should sure pass some params e.g. for handling multiple command send ...
        if ([delegate respondsToSelector:@selector(commandSendFailed)]) {
            [delegate commandSendFailed];
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
    NSLog(@"Command response for component %d, statusCode is %d", component.componentId, [httpResp statusCode]);
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}

- (void) controllerRequestDidFailWithError:(NSError *)error
{
    if ([delegate respondsToSelector:@selector(commandSendFailed)]) {
        [delegate commandSendFailed];
    }
}

@end