/* OpenRemote, the Home of the Digital Home.
 *  * Copyright 2008-2011, OpenRemote Inc-2009, OpenRemote Inc.
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

@interface AppSettingController ()

@property (nonatomic, retain) ORControllerPanelsFetcher *panelsFetcher;



- (void)autoDiscoverChanged:(id)sender;
- (void)updateTableView;
- (void)saveSettings;
- (void)updatePanelIdentityView;
- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath;
- (BOOL)isAutoServerSection:(NSIndexPath *)indexPath;
- (BOOL)isCustomServerSection:(NSIndexPath *)indexPath;
- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath;
- (void)cancelView:(id)sender;

@end

// The section of table cell where autoDiscoverySwitch is in.
#define AUTO_DISCOVERY_SWITCH_SECTION 0

//auto discovery & customized controller server url are treat as one section
#define CONTROLLER_URLS_SECTION 1

// The section of table cell where selected panel identity is in.
#define PANEL_IDENTITY_SECTION 2

// The section of table cell where clearCache table cell is in.
#define CLEAR_CACHE_SECTION 3

// The section of table cell where security table cells is in.
#define SECURITY_SECTION 4

// Default security port.
#define SECURITY_PORT 8443

@implementation AppSettingController

@synthesize panelsFetcher;

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
	if (self) {
        settingsManager = [ORConsoleSettingsManager sharedORConsoleSettingsManager];
        
		[self setTitle:@"Settings"];
		isEditing = NO;
                         
		done = [[UIBarButtonItem alloc]initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(saveSettings)];		
		cancel = [[UIBarButtonItem alloc]initWithTitle:@"Cancel" style:UIBarButtonItemStylePlain target:self action:@selector(cancelView:)];
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
	if (spinner && [settingsManager.consoleSettings.controllers count] > 0 || force) {
		[spinner removeFromSuperview];
		spinner = nil;
	}	
}

// Check if the section parameter indexPath specified is auto discovery section.
- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath {
	return indexPath.section == AUTO_DISCOVERY_SWITCH_SECTION;
}

// Check if the section parameter indexPath specified is servers section by auto discovery.
- (BOOL)isAutoServerSection:(NSIndexPath *)indexPath {
	if (settingsManager.consoleSettings.isAutoDiscovery && indexPath.section == CONTROLLER_URLS_SECTION) {
		return YES;
	}
	return NO;
}

// Check if the section parameter indexPath specified is servers section by customizing.
- (BOOL)isCustomServerSection:(NSIndexPath *)indexPath {
	if (!settingsManager.consoleSettings.autoDiscovery && indexPath.row < [settingsManager.consoleSettings.controllers count] && indexPath.section == CONTROLLER_URLS_SECTION) {
		if (indexPath.row == 0) {
			return YES;
		}
		return YES;
	}
	return NO;
}

// Check if the row parameter indexPath specified is the cell row of add customized controller server.
- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath {
	if (!settingsManager.consoleSettings.autoDiscovery && indexPath.row >= [settingsManager.consoleSettings.controllers count] && indexPath.section == CONTROLLER_URLS_SECTION) {
		return YES;
	}
	return NO;
}

// The method will be called if auto discovery switch is triggered.
- (void)autoDiscoverChanged:(id)sender {
    // Collect the rows that are present now and should get deleted
    NSMutableArray *deleteIndexPaths = [[NSMutableArray alloc] init];
	for (int i = 0; i < [settingsManager.consoleSettings.controllers count]; i++){
		[deleteIndexPaths addObject:[NSIndexPath indexPathForRow:i inSection:CONTROLLER_URLS_SECTION]];
	}
	if (!settingsManager.consoleSettings.autoDiscovery) {
		[deleteIndexPaths addObject:[NSIndexPath indexPathForRow:[settingsManager.consoleSettings.controllers count] inSection:CONTROLLER_URLS_SECTION]];
	}
    
    settingsManager.consoleSettings.autoDiscovery = ((UISwitch *)sender).on;

    // Collect the rows that will be inserted
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	if (!settingsManager.consoleSettings.autoDiscovery) {
        for (int i = 0; i < [settingsManager.consoleSettings.controllers count]; i++){
            [insertIndexPaths addObject:[NSIndexPath indexPathForRow:i inSection:CONTROLLER_URLS_SECTION]];
        }
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:[settingsManager.consoleSettings.controllers count] inSection:CONTROLLER_URLS_SECTION]];
    }

	if (settingsManager.consoleSettings.autoDiscovery) {
        [settingsManager.consoleSettings removeAllAutoDiscoveredControllers];
    }    
    
    // Model is up to date, apply changes to GUI
	[self.tableView beginUpdates];    
	[self.tableView deleteRowsAtIndexPaths:deleteIndexPaths withRowAnimation:UITableViewRowAnimationFade];
    [self.tableView insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationFade];
    [self.tableView endUpdates];
	
	[deleteIndexPaths release];
	[insertIndexPaths release];

    if (autoDiscoverController) {
        [autoDiscoverController setDelegate:nil];
        [autoDiscoverController release];
        autoDiscoverController = nil;
    }
	
    if (settingsManager.consoleSettings.autoDiscovery) {
		[self showSpinner];
		self.navigationItem.leftBarButtonItem = nil;
		autoDiscoverController = [[ServerAutoDiscoveryController alloc] initWithDelegate:self];
		self.navigationItem.leftBarButtonItem = cancel;
	} else {
        [self updatePanelIdentityView];
	}
		
}

// Delegate method of UITextFieldDelegate and is called when 'return' key pressed. return NO to ignore.
- (BOOL)textFieldShouldReturn:(UITextField *)textField {
	NSScanner* scan = [NSScanner scannerWithString:textField.text]; 
	int val; 
	BOOL isInt = [scan scanInt:&val] && [scan isAtEnd];
	if (isInt) {
		[textField resignFirstResponder];
		return YES;
	} else {
		[ViewHelper showAlertViewWithTitle:@"" Message:@"Port must be a number"];
		return NO;		
	}
}

// To be link with your TextField event "Editing Did Begin"
//  memoryze the current TextField
- (void)textFieldDidBeginEditing:(UITextField *)textField {
	[self.tableView scrollToRowAtIndexPath:[NSIndexPath indexPathForRow:1 inSection:SECURITY_SECTION] 
												atScrollPosition:UITableViewScrollPositionMiddle 
																animated:NO];
}

- (BOOL)textFieldShouldBeginEditing:(UITextField *)textField {
	isEditing = YES;
	return YES;
}

- (void)textFieldDidEndEditing:(UITextField *)textField {
	isEditing = NO;
}

//don't let keyboard hide port field, add space to scroll
-(void) keyboardWillShow:(NSNotification *)note {
	if (!isEditing) {
		return;
	}
	// Get the keyboard size
	CGRect keyboardFrame;
	[[note.userInfo valueForKey:UIKeyboardFrameBeginUserInfoKey] getValue: &keyboardFrame];
    keyboardFrame = [self.view convertRect:keyboardFrame fromView:nil];
	
	CGRect frame = self.tableView.frame;
	
	// Start animation
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
	[UIView setAnimationDuration:0.3f];
	
	// Reduce size of the Table view 
	frame.size.height -= keyboardFrame.size.height;
	
	// Apply new size of table view
	self.tableView.frame = frame;
	
	// Scroll the table view to see the TextField just above the keyboard
	if (portField) {
		CGRect textFieldRect = [self.tableView convertRect:portField.bounds fromView:portField];
		[self.tableView scrollRectToVisible:textFieldRect animated:NO];
	}
	
	[UIView commitAnimations];
}

//remove the space for scroll
-(void) keyboardWillHide:(NSNotification *)note {
	// Get the keyboard size
	CGRect keyboardFrame;
	[[note.userInfo valueForKey:UIKeyboardFrameBeginUserInfoKey] getValue: &keyboardFrame];
    keyboardFrame = [self.view convertRect:keyboardFrame fromView:nil];
	
	CGRect frame = self.tableView.frame;
	
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
	[UIView setAnimationDuration:0.3f];
	
	// Restore size of the Table view 
	frame.size.height += keyboardFrame.size.height;
	
	// Apply new size of table view
	self.tableView.frame = frame;
	
	[UIView commitAnimations];
}

- (void)viewDidLoad {
	[super viewDidLoad];
	// Register notification when the keyboard will be show
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
	
	// Register notification when the keyboard will be hide
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
}

// Updates controller server list in tableview, and updates panel identity view in tableview.
- (void)updateTableView {
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j = 0; j < [settingsManager.consoleSettings.controllers count]; j++) {
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:CONTROLLER_URLS_SECTION]];
	}
	
	[self.tableView beginUpdates];
	[self.tableView insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationFade];
	[self.tableView endUpdates];
	
	[insertIndexPaths release];

    [self updatePanelIdentityView];	
	
	[self forceHideSpinner:NO];
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
- (void)onFindServer:(NSString *)serverUrl {
	[self updateTableView];
}

- (void)onFindServerFail:(NSString *)errorMessage {
	[self forceHideSpinner:YES];
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

- (void)viewWillAppear:(BOOL)animated {
	self.navigationItem.rightBarButtonItem = done;
	self.navigationItem.leftBarButtonItem = cancel;

	[self.tableView reloadData];	
}

#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return [[AppSettingsDefinition sharedAppSettingsDefinition].settingsDefinition count] - 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
	if (section == CONTROLLER_URLS_SECTION) {
		return [settingsManager.consoleSettings.controllers count] + (settingsManager.consoleSettings.autoDiscovery?0:1); // custom URLs need extra cell 'Add url >'
	} else if (section == SECURITY_SECTION) {
		return 2;
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
	static NSString *inputCellIdentifier = @"inputCell";
	
	UITableViewCell *switchCell = [tableView dequeueReusableCellWithIdentifier:switchCellIdentifier];
	UITableViewCell *serverCell = [tableView dequeueReusableCellWithIdentifier:serverCellIdentifier];
	UITableViewCell *panelCell = [tableView dequeueReusableCellWithIdentifier:panelCellIdentifier];
	UITableViewCell *buttonCell = [tableView dequeueReusableCellWithIdentifier:buttonCellIdentifier];
	UITableViewCell *inputCell = [tableView dequeueReusableCellWithIdentifier:inputCellIdentifier];
	
	if (switchCell == nil) {
		switchCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:switchCellIdentifier] autorelease];
		switchCell.selectionStyle = UITableViewCellSelectionStyleNone;
		UISwitch *switchView = [[UISwitch alloc]init];
		switchCell.accessoryView = switchView;
		[switchView release];
	}
	if (serverCell == nil) {
		serverCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:serverCellIdentifier] autorelease];
	}
	if (panelCell == nil) {
		panelCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:panelCellIdentifier] autorelease];
	}
	if (buttonCell == nil) {
		buttonCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:buttonCellIdentifier] autorelease];
	}
	if (inputCell == nil) {
		inputCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:inputCellIdentifier] autorelease];
		inputCell.selectionStyle = UITableViewCellSelectionStyleNone;
		
		UITextField *textField = [[UITextField alloc] init];
		textField.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
		textField.textAlignment = UITextAlignmentRight;
		textField.autoresizingMask = UIViewAutoresizingFlexibleRightMargin;
		textField.returnKeyType = UIReturnKeyDone;
		textField.keyboardType = UIKeyboardTypeNumbersAndPunctuation;
		textField.clearButtonMode = UITextFieldViewModeWhileEditing;// has a clear 'x' button to the right
		textField.autocapitalizationType = UITextAutocapitalizationTypeNone;// no auto capitalization support
		textField.autocorrectionType = UITextAutocorrectionTypeNo;// no auto correction support
		textField.textColor = [UIColor darkGrayColor];
		CGFloat w = inputCell.bounds.size.width;
		CGFloat h = inputCell.bounds.size.height;
		textField.frame = CGRectInset(CGRectMake(w/2, (h - 26)/2.0, w/2,26),20,0);
		portField = textField;
		[textField setDelegate:self];		
		[inputCell setAccessoryView:textField];
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
		} else {
			serverCell.textLabel.text = ((ORController *)[settingsManager.consoleSettings.controllers objectAtIndex:indexPath.row]).primaryURL;
			serverCell.selectionStyle = UITableViewCellSelectionStyleNone;
			if ([settingsManager.consoleSettings.controllers objectAtIndex:indexPath.row] == settingsManager.consoleSettings.selectedController) {
				currentSelectedServerIndex = indexPath;
				serverCell.accessoryType = UITableViewCellAccessoryCheckmark;
			} else {
				serverCell.accessoryType = UITableViewCellAccessoryNone;
			}
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

- (UITableViewCellEditingStyle)tableView:(UITableView *)tableView editingStyleForRowAtIndexPath:(NSIndexPath *)indexPath {
	if ([self isCustomServerSection:indexPath]) {
		return UITableViewCellEditingStyleDelete;
	} 
	return UITableViewCellEditingStyleNone;
}

- (void)tableView:(UITableView *)tv commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
	if (editingStyle == UITableViewCellEditingStyleDelete) {
        [settingsManager.consoleSettings removeConfiguredControllerAtIndex:indexPath.row];
		[self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
	}
}


- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
	if (settingsManager.consoleSettings.autoDiscovery) {
		return NO;
	}
	if ([self isCustomServerSection:indexPath]) {
		return YES;
	} 
	return NO;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	
	UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
	
	if (indexPath.section == AUTO_DISCOVERY_SWITCH_SECTION) {
		return;
	} 
	
	if (indexPath.section == CLEAR_CACHE_SECTION) {
		UIAlertView *alert = [[UIAlertView alloc] initWithTitle:nil 
																										message:@"Are you sure you want to clear image cache?" 
																									 delegate:self 
																					cancelButtonTitle:@"NO" 
																					otherButtonTitles:nil];
		[alert addButtonWithTitle:@"YES"];
		[alert show];
		[alert autorelease];
		cell.selected = NO;
		return;
	}
	
	if ([self isAddCustomServerRow:indexPath]) {
		AddServerViewController *addServerViewController = [[AddServerViewController alloc] init];
		addServerViewController.urlToEdit = nil;
        addServerViewController.delegate = self;
		[[self navigationController] pushViewController:addServerViewController animated:YES];
		[addServerViewController release];
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
		if (currentSelectedServerIndex) {
			UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:currentSelectedServerIndex];
			oldCell.accessoryType = UITableViewCellAccessoryNone;
		} 
		cell.accessoryType = UITableViewCellAccessoryCheckmark;
        settingsManager.consoleSettings.selectedController = [settingsManager.consoleSettings.controllers objectAtIndex:indexPath.row];
        
        
        // TODO EBR at this stage, group members should be fetched in the background, then panel identity
        // for now, it is done syncrhonously
        UpdateController *uc = [[UpdateController alloc] init];
        @try {
            [uc getRoundRobinGroupMembers];            
        }
        @catch (CheckNetworkException *exception) {
            [ViewHelper showAlertViewWithTitle:@"Error" Message:exception.message];
        }
        @finally {
            [uc release];
        }
        
		if (currentSelectedServerIndex && currentSelectedServerIndex.row != indexPath.row) {
			[self updatePanelIdentityView];
		}
		currentSelectedServerIndex = indexPath;
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

#pragma mark AddServerViewControllerDelegate implementation

- (void)didAddServerURL:(NSString *)serverURL
{
    [settingsManager.consoleSettings addConfiguredControllerForURL:serverURL];
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

@end