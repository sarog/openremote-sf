/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2011, OpenRemote Inc.
 * 
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3.0 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ORConsoleSettings;
@class ORGroupMember;

@interface ORController : NSManagedObject {
@private
    ORGroupMember *activeGroupMember;
}

@property (nonatomic, retain) NSString * primaryURL;
@property (nonatomic, retain) NSString *selectedPanelIdentity;
@property (nonatomic, retain) NSNumber * index;
@property (nonatomic, retain) NSSet* groupMembers;
@property (nonatomic, retain) ORConsoleSettings * settingsForAutoDiscoveredControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForConfiguredControllers;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedDiscoveredController;
@property (nonatomic, retain) ORConsoleSettings * settingsForSelectedConfiguredController;

@property (nonatomic, readonly) NSString *selectedPanelIdentityDisplayString;

@property (nonatomic, assign) ORGroupMember *activeGroupMember;

- (void)addGroupMemberForURL:(NSString *)url;

@end
