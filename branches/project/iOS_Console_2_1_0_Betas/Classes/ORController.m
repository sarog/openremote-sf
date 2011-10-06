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
#import "ORController.h"
#import "ORGroupMember.h"
#import "ORControllerProxy.h"
#import "NotificationConstant.h"
#import "Definition.h"

@interface ORController ()

- (void)addGroupMembersObject:(ORGroupMember *)value;
- (void)removeGroupMembersObject:(ORGroupMember *)value;
- (void)addGroupMembers:(NSSet *)value;
- (void)removeGroupMembers:(NSSet *)value;

@property (nonatomic, retain) ORControllerGroupMembersFetcher *groupMembersFetcher;

@end

@implementation ORController

@dynamic primaryURL;
@dynamic selectedPanelIdentity;
@dynamic index;
@dynamic userName;
@dynamic password;
@dynamic groupMembers;
@dynamic settingsForControllers;
@dynamic settingsForSelectedController;


// TODO EBR watch groupMembers change and reset activeGroupMember if required


- (void)fetchGroupMembers
{
    if (self.groupMembersFetcher) {
        return;
    }
    groupMembersFetchStatus = GroupMembersFetching;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchingNotification object:self];
    self.groupMembersFetcher = [self.proxy fetchGroupMembersWithDelegate:self];
}

- (void)cancelGroupMembersFetch
{
    [self.groupMembersFetcher cancelFetch];
    self.groupMembersFetcher = nil;
}

- (void)controller:(ORController *)aController fetchGroupMembersDidSucceedWithMembers:(NSArray *)theMembers
{
    // TODO: do that in seperate MOC, save to DB and refresh in main MOC
    self.activeGroupMember = nil;
    self.groupMembers = [NSSet set];
    
    // Add the main url as a group member
    [self addGroupMemberForURL:self.primaryURL];

    NSLog(@"RoundRobin group members are:");
    for (NSString *url in theMembers) {
        NSLog(@"%@", url);
        [self addGroupMemberForURL:url];
    }
    groupMembersFetchStatus = GroupMembersFetchSucceeded;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchSucceededNotification object:self];
    self.groupMembersFetcher = nil;
}

- (void)controller:(ORController *)aController fetchGroupMembersDidFailWithError:(NSError *)error
{
    groupMembersFetchStatus = GroupMembersFetchFailed;    
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchFailedNotification object:self];
    self.groupMembersFetcher = nil;
}

- (void)fetchGroupMembersRequiresAuthenticationForController:(ORController *)aController
{
//    self.password = nil;
    groupMembersFetchStatus = GroupMembersFetchRequiresAuthentication;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchRequiresAuthenticationNotification object:self];    
  //  [[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
    self.groupMembersFetcher = nil;
}

#pragma mark -

- (void)didTurnIntoFault
{
    [proxy release];
    proxy = nil;
    self.groupMembersFetcher = nil;
    self.definition = nil;
    [super didTurnIntoFault];
}

#pragma mark -

- (ORControllerProxy *)proxy
{
    if (!proxy) {
        proxy = [[ORControllerProxy alloc] initWithController:self];
    }
    return proxy;
}
#pragma mark -

- (NSString *)selectedPanelIdentityDisplayString
{
    return self.selectedPanelIdentity?self.selectedPanelIdentity:@"None";    
}

- (void)addGroupMemberForURL:(NSString *)url
{
    for (ORGroupMember *member in self.groupMembers) {
        if ([url isEqualToString:member.url]) {
            return;
        }
    }
    ORGroupMember *groupMember = [NSEntityDescription insertNewObjectForEntityForName:@"ORGroupMember" inManagedObjectContext:self.managedObjectContext];
    groupMember.url = url;
    [self addGroupMembersObject:groupMember];
}

- (void)addGroupMembersObject:(ORGroupMember *)value {    
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"groupMembers"] addObject:value];
    [self didChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)removeGroupMembersObject:(ORGroupMember *)value {
    NSSet *changedObjects = [[NSSet alloc] initWithObjects:&value count:1];
    [self willChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [[self primitiveValueForKey:@"groupMembers"] removeObject:value];
    [self didChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:changedObjects];
    [changedObjects release];
}

- (void)addGroupMembers:(NSSet *)value {    
    [self willChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"groupMembers"] unionSet:value];
    [self didChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueUnionSetMutation usingObjects:value];
}

- (void)removeGroupMembers:(NSSet *)value {
    [self willChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
    [[self primitiveValueForKey:@"groupMembers"] minusSet:value];
    [self didChangeValueForKey:@"groupMembers" withSetMutation:NSKeyValueMinusSetMutation usingObjects:value];
}


@synthesize activeGroupMember;
@synthesize groupMembersFetchStatus;
@synthesize groupMembersFetcher;
@synthesize definition;

@end