/*
 * OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2011, OpenRemote Inc.
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
#import "LogoutHelper.h"
#import "ServerDefinition.h"
#import "ViewHelper.h"
#import "ViewHelper.h"
#import "ControllerException.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"

@interface LogoutHelper (Private)

@end


@implementation LogoutHelper


- (void)requestLogout {
	NSURL *url = [[NSURL alloc] initWithString:[ServerDefinition logoutUrl]];
	
	//assemble put request 
	NSMutableURLRequest *request = [[NSMutableURLRequest alloc] init];
	[request setURL:url];
	[request setHTTPMethod:@"POST"];

	URLConnectionHelper *connection = [[URLConnectionHelper alloc] initWithRequest:request delegate:self];
	
	[url release];
	[request release];
	[connection autorelease];
}

// Handle the server response which are from controller server with status code.
- (void)handleServerResponseWithStatusCode:(int) statusCode {
	if (statusCode != 200) {
		switch (statusCode) {
			case UNAUTHORIZED://logout succuessful
            {
                ORController *activeController = [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController;
				NSLog(@"%@ logged out successfully", activeController.userName);
				[ViewHelper showAlertViewWithTitle:@"" Message:[NSString stringWithFormat:@"%@ logged out successfully", activeController.userName]];
                activeController.password = nil;
                [[ORConsoleSettingsManager sharedORConsoleSettingsManager] saveConsoleSettings];
				return;
            }
		} 		
		[ViewHelper showAlertViewWithTitle:@"Send Request Error" Message:[ControllerException exceptionMessageOfCode:statusCode]];	
	} 
	
}


#pragma mark delegate method of NSURLConnection
- (void) definitionURLConnectionDidFailWithError:(NSError *)error {

}


- (void)definitionURLConnectionDidFinishLoading:(NSData *)data {
	
}

- (void)definitionURLConnectionDidReceiveResponse:(NSURLResponse *)response {
	NSHTTPURLResponse *httpResp = (NSHTTPURLResponse *)response;
	[self handleServerResponseWithStatusCode:[httpResp statusCode]];
}


@end
