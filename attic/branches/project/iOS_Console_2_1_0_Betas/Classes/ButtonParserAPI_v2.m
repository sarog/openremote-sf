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
#import "ButtonParserAPI_v2.h"
#import "Button.h"
#import "NavigateParser.h"
#import "ImageParser.h"
#import "XMLEntity.h"

typedef enum {
    ImageNone  = 0,
	ImageDefault  = 1,
	ImagePressed  = 2,
} ButtonImageType;

@interface ButtonParserAPI_v2 ()

@property (nonatomic, assign) ButtonImageType currentImageType;
@property (nonatomic, retain, readwrite) Button *button;

@end

/**
 * Button stores informations parsed from button element in panel.xml.
 * XML fragment example:
 * <button id="59" name="A" repeat="false" hasControlCommand="false">
 *    <default>
 *       <image src="a.png" />
 *    </default>
 *    <pressed>
 *       <image src="b.png" />
 *    </pressed>
 *    <navigate toScreen="19" />
 * </button>
 */
@implementation ButtonParserAPI_v2

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:NAVIGATE];
        [self addKnownTag:IMAGE];
        Button *tmp = [[Button alloc] initWithId:[[attributeDict objectForKey:@"id"] intValue]
                                       name:[attributeDict objectForKey:@"name"]
                                     repeat:[@"TRUE" isEqualToString:[[attributeDict objectForKey:@"repeat"] uppercaseString]]
                                repeatDelay:300
                            hasPressCommand:[@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasControlCommand"] uppercaseString]]
                     hasShortReleaseCommand:FALSE hasLongPressCommand:FALSE hasLongReleaseCommand:FALSE
                             longPressDelay:250];
        self.button = tmp;
        [tmp release];
    }
    return self;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([DEFAULT isEqualToString:elementName]) {
        self.currentImageType = ImageDefault;
    } else if ([PRESSED isEqualToString:elementName]) {
        self.currentImageType = ImagePressed;
    } else {
        [super parser:parser didStartElement:elementName namespaceURI:namespaceURI qualifiedName:qualifiedName attributes:attributeDict];
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([DEFAULT isEqualToString:elementName] || [PRESSED isEqualToString:elementName]) {
        self.currentImageType = ImageNone;
    } else {
        [super parser:parser didEndElement:elementName namespaceURI:namespaceURI qualifiedName:qName];
    }
}

- (void)endNavigateElement:(NavigateParser *)parser
{
    self.button.navigate = parser.navigate;
}

- (void)endImageElement:(ImageParser *)parser
{
    switch (self.currentImageType) {
        case ImageDefault:
            self.button.defaultImage = parser.image;
            break;
        case ImagePressed:
            self.button.pressedImage = parser.image;
            break;
        default:
            break;
    }
}

@synthesize button;
@synthesize currentImageType;

@end