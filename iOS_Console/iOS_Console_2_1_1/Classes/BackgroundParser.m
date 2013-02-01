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
#import "BackgroundParser.h"
#import "Background.h"
#import "ImageParser.h"
#import "XMLEntity.h"

@interface BackgroundParser ()

@property (nonatomic, retain, readwrite) Background *background;

@end

/**
 * Background stores informations parsed from background element in panel.xml.
 * XML fragment example:
 * <background fillScreen="true">
 *    <image src="living_colors_320.png" />
 * </background>
 */
@implementation BackgroundParser

- (void)dealloc
{
    self.background = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:IMAGE];
        NSString *relativeStr = [attributeDict objectForKey:@"relative"];
        Background *tmp = nil;
        if (relativeStr) {
            tmp = [[Background alloc] initWithRelativePosition:relativeStr fillScreen:[@"true" isEqualToString:[[attributeDict objectForKey:@"fillScreen"] lowercaseString]]];
        } else {
            NSString *absoluteStr = [attributeDict objectForKey:@"absolute"];
			NSRange rangeOfComma = [absoluteStr rangeOfString:@","];
			int indexOfComma = rangeOfComma.location;
            tmp = [[Background alloc] initWithAbsolutePositionLeft:[[absoluteStr substringToIndex:indexOfComma] intValue]
                                                                      top:[[absoluteStr substringFromIndex:indexOfComma+1] intValue]
                                                               fillScreen:[@"true" isEqualToString:[[attributeDict objectForKey:@"fillScreen"] lowercaseString]]];
        }
        self.background = tmp;
        [tmp release];
    }
    return self;
}

- (void)endImageElement:(ImageParser *)parser
{
    self.background.backgroundImage = parser.image;
}

@synthesize background;

@end