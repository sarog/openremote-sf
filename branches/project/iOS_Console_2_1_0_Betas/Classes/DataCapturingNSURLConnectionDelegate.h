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

@interface DataCapturingNSURLConnectionDelegate : NSObject {
    
}

- (id)initWithNSURLConnectionDelegate:(NSObject <DataCapturingNSURLConnectionDelegateDelegate> *)aDelegate;

@end
