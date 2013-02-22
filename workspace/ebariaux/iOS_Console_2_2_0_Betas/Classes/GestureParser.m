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
#import "GestureParser.h"
#import "Gesture.h"
#import "NavigateParser.h"
#import "XMLEntity.h"

@interface GestureParser ()

@property (nonatomic, retain, readwrite) Gesture *gesture;

@end

/**
 * Gesture model stores swipeType, hasControlCommand and navigate data, parsed from element gesture in panel.xml.
 * XML fragment example:
 * <gesture id="514" hasControlCommand="true" type="swipe-bottom-to-top">
 *    <navigate to="setting" />
 * </gesture>
 * <gesture id="515" hasControlCommand="true" type="swipe-top-to-bottom">
 *    <navigate to="setting" />
 * </gesture>
 * <gesture id="516" hasControlCommand="true" type="swipe-left-to-right">
 *    <navigate to="setting" />
 * </gesture>
 * <gesture id="517" hasControlCommand="true" type="swipe-right-to-left">
 *    <navigate to="setting" />
 * </gesture>
 */
@implementation GestureParser

- (void)dealloc
{
    self.gesture = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:NAVIGATE];
        NSString *type = [attributeDict objectForKey:TYPE];
        GestureSwipeType swipeType;
        if ([type isEqualToString:@"swipe-top-to-bottom"]) {
            swipeType = GestureSwipeTypeTopToBottom;
        } else if ([type isEqualToString:@"swipe-bottom-to-top"]) {
            swipeType = GestureSwipeTypeBottomToTop;
        } else if ([type isEqualToString:@"swipe-left-to-right"]) {
            swipeType = GestureSwipeTypeLeftToRight;
        } else if ([type isEqualToString:@"swipe-right-to-left"]) {
            swipeType = GestureSwipeTypeRightToLeft;
        }
        
        Gesture *tmp = [[Gesture alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                    swipeType:swipeType
                            hasControlCommand:[@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasControlCommand"] uppercaseString]]];
        self.gesture = tmp;
        [tmp release];
    }
    return self;
}

- (void)endNavigateElement:(NavigateParser *)parser
{
    self.gesture.navigate = parser.navigate;
}

@synthesize gesture;

@end