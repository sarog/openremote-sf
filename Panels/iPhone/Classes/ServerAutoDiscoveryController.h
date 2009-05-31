//
//  ServerAutoDiscoveryDefinition.h
//  openremote
//
//  Created by finalist on 5/18/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "AsyncSocket.h"
#import "AsyncUdpSocket.h"

@interface ServerAutoDiscoveryController : NSObject {
	AsyncUdpSocket *udpSocket;
	AsyncSocket *tcpSever; 
	NSMutableArray *clients;
	BOOL isReceiveServerUrl;
	NSTimer	 *tcpTImer;
}

- (void)findServer;

@end
