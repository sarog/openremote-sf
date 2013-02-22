/* OpenRemote, the Home of the Digital Home.
 * Copyright 2008-2009, OpenRemote Inc.
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
#import "AddServerViewController.h"
#import "AppSettingsDefinition.h"
#import "ViewHelper.h"
#import "UpdateController.h"
#import "NotificationConstant.h"
#import "ChoosePanelViewController.h"
#import "DataBaseService.h"
#import "NotificationConstant.h"

@interface AppSettingController (Private)
-(NSMutableArray *)getCurrentServersWithAutoDiscoveryEnable:(BOOL)b;
- (void)autoDiscoverChanged:(id)sender;
- (void)deleteAllRow;
- (void)updateTableView;
- (void)saveSettings;
- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath;
- (BOOL)isAutoServerSection:(NSIndexPath *)indexPath;
- (BOOL)isCustomServerSection:(NSIndexPath *)indexPath;
- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath;
- (void)cancelView:(id)sender;
-(NSString *)getUnsavedChosenServerUrl;
@end

#define AUTO_DISCOVERY_SWITCH_SECTION 0

//auto discovery & customized controller server url are treat as one section
#define CONTROLLER_URLS_SECTION 1

#define PANEL_IDENTITY_SECTION 2

#define CLEAR_CACHE_SECTION 3

#define SECURITY_SECTION 4

#define AUTO_DISCOVERY_TIMER_INTERVAL 1
#define SECURITY_PORT 8443

@implementation AppSettingController


- (id)init {
	if (self = [super initWithStyle:UITableViewStyleGrouped]) {
		[self setTitle:@"Settings"];
		isEditing = NO;
		autoDiscovery = [AppSettingsDefinition isAutoDiscoveryEnable];
		
		done = [[UIBarButtonItem alloc]initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(saveSettings)];		
		cancel = [[UIBarButtonItem alloc]initWithTitle:@"Cancel" style:UIBarButtonItemStylePlain target:self action:@selector(cancelView:)];

	}
	return self;
}

- (void)showSpinner {
	spinner = [[[UIActivityIndicatorView alloc] initWithFrame:CGRectMake(210, 113, 44, 44)] autorelease];
	[spinner startAnimating];
	spinner.activityIndicatorViewStyle = UIActivityIndicatorViewStyleGray;
	spinner.autoresizingMask = (UIViewAutoresizingFlexibleLeftMargin | UIViewAutoresizingFlexibleRightMargin |
															UIViewAutoresizingFlexibleTopMargin | UIViewAutoresizingFlexibleBottomMargin);
	[spinner sizeToFit];
	
	[self.view addSubview:spinner];
}

- (void)forceHideSpinner:(BOOL)force {
	if (spinner && serverArray.count > 0 || force) {
		[spinner removeFromSuperview];
		spinner = nil;
	}	
}

-(NSMutableArray *)getCurrentServersWithAutoDiscoveryEnable:(BOOL)b {
	if (b) {
		return [AppSettingsDefinition getAutoServers];
	} else {
		return [AppSettingsDefinition getCustomServers];
	}
}

-(NSString *)getUnsavedChosenServerUrl {
	NSArray *shownServers = [self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery];
	NSString *url = nil;
	for (int i=0; i < shownServers.count; i++) {
		if ([[[shownServers objectAtIndex:i] valueForKey:@"choose"] boolValue]) {
			url = [[shownServers objectAtIndex:i] valueForKey:@"url"];
			break;
		} 
	}
	return url;
}

- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath {
	return indexPath.section == AUTO_DISCOVERY_SWITCH_SECTION;
}
- (BOOL)isAutoServerSection:(NSIndexPath *)indexPath {
	if (autoDiscovery && indexPath.section == CONTROLLER_URLS_SECTION) {
		return YES;
	}
	return NO;
}
- (BOOL)isCustomServerSection:(NSIndexPath *)indexPath {
	if (!autoDiscovery && indexPath.row < [serverArray count] && indexPath.section == CONTROLLER_URLS_SECTION) {
		if (indexPath.row == 0) {
			return YES;
		}
		return YES;
	}
	return NO;
}
- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath {
	if (!autoDiscovery && indexPath.row >= [serverArray count] && indexPath.section == CONTROLLER_URLS_SECTION) {
		return YES;
	}
	return NO;
}

- (void)autoDiscoverChanged:(id)sender {
	UISwitch *s = (UISwitch *)sender;
	autoDiscovery = s.on;
	
	[self deleteAllRow];
	
	if (autoDiscovery) {
		[self showSpinner];
		[AppSettingsDefinition removeAllAutoServer];
		[AppSettingsDefinition writeToFile];
		self.navigationItem.leftBarButtonItem = nil;
		if (autoDiscoverController) {
			[autoDiscoverController setDelegate:nil];
			[autoDiscoverController release];
			autoDiscoverController = nil;
		}
		autoDiscoverController = [[ServerAutoDiscoveryController alloc]initWithDelegate:self];
		getAutoServersTimer = [[NSTimer scheduledTimerWithTimeInterval:AUTO_DISCOVERY_TIMER_INTERVAL target:self selector:@selector(updateTableView) userInfo:nil repeats:NO] retain];
		self.navigationItem.leftBarButtonItem = cancel;
	} else {
		if(getAutoServersTimer && [getAutoServersTimer isValid]){
			[getAutoServersTimer invalidate];
		}
		
		if (autoDiscoverController) {
			[autoDiscoverController setDelegate:nil];
			[autoDiscoverController release];
			autoDiscoverController = nil;
		}
		//self.navigationItem.leftBarButtonItem = edit;
		[self updateTableView];
	}
		
}

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
	CGRect keyboardBounds;
	[[note.userInfo valueForKey:UIKeyboardBoundsUserInfoKey] getValue: &keyboardBounds];
	
	CGRect frame = self.tableView.frame;
	
	// Start animation
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
	[UIView setAnimationDuration:0.3f];
	
	// Reduce size of the Table view 
	frame.size.height -= keyboardBounds.size.height;
	
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
	CGRect keyboardBounds;
	[[note.userInfo valueForKey:UIKeyboardBoundsUserInfoKey] getValue: &keyboardBounds];
	
	// Detect orientation
	CGRect frame = self.tableView.frame;
	
	[UIView beginAnimations:nil context:NULL];
	[UIView setAnimationBeginsFromCurrentState:YES];
	[UIView setAnimationDuration:0.3f];
	
	// Reduce size of the Table view 
	frame.size.height += keyboardBounds.size.height;
	
	// Apply new size of table view
	self.tableView.frame = frame;
	
	[UIView commitAnimations];
}

- (void)viewDidLoad {
	[super viewDidLoad];
	// Register notification when the keyboard will be show
	[[NSNotificationCenter defaultCenter] addObserver:self
																					 selector:@selector(keyboardWillShow:)
																							 name:UIKeyboardWillShowNotification
																						 object:nil];
	
	// Register notification when the keyboard will be hide
	[[NSNotificationCenter defaultCenter] addObserver:self
																					 selector:@selector(keyboardWillHide:)
																							 name:UIKeyboardWillHideNotification
																						 object:nil];
}

- (void)deleteAllRow {
	UITableView *tv = (UITableView *)self.view;
	
	[tv beginUpdates];
	NSMutableArray *deleteIndexPaths = [[NSMutableArray alloc] init];
	for (int i=0;i <serverArray.count;i++){
		[deleteIndexPaths addObject:[NSIndexPath indexPathForRow:i inSection:CONTROLLER_URLS_SECTION]];
	}
	if (autoDiscovery) {
		[deleteIndexPaths addObject:[NSIndexPath indexPathForRow:[serverArray count] inSection:CONTROLLER_URLS_SECTION]];
	}
	[serverArray removeAllObjects];
	[tv deleteRowsAtIndexPaths:deleteIndexPaths withRowAnimation:UITableViewRowAnimationFade];
	
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	
	if (!autoDiscovery) {
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:0 inSection:CONTROLLER_URLS_SECTION]];
		[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationFade];
	}
	[tv endUpdates];
	
	[deleteIndexPaths release];
	[insertIndexPaths release];
	
}
- (void)updateTableView {
	UITableView *tv = (UITableView *)self.view;
	[tv beginUpdates];
	
	NSArray *newArray = nil;
	newArray = [self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery];
	
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j=0;j < newArray.count;j++){
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:CONTROLLER_URLS_SECTION]];
	}
	[serverArray addObjectsFromArray:newArray];
	
	[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationFade];
	[tv endUpdates];
	
	[insertIndexPaths release];
	[self forceHideSpinner:NO];
}


- (void)cancelView:(id)sender {
	[self dismissModalViewControllerAnimated:YES];
}
		
- (void)saveSettings {
	if (serverArray.count == 0) {
		[ViewHelper showAlertViewWithTitle:@"Warning" 
															 Message:@"No Controller. Please configure Controller URL manually."];
	} else {
		[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
		done.enabled = NO;
		cancel.enabled = NO;
		
		[[AppSettingsDefinition getAutoDiscoveryDic] setValue:[NSNumber numberWithBool:autoDiscovery] forKey:@"value"];
		[AppSettingsDefinition writeToFile];
		if (updateController) {
			[updateController release];
			updateController = nil;
		}
		updateController = [[UpdateController alloc] initWithDelegate:self];
		[updateController checkConfigAndUpdate];
	}	
}

- (void)saveUseSSL:(id)sender {
	UISwitch *s = (UISwitch *)sender;
	[AppSettingsDefinition setUseSSL:s.on];
	[AppSettingsDefinition writeToFile];
}

- (void)saveSslPort:(id)sender {
	UITextField *t = (UITextField *)sender;
	[AppSettingsDefinition setSslPort:[t.text intValue]];
	[AppSettingsDefinition writeToFile];
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
- (void)didUpadted {
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
	
	if (serverArray) {
		[serverArray release];
	}
	serverArray = [[NSMutableArray alloc]init];
	if (autoDiscovery) {
		[serverArray addObjectsFromArray:[AppSettingsDefinition getAutoServers]];
	} else {
		[serverArray addObjectsFromArray:[AppSettingsDefinition getCustomServers]];
	}
	
	[self.tableView reloadData];
	
}

#pragma mark Table view methods
// 'auto-discovery URLs' share one section with 'custom URLs', -1
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return [AppSettingsDefinition getAppSettings].count - 1;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	if (section == CONTROLLER_URLS_SECTION){
		return autoDiscovery ? serverArray.count : serverArray.count + 1;// custom URLs need extra cell 'Add url >'
	} else if (section == SECURITY_SECTION){
		return 2;
	}
	return 1;
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
	if (section == [self numberOfSectionsInTableView:tableView] - 1) {
		return [NSString stringWithFormat:@"Version %@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]];
	} 
	return [AppSettingsDefinition getSectionFooterWithIndex:section];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {

	if(section >= PANEL_IDENTITY_SECTION) {
		section++;
	} 
	return [AppSettingsDefinition getSectionHeaderWithIndex:section];
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
		switchCell.textLabel.text = [[AppSettingsDefinition getAutoDiscoveryDic] objectForKey:@"name"];
		UISwitch *switchView = (UISwitch *)switchCell.accessoryView;
		[switchView setOn:autoDiscovery];
		[switchView addTarget:self action:@selector(autoDiscoverChanged:) forControlEvents:UIControlEventValueChanged];
		return switchCell;
	} else if (indexPath.section == CONTROLLER_URLS_SECTION) {
		if ([self isAddCustomServerRow:indexPath]) {
			serverCell.textLabel.text = @"Add New Controller...";
			serverCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			serverCell.selectionStyle = UITableViewCellSelectionStyleBlue;
		}
		else {
			serverCell.textLabel.text = [[serverArray objectAtIndex:indexPath.row] objectForKey:@"url"];
			serverCell.selectionStyle = UITableViewCellSelectionStyleNone;
			if ( [[[serverArray objectAtIndex:indexPath.row] objectForKey:@"choose"] boolValue]) {
				currentSelectedServerIndex = indexPath;
				serverCell.accessoryType = UITableViewCellAccessoryCheckmark;
			} else {
				serverCell.accessoryType = UITableViewCellAccessoryNone;
			}
		}
		return serverCell;
	} else if (indexPath.section == PANEL_IDENTITY_SECTION) {
		panelCell.textLabel.text = [[AppSettingsDefinition getPanelIdentityDic] objectForKey:@"identity"];
		panelCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
		panelCell.selectionStyle = UITableViewCellSelectionStyleBlue;
		return panelCell;
	} else if (indexPath.section == SECURITY_SECTION) {
		if (indexPath.row == 0) {
			switchCell.textLabel.text = @"Use SSL";
			UISwitch *switchView = (UISwitch *)switchCell.accessoryView;
			[switchView setOn:[AppSettingsDefinition useSSL]];
			[switchView addTarget:self action:@selector(saveUseSSL:) forControlEvents:UIControlEventValueChanged];
			return switchCell;
		} else {
			inputCell.textLabel.text = @"SSL Port";
			portField.text = [NSString stringWithFormat:@"%d", [AppSettingsDefinition sslPort]];
			portField.placeholder = [NSString stringWithFormat:@"%d", SECURITY_PORT];
			[portField addTarget:self action:@selector(saveSslPort:) forControlEvents:UIControlEventEditingDidEnd];
			return inputCell;
		}
		
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
	// If row is deleted, remove it from the list.
	if (editingStyle == UITableViewCellEditingStyleDelete) {
		if ([tv cellForRowAtIndexPath:indexPath].accessoryType == UITableViewCellAccessoryCheckmark) {
			currentSelectedServerIndex  = nil;
		}
		[[self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery] removeObjectAtIndex:indexPath.row];
		[serverArray removeObjectAtIndex:indexPath.row];
		[self.tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:UITableViewRowAnimationFade];
	}
}


- (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
	if (autoDiscovery) {
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
		AddServerViewController *addServerViewController = [[AddServerViewController alloc]init];
		addServerViewController.editingItem = nil;
		addServerViewController.servers = [self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery];
		[[self navigationController] pushViewController:addServerViewController animated:YES];
		[addServerViewController release];
		return;
	} else if (indexPath.section == PANEL_IDENTITY_SECTION) {
		if ([self getUnsavedChosenServerUrl] == nil) {
			[ViewHelper showAlertViewWithTitle:@"Warning" 
																 Message:@"No Controller. Please configure Controller URL manually."];
			cell.selected = NO;
			return;
		}
		[AppSettingsDefinition setUnsavedChosenServerUrl:[self getUnsavedChosenServerUrl]];

		ChoosePanelViewController *choosePanelViewController = [[ChoosePanelViewController alloc]init];
		[[self navigationController] pushViewController:choosePanelViewController animated:YES];
		[choosePanelViewController release];
		return;
	}
	
	
	if (indexPath.section == CONTROLLER_URLS_SECTION) {
		if (currentSelectedServerIndex) {
			UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:currentSelectedServerIndex];
			if (oldCell.accessoryType == UITableViewCellAccessoryCheckmark) {
				[[[self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery] objectAtIndex:currentSelectedServerIndex.row] setValue:[NSNumber numberWithBool:NO] forKey:@"choose"];		
				oldCell.accessoryType = UITableViewCellAccessoryNone;
			} 
		} 
		if (cell.accessoryType == UITableViewCellAccessoryNone) {
			[[[self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery] objectAtIndex:indexPath.row] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
			cell.accessoryType = UITableViewCellAccessoryCheckmark;
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

- (void)dealloc {
	if (autoDiscoverController) {
		[autoDiscoverController release];
	}
	[updateController release];
	[done release];
	[cancel release];
	[serverArray release];
	[portField release];
	
	[super dealloc];
}


@end

