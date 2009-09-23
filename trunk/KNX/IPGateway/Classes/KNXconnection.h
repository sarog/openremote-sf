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

//#import <UIKit/UIKit.h>
#import <Foundation/Foundation.h>
#import "KNXDeviceAddress.h"
#import "AsyncUdpSocket.h"
//#import "KNXnetPacket.h"

@interface KNXconnection : NSObject
{
	unsigned char channelID;				// channel ID, assigned by gateway
	short connectionActive;					// flag, indicating that connection is successfully established
	unsigned char controlSequenceCounter;	// received sequence counter for control channel
	unsigned char dataSequenceCounter;		// received sequence counter for data channel
	unsigned char sendingSequenceCounter;		// last sent sequence counter
	KNXDeviceAddress *deviceAddressGateway;// device address of gateway (KNX side)
	AsyncUdpSocket *controlSocket;			// socket of control channel
	AsyncUdpSocket *dataSocket;				// socket of data channel
	AsyncUdpSocket *searchSocket;			// socket for discovery operations
	NSMutableData *controlrequest;			// data packet for control channel
	NSMutableData *datarequest;				// data packet for data channel
	NSString *addressGateway;				// IP address of gateway
	NSString *nameOfBuilding;				// name of connection (not really used)
	int addressType;						// type of address: 0=x/y (old style, not supported yet), 1=x/y/z
	id meindelegate;						// delegate for connection related callbacks
	volatile BOOL sendingFlag;				// flag, indicating that send operation is running
	NSTimer *connectionTimer;				// Timer for connection timeout
	NSMutableArray *foundGateways;			// Array with addresses of found gateways
	NSMutableArray *gatewayDescriptions;	// Descriptions of found gateways
	NSString *myIP;							// IP address of this host
}

@property (nonatomic, retain) id meindelegate;
@property (nonatomic, retain) NSString *myIP;

// initializes new instance
-(id)initWithAddress:(NSString *)adresse andAddressType:(int)typ forBuilding:(NSString *)name;
// open connection with gateway
-(id)connectWithGateway:(NSString *)adresse;
-(void)searchGateway;

// returns connection status
-(short)connectionActive;
// returns channel ID
-(unsigned char)channelID;
// return sockets
-(AsyncUdpSocket *)controlSocket;
-(AsyncUdpSocket *)dataSocket;
-(AsyncUdpSocket *)searchSocket;

// notifier for lower level classes, setting "connection active" flag
-(void)activateConnectionWithID:(short)id;
// notifying function for lower level classes, announcing a failed connection operation
-(void)aufbauFehlgeschlagen;
// sets address type
-(void)setzeAdresstyp:(short)wert;
// sends a read request for a group address
-(void)sendeRequest:(unsigned char *)adresse;
// sends data to a group address
-(void)sendeDaten:(unsigned char *)adresse mitNutzlast:(NSData *)daten;
// quits connection
-(void)beendeVerbindung;
// stores KNX device address of gateway
-(void)setzeGeraeteAdresse:(unsigned char *)woher;
// hands over data to data channel
-(void)sendeAnDataSocket:(NSMutableData *)daten;

// manage sequence counters
-(unsigned char)getDataSequenceCounter;
-(unsigned char)getSendeSequenceCounter;
-(void)setDataSequenceCounter:(unsigned char)wert;
-(void)setSendeSequenceCounter:(unsigned char)wert;

// got ACK packet from gateway
-(void)gatewayAck;

// returns IP address
-(NSString *)myIP;
@end

// declare delegate functions, so XCode won't claim unknown selectors
@interface NSObject (KNXconnectionDelegate)

/* called when the connection could not be established */
-(void)aufbauFehlgeschlagen:(bool)timeout;

/* called when the connection was established */
-(void)verbindungAufgebaut;

/* called when connection has been terminated */
-(void)verbindungBeendet;

/* called when a packet has been received */
-(void)paketEmpfangen:(void *)packet;

/* called when gateway search found one */
-(void)foundGatewayOnAddress:(NSString *)address ofType:(NSString *)type;

@end
