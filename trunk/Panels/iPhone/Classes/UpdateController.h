//
//  UpdateController.h
//  openremote
//
//  Created by finalist on 6/7/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ServerAutoDiscoveryController.h"

@interface UpdateController : NSObject {
	id theDelegate;
	ServerAutoDiscoveryController *serverAutoDiscoveryController;
	int retryTimes;
}

- (id)initWithDelegate:(id)delegate;
- (void)setDelegate:(id)delegate;
- (void)checkConfigAndUpdate;

#pragma mark delegate method
- (void)didUpadted;
- (void)didUseLocalCache:(NSString *)errorMessage;

@end
