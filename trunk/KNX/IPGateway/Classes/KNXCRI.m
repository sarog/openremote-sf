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
 * KNXCRI.m
 * 
 * Created by Jörg Falkenberg on 24.10.08.
 */

#import "KNXCRI.h"


@implementation KNXCRI
// initialisiert Instanz aus Parametern
-(KNXCRI *)initWithType:(unsigned char)typ
{
	if(self=[super init])
	{
		KNXCRIType=typ;
	}
	return self;
}

// erzeugt Datenfeld aus Instanz
-(void)dataIntoPacket:(NSMutableData *)packet
{
	unsigned char daten[4];
	daten[0]=4;					// 4 Byte Länge
	daten[1]=KNXCRIType;		// Typ
	daten[2]=2;					// KNX Layer: Link Layer Tunnel
	daten[3]=0;					// reserved
	[packet appendBytes:daten length:4];
}

-(NSString *)description
{
	return [NSString stringWithFormat:@"CRI: Type %d\n",KNXCRIType];
}

@end
