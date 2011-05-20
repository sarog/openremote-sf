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

#import "ChoosePanelViewController.h"
#import "URLConnectionHelper.h"
#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"
#import "ControllerException.h"
#import "CredentialUtil.h"
#import "Definition.h"
#import "DataBaseService.h"
#import "NotificationConstant.h"
#import "ORConsoleSettingsManager.h"
#import "ORConsoleSettings.h"
#import "ORController.h"
#import "ORControllerProxy.h"

@interface ChoosePanelViewController ()

@property (nonatomic, retain) ORControllerPanelsFetcher *panelsFetcher;
@property (nonatomic, retain) NSArray *panels;

- (void)requestPanelList;

@end

@implementation ChoosePanelViewController

@synthesize delegate;
@synthesize panelsFetcher;
@synthesize panels;

- (id)init
{
    self = [super initWithStyle:UITableViewStyleGrouped];
	if (self) {
		[self setTitle:@"Panel List"];
		chosenPanel = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].consoleSettings.selectedController.selectedPanelIdentityDisplayString retain];
		[self requestPanelList];
	}
	return self;
}

- (void)dealloc
{
	[chosenPanel release];
    self.panels = nil;
    self.panelsFetcher = nil;
	
	[super dealloc];
}

// Load panel list from remote controller server.
- (void)requestPanelList {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationShowLoading object:nil];
    self.panelsFetcher = [[ORConsoleSettingsManager sharedORConsoleSettingsManager].currentController fetchPanelsWithDelegate:self];
    
    // TODO EBR : cancel fetch when user going back
}

#pragma mark Table view methods

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
		return @"Choose Your Panel Identity:";
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
	return @"UI differs according to different panel identity.";
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 1;
}

// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	return panels.count;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	static NSString *panelCellIdentifier = @"panelCell";
	UITableViewCell *panelCell = [tableView dequeueReusableCellWithIdentifier:panelCellIdentifier];
	
	if (panelCell == nil) {
		panelCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:panelCellIdentifier] autorelease];
	}
	
	panelCell.textLabel.text = [panels objectAtIndex:indexPath.row];
	panelCell.selectionStyle = UITableViewCellSelectionStyleNone;
	
	if ([panelCell.textLabel.text isEqualToString:chosenPanel]) {
		panelCell.accessoryType = UITableViewCellAccessoryCheckmark;
	}
	return panelCell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self.delegate didSelectPanelIdentity:[tableView cellForRowAtIndexPath:indexPath].textLabel.text];
}





// TODO EBR : this whole login thing is not active anymore, see code in ORControllerPanelsFetcher

// Show login dialog for users, if users didn't login remote controller server.
- (void)showLoginAlert {
	
    // TODO EBR: check what's the deal with user/pwd required for login -> store in "same place"
    
	UIAlertView *prompt = [[UIAlertView alloc] initWithTitle:@"Controller Login" 
													 message:@"\n\n\n" // IMPORTANT
													delegate:self 
										   cancelButtonTitle:@"Cancel" 
										   otherButtonTitles:@"OK", nil];
	
	textField = [[UITextField alloc] initWithFrame:CGRectMake(22.0, 50.0, 240.0, 25.0)]; 
	textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
	textField.autocorrectionType = UITextAutocorrectionTypeNo;
	textField.clearButtonMode = UITextFieldViewModeWhileEditing;
	textField.returnKeyType = UIReturnKeyDone;
	[textField setBackgroundColor:[UIColor whiteColor]];
	[textField setPlaceholder:@"username"];
	if ([Definition sharedDefinition].username != nil) {
		[textField setText:[Definition sharedDefinition].username];
	}
	
	[prompt addSubview:textField];
	
	textField2 = [[UITextField alloc] initWithFrame:CGRectMake(22.0, 85.0, 240.0, 25.0)]; 
	[textField2 setBackgroundColor:[UIColor whiteColor]];
	textField2.autocapitalizationType = UITextAutocapitalizationTypeNone;
	textField2.autocorrectionType = UITextAutocorrectionTypeNo;
	textField2.clearButtonMode = UITextFieldViewModeWhileEditing;
	[textField2 setPlaceholder:@"password"];
	[textField2 setSecureTextEntry:YES];
	textField2.returnKeyType = UIReturnKeyDone;
	[prompt addSubview:textField2];
	
	// set place
	//[prompt setTransform:CGAffineTransformMakeTranslation(0.0, 110.0)];
	[prompt show];
	[prompt release];
	
	// set cursor and show keyboard
	[textField becomeFirstResponder];
	
}

#pragma mark alert delegate
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex {
	if (buttonIndex == 1) {
		[Definition sharedDefinition].username = textField.text;
		[Definition sharedDefinition].password = textField2.text;
		[[DataBaseService sharedDataBaseService] saveCurrentUser];
		[self requestPanelList];
	} 
}

- (void)updateTableView {
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j = 0; j < [panels count]; j++) {
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:0]];
	}

	[self.tableView beginUpdates];
	[self.tableView insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationBottom];
	[self.tableView endUpdates];
	
	[insertIndexPaths release];
}

- (void)viewWillDisappear:(BOOL)animated {
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation {
	return YES;
}

#pragma mark ORControllerPanelsFetcherDelegate implementation

- (void)fetchPanelsDidSucceedWithPanels:(NSArray *)thePanels
{
    self.panels = thePanels;
    [[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
    [self updateTableView];
}

- (void)fetchPanelsDidFailWithError:(NSError *)error
{
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
    [ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[error localizedDescription]];
}

@end