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
#import "ORController.h"
#import "ORGroupMember.h"
#import "ORControllerProxy.h"
#import "NotificationConstant.h"
#import "Definition.h"
#import "Capabilities.h"
#import "SensorStatusCache.h"
#import "ClientSideRuntime.h"

NSString *kORControllerGroupMembersFetchingNotification = @"kORControllerGroupMembersFetchingNotification";
NSString *kORControllerGroupMembersFetchSucceededNotification = @"kORControllerGroupMembersFetchSucceededNotification";
NSString *kORControllerGroupMembersFetchFailedNotification = @"kORControllerGroupMembersFetchFailedNotification";
NSString *kORControllerGroupMembersFetchRequiresAuthenticationNotification = @"kORControllerGroupMembersFetchRequiresAuthenticationNotification";

NSString *kORControllerCapabilitiesFetchStatusChange = @"kORControllerCapabilitiesFetchStatusChangeNotification";
NSString *kORControllerPanelIdentitiesFetchStatusChange = @"kORControllerPanelIdentitiesFetchStatusChangeNotification";

@interface ORController ()

- (void)addGroupMembersObject:(ORGroupMember *)value;
- (void)removeGroupMembersObject:(ORGroupMember *)value;
- (void)addGroupMembers:(NSSet *)value;
- (void)removeGroupMembers:(NSSet *)value;

@property (nonatomic, retain) ORControllerGroupMembersFetcher *groupMembersFetcher;

@property (nonatomic, readwrite) ORControllerFetchStatus groupMembersFetchStatus;
@property (nonatomic, readwrite) ORControllerFetchStatus capabilitiesFetchStatus;
@property (nonatomic, readwrite) ORControllerFetchStatus panelIdentitiesFetchStatus;

@property (nonatomic, retain, readwrite) SensorStatusCache *sensorStatusCache;
@property (nonatomic, retain, readwrite) ClientSideRuntime *clientSideRuntime;

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

- (void)awakeFromFetch
{
    [super awakeFromFetch];
    self.controllerAPIVersion = DEFAULT_CONTROLLER_API_VERSION;
    self.sensorStatusCache = [[[SensorStatusCache alloc] initWithNotificationCenter:[NSNotificationCenter defaultCenter]] autorelease];
    self.clientSideRuntime = [[[ClientSideRuntime alloc] initWithController:self] autorelease];
}

- (void)awakeFromInsert
{
    [super awakeFromInsert];
    self.controllerAPIVersion = DEFAULT_CONTROLLER_API_VERSION;
    self.sensorStatusCache = [[[SensorStatusCache alloc] initWithNotificationCenter:[NSNotificationCenter defaultCenter]] autorelease];
    self.clientSideRuntime = [[[ClientSideRuntime alloc] initWithController:self] autorelease];
}

- (void)fetchGroupMembers
{
    if (self.groupMembersFetcher) {
        return;
    }
    self.groupMembersFetchStatus = Fetching;
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
    self.groupMembersFetchStatus = FetchSucceeded;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchSucceededNotification object:self];
    self.groupMembersFetcher = nil;
    
    
    // TODO: if we fail to contact primary URL and had persisted group members, we should still try to get the capabilities and panels
    // as maybe the primary controller is down but one of the group members is not
}

- (void)controller:(ORController *)aController fetchGroupMembersDidFailWithError:(NSError *)error
{
    self.groupMembersFetchStatus = FetchFailed;    
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchFailedNotification object:self];
    self.groupMembersFetcher = nil;
}

- (void)fetchGroupMembersRequiresAuthenticationForController:(ORController *)aController
{
//    self.password = nil;
    self.groupMembersFetchStatus = FetchRequiresAuthentication;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerGroupMembersFetchRequiresAuthenticationNotification object:self];    
  //  [[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
    self.groupMembersFetcher = nil;
}

#pragma mark -

- (void)fetchCapabilities
{
    self.capabilitiesFetchStatus = Fetching;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerCapabilitiesFetchStatusChange object:self];
    [self.proxy fetchCapabilitiesWithDelegate:self];
}

#pragma mark - ORControllerCapabilitiesFetcherDelegate

- (void)fetchCapabilitiesDidSucceedWithCapabilities:(Capabilities *)capabilities
{
    // nil means server can not advertise -> use default API version
    if (!capabilities) {
        self.controllerAPIVersion = DEFAULT_CONTROLLER_API_VERSION;
    } else {
        // Find the versions in common between client and server
        
#warning TODO
        // TODO: this will not work anymore, should convert to decimal numbers
        
        NSMutableSet *supportedVersions = [NSMutableSet setWithArray:capabilities.supportedVersions];
        [supportedVersions intersectSet:[NSSet setWithArray:[Capabilities iosConsoleSupportedVersions]]];
        
        // Sort supported versions descending
        self.controllerAPIVersions = [[supportedVersions allObjects] sortedArrayUsingDescriptors:[NSArray arrayWithObject:[NSSortDescriptor sortDescriptorWithKey:@"" ascending:NO]]];

        // First in the array is the best one
        if ([self.controllerAPIVersions count] > 0) {
            NSNumberFormatter *f = [[NSNumberFormatter alloc] init];
            [f setLocale:[[[NSLocale alloc] initWithLocaleIdentifier:@"en_US"] autorelease]];
            f.minimumFractionDigits = 1; // Ensures 2.0 is converted to "2.0" string
            self.controllerAPIVersion = [f stringFromNumber:[self.controllerAPIVersions objectAtIndex:0]];
            [f release];
        }
    }
    
    // TODO: use log4j    
    NSLog(@"Selected version >%@<", self.controllerAPIVersion);
    
    self.capabilitiesFetchStatus = FetchSucceeded;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerCapabilitiesFetchStatusChange object:self];
}

- (void)fetchCapabilitiesDidFailWithError:(NSError *)error
{
    // TODO
    NSLog(@"fetch capabilities error %@", error);
    self.capabilitiesFetchStatus = FetchFailed;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerCapabilitiesFetchStatusChange object:self];
}

#pragma mark - 

- (void)fetchPanels
{
    self.panelIdentitiesFetchStatus = Fetching;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerPanelIdentitiesFetchStatusChange object:self];
    [self.proxy fetchPanelsWithDelegate:self];
}

#pragma mark - ORControllerPanelsFetcherDelegate

- (void)fetchPanelsDidSucceedWithPanels:(NSArray *)thePanels
{
    self.panelIdentities = thePanels;
    
    NSLog(@"Got panel identities %@", thePanels);
    // TODO change state, notification
    self.panelIdentitiesFetchStatus = FetchSucceeded;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerPanelIdentitiesFetchStatusChange object:self];
}

- (void)fetchPanelsDidFailWithError:(NSError *)error
{
    self.panelIdentities = nil;
    // TODO
    self.panelIdentitiesFetchStatus = FetchFailed;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerPanelIdentitiesFetchStatusChange object:self];
}

- (void)fetchPanelsRequiresAuthenticationForControllerRequest:(ControllerRequest *)controllerRequest
{
    // TODO
    self.panelIdentitiesFetchStatus = FetchRequiresAuthentication;
    [[NSNotificationCenter defaultCenter] postNotificationName:kORControllerPanelIdentitiesFetchStatusChange object:self];
}

#pragma mark -

- (BOOL)hasGroupMembers
{
    return (self.groupMembersFetchStatus == FetchSucceeded);
}

- (BOOL)hasCapabilities
{
    return (self.capabilitiesFetchStatus == FetchSucceeded);
}

- (BOOL)hasPanelIdentities
{
    return (self.panelIdentitiesFetchStatus == FetchSucceeded);
}

#pragma mark -

- (void)didTurnIntoFault
{
    [proxy release];
    proxy = nil;
    self.groupMembersFetcher = nil;
    self.definition = nil;
    self.controllerAPIVersions = nil;
    self.controllerAPIVersion = nil;
    self.sensorStatusCache = nil;
    self.panelIdentities = nil;
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
@synthesize groupMembersFetcher;
@synthesize groupMembersFetchStatus;
@synthesize capabilitiesFetchStatus;
@synthesize panelIdentitiesFetchStatus;
@synthesize definition;
@synthesize controllerAPIVersions;
@synthesize controllerAPIVersion;
@synthesize panelIdentities;
@synthesize sensorStatusCache;
@synthesize clientSideRuntime;

@end