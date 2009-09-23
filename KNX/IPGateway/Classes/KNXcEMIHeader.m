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
 *  KNXcEMIHeader.m
 *
 *  Created by Jörg Falkenberg on 08.10.08.
 */

#import <Foundation/NSObject.h>
#import "KNXcEMIHeader.h"

@class NSMutableData;

@implementation KNXcEMIHeader

@synthesize source, destination;

-(id)initFromPacket:(NSData *)packet
{
	NSRange range;

	if(self=[super init])
	{
		/* Instance was created */
		unsigned char *headerPtr;
		headerPtr=(unsigned char *)[packet bytes];
		messageCode=*headerPtr++;
		additionalInfoLength=*headerPtr++;
		headerPtr+=additionalInfoLength;
		switch(messageCode)
		{
			case KNX_CEMI_L_DATA_IND:
			case KNX_CEMI_L_DATA_CON:
				/* L_data.ind */
				ctrl1=*headerPtr++;
				ctrl2=*headerPtr++;
				source=[[KNXDeviceAddress alloc] initAusZweiByteAdresse:headerPtr];
				headerPtr+=2;
				destination=[[KNXGroupAddress alloc] initAusZweiByteAdresse:headerPtr ofType:ctrl2&0x80?YES:NO];
				headerPtr+=2;
				// payload starting here
				range.location=2+additionalInfoLength+6;
				range.length=[packet length]-range.location;
				payload=[packet subdataWithRange:range];
				break;
		}
	}
	return self;
}
-(void)initWithControl1:(unsigned char)control1 Control2:(unsigned char)control2 MessageCode:(unsigned char)code
{
	ctrl1=control1;
	ctrl2=control2;
	messageCode=code;
}

// copies data into packet
-(void)dataIntoPacket:(NSMutableData *)packet
{
	unsigned char daten[4];
	daten[0]=messageCode;		// cEMI message code
	daten[1]=0;					// no additional data
	daten[2]=ctrl1;				// Control 1
	daten[3]=ctrl2;				// Control 2
	[packet appendBytes:daten length:4];
}

-(unsigned char)messageCode
{
	return messageCode;
}
-(NSData *)payload
{
	return payload;
}
-(NSString *)description
{
	return [NSString stringWithFormat:@"cEMI packet: message code %02X from %@ to %@\n",messageCode,[source description],[destination description]];
}

-(void)dealloc
{
	[source release];
	[destination release];
	[super dealloc];
}
@end
