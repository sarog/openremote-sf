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
#import "ServerAutoDiscoveryController.h"

@class DefinitionManager;

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
    DefinitionManager *definitionManager;
}

// TODO EBR : should this be assign instead of retain
@property (nonatomic, retain) NSObject <UpdateControllerDelegate> *delegate;

- (id)initWithDelegate:(NSObject <UpdateControllerDelegate> *)aDelegate;

- (void)checkConfigAndUpdate;

@end
