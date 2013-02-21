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
 * KNXDeviceAddress.m
 *
 * Created by Jörg Falkenberg on 24.10.08.
 */

#import "KNXDeviceAddress.h"

@implementation KNXDeviceAddress

// converts 2 byte into device address
-(id)initAusZweiByteAdresse:(unsigned char *)daten
{
	if(self=[super init])
	{
		adresse[0]=*daten>>4;
		adresse[1]=*daten&15;
		adresse[2]=*(daten+1);
	}
	return self;
}
-(id)initMitDreibyteAdresse:(unsigned char *)daten
{
	if(self=[super init])
	{
		adresse[0]=daten[0];
		adresse[1]=daten[1];
		adresse[2]=daten[2];
	}
	return self;
}
-(id)initMitLeererAdresse
{
	if(self=[super init])
	{
		adresse[0]=0;
		adresse[1]=0;
		adresse[2]=0;
	}
	return self;
}
// copies data into packet
-(void)dataIntoPacket:(NSMutableData *)packet
{
	unsigned char daten[2];
	daten[0]=(adresse[0]<<4) | adresse[1];
	daten[1]=adresse[2];
	[packet appendBytes:daten length:2];
}

-(NSString *)description
{
	return [NSString stringWithFormat:@"%d.%d.%d",adresse[0],adresse[1],adresse[2]];
}

@end

@implementation KNXGroupAddress

// convert 2 byte into KNX address
// 'welcher' is YES for group address, NO for device address
-(id)initAusZweiByteAdresse:(unsigned char *)daten ofType:(short)welcher
{
	if(self=[super init])
	{
		typ=welcher;
		if(welcher)
		{
			adresse[0]=*daten>>3;
			adresse[1]=*daten&7;
			adresse[2]=*(daten+1);
		}
		else
		{
			adresse[0]=*daten>>4;
			adresse[1]=*daten&15;
			adresse[2]=*(daten+1);
		}
	}
	return self;
}
-(id)initMitDreibyteAdresse:(unsigned char *)daten
{
	if(self=[super init])
	{
		adresse[0]=daten[0];
		adresse[1]=daten[1];
		adresse[2]=daten[2];
	}
	return self;
}
// copies data into packet
-(void)dataIntoPacket:(NSMutableData *)packet
{
	unsigned char daten[2];
	daten[0]=(adresse[0]<<3) | adresse[1];
	daten[1]=adresse[2];
	[packet appendBytes:daten length:2];
}

-(NSString *)description
{
	if(typ)
		return [NSString stringWithFormat:@"%d/%d/%d",adresse[0],adresse[1],adresse[2]];
	else
		return [NSString stringWithFormat:@"%d.%d.%d",adresse[0],adresse[1],adresse[2]];
}
-(short)typ
{
	return typ;
}
@end
