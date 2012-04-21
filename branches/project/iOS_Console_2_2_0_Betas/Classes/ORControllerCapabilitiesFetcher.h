//
//  ORControllerCapabilitiesFetcher.h
//  openremote
//
//  Created by Eric Bariaux on 18/04/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ControllerRequest.h"

@class ORController;
@class Capabilities;

@protocol ORControllerCapabilitiesFetcherDelegate

- (void)fetchCapabilitiesDidSucceedWithCapabilities:(Capabilities *)capabilities;

@optional
- (void)fetchCapabilitiesDidFailWithError:(NSError *)error;
- (void)fetchCapabilitiesRequiresAuthenticationForControllerRequest:(ControllerRequest *)controllerRequest;

@end

@interface ORControllerCapabilitiesFetcher : NSObject <ControllerRequestDelegate, NSXMLParserDelegate>

@property (nonatomic, assign) NSObject <ORControllerCapabilitiesFetcherDelegate> *delegate;

- (id)initWithController:(ORController *)aController;
- (void)fetch;

@end
