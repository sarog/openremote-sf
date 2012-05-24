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
#import "CapabilitiesParser.h"
#import "Capabilities.h"
#import "APISecurity.h"
#import "Capability.h"

@interface CapabilitiesParser()

@property (nonatomic, retain) NSMutableString *temporaryXMLElementContent;
@property (nonatomic, retain) NSMutableArray *versions;
@property (nonatomic, retain) NSMutableArray *securities;
@property (nonatomic, retain) APISecurity *security;
@property (nonatomic, retain) NSMutableArray *capabilities;
@property (nonatomic, retain) NSString *capabilityName;
@property (nonatomic, retain) NSMutableDictionary *properties;

@end

@implementation CapabilitiesParser

- (void)dealloc
{
    self.temporaryXMLElementContent = nil;
    self.versions = nil;
    self.securities = nil;
    self.security = nil;
    self.capabilities = nil;
    self.capabilityName = nil;
    self.properties = nil;
    [super dealloc];
}

#pragma mark -

- (Capabilities *)parseXMLData:(NSData *)xmlData
{
    if (!xmlData) {
        return nil;
    }
    
    NSXMLParser *xmlParser = [[NSXMLParser alloc] initWithData:xmlData];
	[xmlParser setDelegate:self];
	[xmlParser parse];
	[xmlParser release];
    
    return [[[Capabilities alloc] initWithSupportedVersions:[NSArray arrayWithArray:self.versions]
                                              apiSecurities:self.securities?[NSArray arrayWithArray:self.securities]:nil
                                               capabilities:self.capabilities?[NSArray arrayWithArray:self.capabilities]:nil] autorelease];
}

#pragma mark NSXMLParserDelegate implementation

- (void)parser:(NSXMLParser *)parser didStartElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qualifiedName attributes:(NSDictionary *)attributeDict
{
    if ([elementName isEqualToString:@"rest-api-versions"]) {
        self.versions = [NSMutableArray array];
    } else if ([elementName isEqualToString:@"security"]) {
        self.securities = [NSMutableArray array];
    } else if ([elementName isEqualToString:@"capabilities"]) {
        self.capabilities = [NSMutableArray array];
    } else if ([elementName isEqualToString:@"version"]) {
        self.temporaryXMLElementContent = [NSMutableString string];
	} else if ([elementName isEqualToString:@"api"]) {
        self.security = [[[APISecurity alloc] initWithPath:[attributeDict valueForKey:@"path"] security:[attributeDict valueForKey:@"security"] sslEnabled:[[attributeDict valueForKey:@"ssl-enabled"] boolValue]] autorelease];
    } else if ([elementName isEqualToString:@"capability"]) {
        self.capabilityName = [attributeDict valueForKey:@"name"];
        self.properties = [NSMutableDictionary dictionary];
    } else if ([elementName isEqualToString:@"property"]) {
        [self.properties setValue:[attributeDict valueForKey:@"value"] forKey:[attributeDict valueForKey:@"name"]];
    }
}

- (void)parser:(NSXMLParser *)parser foundCharacters:(NSString *)string
{
    [self.temporaryXMLElementContent appendString:string];
}

- (void)parser:(NSXMLParser *)parser didEndElement:(NSString *)elementName namespaceURI:(NSString *)namespaceURI qualifiedName:(NSString *)qName
{
	if ([elementName isEqualToString:@"version"]) {
        [self.versions addObject:[self.temporaryXMLElementContent stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]]];
        self.temporaryXMLElementContent = nil;
    } else if ([elementName isEqualToString:@"api"]) {
        [self.securities addObject:self.security];
        self.security = nil;
    } else if ([elementName isEqualToString:@"capability"]) {
        [self.capabilities addObject:[[[Capability alloc] initWithName:self.capabilityName properties:self.properties] autorelease]];
        self.capabilityName = nil;
        self.properties = nil;
    }
}

@synthesize temporaryXMLElementContent;
@synthesize versions;
@synthesize securities;
@synthesize security;
@synthesize capabilities;
@synthesize capabilityName;
@synthesize properties;

@end