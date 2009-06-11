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


@interface ServerAutoDiscoveryController (Private)
- (void)checkFindServerFail;
@end


@implementation ServerAutoDiscoveryController

- (void)findServerWithDelegate:(id)delegate {
	[self setDelegate:delegate];
	isReceiveServerUrl = NO;
	if (!clients) {
		clients = [[NSMutableArray alloc] initWithCapacity:1];
	}
	[AppSettingsDefinition removeAllAutoServer];
	
	if (!udpSocket) {
		udpSocket = [[AsyncUdpSocket alloc] initWithDelegate:self]; 
	}
	NSData *d = [@"openremote" dataUsingEncoding:NSUTF8StringEncoding]; 
	NSString *host = @"224.0.1.100"; 
	UInt16 port = 3333;		
	UInt16 serverPort = 2346;
	[udpSocket sendData:d toHost:host port:port withTimeout:1 tag:0];
	
	
	if (!tcpSever) {
		tcpSever = [[AsyncSocket alloc] initWithDelegate:self];
	}
	[tcpSever acceptOnPort:serverPort error:NULL];
	tcpTImer = [NSTimer scheduledTimerWithTimeInterval:5 target:self selector:@selector(checkFindServerFail) userInfo:nil repeats:NO];
}

- (void)setDelegate:(id)delegate {
	[delegate retain];
	[theDelegate release];
	theDelegate = delegate;
}

- (void)checkFindServerFail {
	if (!isReceiveServerUrl) {
		[self onFindServerFail:@"Find Server Timeout"];
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
	[self onFindServerFail:[error localizedDescription]];
} 

- (void)onSocket:(AsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag {
	isReceiveServerUrl = YES;
	NSLog(@"receive data from server");
	NSString *serverUrl = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	NSLog(@"read server url from controller %@",serverUrl);
	[self onFindServer:serverUrl];
	
}

- (void)onSocket:(AsyncSocket *)sock didAcceptNewSocket:(AsyncSocket *)newSocket {
	NSLog(@"receive new socket");
	[clients addObject:newSocket];
	[newSocket setDelegate:self];
	[newSocket readDataWithTimeout:10 tag:0];
}

- (void)onSocket:(AsyncSocket *)sock didConnectToHost:(NSString *)host port:(UInt16)port{
	NSLog(@"receive socket host %@ port %d",host,port);
}


- (void)onFindServer:(NSString *)serverUrl {
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
	
	if (theDelegate && [theDelegate  respondsToSelector:@selector(onFindServer:)]) {
		[theDelegate performSelector:@selector(onFindServer:) withObject:serverUrl];
	}
}
- (void)onFindServerFail:(NSString *)errorMessage{
	[tcpTImer invalidate];
	[tcpSever disconnect];
	if (theDelegate && [theDelegate  respondsToSelector:@selector(onFindServerFail:)]) {
		[theDelegate performSelector:@selector(onFindServerFail:) withObject:errorMessage];
	}
}

- (void)dealloc {
	[tcpTImer invalidate];
	[tcpTImer release];
	[clients release];
	[tcpSever disconnect];
	[tcpSever release];
	[udpSocket close];
	[udpSocket release];
	[theDelegate release];
	[super dealloc];


}

@end
