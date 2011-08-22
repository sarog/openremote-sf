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

@property (nonatomic, retain) id currentParser;
@property (nonatomic, retain) NSMutableSet *knownTags;

@end

@implementation DefinitionElementParser

@synthesize currentParser;
@synthesize depRegister;
@synthesize knownTags;

- (void)dealloc {
    self.depRegister = nil;
    self.knownTags = nil;
    self.currentParser = nil;
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
    [knownTags addObject:tag];
}

- (void)installParserClass:(Class)parserClass onParser:(NSXMLParser *)parser attributes:(NSDictionary *)attributeDict
{
    DefinitionElementParser *screenParser = [[parserClass alloc] initWithRegister:depRegister attributes:attributeDict];
    parser.delegate = screenParser;
    self.currentParser = screenParser;
    [screenParser release];
}

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([knownTags containsObject:elementName]) {
        Class parserClass = [self.depRegister parserClassForTag:elementName];
        if (parserClass) {
            [self installParserClass:parserClass onParser:parser attributes:attributeDict];
        }
    }
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
    if ([knownTags containsObject:elementName]) {
        SEL endSelector = [self.depRegister endSelectorForTag:elementName];
        
        // TODO: what is returned in case of nil?
        if ([self respondsToSelector:endSelector]) {
            [self performSelector:endSelector withObject:self.currentParser];
            parser.delegate = self;
            self.currentParser = nil;
        }
    }
}

@end