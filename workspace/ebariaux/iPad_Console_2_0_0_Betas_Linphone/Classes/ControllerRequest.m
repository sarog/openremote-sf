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

#import <UIKit/UIKit.h>
#import "ControllerRequest.h"
#import "CredentialUtil.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "ORGroupMember.h"

@interface ControllerRequest ()

@property (nonatomic, retain) ORGroupMember *usedGroupMember;

@end

@implementation ControllerRequest

@synthesize delegate, usedGroupMember;

- (void)dealloc
{
    // Cancel the connection first, so we can nicely clean
    [self cancel];
    [connection release];
    [requestPath release];
    [receivedData release];
    [usedGroupMember release];
    [potentialGroupMembers release];
    [super dealloc];
}

/**
 * Returns NO if none can be selected
 */
- (BOOL)selectNextGroupMemberToTry
{
    ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
    
    if (!self.usedGroupMember) {
        self.usedGroupMember = activeController.activeGroupMember;
        if (self.usedGroupMember) {
            return YES;
        }
    }
    
    if (!potentialGroupMembers) {
        if ([activeController.groupMembers count] == 0) {
            
            // TODO: maybe use primary URL as fallback solution ?
            
            return NO;
        }
        potentialGroupMembers = [activeController.groupMembers mutableCopy];
    }
    if (self.usedGroupMember) {
        [potentialGroupMembers removeObject:self.usedGroupMember];
    } else {
        // First time we're selecting a group member, start with the one matching the primary URL (the one entered by the user)
        // TODO: have comparison on URL and not only on string
        // URL comparison does not help, should somehow normalize the URL or just make sure the URLs are entered the same in configs
        
        for (ORGroupMember *gm in potentialGroupMembers) {
            if ([activeController.primaryURL isEqualToString:gm.url]) {
                self.usedGroupMember = gm;
                break;
            }
        }
    }
    if (!self.usedGroupMember) {
        self.usedGroupMember = [potentialGroupMembers anyObject];        
    }

    // TODO: check when we should reset the activeController.activeGroupMember to nil -> should be when all group members have failed
    
    return (self.usedGroupMember != nil);
}

- (void)send
{
    NSString *location = [usedGroupMember.url stringByAppendingFormat:@"/%@", requestPath];
    NSLog(@"Trying to send command to %@", location);
    
    NSURL *url = [[NSURL alloc] initWithString:location];
    
    //assemble put request 
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
    [request setURL:url];
    [request setHTTPMethod:method];
    
    [CredentialUtil addCredentialToNSMutableURLRequest:request forController:usedGroupMember.controller];

    if (receivedData) {
        [receivedData release];
    }
    receivedData = [[NSMutableData alloc] init];
    
    if (connection) {
        [connection release];
    }
    connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];
    
    [url release];
    [request release];
}

- (void)requestWithPath:(NSString *)path
{
    if (requestPath) {
        [requestPath release];
    }
    requestPath = [path copy];
    
    if (![self selectNextGroupMemberToTry]) {
        // No group member available, report as error
        if ([delegate respondsToSelector:@selector(controllerRequestDidFailWithError:)]) {            
            [delegate controllerRequestDidFailWithError:nil];
        }
        // TODO EBR should we call delegate or handle error differently
        return;
    }
    [self send];    
}

- (void)postRequestWithPath:(NSString *)path
{
    method = @"POST";
    [self requestWithPath:path];
}

- (void)getRequestWithPath:(NSString *)path
{
    method = @"GET";
    [self requestWithPath:path];
}

- (void)cancel
{
    [connection cancel];
    self.delegate = nil;
}

#pragma mark NSURLConnection delegate implementation

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    // Collect data as we receive it
	[receivedData appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.activeGroupMember = self.usedGroupMember;
	[delegate controllerRequestDidFinishLoading:receivedData];
    [delegate release];
    delegate = nil;
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // Connection error, check if failover URLs available
    lastError = error;
    
    if ([self selectNextGroupMemberToTry]) {
        [self send];
    } else {
        if ([delegate respondsToSelector:@selector(controllerRequestDidFailWithError:)]) {
            NSLog(@">>>>>>>>>>connection:didFailWithError:");
            
            [delegate controllerRequestDidFailWithError:error];
            
        } else {
            UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
            [alert show];
            [alert release];
        }
        [delegate release];
        delegate = nil;
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
	if ([delegate respondsToSelector:@selector(controllerRequestDidReceiveResponse:)]) {
		[delegate controllerRequestDidReceiveResponse:response];
	}
}

// HTTPS self-certificate
- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace
{
	return [protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust];
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
	NSLog(@"[async] use HTTPS self-certificate");
	if ([challenge.protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust]) {
		[challenge.sender useCredential:[NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust] forAuthenticationChallenge:challenge];
	}
}

@end