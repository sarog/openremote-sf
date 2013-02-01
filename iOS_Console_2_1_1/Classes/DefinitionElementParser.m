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
#import "DefinitionElementParser.h"
#import "DefinitionElementParserRegister.h"

@interface DefinitionElementParser()

@property (nonatomic, retain) NSMutableSet *knownTags;
@property (nonatomic, retain) DefinitionElementParser *childParser;

@end

@implementation DefinitionElementParser

- (void)dealloc
{
    self.depRegister = nil;
    self.knownTags = nil;
    self.childParser = nil;
    self.handledTag = nil;
    [super dealloc];
}

- (id)initWithRegister:(DefinitionElementParserRegister *)aRegister attributes:(NSDictionary *)attributeDict;
{
    self = [super init];
    if (self) {
        self.depRegister = aRegister;
        self.knownTags = [NSMutableSet set];
    }
    return self;
}

- (void)addKnownTag:(NSString *)tag
{
    [self.knownTags addObject:tag];
}

- (void)installParserClass:(Class)parserClass onParser:(NSXMLParser *)parser elementName:(NSString *)elementName attributes:(NSDictionary *)attributeDict
{
    DefinitionElementParser *aParser = [[parserClass alloc] initWithRegister:self.depRegister attributes:attributeDict];
    aParser.handledTag = elementName;
    aParser.parentParser = self;
    self.childParser = aParser;
    parser.delegate = aParser;
    [aParser release];
}

- (void)restoreParserOnParser:(NSXMLParser *)parser
{
    parser.delegate = self.parentParser;
    self.parentParser = nil;
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([self.knownTags containsObject:elementName]) {
        Class parserClass = [self.depRegister parserClassForTag:elementName];
        if (parserClass) {
            [self installParserClass:parserClass onParser:parser elementName:elementName attributes:attributeDict];
            
        }
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([[self handledTag] isEqualToString:elementName]) {
        SEL endSelector = [self.depRegister endSelectorForTag:elementName];
        // TODO: what is returned in case of nil?
        if ([self.parentParser respondsToSelector:endSelector]) {
            [self.parentParser performSelector:endSelector withObject:self];
        }
        [self restoreParserOnParser:parser];
    }
}

@synthesize parentParser;
@synthesize childParser;
@synthesize depRegister;
@synthesize knownTags;
@synthesize handledTag;

@end