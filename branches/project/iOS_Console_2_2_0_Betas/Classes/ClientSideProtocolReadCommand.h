//
//  ClientSideProtocolReadCommand.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ClientSideProtocolCommand.h"

@protocol ClientSideProtocolReadCommand <ClientSideProtocolCommand>

- (void)startUpdatingSensor:(LocalSensor *)sensor;
- (void)stopUpdatingSensor:(LocalSensor *)sensor;

@end