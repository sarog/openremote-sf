/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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

#import "CredentialUtil.h"
#import "ORController.h"
#import "NSStringAdditions.h"

@implementation CredentialUtil

+ (void)addCredentialToNSMutableURLRequest:(NSMutableURLRequest *)request forController:(ORController *)controller {
	NSString *format = [NSString stringWithFormat:@"%@:%@", controller.userName, controller.password];
	NSData *utf8Data = [format dataUsingEncoding:NSUTF8StringEncoding];
	NSString *base64String = [NSString base64StringFromData:utf8Data length:[utf8Data length]];
	[request setValue:[NSString stringWithFormat:@"Basic %@", base64String] forHTTPHeaderField:@"Authorization"];	
}

@end
