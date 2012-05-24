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
#import "CapabilitiesParserTest.h"
#import "CapabilitiesParser.h"
#import "Capabilities.h"
#import "APISecurity.h"
#import "Capability.h"

@interface CapabilitiesParserTest()

@property (nonatomic, retain) CapabilitiesParser *parser;

@end

@implementation CapabilitiesParserTest

- (void)setUp
{
    self.parser = [[[CapabilitiesParser alloc] init] autorelease];
}

- (void)tearDown
{
    self.parser = nil;
}

- (void)testCompleteCapabilitiesXML
{
    Capabilities *capabilities = [self.parser parseXMLData:[NSData dataWithContentsOfFile:@"CompleteCapabilities.xml"]];
    STAssertNotNil(capabilities, @"Valid XML should return capabilities object");
    
    STAssertNotNil(capabilities.supportedVersions, @"Given XML contains versions information");
    STAssertEquals([capabilities.supportedVersions count], (NSUInteger)2, @"Given XML contains 2 versions");
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.0", @"2.1", nil];
    STAssertEqualObjects(capabilities.supportedVersions, expectedVersions, @"Given XML contains 2.0 and 2.1 versions");

    STAssertNotNil(capabilities.apiSecurities, @"Given XML should contain API security information");
    STAssertEquals([capabilities.apiSecurities count], (NSUInteger)2, @"Given XML contains 2 API securities");
    APISecurity *security = [capabilities.apiSecurities objectAtIndex:0];
    STAssertEqualObjects(security.path, @"panels", @"First API security path is panels");
    STAssertEqualObjects(security.security, @"none", @"First API security security is none");
    STAssertEquals(security.sslEnabled, NO, @"First API security  does not report SSL");
    security = [capabilities.apiSecurities objectAtIndex:1];
    STAssertEqualObjects(security.path, @"panel", @"Second API security path is panel");
    STAssertEqualObjects(security.security, @"HTTP-basic", @"Second API security security is HTTP-basic");
    STAssertEquals(security.sslEnabled, YES, @"Second API security reports SSL");
    
    STAssertNotNil(capabilities.capabilities, @"Given XML contains a capability definition");
    STAssertEquals([capabilities.capabilities count], (NSUInteger)1, @"Given XML contains 1 capability definition");
    Capability *capability = [capabilities.capabilities objectAtIndex:0];
    STAssertEqualObjects(capability.name, @"SIP", @"Given XML contains SIP capability definition");
    STAssertNotNil(capability.properties, @"SIP capability in given XML contains properties");
    STAssertEquals([capability.properties count], (NSUInteger)1, @"SIP capability in given XML contains 1 property");
    STAssertEqualObjects([capability.properties valueForKey:@"port"], @"5060", @"SIP capability in given XML defines property with value 5060");
}

- (void)testVersionsOnlyCapabilitiesXML
{
    Capabilities *capabilities = [self.parser parseXMLData:[NSData dataWithContentsOfFile:@"APIVersionsOnlyCapabilities.xml"]];
    STAssertNotNil(capabilities, @"Valid XML should return capabilities object");
    STAssertNotNil(capabilities.supportedVersions, @"Given XML should contain versions information");
    STAssertEquals([capabilities.supportedVersions count], (NSUInteger)2, @"Given XML should contain 2 versions");
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.0", @"2.1", nil];
    STAssertEqualObjects(capabilities.supportedVersions, expectedVersions, @"Given XML should contain 2.0 and 2.1 versions");
    STAssertNil(capabilities.apiSecurities, @"No API security information should be defined in the given XML");
    STAssertNil(capabilities.capabilities, @"No other capabilities information should be defined in the given XML");
}

- (void)testNilXMLDataReturnsNilCapabilities
{
    STAssertNil([self.parser parseXMLData:nil], @"Nil data should return nil capabilities");
}

@synthesize parser;

@end