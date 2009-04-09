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

-(void)timerExpired:(NSTimer *)timer
{
	NSLog(@"Timer expired");
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

@end
