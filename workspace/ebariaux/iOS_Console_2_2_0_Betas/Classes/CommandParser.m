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
#import "CommandParser.h"
#import "LocalCommand.h"
#import "PropertyParser.h"
#import "XMLEntity.h"

@interface CommandParser ()

@property (nonatomic, retain, readwrite) LocalCommand *command;

@end

@implementation CommandParser

- (void)dealloc
{
    self.command = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super initWithRegister:aRegister attributes:attributeDict];
    if (self) {
        [self addKnownTag:@"ctrl:property"];
        LocalCommand *tmp = [[LocalCommand alloc] initWithId:[[attributeDict objectForKey:ID] intValue] protocol:[attributeDict objectForKey:@"protocol"]];
        self.command = tmp;
        [tmp release];
    }
    return self;
}

- (void)endPropertyElement:(PropertyParser *)parser
{
    [self.command addPropertyValue:parser.value forKey:parser.name];
}

@synthesize command;

@end