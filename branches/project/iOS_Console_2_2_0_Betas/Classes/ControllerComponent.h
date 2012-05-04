//
//  ControllerComponent.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "Component.h"

@class LocalController;

@interface ControllerComponent : Component

/**
 * Only return client side commands, filtering out server side ones that might have been in the XML.
 */
- (NSDictionary *)commandsPerAction:(LocalController *)localController;

@end
