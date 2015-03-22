//
//  SipController.h
//  openremote
//
//  Created by Eric Bariaux on 03/03/11.
//  Copyright 2011 OpenRemote, Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#include "linphonecore.h"

typedef enum _Connectivity {
	wifi,
	wwan
	,none
} Connectivity;

@interface SipController : NSObject {
@private
	CFReadStreamRef mReadStream;
	NSTimer* mIterateTimer;
	bool isbackgroundModeEnabled;

	Connectivity connectivity;
	
}

@property Connectivity connectivity;

+(LinphoneCore*) getLc;

-(void) startLibLinphone;
-(void) destroyLibLinphone;

-(void) enterBackgroundMode;
-(void) becomeActive;
//-(void) kickOffNetworkConnection;

@end