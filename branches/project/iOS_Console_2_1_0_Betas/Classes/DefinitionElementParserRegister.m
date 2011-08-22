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
#import "DefinitionElementParserRegister.h"

@interface DefinitionElementParserRegister()

@property (nonatomic, retain) NSMutableDictionary *parserRegister;
@property (nonatomic, retain) NSMutableDictionary *endSelectorRegister;
@property (nonatomic, retain) NSMutableArray *standbys;

@end

@implementation DefinitionElementParserRegister

@synthesize parserRegister;
@synthesize endSelectorRegister;
@synthesize standbys;

- (id)init
{
    self = [super init];
    if (self) {
        self.parserRegister = [NSMutableDictionary dictionary];
        self.endSelectorRegister = [NSMutableDictionary dictionary];
        self.standbys = [NSMutableSet set];
    }
    
    return self;
}

- (void)dealloc
{
    self.parserRegister = nil;
    self.endSelectorRegister = nil;
    self.standbys = nil;
    [super dealloc];
}

- (void)registerParserClass:(Class)parserClass endSelector:(SEL)selector forTag:(NSString *)tag
{
    [parserRegister setObject:parserClass forKey:tag];
    [endSelectorRegister setObject:[NSValue valueWithBytes:&selector objCType:@encode(SEL)] forKey:tag];
}

- (Class)parserClassForTag:(NSString *)tag
{
    return [parserRegister objectForKey:tag];
}

- (SEL)endSelectorForTag:(NSString *)tag
{
    SEL retValue;
    [[endSelectorRegister objectForKey:tag] getValue:&retValue];
    return retValue;
}

- (void)addStandbyToResolve:(id <Standby>)labelStandby
{
    [self.standbys addObject:labelStandby];
}

- (void)resolveStandbys
{
    [self.standbys makeObjectsPerformSelector:@selector(resolveStandby)];
    [self.standbys removeAllObjects];
}

@end
