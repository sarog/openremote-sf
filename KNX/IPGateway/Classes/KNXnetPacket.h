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
 * KNXnetPacket.h
 *
 * Created by Jörg Falkenberg on 24.10.08.
 */

//#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "AsyncUdpSocket.h"
#import "KNXconnection.h"
#import "KNXcEMIHeader.h"
#import "KNXHPAI.h"
#import "KNXCRI.h"
#import "KNXDeviceAddress.h"
#import "KNXServerDescription.h"

#define KNX__SEARCH_REQUEST				0x0201
#define KNX__SEARCH_RESPONSE			0x0202
#define KNX__CONNECT_REQUEST			0x0205 
#define KNX__CONNECT_RESPONSE			0x0206
#define KNX__CONNECTIONSTATE_REQUEST	0x0207
#define KNX__CONNECTIONSTATE_RESPONSE	0x0208
#define KNX__DISCONNECT_REQUEST			0x0209
#define KNX__DISCONNECT_RESPONSE		0x020A
#define KNX__TUNNELING_REQUEST			0x0420
#define KNX__TUNNELING_RESPONSE			0x0421

// Error codes
#define KNX__E_NO_ERROR	0x00

@interface KNXnetPacket : NSObject {
	int serviceTypeIdentifier;
	int totalLength;
	KNXcEMIHeader *cEMIHeader;
	KNXconnection *knxConnection;
	unsigned char *zieladresse;
	unsigned char pakettyp;
	NSData *nutzdaten;
	KNXHPAI *serverHPAI;
	KNXServerDescription *serverDescription;
}
-(KNXnetPacket *)initMitTyp: (int)serviceTypeIdentifier fuerVerbindung:(KNXconnection *)verbindung;
-(KNXnetPacket *)initFromPacket: (NSData *)packet fuerVerbindung:(KNXconnection *)verbindung;
-(void)setzeDatenFuerPaket:(unsigned char *)wohin; // mitTyp:(unsigned char)requestTyp;
-(void)setzeDatenFuerDatenpaket:(unsigned char *)wohin Nutzlast:(NSData *)daten;
-(void)dataIntoPacket:(NSMutableData *)packet;
-(NSString *)description;
-(int)serviceTypeIdentifier;
-(NSString *)woher;
-(NSString *)wohin;
-(NSData *)payload;
-(int)cEMITyp;


@end
