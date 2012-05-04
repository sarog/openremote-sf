//
//  ClientSideProtocol.h
//  openremote
//
//  Created by Eric Bariaux on 04/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@class LocalCommand;

@protocol ClientSideProtocol <NSObject>

- (void)executeCommand:(LocalCommand *)command;

@end
