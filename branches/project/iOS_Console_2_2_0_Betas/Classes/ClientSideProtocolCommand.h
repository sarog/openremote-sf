//
//  ClientSideProtocolCommand.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class ClientSideRuntime;
@class LocalSensor;

@protocol ClientSideProtocolCommand <NSObject>

- (id)initWithRuntime:(ClientSideRuntime *)runtime;

@end
