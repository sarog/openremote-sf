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
#import <UIKit/UIKit.h>
#import "ControllerRequest.h"
#import "NSURLRequest+ORAdditions.h"
#import "ORController.h"
#import "ORGroupMember.h"

@interface ControllerRequest ()

@property (nonatomic, retain) ORGroupMember *usedGroupMember;
@property (nonatomic, assign) ORController *controller;

@end

@implementation ControllerRequest

@synthesize controller;
@synthesize delegate, usedGroupMember;

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        self.controller = aController;
    }
    return self;
}

- (void)dealloc
{
    // Cancel the connection first, so we can nicely clean
    [self cancel];
    [connection release];
    [requestPath release];
    [usedGroupMember release];
    [potentialGroupMembers release];
    self.controller = nil;
    [super dealloc];
}

/**
 * Returns NO if none can be selected
 */
- (BOOL)selectNextGroupMemberToTry
{
    if (!self.usedGroupMember) {
        self.usedGroupMember = self.controller.activeGroupMember;
        if (self.usedGroupMember) {
            return YES;
        }
    }
    
    if (!potentialGroupMembers) {
        if ([self.controller.groupMembers count] == 0) {
            
            // TODO: maybe use primary URL as fallback solution ?
            
            return NO;
        }
        potentialGroupMembers = [self.controller.groupMembers mutableCopy];
    }
    if (self.usedGroupMember) {
        [potentialGroupMembers removeObject:self.usedGroupMember];
        self.usedGroupMember = nil;
    } else {
        // First time we're selecting a group member, start with the one matching the primary URL (the one entered by the user)
        // TODO: have comparison on URL and not only on string
        // URL comparison does not help, should somehow normalize the URL or just make sure the URLs are entered the same in configs
        
        for (ORGroupMember *gm in potentialGroupMembers) {
            if ([self.controller.primaryURL isEqualToString:gm.url]) {
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
    
    NSURLRequest *request = [NSURLRequest or_requestWithURLString:location method:method userName:usedGroupMember.controller.userName password:usedGroupMember.controller.password];
    
    if (connection) {
        [connection cancel];
        [connection release];
    }
    connection = [[NSURLConnection alloc] initWithRequest:request delegate:[[[DataCapturingNSURLConnectionDelegate alloc] initWithNSURLConnectionDelegate:self] autorelease]];
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

#pragma mark DataCapturingNSURLConnectionDelegate delegate implementation

- (void)connectionDidFinishLoading:(NSURLConnection *)connection receivedData:(NSData *)receivedData
{
    self.controller.activeGroupMember = self.usedGroupMember;
	[delegate controllerRequestDidFinishLoading:receivedData];
    [delegate release];
    delegate = nil;
}

#pragma mark NSURLConnection delegate implementation

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