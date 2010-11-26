/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */


#import "ServerAutoDiscoveryController.h"
#import "AsyncSocket.h"
#import "AsyncUdpSocket.h"
#import "AppSettingsDefinition.h"
#import "DirectoryDefinition.h"
#import "NotificationConstant.h"
#import "CheckNetwork.h"


@interface ServerAutoDiscoveryController (Private)
- (void)checkFindServerFail;
@end


@implementation ServerAutoDiscoveryController

//Setup autodiscovery and start. 
// Needn't call annother method to send upd and start tcp server.
- (id)init {			
	if (self = [super init]) {
		
		isReceiveServerUrl = NO;
		//Store the received TcpClient sockets.
		clients = [[NSMutableArray alloc] initWithCapacity:1];
		udpSocket = [[AsyncUdpSocket alloc] initWithDelegate:self]; 
		
		// init the data send to multicast server
		NSData *d = [@"openremote" dataUsingEncoding:NSUTF8StringEncoding]; 
		// init the multicast ip
		NSString *host = @"224.0.1.100"; 
		// init the multicast port
		UInt16 port = 3333;
		// Send the data to multicast ip with timeout 3 seconds.
		[udpSocket sendData:d toHost:host port:port withTimeout:3 tag:0];
		[udpSocket closeAfterSending];
		
		// init the tcp server port
		UInt16 serverPort = 2346;
		//Setup a tcp server recevice the multicast feedback.
		tcpSever = [[AsyncSocket alloc] initWithDelegate:self];
		[tcpSever acceptOnPort:serverPort error:NULL];
		
		//Set a timer with 3 interval.
		tcpTimer = [[NSTimer scheduledTimerWithTimeInterval:3 target:self selector:@selector(checkFindServerFail) userInfo:nil repeats:NO] retain];
		
	}
	return self;
}




- (void)reTry {
	
}

- (id)initWithDelegate:(id)d {
	if(d) {
		[d retain];
	}
	theDelegate = d;
	return [self init];
}


- (void)setDelegate:(id)delegate {
	if (delegate != nil) {
		[delegate retain];
		[theDelegate release];
		theDelegate = delegate;
	} else {
		[theDelegate release];
		theDelegate = nil;
	}
}

//check where find server time out.
- (void)checkFindServerFail {
	if (!isReceiveServerUrl) {
		[self onFindServerFail:@"No Controller detected."];
	}
	isReceiveServerUrl = NO;	
}

//after find server		
- (void)onFindServer:(NSString *)serverUrl {
	isReceiveServerUrl = YES;
	NSMutableDictionary *server = [NSMutableDictionary dictionaryWithObject:serverUrl forKey:@"url"];
	if([AppSettingsDefinition getAutoServers].count ==0) {
		[server setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
	} else {
		[server setValue:[NSNumber numberWithBool:NO] forKey:@"choose"];
	}
	// Add finded auto server url 
	[AppSettingsDefinition addAutoServer:server];
	// Write the result into plist file
	[AppSettingsDefinition writeToFile];
	
	NSLog(@"current url at receive socket %@",[AppSettingsDefinition getCurrentServerUrl]);

    //Disconnect all the tcp client received
	for(int i = 0; i < [clients count]; i++)
	{
		// Call disconnect on the socket,
		// which will invoke the onSocketDidDisconnect: method,
		// which will remove the socket from the list.
		[[clients objectAtIndex:i] disconnect];
		[clients removeObjectAtIndex:i];
	}
	[clients release];
	clients = nil;
	
	
	[tcpSever disconnectAfterReading];
	[tcpTimer invalidate];
	
			
	// Call the delegate method delegate implemented
//	if (theDelegate && [theDelegate  respondsToSelector:@selector(onFindServer:)]) {
//		NSLog(@"performSelector onFindServer");
//		[theDelegate performSelector:@selector(onFindServer:) withObject:serverUrl];
//	}
}
		
//after find server fail
- (void)onFindServerFail:(NSString *)errorMessage{
	[tcpTimer invalidate];
	// Call the delegate method delegate implemented
	if (theDelegate && [theDelegate  respondsToSelector:@selector(onFindServerFail:)]) {
		NSLog(@"performSelector onFindServerFail");
		[theDelegate performSelector:@selector(onFindServerFail:) withObject:errorMessage];
	}
}
		
#pragma mark UdpSocket delegate method
- (void)onUdpSocket:(AsyncUdpSocket *)sock didSendDataWithTag:(long)tag {
	NSLog(@"%@", [NSString stringWithFormat:@"onUdpSocket didSendData."]); 
	[sock close];	
}

//Called after AsyncUdpSocket did not send data 
- (void)onUdpSocket:(AsyncUdpSocket *)sock
didNotSendDataWithTag:(long)tag dueToError:(NSError *)error{
	NSLog(@"DidNotSend: %@", error);
	[self onFindServerFail:[error localizedDescription]];
} 

#pragma mark TCPSocket delegate method		
- (void)onSocket:(AsyncSocket *)sock didReadData:(NSData *)data withTag:(long)tag {
	
	NSLog(@"receive data from server");
	NSString *serverUrl = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
	NSLog(@"read server url from controller %@",serverUrl);	
	[serverUrl autorelease];
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

- (void)dealloc {
	if (tcpTimer && [tcpTimer isValid])  {
		[tcpTimer invalidate];
		[tcpTimer release];
	}
	NSLog(@"clients count is %d",[clients count]);
	for(int i = 0; i < [clients count]; i++)
	{
		
		[[clients objectAtIndex:i] disconnect];
		[clients removeObjectAtIndex:i];
	}
	[clients release];
	
	[tcpSever release];
	[udpSocket release];
	
	if (theDelegate) {
		[theDelegate release];
		theDelegate = nil;
	}
	
	[super dealloc];
	
}
@end
