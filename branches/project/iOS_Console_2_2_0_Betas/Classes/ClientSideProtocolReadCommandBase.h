//
//  ClientSideProtocolReadCommandBase.h
//  openremote
//
//  Created by Eric Bariaux on 09/05/12.
//  Copyright (c) 2012 OpenRemote, Inc. All rights reserved.
//

#import "ClientSideProtocolReadCommand.h"

@interface ClientSideProtocolReadCommandBase : NSObject <ClientSideProtocolReadCommand>

- (void)publishValue;

@end