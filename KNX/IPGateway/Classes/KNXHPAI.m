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
 * KNXHPAI.m
 *
 * Created by Jörg Falkenberg on 24.10.08.
 */

#import "KNXHPAI.h"

@implementation KNXHPAI

// initializes instance from packet
-(KNXHPAI *)initFromPacket:(unsigned char *)packet
{
	unsigned char *headerPtr, c;
	int i;

	if(self=[super init])
	{
		headerPtr=packet;
		c=*headerPtr++;
		if(c!=8)
		{
			[self release];
			return nil;
		}
		c=*headerPtr++;
		if(c!=1)
		{
			[self release];
			return nil;
		}
		ip_adresse=0;
		for(i=0;i<4;i++)
		{
			ip_adresse<<=8;
			ip_adresse+=*headerPtr++;
		}
		port=*headerPtr++;
		port<<=8;
		port+=*headerPtr;
	}
	return self;
}

// initializes instance from parameters
-(KNXHPAI *)initWithAddress:(unsigned long)adresse andPort:(unsigned short)portnummer
{
	if(self=[super init])
	{
		ip_adresse=adresse;
		port=portnummer;
	}
	return self;
}

// creates bitstream data from instance values
-(void)dataIntoPacket:(NSMutableData *)packet
{
	unsigned char daten[8];
	daten[0]=8;		// 8 byte length
	daten[1]=1;		// =UDP over IPv4
	daten[2]=ip_adresse>>24;
	daten[3]=ip_adresse>>16;
	daten[4]=ip_adresse>>8;
	daten[5]=ip_adresse;
	daten[6]=port>>8;
	daten[7]=port;
	[packet appendBytes:daten length:8];
}

-(NSString *)IPAsText
{
	return [NSString stringWithFormat:@"%d.%d.%d.%d",(ip_adresse>>24)&255,(ip_adresse>>16)&255,(ip_adresse>>8)&255,ip_adresse&255];
}
-(NSString *)description
{
	return [NSString stringWithFormat:@"HPAI: IP %08X, Port %d\n",ip_adresse,port];
}
@end
