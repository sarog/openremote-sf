//
//  ControllerComponent.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "Component.h"

@class LocalCommand;
@class LocalController;

@interface ControllerComponent : Component

- (void)addCommand:(LocalCommand *)aCommand forAction:(NSString *)anAction;
- (NSDictionary *)commandsPerAction;

@end
