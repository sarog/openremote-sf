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
#import "GestureParser.h"
#import "Gesture.h"
#import "NavigateParser.h"

@implementation GestureParser

@synthesize gesture;

- (void)dealloc
{
    [gesture release];
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
        
        gesture = [[Gesture alloc] initWithId:[[attributeDict objectForKey:ID] intValue]
                                    swipeType:swipeType
                            hasControlCommand:[@"TRUE" isEqualToString:[[attributeDict objectForKey:@"hasControlCommand"] uppercaseString]]];
    }
    return self;
}

- (void)endNavigateElement:(NavigateParser *)parser
{
    gesture.navigate = parser.navigate;
}

@end
