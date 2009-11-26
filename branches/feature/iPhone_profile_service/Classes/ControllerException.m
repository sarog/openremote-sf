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

#import "ControllerException.h"
#import "AppSettingsDefinition.h"

@implementation ControllerException

+ (NSString *)exceptionMessageOfCode:(int)code {
	NSString *errorMessage = nil;
	if (code != 200) {
		switch (code) {
			case REQUEST_ERROR:
				errorMessage = @"The command was sent to an invalid URL.";
				break;
			case CMD_BUILDER_ERROR:
				errorMessage = @"Controller failed to construct an event for this command.";
				break;
			case NO_SUCH_COMPONENT:
				errorMessage = @"Controller did not recognize the sent command id.";
				break;
			case NO_SUCH_CMD_BUILDER:
				errorMessage = @"Command builder not found.";
				break;
			case INVALID_COMMAND_TYPE:
				errorMessage = @"Invalid command type.";
				break;
			case CONTROLLER_XML_NOT_FOUND:
				errorMessage = @"Error in controller - controller.xml is not correctly deployed.";
				break;
			case NO_SUCH_CMD:
				errorMessage = @"Command not found.";
				break;
			case INVALID_CONTROLLER_XML:
				errorMessage = @"Invalid controller.xml.";
				break;
			case INVALID_POLLING_URL:
				errorMessage = @"Invalid polling url.";
				break;
			case PANEL_XML_NOT_FOUND:
				errorMessage = @"panel.xml not found.";
				break;
			case INVALID_PANEL_XML:
				errorMessage = @"Invalid panel.xml.";
				break;
			case NO_SUCH_PANEL:
				errorMessage = [NSString stringWithFormat:@"Current panel identity ‘%@’ isn't available. Please rechoose in Settings.", [AppSettingsDefinition getCurrentPanelIdentity]];
				break;
			case INVALID_ELEMENT:
				errorMessage = @"Invalid XML element.";
				break;
			case SERVER_ERROR:
				errorMessage = @"Error in controller. Please check controller log.";
				break;
			case UNAUTHORIZED:
				errorMessage = @"You can't execute a protected command without authentication.";
				break;
		}
		if (!errorMessage) {
			errorMessage = [NSString stringWithFormat:@"Occured unknown error, satus code is %d", code];
		}
	}
	return errorMessage;
}

@end
