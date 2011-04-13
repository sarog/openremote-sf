//
//  SipController.h
//  openremote
//
//  Created by Eric Bariaux on 03/03/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

#include "call.h"

@interface SipController : NSObject {

	app_config_t _app_config; // pointer ???
	pjsua_acc_id  _sip_acc_id;
	
}

- (BOOL)sipConnect;
- (BOOL)sipDisconnect;

- (app_config_t *)pjsipConfig;

@end
