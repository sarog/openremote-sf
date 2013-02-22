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
#import "ORControllerGroupMembersFetcher.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "NotificationConstant.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "NSURLRequest+ORAdditions.h"

@interface ORControllerGroupMembersFetcher ()

@property (nonatomic, retain) NSURLConnection *connection;
@property (nonatomic, retain) ORController *controller;
@property (nonatomic) BOOL lastRequestWasError;

@end

/**
 * The request is only sent to the primary URL of the controller.
 */
@implementation ORControllerGroupMembersFetcher

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        members = [[NSMutableArray alloc] init];
        self.controller = aController;
    }
    return self;
}

- (void)dealloc
{
    [members release];
    [self.connection cancel];
    self.connection = nil;
    self.controller = nil;
    [super dealloc];
}

- (void)fetch
{
    NSAssert(!self.connection, @"ORControllerGroupMembersFetcher can only be used to send a request once");
    self.lastRequestWasError = NO;
    NSURLRequest *request = [NSURLRequest or_requestWithURLString:[self.controller.primaryURL stringByAppendingFormat:@"/%@", kControllerFetchGroupMembersPath]
                                                           method:@"GET" userName:self.controller.userName password:self.controller.password];
    self.connection = [[[NSURLConnection alloc] initWithRequest:request delegate:[[[DataCapturingNSURLConnectionDelegate alloc] initWithNSURLConnectionDelegate:self] autorelease]] autorelease];
}

- (void)cancelFetch
{
    [self.connection cancel];
}

#pragma mark DataCapturingNSURLConnectionDelegate delegate implementation

- (void)connectionDidFinishLoading:(NSURLConnection *)connection receivedData:(NSData *)receivedData
{
    if (!self.lastRequestWasError) {
        NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:receivedData];
        [xmlParser setDelegate:self];
        [xmlParser parse];
        [xmlParser release];
        [self.delegate controller:self.controller fetchGroupMembersDidSucceedWithMembers:[NSArray arrayWithArray:members]];
    }
}

#pragma mark NSURLConnection delegate implementation

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
//    NSLog(@"controller %@ - connection:didFailWithError: %@", self.controller.primaryURL, error);
    if ([self.delegate respondsToSelector:@selector(controller:fetchGroupMembersDidFailWithError:)]) {
        [self.delegate controller:self.controller fetchGroupMembersDidFailWithError:error];
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        [alert release];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    int statusCode = ((NSHTTPURLResponse *)response).statusCode;
//    NSLog(@"controller %@ - connection:didReceiveResponse: %d", self.controller.primaryURL, statusCode);
	if (statusCode != 200) {
        if (statusCode == UNAUTHORIZED) {
            if ([self.delegate respondsToSelector:@selector(fetchGroupMembersRequiresAuthenticationForController:)]) {
                [self.delegate fetchGroupMembersRequiresAuthenticationForController:self.controller];
            }
        }
        self.lastRequestWasError = YES;

        // TODO: should we have our own error domain ?
        if ([self.delegate respondsToSelector:@selector(controller:fetchGroupMembersDidFailWithError:)]) {
            [self.delegate controller:self.controller fetchGroupMembersDidFailWithError:[NSError errorWithDomain:NSURLErrorDomain code:statusCode userInfo:nil]];
        }

        // TODO: log / have some notification mechanism
//		[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
        
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

#pragma mark NSXMLParserDelegate implementation

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:@"server"]) {
        [members addObject:[attributeDict valueForKey:@"url"]];
    }
}

@synthesize controller;
@synthesize delegate;
@synthesize connection;
@synthesize lastRequestWasError;

@end