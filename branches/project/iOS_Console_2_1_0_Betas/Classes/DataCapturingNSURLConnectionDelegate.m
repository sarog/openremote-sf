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
#import "DataCapturingNSURLConnectionDelegate.h"
#import "RuntimeUtils.h"

#define NUM_DELEGATE_METHODS 10

@interface DataCapturingNSURLConnectionDelegate ()

@property (nonatomic, retain) NSObject <DataCapturingNSURLConnectionDelegateDelegate> *delegate;
@property (nonatomic, retain) NSMutableData *receiveData;

@end

@implementation DataCapturingNSURLConnectionDelegate

static NSArray* connectionDelegateSelectors;

+ (void)initialize
{
    NSMutableArray *tmp = [NSMutableArray array];
    
    // TODO: NSURLConnectionDataDelegate extends NSURLConnectionDelegate, should be able to get methods from there
    
    // Doing a dynamic lookup of methods in the NSURLConnectionDelegate protocol did not work, adding them manually
    [tmp addObject:[NSValue valueWithPointer:@selector(connection:didFailWithError:)]];
    [tmp addObject:[NSValue valueWithPointer:@selector(connectionShouldUseCredentialStorage:)]];
    [tmp addObject:[NSValue valueWithPointer:@selector(connection:willSendRequestForAuthenticationChallenge:)]];
    [tmp addObject:[NSValue valueWithPointer:@selector(connection:canAuthenticateAgainstProtectionSpace:)]];
    [tmp addObject:[NSValue valueWithPointer:@selector(connection:didReceiveAuthenticationChallenge:)]];
    [tmp addObject:[NSValue valueWithPointer:@selector(connection:didCancelAuthenticationChallenge:)]];
    
    [tmp addObjectsFromArray:[RuntimeUtils selectorsFromProtocol:@protocol(NSURLConnectionDataDelegate)]];
    
    // Make sure methods we do implement are not in that array otherwhise we might not get the call
    [tmp removeObject:[NSValue valueWithPointer:@selector(connection:didReceiveData:)]];
    [tmp removeObject:[NSValue valueWithPointer:@selector(connectionDidFinishLoading:)]];
    
    connectionDelegateSelectors = [[NSArray arrayWithArray:tmp] retain];
}

- (id)initWithNSURLConnectionDelegate:(id <DataCapturingNSURLConnectionDelegateDelegate>)aDelegate;
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
    return [connectionDelegateSelectors containsObject:[NSValue valueWithPointer:selector]];
}

- (BOOL)respondsToSelector:(SEL)aSelector
{
    if ([self isDelegateSelector:aSelector]) {
        return [self.delegate respondsToSelector:aSelector];
    }
    return [super respondsToSelector:aSelector];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    [self.receiveData appendData:data];
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    [self.delegate connectionDidFinishLoading:connection receivedData:self.receiveData];
}

#pragma mark - Forwarded NSURLConnectionDelegate methods

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    if ([self.delegate respondsToSelector:@selector(connection:didFailWithError:)]) {
        [self.delegate connection:connection didFailWithError:error];
    }
}

- (BOOL)connectionShouldUseCredentialStorage:(NSURLConnection *)connection
{
    if ([self.delegate respondsToSelector:@selector(connectionShouldUseCredentialStorage:)]) {
        return [self.delegate connectionShouldUseCredentialStorage:connection];
    }
    return NO;
}

- (void)connection:(NSURLConnection *)connection willSendRequestForAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    if ([self.delegate respondsToSelector:@selector(connection:willSendRequestForAuthenticationChallenge:)]) {
        [self.delegate connection:connection willSendRequestForAuthenticationChallenge:challenge];
    }
}

- (BOOL)connection:(NSURLConnection *)connection canAuthenticateAgainstProtectionSpace:(NSURLProtectionSpace *)protectionSpace
{
    if ([self.delegate respondsToSelector:@selector(connection:canAuthenticateAgainstProtectionSpace:)]) {
        return [self.delegate connection:connection canAuthenticateAgainstProtectionSpace:protectionSpace];
    }
    return NO;
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    if ([self.delegate respondsToSelector:@selector(connection:didReceiveAuthenticationChallenge:)]) {
        [self.delegate connection:connection didReceiveAuthenticationChallenge:challenge];
    }
}

- (void)connection:(NSURLConnection *)connection didCancelAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge
{
    if ([self.delegate respondsToSelector:@selector(connection:didCancelAuthenticationChallenge:)]) {
        [self.delegate connection:connection didCancelAuthenticationChallenge:challenge];
    }
}

#pragma mark - Forwarded NSURLConnectionDataDelegate methods

- (NSURLRequest *)connection:(NSURLConnection *)connection willSendRequest:(NSURLRequest *)request redirectResponse:(NSURLResponse *)response
{
    if ([self.delegate respondsToSelector:@selector(connection:willSendRequest:redirectResponse:)]) {
        return [self.delegate connection:connection willSendRequest:request redirectResponse:response];
    }
    return request;
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    if ([self.delegate respondsToSelector:@selector(connection:didReceiveResponse:)]) {
        [self.delegate connection:connection didReceiveResponse:response];
    }
}

- (NSInputStream *)connection:(NSURLConnection *)connection needNewBodyStream:(NSURLRequest *)request
{
    if ([self.delegate respondsToSelector:@selector(connection:needNewBodyStream:)]) {
        return [self.delegate connection:connection needNewBodyStream:request];
    }
    return nil;
}

- (void)connection:(NSURLConnection *)connection didSendBodyData:(NSInteger)bytesWritten totalBytesWritten:(NSInteger)totalBytesWritten totalBytesExpectedToWrite:(NSInteger)totalBytesExpectedToWrite
{
    if ([self.delegate respondsToSelector:@selector(connection:didSendBodyData:totalBytesWritten:totalBytesExpectedToWrite:)]) {
        [self.delegate connection:connection didSendBodyData:bytesWritten totalBytesWritten:totalBytesWritten totalBytesExpectedToWrite:totalBytesExpectedToWrite];
    }
}

- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse
{
    if ([self.delegate respondsToSelector:@selector(connection:willCacheResponse:)]) {
        return [self.delegate connection:connection willCacheResponse:cachedResponse];
    }
    return cachedResponse;
}

@synthesize delegate;
@synthesize receiveData;

@end