//
//  ORControllerGroupMembersFetcher.m
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

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

@end

@implementation ORControllerGroupMembersFetcher

@synthesize controller;
@synthesize delegate;
@synthesize connection;

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

// TODO: delegate methods must pass back controller ?

- (void)fetch
{
    NSAssert(!self.connection, @"ORControllerGroupMembersFetcher can only be used to send a request once");
    NSURLRequest *request = [NSURLRequest or_requestWithURLString:[self.controller.primaryURL stringByAppendingFormat:@"/%@", kControllerFetchGroupMembersPath]
                                                           method:@"GET" userName:controller.userName password:controller.password];    
    self.connection = [[[NSURLConnection alloc] initWithRequest:request delegate:[[[DataCapturingNSURLConnectionDelegate alloc] initWithNSURLConnectionDelegate:self] autorelease]] autorelease];
}

#pragma mark DataCapturingNSURLConnectionDelegate delegate implementation

- (void)connectionDidFinishLoading:(NSURLConnection *)connection receivedData:(NSData *)receivedData
{
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:receivedData];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[xmlParser release];
    [delegate fetchGroupMembersDidSucceedWithMembers:[NSArray arrayWithArray:members]];
}

#pragma mark NSURLConnection delegate implementation

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    if ([delegate respondsToSelector:@selector(fetchGroupMembersDidFailWithError:)]) {
        [delegate fetchGroupMembersDidFailWithError:error];
    } else {
        UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error Occured" message:[error localizedDescription] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
        [alert show];
        [alert release];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    int statusCode = ((NSHTTPURLResponse *)response).statusCode;
	if (statusCode != 200) {
		if (statusCode == UNAUTHORIZED) {
            [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.password = nil;
			[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
		} else {
			[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
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

#pragma mark NSXMLParserDelegate implementation

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:@"server"]) {
        [members addObject:[attributeDict valueForKey:@"url"]];
    }
}

@end
