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

@class ORController;

/**
 * Util for appending HTTP Basic Authentication header for request to http basic authenticate.
 */
@interface CredentialUtil : NSObject {

}

/**
 * Append HTTP Basic Authentication header into request.
 */
+ (void)addCredentialToNSMutableURLRequest:(NSMutableURLRequest *)request forController:(ORController *)controller;
+ (void)addCredentialToNSMutableURLRequest:(NSMutableURLRequest *)request withUserName:(NSString *)userName password:(NSString *)password;


@end
