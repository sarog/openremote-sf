//
//  ServerAutoDiscoveryDefinition.m
//  openremote
//
//  Created by finalist on 5/18/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "ServerAutoDiscoveryController.h"
#import "AsyncSocket.h"
#import "AsyncUdpSocket.h"
#import "AppSettingsDefinition.h"
#import "DirectoryDefinition.h"
#import "NotificationConstant.h"

#import "CheckNetworkStaff.h"
@interface ServerAutoDiscoveryController (private)

- (void)findServerFail;
@end


@implementation ServerAutoDiscoveryController

- (void)findServer {
	isReceiveServerUrl = NO;
	clients = [[NSMutableArray alloc] initWithCapacity:1];
	[AppSettingsDefinition removeAllAutoServer];
	
	if (!udpSocket) {
		udpSocket = [[AsyncUdpSocket alloc] initWithDelegate:self]; 
	}
	NSData *d = [@"openremote" dataUsingEncoding:NSUTF8StringEncoding]; 
	NSString *host = @"224.0.1.100"; 
	UInt16 port = 3333;		
	UInt16 serverPort = 2346;
	
	if(![udpSocket sendData:d toHost:host port:port withTimeout:2.0 tag:0]) {
		NSLog(@"Invalid send parameters..."); 
	}
	
	if (!tcpSever) {
		tcpSever = [[AsyncSocket alloc] initWithDelegate:self];
	}
	[tcpSever acceptOnPort:serverPort error:NULL];
	tcpTImer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(findServerFail) userInfo:nil repeats:NO];
	
}
	
- (void)findServerFail {
	if (!isReceiveServerUrl) {
		[tcpTImer invalidate];
		[tcpSever disconnect];
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationFindServerFail object:self];
	}
	isReceiveServerUrl = NO;
}


- (void)onUdpSocket:(AsyncUdpSocket *)sock didSendDataWithTag:(long)tag {
	NSString *myString = [NSString stringWithFormat:@"Count +1"];
	NSLog(myString); 
	[sock close];	
}

- (void)onUdpSocket:(AsyncUdpSocket *)sock
didNotSendDataWithTag:(long)tag dueToError:(NSError *)error{
	NSLog(@"DidNotSend: %@", error);
	[self findServerFail];
} 

- (void)onSocket:(AsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag {
	isReceiveServerUrl = YES;
	NSLog(@"receive data from server");
	NSString *serverUrl = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	NSLog(@"read server url from controller %@",serverUrl);
	
	NSMutableDictionary *server = [NSMutableDictionary dictionaryWithObject:serverUrl forKey:@"url"];
	[server setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
	[AppSettingsDefinition removeAllAutoServer];
	[AppSettingsDefinition addAutoServer:server];
	[AppSettingsDefinition writeToFile];
	[AppSettingsDefinition setCurrentServerUrl:serverUrl];
	
	NSLog(@"current url at receive socket %@",[AppSettingsDefinition getCurrentServerUrl]);
	[serverUrl release];
	for(int i = 0; i < [clients count]; i++)
	{
		// Call disconnect on the socket,
		// which will invoke the onSocketDidDisconnect: method,
		// which will remove the socket from the list.
		[[clients objectAtIndex:i] disconnect];
	}
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationAfterFindServer object:nil];
}

- (void)onSocket:(AsyncSocket *)sock didReadPartialDataOfLength:(CFIndex)partialLength tag:(long)tag {
	NSLog(@"receive partial data");
}
- (void)onSocket:(AsyncSocket *)sock didAcceptNewSocket:(AsyncSocket *)newSocket {
	NSLog(@"receive new socket");
	[clients addObject:newSocket];
	[newSocket setDelegate:self];
	[newSocket readDataWithTimeout:10 tag:0];
}

//- (void)onSocketDidDisconnect:(AsyncSocket *)sock {
//
//}

//- (void)onSocket:(AsyncSocket *)sock didWriteDataWithTag:(long)tag {
//	
//}

- (void)onSocket:(AsyncSocket *)sock didConnectToHost:(NSString *)host port:(UInt16)port{
	NSLog(@"receive socket host %@ port %d",host,port);
	
}

- (void)dealloc {
	[tcpTImer invalidate];
	[tcpTImer release];
	[clients release];
	[tcpSever disconnect];
	[tcpSever release];
	[udpSocket close];
	[udpSocket release];
	[super dealloc];


}

@end
