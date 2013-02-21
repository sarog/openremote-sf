//
//  DataCapturingNSURLConnectionDelegate.h
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol DataCapturingNSURLConnectionDelegateDelegate <NSObject>

- (void)connectionDidFinishLoading:(NSURLConnection *)connection receivedData:(NSData *)receiveData;

@end

/**
 * This is a Decorator to be used on objects setting themselves as delegate of NSURLConnection.
 * As data is received, it is captured and handled back in one go once connection is completed.
 * All other delegate methods are forwarded to encapsulated object.
 */
@interface DataCapturingNSURLConnectionDelegate : NSObject {
    
}

- (id)initWithNSURLConnectionDelegate:(NSObject <DataCapturingNSURLConnectionDelegateDelegate> *)aDelegate;

@end
