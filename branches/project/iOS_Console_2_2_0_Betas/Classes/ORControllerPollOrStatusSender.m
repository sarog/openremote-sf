//
//  ORControllerPollOrStatusSender.m
//  openremote
//
//  Created by Eric Bariaux on 21/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerPollOrStatusSender.h"
#import "ServerDefinition.h"
#import "Definition.h"
#import "PollingStatusParserDelegate.h"
#import "ORController.h"
#import "ControllerException.h"

@interface ORControllerPollOrStatusSender()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) NSString *ids;
@property (nonatomic, retain) ControllerRequest *controllerRequest;

@end

@implementation ORControllerPollOrStatusSender

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
    self.ids = nil;
    self.controllerRequest = nil;
    self.controller = nil;
    self.delegate = nil;
    [super dealloc];
}

- (void)send
{
    // Don't do anything in this class, subclasses implement as appropriate
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
	PollingStatusParserDelegate *parserDelegate = [[PollingStatusParserDelegate alloc] initWithSensorStatusCache:self.controller.sensorStatusCache];
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
@synthesize ids;
@synthesize controllerRequest;
@synthesize delegate;

@end