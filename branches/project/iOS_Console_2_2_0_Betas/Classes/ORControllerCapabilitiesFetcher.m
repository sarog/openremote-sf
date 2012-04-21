//
//  ORControllerCapabilitiesFetcher.m
//  openremote
//
//  Created by Eric Bariaux on 18/04/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ORControllerCapabilitiesFetcher.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "Capabilities.h"

@interface ORControllerCapabilitiesFetcher ()

@property (nonatomic, retain) ORController *controller;
@property (nonatomic, retain) ControllerRequest *controllerRequest;
@property (nonatomic, retain) NSMutableString *temporaryXMLElementContent;
@property (nonatomic, retain) NSMutableArray *versions;

@end

@implementation ORControllerCapabilitiesFetcher

- (id)initWithController:(ORController *)aController
{
    self = [super init];
    if (self) {
        self.versions = [NSMutableArray array];
        self.controller = aController;
    }
    return self;
}

- (void)dealloc
{
    self.controllerRequest = nil;
    self.versions = nil;
    self.controller = nil;
    [super dealloc];
}

- (void)fetch
{
    NSAssert(!self.controllerRequest, @"ORControllerPanelsFetcher can only be used to send a request once");
    
    ControllerRequest *request = [[ControllerRequest alloc] initWithController:self.controller];
    request.delegate = self;
    self.controllerRequest = request;
    [request release];
    [self.controllerRequest getRequestWithPath:kControllerFetchCapabilitiesPath];
}

#pragma mark NSXMLParserDelegate implementation

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    /*<openremote><rest-api-versions><version>2.0</version><version>2.1</version></rest-api-versions></openremote>*/
	if ([elementName isEqualToString:@"version"]) {
        self.temporaryXMLElementContent = [NSMutableString string];
	}
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    [self.temporaryXMLElementContent appendString:string];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	if ([elementName isEqualToString:@"version"]) {
        [self.versions addObject:[NSDecimalNumber decimalNumberWithString:[self.temporaryXMLElementContent stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]]];
    }
}

#pragma mark ControllerRequestDelegate implementation

- (void)controllerRequestDidFinishLoading:(NSData *)data
{
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:data];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[xmlParser release];
    [delegate fetchCapabilitiesDidSucceedWithCapabilities:[[[Capabilities alloc] initWithSupportedVersions:[NSArray arrayWithArray:self.versions]] autorelease]];
}

// optional TODO EBR is it required
- (void)controllerRequestDidFailWithError:(NSError *)error
{
    if ([delegate respondsToSelector:@selector(fetchCapabilitiesDidFailWithError:)]) {
        [delegate fetchCapabilitiesDidFailWithError:error];
    }
}

- (void)controllerRequestDidReceiveResponse:(NSURLResponse *)response
{
    int statusCode = ((NSHTTPURLResponse *)response).statusCode;
    if (statusCode == 404) {
        // Controller does not support /rest/capabilities call -> return nil capabilities
        [delegate fetchCapabilitiesDidSucceedWithCapabilities:nil];
    } else if (statusCode != 200) {
		[ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
	}
}

- (void)controllerRequestRequiresAuthentication:(ControllerRequest *)request
{
    if ([delegate respondsToSelector:@selector(fetchCapabilitiesRequiresAuthenticationForControllerRequest:)]) {
        [delegate fetchCapabilitiesRequiresAuthenticationForControllerRequest:request];
    }
}

@synthesize controller;
@synthesize controllerRequest;
@synthesize delegate;
@synthesize temporaryXMLElementContent;
@synthesize versions;

@end