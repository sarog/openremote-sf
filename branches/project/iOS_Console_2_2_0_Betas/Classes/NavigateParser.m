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
#import "NavigateParser.h"
#import "Navigate.h"

@interface NavigateParser ()

@property (nonatomic, retain, readwrite) Navigate *navigate;

@end

/**
 * Stores data about navigation and parsed from element navigate in panel.xml.
 * XML fragment example:
 * <navigate toGroup="491" toScreen="493" />
 * <navigate to="setting" />
 */
@implementation NavigateParser

- (void)dealloc
{
    self.navigate = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        NSString *to = [[attributeDict objectForKey:@"to"] lowercaseString];
        Navigate *tmp = [[Navigate alloc] initWithToScreen:[[attributeDict objectForKey:@"toScreen"] intValue]
                                              toGroup:[[attributeDict objectForKey:@"toGroup"] intValue]
                                     isPreviousScreen:[@"previousscreen" isEqualToString:to]
                                         isNextScreen:[@"nextscreen" isEqualToString:to]
                                           isSetting:[@"setting" isEqualToString:to]
                                               isBack:[@"back" isEqualToString:to]
                                              isLogin:[@"login" isEqualToString:to]
                                             isLogout:[@"logout" isEqualToString:to]];
        self.navigate = tmp;
        [tmp release];
    }
    return self;
}

@synthesize navigate;

@end