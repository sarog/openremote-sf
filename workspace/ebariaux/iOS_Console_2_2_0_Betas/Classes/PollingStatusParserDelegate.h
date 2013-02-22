/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2012, OpenRemote Inc.
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
#import <Foundation/Foundation.h>

@class SensorStatusCache;

/* 
 NSXMLParser delegate: Parse the returned status XML from polling REST API
 usually a status xml returned from Controller looks like:
 <openremote>
  <status id="1">on</status>
  <status id="2">off</status>
 </openremote>
 
 'id' is sensor id, element body is latest status.
 so here sensor '1' is 'on'; sensor '2' is 'off'.
*/
@interface PollingStatusParserDelegate : NSObject <NSXMLParserDelegate> {
	
	NSString *lastId;                 //last sensor id while parsing

}

@property (nonatomic, readonly)	NSString *lastId;

- (id)initWithSensorStatusCache:(SensorStatusCache *)cache;

@end
