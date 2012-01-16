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

/********************* Standard Client error *********************/ 

#define UNAUTHORIZED              401 //unauthorized by Controller, should login.
#define REQUEST_ERROR             404 //bad request

/********************* Standard Server error *********************/ 

#define SERVER_ERROR              500 //unkown server error
#define NA_SERVICE                503 //server not available

/********************* Controller Custom error *********************/ 

#define POLLING_TIMEOUT           504 //Controller polling timeout, it's not a error, just mean no status change happes.
#define CONTROLLER_CONFIG_CHANGED  506 //Controller config is changed, reload is required.

#define CMD_BUILDER_ERROR         418 //command builder error
#define NO_SUCH_COMPONENT         419 //no such component
#define NO_SUCH_CMD_BUILDER       420 //no such command builder
#define INVALID_COMMAND_TYPE      421 //invalid command type
#define CONTROLLER_XML_NOT_FOUND  422 //controller.xml not found
#define NO_SUCH_CMD               423 //no such command
#define INVALID_CONTROLLER_XML    424 //invalid controller.xml
#define INVALID_POLLING_URL       425 //invalid polling url
#define PANEL_XML_NOT_FOUND       426 //panel.xml not found
#define INVALID_PANEL_XML         427 //invalid panel.xml
#define NO_SUCH_PANEL             428 //no such panel identity
#define INVALID_ELEMENT           429 //invalid xml element


/*
 Includes all exceptions happens in Controller server, not this console.
 */
@interface ControllerException : NSObject {

}

//convenient method to find concrete exception message by error code.
+ (NSString *)exceptionMessageOfCode:(int)code;

@end
