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
#import "ServerAutoDiscoveryController.h"

@protocol UpdateControllerDelegate <NSObject>

/**
 * This method will be called after update did finished.
 */
- (void)didUpdate;

/**
 * This method will be called after application choose to use local cache.
 */
- (void)didUseLocalCache:(NSString *)errorMessage;

/**
 * This method will be called after update failed and application can't use local cache.
 */
- (void)didUpdateFail:(NSString *)errorMessage;

@end

/**
 * It's responsible for checking network, download panel.xml, parse panel.xml and notify DefaultViewController to refresh views.
 */
@interface UpdateController : NSObject <NSXMLParserDelegate, ServerAutoDiscoveryControllerDelagate> {
	NSObject <UpdateControllerDelegate> *delegate;
	ServerAutoDiscoveryController *serverAutoDiscoveryController;
	int retryTimes;
}

// TODO EBR : should this be assign instead of retain
@property (nonatomic, retain) NSObject <UpdateControllerDelegate> *delegate;

- (id)initWithDelegate:(NSObject <UpdateControllerDelegate> *)aDelegate;

- (void)checkConfigAndUpdate;

- (void)getRoundRobinGroupMembers;

@end
