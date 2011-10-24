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
#import "AppSettingController.h"
#import "DirectoryDefinition.h"
#import "ServerAutoDiscoveryController.h"
#import "AppSettingsDefinition.h"
#import "ViewHelper.h"
#import "UpdateController.h"
#import "NotificationConstant.h"
#import "CheckNetworkException.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "ORControllerProxy.h"
#import "UIColor+ORAdditions.h"
#import "ORControllerGroupMembersFetchStatusIconProvider.h"
#import "TableViewCellWithSelectionAndIndicator.h"

@interface AppSettingController ()

@property (nonatomic, retain) ORControllerPanelsFetcher *panelsFetcher;

// Indicates if a login window must be presented to user for entering credentials when a controller says authentication is required
@property (nonatomic, assign) BOOL askUserForCredentials;

@property (nonatomic, retain) NSIndexPath *currentSelectedServerIndex;

- (void)autoDiscoverChanged:(id)sender;
- (void)saveSettings;
- (void)updatePanelIdentityView;
- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath;
- (BOOL)isControllerRowAtIndexPath:(NSIndexPath *)indexPath;

- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath;
- (void)cancelView:(id)sender;

- (void)autodiscoverControllersIfRequired;
- (void)fetchGroupMembersForAllControllers;
- (void)cancelFetchGroupMembers;

- (void)presentLoginRequestForControllerRequest:(ControllerRequest *)controllerRequest;

@end

// The section of table cell where autoDiscoverySwitch is in.
#define AUTO_DISCOVERY_SWITCH_SECTION 0

//auto discovery & customized controller server url are treat as one section
#define CONTROLLER_URLS_SECTION 1

// The section of table cell where selected panel identity is in.
#define PANEL_IDENTITY_SECTION 2

// The section of table cell where clearCache table cell is in.
#define CLEAR_CACHE_SECTION 3

@implementation AppSettingController

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
	if (self) {
        settingsManager = [ORConsoleSettingsManager sharedORConsoleSettingsManager];
        
		[self setTitle:@"Settings"];
		isEditing = NO;
        
		done = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveSettings)];		
		cancel = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelView:)];
	}
	return self;
}

- (void)dealloc
{
	if (autoDiscoverController) {
		[autoDiscoverController release];
	}
	[updateController release];
	[done release];
	[cancel release];
    self.panelsFetcher = nil;
	self.currentSelectedServerIndex = nil;
	[super dealloc];
}

// Show spinner after title of "Choose Controller" while auto discovery running.
- (void)showSpinner {
	spinner = [[[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(210, 113, 44, 44)] autorelease];
	[spinner startAnimating];
	spinner.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
	spinner.autoresizingMask = (UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin |
															UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin);
	[spinner sizeToFit];
	
	[self.view addSubview:spinner];
}

// Hide spinner
- (void)forceHideSpinner:(BOOL)force {
	if (spinner && ([settingsManager.consoleSettings.controllers count] > 0 || force)) {
		[spinner removeFromSuperview];
		spinner = nil;
	}
}

- (void)populateLoginView:(NSNotification *)notification
{
    [self presentLoginRequestForControllerRequest:[notification.userInfo objectForKey:kAuthenticationRequiredControllerRequest]];
}

- (void)presentLoginRequestForControllerRequest:(ControllerRequest *)controllerRequest
{
	LoginViewController *loginController = [[LoginViewController alloc] initWithDelegate:self context:controllerRequest];
	UINavigationController *loginNavController = [[UINavigationController alloc] initWithRootViewController:loginController];
	[self presentModalViewController:loginNavController animated:NO];
	[loginController release];
	[loginNavController release];
}

// Check if the section parameter indexPath specified is auto discovery section.
- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath {
	return indexPath.section == AUTO_DISCOVERY_SWITCH_SECTION;
}

/**
 * Indicates if the row at the given index path represents a controller entry,
 * i.e. is in the controllers section and is not the "Add" entry.
 */
- (BOOL)isControllerRowAtIndexPath:(NSIndexPath *)indexPath
{
    return (indexPath.section == CONTROLLER_URLS_SECTION && indexPath.row < [settingsManager.consoleSettings.controllers count]);
}

/**
 * Indicates if the row at the given index path is the "Add" controller one.
 */
- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath
{
	return (indexPath.row >= [settingsManager.consoleSettings.controllers count] && indexPath.section == CONTROLLER_URLS_SECTION);
}

- (void)autoDiscoverChanged:(id)sender
{
    settingsManager.consoleSettings.autoDiscovery = ((UISwitch *)sender).on;

    // Irrelevant of the choice, first cancel the current auto-discovery process
    if (autoDiscoverController) {
        [autoDiscoverController setDelegate:nil];
        [autoDiscoverController release]; // This will cancel connections if any
        autoDiscoverController = nil;
    }

    [self autodiscoverControllersIfRequired];
}

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];

    // TODO: get rid of that when fully handled by delegate messages
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchingNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchStatusChanged:) name:kORControllerGroupMembersFetchFailedNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchSucceeded:) name:kORControllerGroupMembersFetchSucceededNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(orControllerGroupMembersFetchRequiresAuthentication:) name:kORControllerGroupMembersFetchRequiresAuthenticationNotification object:nil];

    self.navigationItem.rightBarButtonItem = done;
	self.navigationItem.leftBarButtonItem = cancel;
    
    [self.tableView reloadData]; // IPHONE-107, should not be required otherwise
}

- (void)viewWillDisappear:(BOOL)animated
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];

    [super viewWillDisappear:animated];
}

- (void)viewDidAppear:(BOOL)animated
{
    [self autodiscoverControllersIfRequired];
    [self fetchGroupMembersForAllControllers];
    [super viewDidAppear:animated];
}

// Updates panel identity view, but not persistes identity data into appSettings.plist.
- (void)updatePanelIdentityView {
	UITableView *tv = (UITableView *)self.view;
	UITableViewCell *identityCell = [tv cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:PANEL_IDENTITY_SECTION]];
	identityCell.textLabel.text = @"None";

    self.panelsFetcher = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController fetchPanelsWithDelegate:self];
    
    // TODO EBR : this might need to be cancelled some time
}

// Cancle(Dismiss) appSettings view.
- (void)cancelView:(id)sender {
    [[ORConsoleSettingsManager sharedORConsoleSettingsManager] cancelConsoleSettingsChanges];
	[self dismissModalViewControllerAnimated:YES];
}

// Persists settings info into appSettings.plist .
- (void)saveSettings {
	if ([settingsManager.consoleSettings.controllers count] == 0) {
		[ViewHelper showAlertViewWithTitle:@"Warning" Message:@"No Controller. Please configure Controller URL manually."];
	} else {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
		done.enabled = NO;
		cancel.enabled = NO;
		
        [[ORConsoleSettingsManager sharedORConsoleSettingsManager] saveConsoleSettings];

		if (updateController) {
			[updateController release];
			updateController = nil;
		}
		updateController = [[UpdateController alloc] initWithDelegate:self];
		[updateController checkConfigAndUpdate];
	}	
}

#pragma mark Delegate method of ServerAutoDiscoveryController

- (void)onFindServer:(ORController *)aController {
    // TODO: check what happens when multiple controllers are discovered
    
    // TODO: aController has already been added to the ORConsoleSettings collection of controller by the ServerAutoDiscoveryController but the MOC hasn't been saved. Is this OK ? Should we add here ? I think not
    [aController fetchGroupMembers];

    [self.tableView reloadData];
	// TODO: Disabled for now, see IPHONE-111 [self forceHideSpinner:NO];
}

- (void)onFindServerFail:(NSString *)errorMessage {
    // TODO: check when this is reported
    // TODO: there should be a way to get notified when the auto-discovery process is finished, not an error if nothing is found
    
	// TODO: Disabled for now, see IPHONE-111 [self forceHideSpinner:YES];
	[ViewHelper showAlertViewWithTitle:@"Auto Discovery" Message:errorMessage];	
}

#pragma mark Delegate method of UpdateController

- (void)didUpdate {
	[self dismissModalViewControllerAnimated:YES];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshGroupsView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	[self dismissModalViewControllerAnimated:NO];
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Use Local Cache" Message:errorMessage];
	}
}

- (void)didUpdateFail:(NSString *)errorMessage {
	[self dismissModalViewControllerAnimated:NO];
	if ([errorMessage isEqualToString:@"401"]) {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationPopulateCredentialView object:nil];
	} else {
		[ViewHelper showAlertViewWithTitle:@"Update Failed" Message:errorMessage];
	}
}

#pragma mark - ORController group members fetch notifications

- (void)orControllerGroupMembersFetchStatusChanged:(NSNotification *)notification
{
// IPHONE-107    [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:[NSIndexPath indexPathForRow:[settingsManager.consoleSettings.controllers indexOfObject:[notification object]] inSection:CONTROLLER_URLS_SECTION]] withRowAnimation:UITableViewRowAnimationNone];
    [self.tableView reloadData];
}

- (void)orControllerGroupMembersFetchSucceeded:(NSNotification *)notification
{
    // IPHONE-107    [self.tableView reloadRowsAtIndexPaths:[NSArray arrayWithObject:[NSIndexPath indexPathForRow:[settingsManager.consoleSettings.controllers indexOfObject:[notification object]] inSection:CONTROLLER_URLS_SECTION]] withRowAnimation:UITableViewRowAnimationNone];

    NSLog(@"controllers %d", [settingsManager.consoleSettings.controllers count]);
    if (!settingsManager.consoleSettings.selectedController && [settingsManager.consoleSettings.controllers count] == 1) {
        settingsManager.consoleSettings.selectedController = [notification object];
    }
 
    [self.tableView reloadData];
}

- (void)orControllerGroupMembersFetchRequiresAuthentication:(NSNotification *)notification
{
    [self orControllerGroupMembersFetchStatusChanged:notification];
    if (self.askUserForCredentials) {
        [self populateLoginView:nil];
    }
    self.askUserForCredentials = NO;
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return [[AppSettingsDefinition sharedAppSettingsDefinition].settingsDefinition count] - 1;
}

// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if (section == CONTROLLER_URLS_SECTION) {
        NSLog(@"Number of rows in table view controller section %d", [settingsManager.consoleSettings.controllers count] + 1);
		return [settingsManager.consoleSettings.controllers count] + 1; // custom URLs need extra cell 'Add url >'
	}
	return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section
{
	if (section == [self numberOfSectionsInTableView:tableView] - 1) {
		return [NSString stringWithFormat:@"Version %@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]];
	} 
	return [[AppSettingsDefinition sharedAppSettingsDefinition] getSectionFooterWithIndex:section];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {

	if(section >= PANEL_IDENTITY_SECTION) {
		section++;
	} 
	return [[AppSettingsDefinition sharedAppSettingsDefinition] getSectionHeaderWithIndex:section];
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	static NSString *switchCellIdentifier = @"switchCell";
	static NSString *serverCellIdentifier = @"serverCell";
	static NSString *panelCellIdentifier = @"panelCell";
	static NSString *buttonCellIdentifier = @"buttonCell";
	
	UITableViewCell *switchCell = [tableView dequeueReusableCellWithIdentifier:switchCellIdentifier];
	TableViewCellWithSelectionAndIndicator *serverCell = (TableViewCellWithSelectionAndIndicator *)[tableView dequeueReusableCellWithIdentifier:serverCellIdentifier];
	UITableViewCell *panelCell = [tableView dequeueReusableCellWithIdentifier:panelCellIdentifier];
	UITableViewCell *buttonCell = [tableView dequeueReusableCellWithIdentifier:buttonCellIdentifier];
	
	if (switchCell == nil) {
		switchCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:switchCellIdentifier] autorelease];
		switchCell.selectionStyle = UITableViewCellSelectionStyleNone;
		UISwitch *switchView = [[UISwitch alloc]init];
		switchCell.accessoryView = switchView;
		[switchView release];
	}
	if (serverCell == nil) {
		serverCell = [[[TableViewCellWithSelectionAndIndicator alloc] initWithReuseIdentifier:serverCellIdentifier] autorelease];
	}
	if (panelCell == nil) {
		panelCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:panelCellIdentifier] autorelease];
	}
	if (buttonCell == nil) {
		buttonCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:buttonCellIdentifier] autorelease];
	}
	
	if ([self isAutoDiscoverySection:indexPath]) {
		switchCell.textLabel.text = [[[AppSettingsDefinition sharedAppSettingsDefinition] getAutoDiscoveryDic] objectForKey:@"name"];
		UISwitch *switchView = (UISwitch *)switchCell.accessoryView;
		[switchView setOn:settingsManager.consoleSettings.autoDiscovery];
		[switchView addTarget:self action:@selector(autoDiscoverChanged:) forControlEvents:UIControlEventValueChanged];
		return switchCell;
	} else if (indexPath.section == CONTROLLER_URLS_SECTION) {
		if ([self isAddCustomServerRow:indexPath]) {
			serverCell.textLabel.text = @"Add New Controller...";
			serverCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			serverCell.selectionStyle = UITableViewCellSelectionStyleBlue;
            serverCell.entrySelected = NO;
            serverCell.indicatorView = nil;
		} else {
            ORController *controller = (ORController *)[settingsManager.consoleSettings.controllers objectAtIndex:indexPath.row];
			serverCell.textLabel.text = controller.primaryURL;
			serverCell.selectionStyle = UITableViewCellSelectionStyleNone;
            serverCell.accessoryType = UITableViewCellAccessoryDetailDisclosureButton;

			if (controller == settingsManager.consoleSettings.selectedController) {
				self.currentSelectedServerIndex = indexPath;
                serverCell.entrySelected = YES;
			} else {
                serverCell.entrySelected = NO;
			}
            serverCell.indicatorView = [ORControllerGroupMembersFetchStatusIconProvider viewForGroupMembersFetchStatus:controller.groupMembersFetchStatus];
		}
		return serverCell;
	} else if (indexPath.section == PANEL_IDENTITY_SECTION) {
		panelCell.textLabel.text = settingsManager.consoleSettings.selectedController.selectedPanelIdentity?settingsManager.consoleSettings.selectedController.selectedPanelIdentity:@"None";
		panelCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
		panelCell.selectionStyle = UITableViewCellSelectionStyleBlue;
		return panelCell;
	} else if (indexPath.section == CLEAR_CACHE_SECTION) {
		buttonCell.textLabel.text = @"Clear Image Cache";
		buttonCell.textLabel.textAlignment = UITextAlignmentCenter;
		buttonCell.selectionStyle = UITableViewCellSelectionStyleGray;
		return buttonCell;
	}
	return nil;
}

- (void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath
{
    if (indexPath.section == CONTROLLER_URLS_SECTION) {
        ControllerDetailViewController *cdvc = [[ControllerDetailViewController alloc] initWithController:((ORController *)[settingsManager.consoleSettings.controllers objectAtIndex:indexPath.row])];
        cdvc.delegate = self;
		[[self navigationController] pushViewController:cdvc animated:YES];
		[cdvc release];        
    }
}

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath
{
	return [self isControllerRowAtIndexPath:indexPath]?UITableViewCellEditingStyleDelete:UITableViewCellEditingStyleNone;
}

- (void)tableView:(UITableView *)tv commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
	if (editingStyle == UITableViewCellEditingStyleDelete) {
        [settingsManager.consoleSettings removeControllerAtIndex:indexPath.row];
		[self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
	}
}

- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath
{
    return [self isControllerRowAtIndexPath:indexPath];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
	
	if (indexPath.section == AUTO_DISCOVERY_SWITCH_SECTION) {
		return;
	} 
	
	if (indexPath.section == CLEAR_CACHE_SECTION) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil message:@"Are you sure you want to clear image cache?" delegate:self cancelButtonTitle:@"NO" otherButtonTitles:nil];
		[alert addButtonWithTitle:@"YES"];
		[alert show];
		[alert autorelease];
		cell.selected = NO;
		return;
	}
	
	if ([self isAddCustomServerRow:indexPath]) {
        ControllerDetailViewController *cdvc = [[ControllerDetailViewController alloc] initWithManagedObjectContext:settingsManager.managedObjectContext];
        cdvc.delegate = self;
		[[self navigationController] pushViewController:cdvc animated:YES];
		[cdvc release];
		return;
	} else if (indexPath.section == PANEL_IDENTITY_SECTION) {
		if (!settingsManager.consoleSettings.selectedController) {
			[ViewHelper showAlertViewWithTitle:@"Warning" Message:@"No Controller. Please configure Controller URL manually."];
			cell.selected = NO;
			return;
		}
		ChoosePanelViewController *choosePanelViewController = [[ChoosePanelViewController alloc] init];
        choosePanelViewController.delegate = self;
		[[self navigationController] pushViewController:choosePanelViewController animated:YES];
		[choosePanelViewController release];
		return;
	}
	
	
	if (indexPath.section == CONTROLLER_URLS_SECTION) {        
		if (self.currentSelectedServerIndex) {
			UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:self.currentSelectedServerIndex];
            ((TableViewCellWithSelectionAndIndicator *)oldCell).entrySelected = NO;
 		}
        ((TableViewCellWithSelectionAndIndicator *)cell).entrySelected = YES;

        settingsManager.consoleSettings.selectedController = [settingsManager.consoleSettings.controllers objectAtIndex:indexPath.row];
        
        self.askUserForCredentials = YES;
        
        // TODO: might not be required if update of panel identities trigger this
        [settingsManager.consoleSettings.selectedController fetchGroupMembers];
        
		if (self.currentSelectedServerIndex && self.currentSelectedServerIndex.row != indexPath.row) {            
            // TODO: review how this gets updated
			[self updatePanelIdentityView];
		}
		self.currentSelectedServerIndex = indexPath;
	}
	
}

#pragma mark alert delegate
-(void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (buttonIndex == 1) {
		[FileUtils deleteFolderWithPath:[DirectoryDefinition imageCacheFolder]];
	} 
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	return YES;
}

#pragma mark ControllerDetailViewControllerDelegate implementation

- (void)didAddController:(ORController *)controller
{
    [settingsManager.consoleSettings addController:controller];
    [self.navigationController popViewControllerAnimated:YES];
//IPHONE-107    [self.tableView insertRowsAtIndexPaths:[NSArray arrayWithObject:[NSIndexPath indexPathForRow:[settingsManager.consoleSettings.controllers count] - 1 inSection:CONTROLLER_URLS_SECTION]] withRowAnimation:UITableViewRowAnimationFade];
    
    [controller fetchGroupMembers];
}

- (void)didEditController:(ORController *)controller
{
    [self.navigationController popViewControllerAnimated:YES];
//IPHONE-107 : was not there but if there is no reloadData in viewWillAppear, there should be a reload of the updated row here
    [controller fetchGroupMembers];
}

- (void)didFailToAddController
{
    [self.navigationController popViewControllerAnimated:YES];
}

#pragma mark ChoosePanelViewControllerDelegate implementation

- (void)didSelectPanelIdentity:(NSString *)identity
{
    [ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.selectedPanelIdentity = identity;
    [self.navigationController popViewControllerAnimated:YES];    
}

#pragma mark ORControllerPanelsFetcherDelegate implementation

- (void)fetchPanelsDidSucceedWithPanels:(NSArray *)panels
{
    // When a controller gets selected, the list of available panels is fetched.
    // If there is only one panel available, it is automatically selected.
	UITableViewCell *identityCell = [self.tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:PANEL_IDENTITY_SECTION]];
	if (panels.count == 1) {
        settingsManager.consoleSettings.selectedController.selectedPanelIdentity = [panels objectAtIndex:0];
        identityCell.textLabel.text = settingsManager.consoleSettings.selectedController.selectedPanelIdentity;
	} else {
		settingsManager.consoleSettings.selectedController.selectedPanelIdentity = nil;
        identityCell.textLabel.text = @"None";
	}
}

- (void)fetchPanelsRequiresAuthenticationForControllerRequest:(ControllerRequest *)controllerRequest
{
    [self presentLoginRequestForControllerRequest:controllerRequest];
}

- (void)fetchPanelsRequiresAuthentication
{
    [self populateLoginView:nil];
}

#pragma mark LoginViewControllerDelegate implementation

- (void)loginViewControllerDidCancelLogin:(LoginViewController *)controller
{
    // TODO: Is this still required ?
    [[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];

    [self dismissModalViewControllerAnimated:YES];
}

- (void)loginViewController:(LoginViewController *)controller didProvideUserName:(NSString *)username password:(NSString *)password
{
    ORController *orController = ((ControllerRequest *)controller.context).controller;
    orController.userName = username;
	orController.password = password;
    
    // TODO: we might not want to save here, maybe have a method to set this and save in dedicated MOC
    [[ORConsoleSettingsManager sharedORConsoleSettingsManager] saveConsoleSettings];
    
	[self dismissModalViewControllerAnimated:YES];
    
    if (controller.context) {
        [(ControllerRequest *)controller.context retry];
    } else {
        // By default if we don't know where we're coming from, trigger a fetch group members

        // TODO: double check fetchGroupMember is the only source that can trigger this
        // No, it is not the only source:
        // - fetchPanels will trigger this
        // - UpdateController can trigger it
        
        // TODO: put appropriate info in context so that correct call can be performed
        
        // TODO: the controller should be passed back in the message
        [settingsManager.consoleSettings.selectedController fetchGroupMembers];
    }
}

#pragma mark -

/**
 * Launch an auto-discovery process in the background.
 * The process is only conducted if the consoleSettings.isAutoDiscovery is true.
 */
- (void)autodiscoverControllersIfRequired
{
    if (settingsManager.consoleSettings.autoDiscovery) {
        // TODO: Disabled for now, see IPHONE-111 [self showSpinner];

        if (autoDiscoverController) {
            [autoDiscoverController setDelegate:nil];
            [autoDiscoverController release]; // This will cancel connections if any
            autoDiscoverController = nil;
        }
		autoDiscoverController = [[ServerAutoDiscoveryController alloc] initWithDelegate:self];
	}
}

- (void)fetchGroupMembersForAllControllers
{
    NSLog(@">>fetchGroupMembersForAllControllers -> settingsManager.consoleSettings.controllers: %d", [settingsManager.consoleSettings.controllers count]);
    [settingsManager.consoleSettings.controllers makeObjectsPerformSelector:@selector(fetchGroupMembers)];
}

- (void)cancelFetchGroupMembers
{
    [settingsManager.consoleSettings.controllers makeObjectsPerformSelector:@selector(cancelGroupMembersFetch)];
}

@synthesize panelsFetcher;
@synthesize askUserForCredentials;
@synthesize currentSelectedServerIndex;

- (void)setPanelsFetcher:(ORControllerPanelsFetcher *)aPanelsFetcher
{
    if (panelsFetcher != aPanelsFetcher) {
        panelsFetcher.delegate = nil;
        [panelsFetcher release];
        panelsFetcher = [aPanelsFetcher retain];
    }
}

@end