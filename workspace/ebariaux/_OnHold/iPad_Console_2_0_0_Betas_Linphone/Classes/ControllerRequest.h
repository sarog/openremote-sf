/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import <Foundation/Foundation.h>

@class ORGroupMember;

@protocol ControllerRequestDelegate <NSObject>

- (void)controllerRequestDidFinishLoading:(NSData *)data;

@optional
- (void)controllerRequestDidFailWithError:(NSError *)error;
- (void)controllerRequestDidReceiveResponse:(NSURLResponse *)response;

// TODO EBR : do we really want to pass URL classes back to our delegate ? this should be hidden

@end

/**
 * This classes uses the same pattern as NSURLConnection for memory management of delegate.
 * It is retained and released when the connection is finished (after NSURLConnection sent
 * connectionDidFinishLoading: or connection:didFailWithError:
 */
@interface ControllerRequest : NSObject {

    NSString *requestPath;
    NSString *method;
    NSMutableData *receivedData;
	NSURLConnection *connection;
    NSError* lastError;

    ORGroupMember *usedGroupMember;
    NSMutableSet *potentialGroupMembers;
    
    NSObject <ControllerRequestDelegate> *delegate;
}

@property (nonatomic, retain) NSObject <ControllerRequestDelegate> *delegate;

- (void)postRequestWithPath:(NSString *)path;
- (void)getRequestWithPath:(NSString *)path;
- (void)cancel;

@end