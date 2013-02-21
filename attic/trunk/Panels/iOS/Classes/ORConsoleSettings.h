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
#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ORController;

@interface ORConsoleSettings : NSManagedObject {
@private
    NSArray *autoDiscoveredControllers;
    NSArray *configuredController;
}

- (void)addConfiguredController:(ORController *)controller;
- (ORController *)addConfiguredControllerForURL:(NSString *)url;
- (void)removeConfiguredControllerAtIndex:(NSUInteger)index;

- (void)removeAllAutoDiscoveredControllers;
- (void)addAutoDiscoveredController:(ORController *)controller;
- (ORController *)addAutoDiscoveredControllerForURL:(NSString *)url;

@property (nonatomic, assign, getter=isAutoDiscovery) BOOL autoDiscovery;
@property (nonatomic, retain) NSSet *unorderedAutoDiscoveredControllers;
@property (nonatomic, retain) NSSet *unorderedConfiguredControllers;
@property (nonatomic, retain) ORController *selectedDiscoveredController;
@property (nonatomic, retain) ORController *selectedConfiguredController;

@property (readonly) NSArray *autoDiscoveredControllers;
@property (readonly) NSArray *configuredControllers;

@property (readonly) NSArray *controllers;
@property (nonatomic, assign) ORController *selectedController;

@end
