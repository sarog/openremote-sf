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
	NSMutableDictionary *statusMap;   //contains sensor id and related latest status value

}

@property (nonatomic,readonly)	NSString *lastId;
@property (nonatomic,readonly)	NSMutableDictionary *statusMap;

/**
 * The sensor value is usually published as part of parsing the XML reply from the controller.
 * However, there are certain cases (e.g. local sensors) where we want to be able to update a sensor value from "the outside".
 * This method provides this ability and appropriately notifies observers of the change.
 */
- (void)publishNewValue:(NSString *)status forSensorId:(NSString *)sensorId;

@end
