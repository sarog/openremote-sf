/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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


#import <UIKit/UIKit.h>
#import "ViewHelper.h"
#import "ServerAutoDiscoveryController.h"

//Define a protocol for delegate implementation
@protocol URLConnectionHelperDelegate <NSObject>

/**
 * This method will be called when request is finisehed with URLConnection.
 */
- (void)definitionURLConnectionDidFinishLoading:(NSData *)data;

/**
 * This method will be called when failed to request with URLConnection.
 */
- (void)definitionURLConnectionDidFailWithError:(NSError *)error;

/**
 * This method will be called when receive response from controller server with URLConnection.
 */
- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response;
@end

@interface URLConnectionHelper : NSObject {
	//this object must implement this protocal like a interface in java.So we can ensure to call the mehod in deleget instance.
	id <URLConnectionHelperDelegate> delegate;
	NSMutableData *receivedData;
	NSURLConnection *connection;
	NSError *errorMsg;
	ServerAutoDiscoveryController *autoDiscoverController;
	NSTimer *getAutoServersTimer;
}

- (id)initWithURL:(NSURL *)url delegate:(id <URLConnectionHelperDelegate>)delegate;
- (id)initWithRequest:(NSURLRequest *)request delegate:(id <URLConnectionHelperDelegate>)d;



- (void)cancelConnection;                 // cancel a connection, actually ignore the callback.
+ (BOOL)isWifiActive;                     // get a flag that indicates WIFI is active or not.
+ (void)setWifiActive:(BOOL)active;       // set a flag that indicates WIFI is active or not.

//reimplement +[NSURLConnection sendSynchronousRequest:returningResponse:error:]
- (NSData *)sendSynchronousRequest:(NSURLRequest *)request returningResponse:(NSURLResponse **)response error:(NSError **)error;

@property(nonatomic,retain) id <URLConnectionHelperDelegate> delegate;
@property(nonatomic,retain) NSURLConnection *connection;
@property(nonatomic,retain) NSError *errorMsg;
@property(nonatomic,retain) ServerAutoDiscoveryController *autoDiscoverController;
@property(nonatomic,retain) NSTimer *getAutoServersTimer;

@end
