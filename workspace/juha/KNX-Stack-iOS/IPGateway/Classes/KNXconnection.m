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
 * KNXconnection.h
 * 
 * Class for managing ONE connection with a gateway
 *
 * Created by Jörg Falkenberg on 24.10.08.
 */

#import <Foundation/Foundation.h>
#import "KNXconnection.h"
#import "KNXCRI.h"
#import "KNXHPAI.h"
#import "KNXnetPacket.h"

@implementation KNXconnection

@synthesize meindelegate, myIP;

-(id)initWithAddress:(NSString *)adresse andAddressType:(int)typ forBuilding:(NSString *)name
{
	if (self = [super init])
	{
		addressType=typ;
		addressGateway=[adresse copy];
		nameOfBuilding=[name copy];
	}
	return self;
}

-(void)searchGateway
{
	NSError *searchError;
	int result;
	KNXnetPacket *searchRequest;
	NSMutableData *searchData;

	searchSocket = [[AsyncUdpSocket alloc] initWithDelegate:self userData:0 enableIPv4:YES enableIPv6:NO];
	result = [searchSocket bindToPort:3671 error:&searchError];
#ifdef _DEBUG
	NSLog(@"Bind: %d",result);
#endif
	result = [searchSocket enableBroadcast:YES error:&searchError];
#ifdef _DEBUG
	NSLog(@"EnableBroadcast: %d",result);
#endif
	result=[searchSocket joinMulticastGroup:@"224.0.23.12"  error:&searchError];
#ifdef _DEBUG
	NSLog(@"join: %d",result);
	if(!result)
		NSLog(@"Error on joinMulticast: %@ - %d\n",[searchError localizedDescription],[searchError code]);
#endif

	searchData=[[NSMutableData alloc] init];

	// generate data packet for connection request
	[searchSocket receiveWithTimeout:-1 tag:KNX__SEARCH_RESPONSE];
	searchRequest=[[KNXnetPacket alloc] initMitTyp:KNX__SEARCH_REQUEST fuerVerbindung:self];
	[searchRequest dataIntoPacket:searchData];
	result=[searchSocket sendData:searchData toHost:@"224.0.23.12" port:3671 withTimeout:-1	tag:KNX__SEARCH_REQUEST];
#ifdef _DEBUG
	NSLog(@"send: %d",result);
#endif
	[searchData autorelease];
	[searchSocket maybeDequeueSend];	// call Runloop of AsyncUdpSocket to be sure search packet is sent

	// OK, now wait some seconds for results
//	[searchSocket closeAfterSending];
}

/* called when gateway search found one */
-(void)foundGatewayOnAddress:(NSString *)address ofType:(NSString *)type
{
#ifdef _DEBUG
	NSLog(@"foundGatewayOnAddress: %@, %@",address,type);
#endif
	if ([meindelegate respondsToSelector:@selector(foundGatewayOnAddress)])
	{
		// send delegate a copy of the found gateway
		[meindelegate foundGatewayOnAddress:address ofType:type];
	}
}

-(id)connectWithGateway:(NSString *)adresse
{
	connectionActive=NO;
	addressType=1;
	controlSequenceCounter=255;
	dataSequenceCounter=255;
	sendingSequenceCounter=0;	// start with 0 for sequence counter for sent packets
	if (self = [super init])
	{
		NSError *dataError, *controlError;

		controlSocket = [[AsyncUdpSocket alloc] initWithDelegate:self userData:1 enableIPv4:YES enableIPv6:NO];
		dataSocket = [[AsyncUdpSocket alloc] initWithDelegate:self userData:1 enableIPv4:YES enableIPv6:NO];

		if([controlSocket connectToHost:adresse onPort:3671 error:&controlError]==YES && [dataSocket connectToHost:adresse onPort:3671  error:&dataError]==YES)
		{
			KNXnetPacket *connectionRequest;
			NSMutableData *verbindungsDaten=[[NSMutableData alloc] init];

#ifdef _DEBUG
			NSLog(@"Generated both sockets");
#endif
			// generate data packet for connection request
			connectionRequest=[[KNXnetPacket alloc] initMitTyp:KNX__CONNECT_REQUEST fuerVerbindung:self];
			[connectionRequest dataIntoPacket:verbindungsDaten];
			[controlSocket sendData:verbindungsDaten withTimeout:-1	tag:KNX__CONNECT_REQUEST];
			[controlSocket receiveWithTimeout:-1 tag:KNX__CONNECT_RESPONSE];
			[dataSocket receiveWithTimeout:-1 tag:KNX__TUNNELING_REQUEST];
			[connectionRequest autorelease];
			[verbindungsDaten autorelease];
		}
		else
		{
#ifdef _DEBUG
			NSLog(@"No connection to gateway");
			if(dataError!=nil)
				NSLog(@"Error on data socket: %@\n",[dataError localizedDescription]);
			if(controlError!=nil)
				NSLog(@"Error on control socket: %@\n",[controlError localizedDescription]);
#endif
			[self release];
			return nil;
		}
	}
	connectionTimer=[NSTimer scheduledTimerWithTimeInterval:15
													  target:self 
													selector:@selector(ueberpruefeVerbindung:)
													userInfo:nil
													 repeats:NO];
	return self;
}
-(void)ueberpruefeVerbindung:(NSTimer *)timer
{
	if(connectionActive==NO)
	{
		if ([meindelegate respondsToSelector:@selector(aufbauFehlgeschlagen:)])
		{
			[meindelegate aufbauFehlgeschlagen:YES];
		}
		connectionTimer=nil;
	}
}

// set status of connection with 'id' as active
-(void)activateConnectionWithID:(short)id
{
	connectionActive=YES;
	channelID=id;
	if ([meindelegate respondsToSelector:@selector(verbindungAufgebaut)])
	{
#ifdef _DEBUG
		NSLog(@"Notify AppDelegate");
#endif
		// use timer, else it will crash under 3.0
		[NSTimer scheduledTimerWithTimeInterval:.1
										 target:meindelegate 
									   selector:@selector(verbindungAufgebaut)
									   userInfo:nil
										repeats:NO];
	}
	if(connectionTimer)
		[connectionTimer invalidate];
	connectionTimer=nil;
#ifdef _DEBUG
	NSLog(@"verbindungAufgebaut finished");
#endif
}
// Could not connect
-(void)aufbauFehlgeschlagen
{
	connectionActive=NO;
	if(connectionTimer)
		[connectionTimer invalidate];
	connectionTimer=nil;
	if ([meindelegate respondsToSelector:@selector(aufbauFehlgeschlagen:)])
	{
		[meindelegate aufbauFehlgeschlagen:NO];
	}
}
// setzt Adresstyp auf 0 oder 1
-(void)setzeAdresstyp:(short)wert
{
	addressType=wert;
}

// speichert KNX_-Geräteadresse des Gateway
-(void)setzeGeraeteAdresse:(unsigned char *)woher
{
	deviceAddressGateway=[[KNXDeviceAddress alloc] init];
	[deviceAddressGateway initAusZweiByteAdresse:woher];
}

// closes connection
-(void)beendeVerbindung
{
	//	NSLog(@"Terminate connection");
	if(connectionTimer)
		[connectionTimer invalidate];
	connectionTimer=nil;
	
	if(connectionActive)
	{
		// noch ein Beende-Paket an Gateway schicken
		KNXnetPacket *disconnectionRequest;
		NSMutableData *verbindungsDaten=[[NSMutableData alloc] init];
		// nun Datenpaket zum Verbindungsabbau generieren
		disconnectionRequest=[[KNXnetPacket alloc] initMitTyp:KNX__DISCONNECT_REQUEST fuerVerbindung:self];
		[disconnectionRequest dataIntoPacket:verbindungsDaten];
		[controlSocket sendData:verbindungsDaten withTimeout:-1	tag:KNX__DISCONNECT_REQUEST];
		[disconnectionRequest release];
		[controlSocket maybeDequeueSend];	// call Runloop of AsyncUdpSocket to be sure disconnect packet is sent
		// NSLog(@"Send disconnect, %d byte\n",[verbindungsDaten length]);
	}
	connectionActive=NO;
	[dataSocket release];
	[controlSocket release];
	[deviceAddressGateway release];
	if ([meindelegate respondsToSelector:@selector(verbindungBeendet)])
	{
		[meindelegate verbindungBeendet];
	}
}

// read status of a group address
-(void)sendeRequest:(unsigned char *)adresse
{
	KNXnetPacket *statusRequest;
	NSMutableData *verbindungsDaten=[[NSMutableData alloc] init];
	// generate data packet for read request
	statusRequest=[[KNXnetPacket alloc] initMitTyp:KNX__TUNNELING_REQUEST fuerVerbindung:self];
	[statusRequest setzeDatenFuerPaket:adresse];
	[statusRequest dataIntoPacket:verbindungsDaten];
	[dataSocket sendData:verbindungsDaten withTimeout:.1 tag:KNX__TUNNELING_REQUEST];
	[statusRequest autorelease];
	[verbindungsDaten autorelease];
}
-(void)sendeDaten:(unsigned char *)adresse mitNutzlast:(NSData *)daten
{
	NSDate *startzeit;
	KNXnetPacket *statusRequest;
	NSMutableData *verbindungsDaten=[[NSMutableData alloc] init];
	// generate data packet for tunneling
	statusRequest=[[KNXnetPacket alloc] initMitTyp:KNX__TUNNELING_REQUEST fuerVerbindung:self];
	[statusRequest setzeDatenFuerDatenpaket:adresse Nutzlast:daten];
	[statusRequest dataIntoPacket:verbindungsDaten];
	sendingFlag=TRUE;
	startzeit=[NSDate dateWithTimeIntervalSinceNow:0];
	[dataSocket sendData:verbindungsDaten withTimeout:-1 tag:KNX__TUNNELING_REQUEST];
	for(;;)
	{
		if(!sendingFlag)
		{
//			NSLog(@"OK");
			break;
		}
		if([startzeit timeIntervalSinceNow]<-.1)
		{
//			NSLog(@"timeout: %f",[startzeit timeIntervalSinceNow]);
			break;
		}
	}
	[statusRequest autorelease];
	[verbindungsDaten autorelease];
}

// returns status of connecting
-(short)connectionActive
{
	return connectionActive;
}

// returns channel ID of connection
-(unsigned char)channelID
{
	return channelID;
}

// returns sockets for control and data
-(AsyncUdpSocket *)controlSocket
{
	return controlSocket;
}
-(AsyncUdpSocket *)dataSocket
{
	return dataSocket;
}
-(AsyncUdpSocket *)searchSocket
{
	return searchSocket;
}

// übergibt Daten zum Versenden
-(void)sendeAnDataSocket:(NSMutableData *)daten
{
	//[datarequest appendData:(NSData *)daten];
	@try
	{
		[dataSocket sendData:daten withTimeout:30 tag:0];
		// NSLog(@"Sending %d byte\n",[daten length]);
	}
	@catch (NSException *e)
	{
	//	NSLog([e description]);
		[controlSocket closeAfterSending];
		[dataSocket closeAfterSending];
	}
	@finally
	{
		[daten release];
	}
}

// manage sequence counters
-(unsigned char)getDataSequenceCounter
{
	return dataSequenceCounter;
}
-(void)setDataSequenceCounter:(unsigned char)wert
{
	dataSequenceCounter=wert;
}
-(unsigned char)getSendeSequenceCounter
{
	return sendingSequenceCounter;
}
-(void)setSendeSequenceCounter:(unsigned char)wert
{
	sendingSequenceCounter=wert;
}

//  socket callback functions
- (BOOL)onUdpSocket:(AsyncUdpSocket *)socket didReceiveData:(NSData *)paketDaten withTag:(long)tag fromHost:(NSString *)host port:(UInt16)port
{
	KNXnetPacket *eibnetpaket;
#ifdef _DEBUG
	NSLog(@"Packet with Tag %x from %@",tag,host);
#endif
	@try{
		eibnetpaket=[KNXnetPacket alloc];
		[eibnetpaket initFromPacket:paketDaten fuerVerbindung:self];
		if ([meindelegate respondsToSelector:@selector(paketEmpfangen:)])
		{
#ifdef _DEBUG
			NSLog(@"Forward to delegate");
#endif
			[meindelegate paketEmpfangen:eibnetpaket];
		}
		else
		{
#ifdef _DEBUG
			NSLog(@"No delegate :-(");
#endif
		}
		[eibnetpaket release];
	}
	@catch (NSException *e)
	{
		// NSLog([e reason]);
		[socket close];
	}
	@finally
	{
		//NSLog(@"Received %d byte: '%s'\n",[data length],[NSString stringWithCString:[data bytes]]);
		if (![paketDaten length])
			[socket close];
	}
	return NO;	// do not close connection!
}

- (void)onUdpSocket:(AsyncUdpSocket *)socket didNotReceiveDataWithTag:(long)tag dueToError:(NSError *)error
{
	//NSLog(@"Socket error: %@\n",[error localizedDescription]);
}

- (void)onUdpSocket:(AsyncUdpSocket *)sock didSendDataWithTag:(long)tag
{
	//	NSLog(@"Sent tag %x\n",tag);
}
- (void)onUdpSocket:(AsyncUdpSocket *)sock didNotSendDataWithTag:(long)tag dueToError:(NSError *)error
{
}
-(void)gatewayAck
{
//	NSLog(@"gatewayAck");
	sendingFlag=NO;
}
-(void)dealloc
{
//	NSLog(@"Cleaning up connection data\n");
	[controlSocket close];
	[controlSocket release];
	[controlrequest release];
	[dataSocket close];
	[dataSocket release];
	[datarequest release];
	[deviceAddressGateway release];
	[super dealloc];
}
@end
