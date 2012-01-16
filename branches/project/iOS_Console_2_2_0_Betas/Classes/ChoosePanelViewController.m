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
#import "ChoosePanelViewController.h"
#import "URLConnectionHelper.h"
#import "ServerDefinition.h"
#import "AppSettingsDefinition.h"
#import "ControllerException.h"
#import "CredentialUtil.h"
#import "Definition.h"
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

- (void)viewDidLoad
{
    [super viewDidLoad];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(populateLoginView:) name:NotificationPopulateCredentialView object:nil];    
}

- (void)viewDidUnload
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:NotificationPopulateCredentialView object:nil];
    [super viewDidUnload];
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
	return self.panels.count;
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	static NSString *panelCellIdentifier = @"panelCell";
	UITableViewCell *panelCell = [tableView dequeueReusableCellWithIdentifier:panelCellIdentifier];
	
	if (panelCell == nil) {
		panelCell = [[[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:panelCellIdentifier] autorelease];
	}
	
	panelCell.textLabel.text = [self.panels objectAtIndex:indexPath.row];
	panelCell.selectionStyle = UITableViewCellSelectionStyleNone;
	
	if ([panelCell.textLabel.text isEqualToString:chosenPanel]) {
		panelCell.accessoryType = UITableViewCellAccessoryCheckmark;
	}
	return panelCell;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	[self.delegate didSelectPanelIdentity:[tableView cellForRowAtIndexPath:indexPath].textLabel.text];
}

- (void)presentLoginRequestForControllerRequest:(ControllerRequest *)controllerRequest
{
	LoginViewController *loginController = [[LoginViewController alloc] initWithDelegate:self context:controllerRequest];
	UINavigationController *loginNavController = [[UINavigationController alloc] initWithRootViewController:loginController];
	[self presentModalViewController:loginNavController animated:YES];
	[loginController release];
	[loginNavController release];
}

- (void)loginViewControllerDidCancelLogin:(LoginViewController *)controller
{
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
    
    [(ControllerRequest *)controller.context retry];
}

- (void)updateTableView {
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j = 0; j < [self.panels count]; j++) {
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

- (void)fetchPanelsRequiresAuthenticationForControllerRequest:(ControllerRequest *)controllerRequest
{
    [self presentLoginRequestForControllerRequest:controllerRequest];
}

- (void)fetchPanelsDidFailWithError:(NSError *)error
{
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationHideLoading object:nil];
    [ViewHelper showAlertViewWithTitle:@"Panel List Error" Message:[error localizedDescription]];
}

@synthesize delegate;
@synthesize panelsFetcher;
@synthesize panels;

- (void)setPanelsFetcher:(ORControllerPanelsFetcher *)aPanelsFetcher
{
    if (panelsFetcher != aPanelsFetcher) {
        panelsFetcher.delegate = nil;
        panelsFetcher = [aPanelsFetcher retain];
    }
}

@end