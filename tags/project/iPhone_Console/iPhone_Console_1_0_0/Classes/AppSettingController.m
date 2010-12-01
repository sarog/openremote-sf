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
#import "ActivitiesController.h"
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
@end


@implementation AppSettingController


/*
- (id)initWithStyle:(UITableViewStyle)style {
    // Override initWithStyle: if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
    if (self = [super initWithStyle:style]) {
    }
    return self;
}
*/

- (id)init {
	if (self = [super initWithStyle:UITableViewStyleGrouped]) {
		[self setTitle:@"Settings"];
		isEditing = NO;
		autoDiscovery = [AppSettingsDefinition isAutoDiscoveryEnable];
		
		if (!done) {
			done = [[UIBarButtonItem alloc]initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(saveSettings)];		
		}
		if (!edit) {
			edit = [[UIBarButtonItem alloc]initWithTitle:@"Edit" style:UIBarButtonItemStyleDone target:self action:@selector(editSettings)];
			[edit setStyle:UIButtonTypeRoundedRect];
		}		
	}
	return self;
}

-(NSMutableArray *)getCurrentServersWithAutoDiscoveryEnable:(BOOL)b {
	if (b) {
		return [AppSettingsDefinition getAutoServers];
	} else {
		return [AppSettingsDefinition getCustomServers];
	}
}

- (BOOL)isAutoDiscoverySection:(NSIndexPath *)indexPath {
	return indexPath.section == 0;
}
- (BOOL)isAutoServerSection:(NSIndexPath *)indexPath {
	if (autoDiscovery && indexPath.section == 1) {
		return YES;
	}
	return NO;
}
- (BOOL)isCustomServerSection:(NSIndexPath *)indexPath {
	if (!autoDiscovery && indexPath.row < [serverArray count] && indexPath.section == 1) {
		if (indexPath.row == 0) {
			return NO;
		}
		return YES;
	}
	return NO;
}
- (BOOL)isAddCustomServerRow:(NSIndexPath *)indexPath {
	if (!autoDiscovery && indexPath.row >= [serverArray count] && indexPath.section == 1) {
		return YES;
	}
	return NO;
}

- (void)autoDiscoverChanged:(id)sender {
	UISwitch *s = (UISwitch *)sender;
	autoDiscovery = s.on;
	
	[self deleteAllRow];
	
	if (autoDiscovery) {
		self.navigationItem.leftBarButtonItem = nil;
		if (autoDiscoverController) {
			[autoDiscoverController setDelegate:nil];
			[autoDiscoverController release];
			autoDiscoverController = nil;
		}
		autoDiscoverController = [[ServerAutoDiscoveryController alloc]initWithDelegate:self];
	} else {
		if (autoDiscoverController) {
			[autoDiscoverController setDelegate:nil];
			[autoDiscoverController release];
			autoDiscoverController = nil;
		}
		self.navigationItem.leftBarButtonItem = edit;
		[self updateTableView];
	}
		
}


- (void)deleteAllRow {
	UITableView *tv = (UITableView *)self.view;
	
	[tv beginUpdates];
	NSMutableArray *deleteIndexPaths = [[NSMutableArray alloc] init];
	for (int i=0;i <serverArray.count;i++){
		[deleteIndexPaths addObject:[NSIndexPath indexPathForRow:i inSection:1]];
	}
	if (autoDiscovery) {
		[deleteIndexPaths addObject:[NSIndexPath indexPathForRow:[serverArray count] inSection:1]];
	}
	[serverArray removeAllObjects];
	[tv deleteRowsAtIndexPaths:deleteIndexPaths withRowAnimation:UITableViewRowAnimationTop];
	
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	
	if (!autoDiscovery) {
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:0 inSection:1]];
		[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationBottom];
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
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:1]];
	}

	[serverArray addObjectsFromArray:newArray];
		NSLog(@"Insert paths %d",[insertIndexPaths count]);
	[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationBottom];
	[tv endUpdates];
	
	[insertIndexPaths release];
}


/*
- (void)viewDidLoad {
    [super viewDidLoad];

    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
}
*/

/*
- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
}
*/
/*
- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}
*/
/*
- (void)viewWillDisappear:(BOOL)animated {
	[super viewWillDisappear:animated];
}
*/
/*
- (void)viewDidDisappear:(BOOL)animated {
	[super viewDidDisappear:animated];
}
*/

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/


		
- (void)saveSettings {
	if (serverArray.count == 0) {
		[ViewHelper showAlertViewWithTitle:@"Warning" 
								   Message:@"Controller autodiscovery failed. Please configure controller URL manually."];
	} else {
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

- (void)editSettings {
	isEditing = !isEditing;
	if (isEditing) {
		self.navigationItem.leftBarButtonItem.title = @"Done";
		self.navigationItem.rightBarButtonItem = nil;
		[[self tableView] setEditing:YES animated:YES];
	} else {
		[AppSettingsDefinition writeToFile];
		self.navigationItem.leftBarButtonItem = edit;
		self.navigationItem.leftBarButtonItem.title = @"Edit";
		self.navigationItem.rightBarButtonItem = done;
		[[self tableView] setEditing:NO animated:YES];
	}
	
	
}

#pragma mark Delegate method of ServerAutoDiscoveryController
- (void)onFindServer:(NSString *)serverUrl {
	[self updateTableView];
}

- (void)onFindServerFail:(NSString *)errorMessage {
	
	[ViewHelper showAlertViewWithTitle:@"Find Server Error" Message:errorMessage];
	
}


#pragma mark Delegate method of UpdateController
- (void)didUpadted {
	[self dismissModalViewControllerAnimated:YES];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshAcitivitiesView object:nil];
}

- (void)didUseLocalCache:(NSString *)errorMessage {
	[ViewHelper showAlertViewWithTitle:@"Warning" Message:errorMessage];
	[self dismissModalViewControllerAnimated:YES];
	[[NSNotificationCenter defaultCenter] postNotificationName:NotificationRefreshAcitivitiesView object:nil];
}


- (void)viewWillAppear:(BOOL)animated {
	
	self.navigationItem.rightBarButtonItem = done;
	if (!autoDiscovery) {
		self.navigationItem.leftBarButtonItem = edit;
	}
	
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

//- (void)viewWillDisappear:(BOOL)animated {
//	
//}
//- (void)didReceiveMemoryWarning {
//	[super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
//	// Release anything that's not essential, such as cached data
//}
#pragma mark Table view methods

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
	return 2;
}


// Customize the number of rows in the table view.
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
	if (section == 0) {
		return 1;
	} else {
		if (!autoDiscovery) {
			return serverArray.count+1;
		}
		return serverArray.count;
	}
}

- (NSString *)tableView:(UITableView *)tableView titleForFooterInSection:(NSInteger)section{
	if (section == 1) {
		return [NSString stringWithFormat:@"version:v%@",[[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleVersion"]];
	}
	return [AppSettingsDefinition getSectionFooterWithIndex:section];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return [AppSettingsDefinition getSectionHeaderWithIndex:section];
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
	
	static NSString *autoCellIdentifier = @"autoCell";
	static NSString *serverCellIdentifier = @"serverCell";
	
	UITableViewCell *autoCell = [tableView dequeueReusableCellWithIdentifier:autoCellIdentifier];
	UITableViewCell *serverCell = [tableView dequeueReusableCellWithIdentifier:serverCellIdentifier];
	
	if (autoCell == nil) {
		autoCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:autoCellIdentifier] autorelease];
	}
	if (serverCell == nil) {
		serverCell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:serverCellIdentifier] autorelease];
	}
	
	if ([self isAutoDiscoverySection:indexPath]) {
		autoCell.text = [[AppSettingsDefinition getAutoDiscoveryDic] objectForKey:@"name"];
		autoCell.selectionStyle = UITableViewCellSelectionStyleNone;
		UISwitch *switchView = [[UISwitch alloc]init];
		[switchView setOn:autoDiscovery];
		[switchView addTarget:self action:@selector(autoDiscoverChanged:) forControlEvents:UIControlEventValueChanged];
		autoCell.accessoryView = switchView;
		[switchView release];
		return autoCell;
	} else {
		if ([self isAddCustomServerRow:indexPath]) {
			serverCell.text = @"Add New Controller...";
			serverCell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
			serverCell.selectionStyle = UITableViewCellSelectionStyleBlue;
		}
		else {
			serverCell.text = [[serverArray objectAtIndex:indexPath.row] objectForKey:@"url"];
			serverCell.selectionStyle = UITableViewCellSelectionStyleNone;
			if ( [[[serverArray objectAtIndex:indexPath.row] objectForKey:@"choose"] boolValue]) {
				currentSelectedServerIndex = indexPath;
				serverCell.accessoryType = UITableViewCellAccessoryCheckmark;
			} else {
				serverCell.accessoryType = UITableViewCellAccessoryNone;
			}
		}
		return serverCell;
	}
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
	//if (isEditing) {
	//		if ([self isCustomServerSection:indexPath]) {
	//			AddServerViewController *addServerViewController = [[AddServerViewController alloc]init];
	//			addServerViewController.editingItem = [[self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery] objectAtIndex:indexPath.row];
	//			[self.navigationController pushViewController:addServerViewController animated:YES];
	//			[addServerViewController release];
	//			return;
	//		}
	//		return;
	//	} 
	
	
	
	if (indexPath.section == 0) {
		return;
	}
	
	if([self isAddCustomServerRow:indexPath]) {
		AddServerViewController *addServerViewController = [[AddServerViewController alloc]init];
		addServerViewController.editingItem = nil;
		addServerViewController.servers = [self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery];
		[[self navigationController] pushViewController:addServerViewController animated:YES];
		[addServerViewController release];
		return;
	}
	
	
	UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
	if (currentSelectedServerIndex) {
		UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:currentSelectedServerIndex];
		if (cell.accessoryType  == oldCell.accessoryType) {
			return;
		}
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


/*
 // Override to support conditional editing of the table view.
 - (BOOL)tableView:(UITableView *)tableView canEditRowAtIndexPath:(NSIndexPath *)indexPath {
 // Return NO if you do not want the specified item to be editable.
 return YES;
 }
 */


/*
 // Override to support editing the table view.
 - (void)tableView:(UITableView *)tableView commitEditingStyle:(UITableViewCellEditingStyle)editingStyle forRowAtIndexPath:(NSIndexPath *)indexPath {
 
 if (editingStyle == UITableViewCellEditingStyleDelete) {
 // Delete the row from the data source
 [tableView deleteRowsAtIndexPaths:[NSArray arrayWithObject:indexPath] withRowAnimation:YES];
 }   
 else if (editingStyle == UITableViewCellEditingStyleInsert) {
 // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
 }   
 }
 */


/*
 // Override to support rearranging the table view.
 - (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath *)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
 }
 */


/*
 // Override to support conditional rearranging of the table view.
 - (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath *)indexPath {
 // Return NO if you do not want the item to be re-orderable.
 return YES;
 }
 */



- (void)dealloc {
	if (autoDiscoverController) {
		[autoDiscoverController release];
	}
	[updateController release];
	[edit release];
	[done release];
	[serverArray release];
	[super dealloc];
}


@end

