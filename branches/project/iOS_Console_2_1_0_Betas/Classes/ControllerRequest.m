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
#import "NotificationConstant.h"
#import "ControllerException.h"
#import "ViewHelper.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface ControllerRequest ()

@property (nonatomic, retain) ORGroupMember *usedGroupMember;
@property (nonatomic, assign) ORController *controller;

@end

@implementation ControllerRequest

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

#pragma mark NSURLConnectionDelegate implementation

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    // Connection error, check if failover URLs available

    // TODO: there are certain errors for which we should not switch to a different server
    if ([self selectNextGroupMemberToTry]) {
        [self send];
    } else {
        NSLog(@"Connection did fail with error %@", error);
        if ([delegate respondsToSelector:@selector(controllerRequestDidFailWithError:)]) {
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
    
    // TODO: shouldn't this also trigger the switch to another controller ?
    
    NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
    switch ([httpResp statusCode]) {
        case CONTROLLER_CONFIG_CHANGED: //controller config changed
        {
            UpdateController *updateController = [[UpdateController alloc] initWithDelegate:self];
            [updateController checkConfigAndUpdate];
            [updateController release];
            break;
        }
        case UNAUTHORIZED:
        {
            if ([delegate respondsToSelector:@selector(controllerRequestRequiresAuthentication:)]) {
                [delegate controllerRequestRequiresAuthentication:self];
            } else {
                // No specific handling by the delegate, post a notification so "anyone" can handle
//                [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.password = nil; // TODO: move to specific cases, don't always get rid of password
                [[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
            }
            break;
        }
        default:
        {
            if ([delegate respondsToSelector:@selector(controllerRequestDidReceiveResponse:)]) {
                [delegate controllerRequestDidReceiveResponse:response];
            }
        }
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

// TODO EBR : this should be moved to another class

#pragma mark Delegate method of UpdateController

- (void)didUpdate
{
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage
{
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Use Local Cache" Message:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage
{
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Update Failed" Message:errorMessage];
	}
}

@synthesize controller;
@synthesize delegate, usedGroupMember;

@end