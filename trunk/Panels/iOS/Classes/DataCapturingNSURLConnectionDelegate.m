//
//  DataCapturingNSURLConnectionDelegate.m
//  openremote
//
//  Created by Eric Bariaux on 13/07/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import "DataCapturingNSURLConnectionDelegate.h"

#define NUM_DELEGATE_METHODS 10

@interface DataCapturingNSURLConnectionDelegate ()

@property (nonatomic, retain) NSObject <DataCapturingNSURLConnectionDelegateDelegate> *delegate;
@property (nonatomic, retain) NSMutableData *receiveData;

@end

@implementation DataCapturingNSURLConnectionDelegate

@synthesize delegate;
@synthesize receiveData;

static SEL connectionDelegateSelectors[NUM_DELEGATE_METHODS];

+ (void)initialize
{
    connectionDelegateSelectors[0] = @selector(connection:willSendRequest:redirectResponse:);
    connectionDelegateSelectors[1] = @selector(connection:needNewBodyStream:);
    connectionDelegateSelectors[2] = @selector(connection:canAuthenticateAgainstProtectionSpace:);
    connectionDelegateSelectors[3] = @selector(connection:didReceiveAuthenticationChallenge:);
    connectionDelegateSelectors[4] = @selector(connection:didCancelAuthenticationChallenge:);
    connectionDelegateSelectors[5] = @selector(connectionShouldUseCredentialStorage:);
    connectionDelegateSelectors[6] = @selector(connection:didReceiveResponse:);
    connectionDelegateSelectors[7] = @selector(connection:didSendBodyData:totalBytesWritten:totalBytesExpectedToWrite:);
    connectionDelegateSelectors[8] = @selector(connection:didFailWithError:);
    connectionDelegateSelectors[9] = @selector(connection:willCacheResponse:);
}

- (id)initWithNSURLConnectionDelegate:(NSObject <DataCapturingNSURLConnectionDelegateDelegate> *)aDelegate
{
    self = [super init];
    if (self) {
        self.delegate = aDelegate;
        self.receiveData = [NSMutableData dataWithCapacity:0];
    }
    return self;
}

- (void)dealloc
{
    self.delegate = nil;
    self.receiveData = nil;
    [super dealloc];
}

#pragma mark -

- (BOOL)isDelegateSelector:(SEL)selector
{
    for (int i = 0; i < NUM_DELEGATE_METHODS; i++) {
        if (connectionDelegateSelectors[i] == selector) {
            return YES;
        }
    }
    return NO;
}

- (BOOL)respondsToSelector:(SEL)aSelector
{
    if ([self isDelegateSelector:aSelector]) {
        return [delegate respondsToSelector:aSelector];
    }
    return [super respondsToSelector:aSelector];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [receiveData appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [delegate connectionDidFinishLoading:connection receivedData:receiveData];
}

#pragma mark - Forwarded delegate methods

- (NSURLRequest *)connection:(NSURLConnection *)connection willSendRequest:(NSURLRequest *)request redirectResponse:(NSURLResponse *)response
{
    return [delegate connection:connection willSendRequest:request redirectResponse:response];
}

- (NSInputStream *)connection:(NSURLConnection *)connection needNewBodyStream:(NSURLRequest *)request
{
    return [delegate connection:connection needNewBodyStream:request];
}

- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace
{
    return [delegate connection:connection canAuthenticateAgainstProtectionSpace:protectionSpace];
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    [delegate connection:connection didReceiveAuthenticationChallenge:challenge];
}

- (void)connection:(NSURLConnection *)connection didCancelAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    [delegate connection:connection didCancelAuthenticationChallenge:challenge];
}

- (BOOL)connectionShouldUseCredentialStorage:(NSURLConnection *)connection
{
    return [delegate connectionShouldUseCredentialStorage:connection];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    return [delegate connection:connection didReceiveResponse:response];
}

- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite
{
    [delegate connection:connection didSendBodyData:bytesWritten totalBytesWritten:totalBytesWritten totalBytesExpectedToWrite:totalBytesExpectedToWrite];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    [delegate connection:connection didFailWithError:error];
}

- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse
{
    return [delegate connection:connection willCacheResponse:cachedResponse];
}

@end
