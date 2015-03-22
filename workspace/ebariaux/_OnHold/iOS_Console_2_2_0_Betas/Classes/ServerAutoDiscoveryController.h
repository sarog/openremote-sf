/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
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
#import "AsyncSocket.h"
#import "AsyncUdpSocket.h"

@class ORController;

@protocol ServerAutoDiscoveryControllerDelagate <NSObject>

- (void)onFindServer:(ORController *)aController;
- (void)onFindServerFail:(NSString *)errorMessage;

@end

/**
 * It's responsible for controller server  discovery automatically.
 */
@interface ServerAutoDiscoveryController : NSObject {
	id <ServerAutoDiscoveryControllerDelagate>delegate;
    
	AsyncUdpSocket *udpSocket;
	AsyncSocket *tcpSever; 
	NSMutableArray *clients;
	BOOL isReceiveServerUrl;
	NSTimer	 *tcpTimer;
}

- (id)initWithDelegate:(id <ServerAutoDiscoveryControllerDelagate>)aDelegate;

// TODO EBR : is it OK to have this delegate assign instead of retain ?
@property (nonatomic, retain) id <ServerAutoDiscoveryControllerDelagate>delegate;

@end
