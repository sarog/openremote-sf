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
 *  KNXcEMIHeader.h
 *
 *  Created by Jörg Falkenberg on 08.10.08.
*/

#import <Foundation/Foundation.h>
#import "KNXDeviceAddress.h"

/* often used message codes */
#define KNX_CEMI_L_DATA_REQ 0x11
#define KNX_CEMI_L_DATA_IND	0x29
#define KNX_CEMI_L_DATA_CON	0x2E

@interface KNXcEMIHeader : NSObject {
	unsigned char messageCode;
	unsigned char additionalInfoLength;
	unsigned char communicationChannel;
	unsigned char sequenceCounter;
	unsigned char ctrl1;
	unsigned char ctrl2;
	KNXDeviceAddress *source;
	KNXGroupAddress *destination;
	NSData *payload;
}

@property (nonatomic, retain) KNXDeviceAddress *source;
@property (nonatomic, retain) KNXGroupAddress *destination;

// initialize instance
-(id)initFromPacket: (NSData *)packet;
-(void)initWithControl1:(unsigned char)control1 Control2:(unsigned char)control2 MessageCode:(unsigned char)code;

// generate data from instance values
-(void)dataIntoPacket:(NSMutableData *)packet;

// description of header data
-(NSString *)description;

// access to instance variables
-(unsigned char)messageCode;
-(KNXDeviceAddress *)source;
-(KNXGroupAddress *)destination;
-(NSData *)payload;

@end
