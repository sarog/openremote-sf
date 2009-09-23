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
 * KNXnetPacket.m
 *
 * Created by Jörg Falkenberg on 24.10.08.
 */

#import "KNXnetPacket.h"
#include <arpa/inet.h>

@implementation KNXnetPacket

-(KNXnetPacket *)initMitTyp: (int)typ fuerVerbindung:(KNXconnection *)verbindung
{
	if(self=[super init])
	{
		// Instanz angelegt
		serviceTypeIdentifier=typ;
		knxConnection=verbindung;
	}
	return self;
}

-(void)setzeDatenFuerPaket:(unsigned char *)wohin
{
	zieladresse=wohin;
	pakettyp=KNX_CEMI_L_DATA_REQ;
}

-(void)setzeDatenFuerDatenpaket:(unsigned char *)wohin Nutzlast:(NSData *)daten
{
	zieladresse=wohin;
	pakettyp=KNX_CEMI_L_DATA_IND;
	nutzdaten=daten;
}

static short istneuer(unsigned char neuer, unsigned char alter)
{
	if(neuer>alter)
		return YES;
	if(!neuer && alter>250)
		return YES;
	return NO;
}

-(KNXnetPacket *)initFromPacket:(NSData *)packet fuerVerbindung:(KNXconnection *)verbindung
{
	unsigned char headerLength, protocolVersion;
	unsigned char *headerPtr;
	unsigned char channelID, statusCode;
	unsigned char structureLength, sequenceCounter;
	KNXnetPacket *zweitpaket;
	NSMutableData *antwort;

	if(self=[super init])
	{
		/* created instance, read payload now */
		headerPtr=(unsigned char *)[packet bytes];
		headerLength=*headerPtr++;
		protocolVersion=*headerPtr++;
		if(headerLength!=6 || protocolVersion!=0x10)
		{
#ifdef _DEBUG
			NSLog(@"Invalid header: length=%d, protocol version=%d\n",headerLength,protocolVersion);
#endif
			[self release];
			return nil;
		}
		else
		{
			NSRange range;
			serviceTypeIdentifier=(int)(256*(*headerPtr)+(*(headerPtr+1)));
			headerPtr+=2;
			totalLength=(int)(256*(*headerPtr)+(*(headerPtr+1)));
			headerPtr+=2;
			switch(serviceTypeIdentifier)
			{
				case KNX__SEARCH_RESPONSE:
					/* got a packet, now check who was responding */
#ifdef _DEBUG
					NSLog(@"Got a search response, total length=%d\n",totalLength);
#endif
					serverHPAI=[[KNXHPAI alloc] initFromPacket:headerPtr];
					headerPtr+=8;
					serverDescription=[[KNXServerDescription alloc] initFromPacket:headerPtr];
					if(serverDescription)
					{
						// success, now offer the new gateway to the connection
						[verbindung foundGatewayOnAddress:[serverHPAI IPAsText] ofType:[serverDescription friendlyName]];
					}
					// ignore capabilities for now...
					break;
				case KNX__CONNECT_RESPONSE:
					/* we made it - we are connected, now check packet and extract some interesting information */
					channelID=*headerPtr++;
					statusCode=*headerPtr++;
					headerPtr+=10;	// skip HPAI
					if(totalLength>=20 && !statusCode)
					{
#ifdef _DEBUG
						NSLog(@"Successfully connected, channel ID: %d\n", channelID);
#endif
						[verbindung activateConnectionWithID:channelID];
						[verbindung setzeGeraeteAdresse:headerPtr];
					}
					else
					{
#ifdef _DEBUG
						NSLog(@"Could not connect, statusCode: %d\n",statusCode);
#endif
						[verbindung aufbauFehlgeschlagen];
					}
					break;
				case KNX__TUNNELING_REQUEST:
					/* Got tunneling packet, check and reply with ACK */
					structureLength=*headerPtr++;
					channelID=*headerPtr++;
					sequenceCounter=*headerPtr++;
					headerPtr++;	// reserved byte
					if(channelID==[verbindung channelID])
					{
						if(istneuer(sequenceCounter,[verbindung getDataSequenceCounter]))
						{
							/* OK, this packet belongs to this connection, sequence counter is higher */
							[verbindung setDataSequenceCounter:sequenceCounter];
							range.location=6+structureLength;
							range.length=totalLength-6-structureLength;
							cEMIHeader=[KNXcEMIHeader alloc];
							[cEMIHeader initFromPacket:[packet subdataWithRange:range]];
						}
						/* build ACK and send it */
						zweitpaket=[[KNXnetPacket alloc] initMitTyp:KNX__TUNNELING_RESPONSE fuerVerbindung:verbindung];
						antwort=[[NSMutableData alloc] init];
						[zweitpaket dataIntoPacket:antwort];
						[verbindung sendeAnDataSocket:antwort];
						[zweitpaket release];
					}
					else
					{
						/* Packet is not for us */
						[self release];
						return nil;
					}
					break;
				case KNX__TUNNELING_RESPONSE:
#ifdef _DEBUG
					NSLog(@"ACK for %@",[verbindung description]);
#endif
					[verbindung gatewayAck];
					break;
				case KNX__DISCONNECT_REQUEST:
					break;
				case KNX__DISCONNECT_RESPONSE:
					break;
			}
		}
	}
	return self;
}

// appends data to packet
-(void)dataIntoPacket:(NSMutableData *)packet
{
	unsigned char zwischenpuffer[64];
	KNXHPAI *controlHPAI, *dataHPAI;
	KNXCRI *tunnelCRI;
	KNXDeviceAddress *quelle;
	KNXGroupAddress *ziel;
	UInt32 meineAdresse;
	UInt16 meinControlPort;
	UInt16 meinDataPort;
	
	zwischenpuffer[0]=0x06;	// KNXnet
	zwischenpuffer[1]=0x10;	// Version
	zwischenpuffer[2]=serviceTypeIdentifier>>8;
	zwischenpuffer[3]=serviceTypeIdentifier&255;

	// now add data, depending on type
	switch (serviceTypeIdentifier)
	{
		case KNX__SEARCH_REQUEST:
			// finish header
			zwischenpuffer[4]=0;
			zwischenpuffer[5]=0x0E;	// Length of packet
			[packet appendBytes:zwischenpuffer length:6];

			// gather information about my IP address and ports first
			// meineAdresse=[[knxConnection searchSocket] localHostAsInteger];
			// meineAdresse=0xC0A8001B;
			meineAdresse=ntohl(inet_addr([knxConnection.myIP UTF8String]));
#ifdef _DEBUG
			NSLog(@"my address: %08X",meineAdresse);
#endif
			// put them in their classes
			controlHPAI=[[KNXHPAI alloc] initWithAddress:meineAdresse andPort:3671];
			// and append to the packet
			[controlHPAI dataIntoPacket:packet];
			// release those temporary class objects
			[controlHPAI release];
			break;
		case KNX__CONNECT_REQUEST:
			// finish header
			zwischenpuffer[4]=0;
			zwischenpuffer[5]=0x1A;	// Length of packet
			[packet appendBytes:zwischenpuffer length:6];

			// gather information about my IP address and ports first
			meineAdresse=[[knxConnection controlSocket] localHostAsInteger];
			meinControlPort=[[knxConnection controlSocket] localPort];
			meinDataPort=[[knxConnection dataSocket] localPort];

			// put them in their classes
			controlHPAI=[[KNXHPAI alloc] initWithAddress:meineAdresse andPort:meinControlPort];
			dataHPAI=[[KNXHPAI alloc] initWithAddress:meineAdresse andPort:meinDataPort];
			tunnelCRI=[[KNXCRI alloc] initWithType:KNX_CRI_TYPE_TUNNEL];

			// and append to the packet
			[controlHPAI dataIntoPacket:packet];
			[dataHPAI dataIntoPacket:packet];
			[tunnelCRI dataIntoPacket:packet];

			// release those temporary class objects
			[controlHPAI release];
			[dataHPAI release];
			[tunnelCRI release];
			break;

		case KNX__TUNNELING_REQUEST:
			zwischenpuffer[4]=0;
			zwischenpuffer[5]=0x2B;	// Length of packet (why 0x2B? no idea yet...)
			zwischenpuffer[6]=4;	// Length of connection header
			zwischenpuffer[7]=[knxConnection channelID];
			zwischenpuffer[8]=[knxConnection getSendeSequenceCounter];
			zwischenpuffer[9]=KNX__E_NO_ERROR;
			[packet appendBytes:zwischenpuffer length:10];
			[knxConnection setSendeSequenceCounter:(zwischenpuffer[8]+1)&255];	// increment sequence counter

			// cEMI header with addresses
			cEMIHeader=[KNXcEMIHeader alloc];
			[cEMIHeader initWithControl1:0xAC Control2:0xE0 MessageCode:KNX_CEMI_L_DATA_REQ];
			[cEMIHeader dataIntoPacket:packet];
			quelle=[[KNXDeviceAddress alloc] initMitLeererAdresse];
			ziel=[[KNXGroupAddress alloc] initMitDreibyteAdresse:zieladresse];
			[quelle dataIntoPacket:packet];
			[ziel dataIntoPacket:packet];
			[quelle release];
			[ziel release];
			memset(zwischenpuffer,0,25);
			if(pakettyp==KNX_CEMI_L_DATA_IND)
			{
				// data packet, payload is in NSMutableData object
				memcpy(zwischenpuffer,[nutzdaten bytes],[nutzdaten length]);
			}
			else
			{
				// read request, payload is empty
				zwischenpuffer[0]=1;
			}
			[packet appendBytes:zwischenpuffer length:25];
			break;

		case KNX__TUNNELING_RESPONSE:
			zwischenpuffer[4]=0;
			zwischenpuffer[5]=0x0A;	// Length of packet
			zwischenpuffer[6]=4;	// Length connection header
			zwischenpuffer[7]=[knxConnection channelID];
			zwischenpuffer[8]=[knxConnection getDataSequenceCounter];
			zwischenpuffer[9]=KNX__E_NO_ERROR;
			[packet appendBytes:zwischenpuffer length:10];
			break;

		case KNX__DISCONNECT_REQUEST:
			zwischenpuffer[4]=0;
			zwischenpuffer[5]=0x10;							// Length of packet
			zwischenpuffer[6]=[knxConnection channelID];	// Channel ID
			zwischenpuffer[7]=0;
			[packet appendBytes:zwischenpuffer length:8];
			// fill in sub structures
			meineAdresse=[[knxConnection controlSocket] localHostAsInteger];
			meinControlPort=[[knxConnection controlSocket] localPort];
			controlHPAI=[[KNXHPAI alloc] initWithAddress:meineAdresse andPort:meinControlPort];
			[controlHPAI dataIntoPacket:packet];
			[controlHPAI autorelease];
			break;
	}
}

-(int)serviceTypeIdentifier
{
	return serviceTypeIdentifier;
}
-(int)cEMITyp
{
	return [cEMIHeader messageCode];
}
-(NSString *)woher
{
	return [[cEMIHeader source] description];
}
-(NSString *)wohin
{
	return [[cEMIHeader destination] description];
}
-(NSData *)payload
{
	return [cEMIHeader payload];
}

-(NSString *)description
{
	char *paketbeschreibung="?";
	NSString *zusatzinfo=nil;
	switch (serviceTypeIdentifier)
	{
		case KNX__SEARCH_REQUEST:
			paketbeschreibung="SEARCH_REQUEST";
			break;
		case KNX__SEARCH_RESPONSE:
			paketbeschreibung="SEARCH_RESPONSE";
			zusatzinfo=[serverDescription description];
			break;
		case KNX__CONNECT_REQUEST:
			paketbeschreibung="CONNECT_REQUEST";
			break;
		case KNX__CONNECT_RESPONSE:
			paketbeschreibung="CONNECT_RESPONSE";
			break;
		case KNX__TUNNELING_REQUEST:
			paketbeschreibung="TUNNELING_REQUEST";
			zusatzinfo=[cEMIHeader description];
			break;
		case KNX__DISCONNECT_REQUEST:
			paketbeschreibung="DISCONNECT_REQUEST";
			break;
		default:
			break;
	}
	return [NSString stringWithFormat:@"KNXnetPacket: serviceTypeIdentifier: %s, total length : %d, additional info:%@\n",paketbeschreibung,totalLength,zusatzinfo];
}

- (void)dealloc
{
	[cEMIHeader release];
	[serverHPAI release];
	[super dealloc];
}

@end
