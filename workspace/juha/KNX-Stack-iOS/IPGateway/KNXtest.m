/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008, OpenRemote Inc.
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
/*
 *  KNXtest.m
 *  Simple test program for KNX-Stack
 *
 *  Created by Jörg Falkenberg on 07.04.09.
 */
#import "KNXtest.h"
#import "KNXconnection.h"
#import "KNXnetPacket.h"
#import "AsyncUdpSocket.h"

//------- includes

#include <ifaddrs.h>
#include <arpa/inet.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <net/if.h>
#include <net/if_dl.h>
#include <arpa/inet.h>
#include <ifaddrs.h>

#if ! defined(IFT_ETHER)
#define IFT_ETHER 0x6/* Ethernet CSMACD */
#endif

#if TARGET_OS_IPHONE && ! TARGET_IPHONE_SIMULATOR
	#define _INTERFACE	@"en0"
#else
	#define _INTERFACE	@"en1"
#endif

//------- Implementation

@implementation KNXtest

int packets;
BOOL active;

/* called when the connection could not be established */
-(void)aufbauFehlgeschlagen
{
	NSLog(@"Could not connect");
}

/* called when the connection was established */
-(void)verbindungAufgebaut
{
	NSLog(@"Connection successfully established");
}

/* called when connection has been terminated */
-(void)verbindungBeendet
{
	NSLog(@"Connection has been terminated");
}

/* called when a packet has been received */
-(void)paketEmpfangen:(KNXnetPacket *)packet
{
	NSLog(@"Got a packet: %@",[packet description]);
	packets++;
}

/* called when a gateway gave an answer to the search request */
-(void)foundGatewayOnAddress:(NSString *)address ofType:(NSString *)type
{
	NSLog(@"foundGatewayOnAddress: %@, %@",address,type);
}

-(void)timerExpired:(NSTimer *)timer
{
	NSLog(@"Connection timer expired");
	active=FALSE;
}
-(BOOL)isActive
{
	return active;
}
-(void)connectTo:(NSString *)address
{
	packets=0;
	KNXconnection *knxConnection=[[[KNXconnection alloc] init] retain];
	[knxConnection setMeindelegate:self];
	NSLog(@"Connect with %@",address);
	[knxConnection connectWithGateway:address];
	active=TRUE;
	[NSTimer scheduledTimerWithTimeInterval:15
									 target:self 
								   selector:@selector(timerExpired:)
								   userInfo:nil
									repeats:NO];
}
- (NSString*)getWiFiIPAddress
{
	
	BOOL success;
	struct ifaddrs * addrs;
	const struct ifaddrs * cursor;

	success = getifaddrs(&addrs) == 0;
	if (success) {
		cursor = addrs;
		while (cursor != NULL) {
			if (cursor->ifa_addr->sa_family == AF_INET && (cursor->ifa_flags & IFF_LOOPBACK) == 0) // this second test keeps from picking up the loopback address
			{
				NSString *name = [NSString stringWithUTF8String:cursor->ifa_name];
				//NSLog(@"Interface: %@, Address: %@",name,[NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)cursor->ifa_addr)->sin_addr)]);
				if ([name isEqualToString:_INTERFACE]) { // found the WiFi adapter
					NSString *myaddress=[NSString stringWithUTF8String:inet_ntoa(((struct sockaddr_in *)cursor->ifa_addr)->sin_addr)];
					freeifaddrs(addrs);
					return myaddress;
				}
			}
			cursor = cursor->ifa_next;
		}
		freeifaddrs(addrs);
	}
	return NULL;
}

// test function for gateway search
-(void)searchGateway
{
	int counter;

	KNXconnection *knxConnection=[[[KNXconnection alloc] init] retain];
	knxConnection.myIP=[self getWiFiIPAddress];
	[knxConnection setMeindelegate:self];
	[knxConnection searchGateway];

	// now wait 3 seconds for replies
	counter=0;
	while (counter<3)
	{
        [[NSRunLoop currentRunLoop] runUntilDate:[NSDate dateWithTimeIntervalSinceNow:1]];
		NSLog(@"Counter: %d",counter);
		counter++;
	}

	[knxConnection release];
}
@end
