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

#import "Button.h" 

@implementation Button

@synthesize defaultImage, pressedImage, name, navigate, subElememntNameOfBackground;
@synthesize repeat, repeatDelay, hasPressCommand, hasShortReleaseCommand, hasLongPressCommand, hasLongReleaseCommand, longPressDelay;

- (id)initWithXMLParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict parentDelegate:(NSObject<NSXMLParserDelegate> *)parent
{
    self = [super init];
	if (self) {		
		componentId = [[attributeDict objectForKey:@"id"] intValue];
		name = [[attributeDict objectForKey:@"name"] copy];
		repeat = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"repeat"] uppercaseString]];
        NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
        [formatter setFormatterBehavior:NSNumberFormatterBehavior10_4];
        NSNumber *result = nil;
        NSError *error = nil;
        NSString *input = [attributeDict objectForKey:@"repeatDelay"];
        if (input) {
            NSRange range = NSMakeRange(0, input.length);
            if ([formatter getObjectValue:&result forString:input range:&range error:&error]) {
                repeatDelay = [result intValue];
            }
        }
        if (repeatDelay < 100) {
            repeatDelay = 100;
        }
        
		hasPressCommand = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasPressCommand"] uppercaseString]];
		hasShortReleaseCommand = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasShortReleaseCommand"] uppercaseString]];
		hasLongPressCommand = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasLongPressCommand"] uppercaseString]];
		hasLongReleaseCommand = [@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasLongReleaseCommand"] uppercaseString]];

        result = nil;
        error = nil;
        input = [attributeDict objectForKey:@"longPressDelay"];
        if (input) {
            NSRange range = NSMakeRange(0, input.length);
            if ([formatter getObjectValue:&result forString:input range:&range error:&error]) {
                longPressDelay = [result intValue];
            }
        }
        if (longPressDelay < 250) {
            longPressDelay = 250;
        }

		if (hasLongPressCommand || hasLongReleaseCommand) {
            repeat = NO;
        }
        [formatter release];

		xmlParserParentDelegate = [parent retain];
		[parser setDelegate:self];
	}
	return self;
}

// parse defaultIcon, pressedIcon, command, navigate.
- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict{
	if ([elementName isEqualToString:DEFAULT]) {
		subElememntNameOfBackground = DEFAULT;
	} else if ([elementName isEqualToString:PRESSED]) {
		subElememntNameOfBackground = PRESSED;	
	} else if ([elementName isEqualToString:IMAGE]) {
		if ([DEFAULT isEqualToString:subElememntNameOfBackground]) {
			defaultImage = [[Image alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		} else if ([PRESSED isEqualToString:subElememntNameOfBackground]) {
			pressedImage = [[Image alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
		}
	} else if ([elementName isEqualToString:@"navigate"]) {
		navigate = [[Navigate alloc] initWithXMLParser:parser elementName:elementName attributes:attributeDict parentDelegate:self];
	}	
}

// get element name, must be overriden in subclass
- (NSString *) elementName {
	return BUTTON;
}


- (void)dealloc
{
	[defaultImage release];
	[pressedImage release];
	[navigate release];
	[name release];
	[subElememntNameOfBackground release];
	
	[super dealloc];
}

@end