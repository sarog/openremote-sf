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

#import "Navigate.h"


@implementation Navigate

@synthesize toScreen, toGroup, isPreviousScreen, isNextScreen, isSetting, isBack, isLogin, isLogout, fromGroup, fromScreen;

- (id)init {
	if (self = [super init]) {
		
	}
	return self;
}

//Initialize itself accoding to xml parser
- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject *)parent {
	if (self = [super init]) {
		toScreen = [[attributeDict objectForKey:@"toScreen"] intValue];
		toGroup = [[attributeDict objectForKey:@"toGroup"] intValue];
		NSString *to = [[attributeDict objectForKey:@"to"] lowercaseString];
		isPreviousScreen = [@"previousscreen" isEqualToString:to] ? YES : NO;
		isNextScreen = [@"nextscreen" isEqualToString:to] ? YES : NO;
		isSetting = [@"setting" isEqualToString:to] ? YES : NO;
		isBack = [@"back" isEqualToString:to] ? YES : NO;
		isLogin = [@"login" isEqualToString:to] ? YES : NO;
		isLogout = [@"logout" isEqualToString:to] ? YES : NO;
		
		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return NAVIGATE;
}

@end
