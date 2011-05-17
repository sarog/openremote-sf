/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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
#import "AsyncSocket.h"
#import "AsyncUdpSocket.h"

@protocol ServerAutoDiscoveryControllerDelagate <NSObject>

- (void)onFindServer:(NSString *)serverUrl;
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
