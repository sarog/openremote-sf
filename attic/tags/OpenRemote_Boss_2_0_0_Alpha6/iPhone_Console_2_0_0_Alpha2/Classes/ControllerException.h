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

#import <Foundation/Foundation.h>

#define UNAUTHORIZED              401
#define REQUEST_ERROR             404

#define SERVER_ERROR              500
#define NA_SERVICE                503
#define GATEWAY_TIMEOUT           504
#define CONTROLLER_CONFIG_CHANGE  506

#define CMD_BUILDER_ERROR         418
#define NO_SUCH_COMPONENT         419
#define NO_SUCH_CMD_BUILDER       420
#define INVALID_COMMAND_TYPE      421
#define CONTROLLER_XML_NOT_FOUND  422
#define NO_SUCH_CMD               423
#define INVALID_CONTROLLER_XML    424
#define INVALID_POLLING_URL       425
#define PANEL_XML_NOT_FOUND       426
#define INVALID_PANEL_XML         427
#define NO_SUCH_PANEL             428
#define INVALID_ELEMENT           429


@interface ControllerException : NSObject {

}

+ (NSString *)exceptionMessageOfCode:(int)code;

@end
