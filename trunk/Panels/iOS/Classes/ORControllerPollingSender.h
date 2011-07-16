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

//
//  ORControllerPollingSender.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ControllerRequest.h"
#import "UpdateController.h"

@class ORController;

@protocol ORControllerPollingSenderDelegate <NSObject>

// Note that this applies to both polling and status request, even if the name would indicate otherwise
- (void)pollingDidFailWithError:(NSError *)error;
- (void)pollingDidSucceed;
- (void)pollingDidTimeout;
- (void)pollingDidReceiveErrorResponse;

@end

@interface ORControllerPollingSender : NSObject <ControllerRequestDelegate, UpdateControllerDelegate> {
    NSString *ids;
    ControllerRequest *controllerRequest;
    
    NSObject <ORControllerPollingSenderDelegate> *delegate;
}

@property (nonatomic, assign) NSObject <ORControllerPollingSenderDelegate> *delegate;

- (id)initWithController:(ORController *)aController ids:(NSString *)someIds;
- (void)requestStatus;
- (void)poll;

- (void)cancel;

@end