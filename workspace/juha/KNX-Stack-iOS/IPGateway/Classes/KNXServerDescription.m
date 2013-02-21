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
 * KNXServerDescription.m
 *
 * Created by Jörg Falkenberg on 28.06.09.
 */


#import "KNXServerDescription.h"


@implementation KNXServerDescription
// initializes instance from packet
-(KNXServerDescription *)initFromPacket:(unsigned char *)packet
{
	unsigned char *headerPtr, c;
	
	if(self=[super init])
	{
		headerPtr=packet;
		c=*headerPtr++;
		if(c!=54)
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
		headerPtr+=2;	// skip medium and status
		deviceAddress=[[KNXGroupAddress alloc] initAusZweiByteAdresse:headerPtr ofType:NO];
		headerPtr+=14;  // skip installation identifier, serial number, multicast address
		MACAddress = [NSString stringWithFormat:@"%02X:%02X:%02X:%02X:%02X:%02X",headerPtr[0],headerPtr[1],headerPtr[2],headerPtr[3],headerPtr[4],headerPtr[5]];
		headerPtr+=6;
		friendlyName = [NSString stringWithCString:(char *)headerPtr encoding:NSASCIIStringEncoding];
#ifdef _DEBUG
		NSLog(@"Found %@",friendlyName);
#endif
	}
	return self;
}

-(NSString *)friendlyName
{
	return friendlyName;
}

-(NSString *)description
{
	return [NSString stringWithFormat:@"KNX gateway '%@', KNX address %@, MAC %@",friendlyName,[deviceAddress description],MACAddress];
}
@end
