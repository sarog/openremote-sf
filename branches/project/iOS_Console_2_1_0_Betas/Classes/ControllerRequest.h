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
#import <Foundation/Foundation.h>
#import "DataCapturingNSURLConnectionDelegate.h"
#import "UpdateController.h"

@class ORGroupMember;
@class ORController;

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
@interface ControllerRequest : NSObject <DataCapturingNSURLConnectionDelegateDelegate, NSURLConnectionDelegate, UpdateControllerDelegate> {

    NSString *requestPath;
    NSString *method;
	NSURLConnection *connection;

    ORGroupMember *usedGroupMember;
    NSMutableSet *potentialGroupMembers;
    
    NSObject <ControllerRequestDelegate> *delegate;
}

@property (nonatomic, retain) NSObject <ControllerRequestDelegate> *delegate;

- (id)initWithController:(ORController *)aController;

- (void)postRequestWithPath:(NSString *)path;
- (void)getRequestWithPath:(NSString *)path;
- (void)cancel;

@end