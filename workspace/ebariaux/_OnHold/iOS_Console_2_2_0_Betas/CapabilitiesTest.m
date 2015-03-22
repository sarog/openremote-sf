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
#import "CapabilitiesTest.h"
#import "CapabilitiesWithSettableConsoleVersions.h"

@interface CapabilitiesTest ()

@end

@implementation CapabilitiesTest

- (void)setUp
{
    [CapabilitiesWithSettableConsoleVersions setConsoleVersionsToReport:[NSArray arrayWithObjects:[NSDecimalNumber decimalNumberWithString:@"2.1"], [NSDecimalNumber decimalNumberWithString:@"2.0"], nil]];
}

- (void)testMatchingVersionsEmptyWhenControllerReportsNoVersionAtAll
{
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:nil apiSecurities:nil capabilities:nil];
    STAssertEquals([[capabilities matchingVersions] count], (NSUInteger)0, @"No versions should match");
    [capabilities release];
}

- (void)testMatchingVersionsEmptyWhenControllerReportsNoVersionSupportedByConsole
{
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:[NSArray arrayWithObjects:@"2.3", @"2.5", nil] apiSecurities:nil capabilities:nil];
    STAssertEquals([[capabilities matchingVersions] count], (NSUInteger)0, @"No versions should match");
    [capabilities release];
}

- (void)testMatchingVersionsIdenticalToReportedVersionsWhenControllerReportsExactSameVersionsFromConsole
{
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.1", @"2.0", nil];
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:expectedVersions apiSecurities:nil capabilities:nil];
    STAssertEqualObjects([capabilities matchingVersions], expectedVersions, @"All versions should match");
    [capabilities release];
}

- (void)testMatchingVersionsIdenticalToOrderedReportedVersionsWhenControllerReportsExactSameVersionsFromConsoleButInDifferentOrder
{
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.1", @"2.0", nil];
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:[NSArray arrayWithObjects:@"2.0", @"2.1", nil] apiSecurities:nil capabilities:nil];
    STAssertEqualObjects([capabilities matchingVersions], expectedVersions, @"All versions should match");
    [capabilities release];
}

- (void)testMatchingVersionsIdenticalToReportedVersionsWhenControllerReportsSupersetOfVersionsFromConsole
{
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.1", @"2.0", nil];
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:[NSArray arrayWithObjects:@"2.0", @"2.1", @"2.2", @"1.9", nil] apiSecurities:nil capabilities:nil];
    STAssertEqualObjects([capabilities matchingVersions], expectedVersions, @"All console versions should match");
    [capabilities release];
}

- (void)testMatchingVersionCorrectWhenControllerReportsOneOfTheVersionsFromConsole
{
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.1", nil];
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:expectedVersions apiSecurities:nil capabilities:nil];
    STAssertEqualObjects([capabilities matchingVersions], expectedVersions, @"Reported version should match");
    [capabilities release];
}

- (void)testMatchingVersionCorrectWhenControllerReportsMultipleVersionWithOnlyOneOfTheVersionsFromConsole
{
    NSArray *expectedVersions = [NSArray arrayWithObjects:@"2.1", nil];
    CapabilitiesWithSettableConsoleVersions *capabilities = [[CapabilitiesWithSettableConsoleVersions alloc] initWithSupportedVersions:[NSArray arrayWithObjects:@"2.2", @"2.1", nil] apiSecurities:nil capabilities:nil];
    STAssertEqualObjects([capabilities matchingVersions], expectedVersions, @"Version in common should match");
    [capabilities release];
}

@end