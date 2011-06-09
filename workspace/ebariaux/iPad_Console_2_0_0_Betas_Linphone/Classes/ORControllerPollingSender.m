//
//  ORControllerPollingSender.m
//  openremote
//
//  Created by Eric Bariaux on 09/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerPollingSender.h"
#import "Definition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "NotificationConstant.h"
#import "ServerDefinition.h"
#import "PollingStatusParserDelegate.h"

@implementation ORControllerPollingSender

@synthesize delegate;

- (id)initWithIds:(NSString *)someIds
{
    self = [super init];
    if (self) {
        ids = [someIds retain];
    }
    return self;
}

- (void)dealloc
{
    [ids release];
    [controllerRequest release];
    [super dealloc];
}

- (void)requestStatus
{
    NSAssert(!controllerRequest, @"ORControllerPollingSender can only be used to send a request once");

    NSString *urlPath = [kControllerStatusPath stringByAppendingFormat:@"/%@", ids];
    controllerRequest = [[ControllerRequest alloc] init];
    controllerRequest.delegate = self;
    [controllerRequest getRequestWithPath:urlPath];
}

- (void)poll
{
    NSAssert(!controllerRequest, @"ORControllerPollingSender can only be used to send a request once");
    
    NSString *deviceId = [[UIDevice currentDevice] uniqueIdentifier];
    NSString *urlPath = [kControllerPollingPath stringByAppendingFormat:@"/%@/%@", deviceId, ids];
    controllerRequest = [[ControllerRequest alloc] init];
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
			case CONTROLLER_CONFIG_CHANGED: //controller config changed
            {
				UpdateController *updateController = [[UpdateController alloc] initWithDelegate:self];
				[updateController checkConfigAndUpdate];
                [updateController release];
				return;
            }
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
		[ViewHelper showAlertViewWithTitle:@"Polling Failed" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
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

- (void) controllerRequestDidFailWithError:(NSError *)error
{
	if ([delegate respondsToSelector:@selector(pollingDidFailWithError:)]) {
        [delegate pollingDidFailWithError:error];
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

@end