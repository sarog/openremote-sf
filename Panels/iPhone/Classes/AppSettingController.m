//
//  AppSettingController.m
//  openremote
//
//  Created by finalist on 5/14/09.
//  Copyright 2009 __MyCompanyName__. All rights reserved.
//

#import "AppSettingController.h"
#import "DirectoryDefinition.h"
#import "AppSettingsDefinition.h"
#import "ServerAutoDiscoveryController.h"
#import "NotificationConstant.h"

@interface AppSettingController (Private)
- (void)initData;
- (NSDictionary *)getSectionWithIndex:(int)index;
- (NSString *)getSectionHeaderWithIndex:(int)index;
- (NSString *)getSectionFooterWithIndex:(int)index;
- (NSMutableDictionary *)getAutoDiscoveryDic;
-(BOOL)isAutoDiscoveryEnable;
- (NSMutableArray *)getAutoServers;
- (NSMutableArray *)getCustomServers;
-(NSMutableArray *)getCurrentServersWithAutoDiscoveryEnable:(BOOL)b;
- (void)autoDiscoverChanged:(id)sender;
- (void)updateTableView;
- (void)saveSettings;

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
		UIBarButtonItem *done = [[UIBarButtonItem alloc]initWithTitle:@"Done" style:UIBarButtonItemStyleDone target:self action:@selector(saveSettings)];
		self.navigationItem.rightBarButtonItem = done;
		[done release];
		[self initData];
		serverArray = [[NSMutableArray alloc]init];
		if ([self isAutoDiscoveryEnable]) {
			[serverArray addObjectsFromArray:[self getAutoServers]];
			
		} else {
			[serverArray addObjectsFromArray:[self getCustomServers]];
		}
		
		autoDiscovery = [self isAutoDiscoveryEnable];
	}
	return self;
}

- (void)initData{
		// Unarchive the data, store it in the local property, and pass it to the main view controller
	settingData = [[NSMutableArray alloc] initWithContentsOfFile:[DirectoryDefinition appSettingsFilePath]];
}		

- (NSDictionary *)getSectionWithIndex:(int)index {
	return [settingData objectAtIndex:index];
}
- (NSString *)getSectionHeaderWithIndex:(int)index {
	return [[settingData objectAtIndex:index] valueForKey:@"header"];
}
- (NSString *)getSectionFooterWithIndex:(int)index {
	return [[settingData objectAtIndex:index] valueForKey:@"footer"];
}
- (NSMutableDictionary *)getAutoDiscoveryDic {
	return (NSMutableDictionary *)[[self getSectionWithIndex:0] objectForKey:@"item"];
}

-(BOOL)isAutoDiscoveryEnable {
	return [[[self getAutoDiscoveryDic] objectForKey:@"value"] boolValue];
}

- (NSMutableArray *)getAutoServers{
	return (NSMutableArray *)[[self getSectionWithIndex:1] objectForKey:@"servers"];
}

- (NSMutableArray *)getCustomServers {
	return (NSMutableArray *)[[self getSectionWithIndex:2] objectForKey:@"servers"];
}

-(NSMutableArray *)getCurrentServersWithAutoDiscoveryEnable:(BOOL)b {
	if (b) {
		return [self getAutoServers];
	} else {
		return [self getCustomServers];
	}
}

- (void)autoDiscoverChanged:(id)sender {
	UISwitch *s = (UISwitch *)sender;
	autoDiscovery = s.on;
	if (autoDiscovery) {
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(updateTableView) name:NotificationAfterFindServer object:nil];
		autoDiscoverController = [[ServerAutoDiscoveryController alloc]init];
		[autoDiscoverController findServer];
	} else {
		[self updateTableView];
	}
		
}

- (void)updateTableView{
	[[NSNotificationCenter defaultCenter] removeObserver:self name:NotificationAfterFindServer object:nil];
	if (autoDiscoverController != nil) {
		[autoDiscoverController release];
		autoDiscoverController = nil;
	}
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
	autoDiscovery = !autoDiscovery;
	[tv deleteRowsAtIndexPaths:deleteIndexPaths withRowAnimation:UITableViewRowAnimationTop];
	autoDiscovery = !autoDiscovery;
	NSArray *newArray = nil;
	newArray = [self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery];
	
	NSMutableArray *insertIndexPaths = [[NSMutableArray alloc] init];
	for (int j=0;j < newArray.count;j++){
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:j inSection:1]];
	}
	if (!autoDiscovery) {
		[insertIndexPaths addObject:[NSIndexPath indexPathForRow:[newArray count] inSection:1]];
	}
	[serverArray addObjectsFromArray:newArray];
	[tv insertRowsAtIndexPaths:insertIndexPaths withRowAnimation:UITableViewRowAnimationBottom];
	[tv endUpdates];
	
	
	[deleteIndexPaths release];
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

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning]; // Releases the view if it doesn't have a superview
    // Release anything that's not essential, such as cached data
}

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
	return [self getSectionFooterWithIndex:section];
}

- (NSString *)tableView:(UITableView *)tableView titleForHeaderInSection:(NSInteger)section {
	return [self getSectionHeaderWithIndex:section];
}

// Customize the appearance of table view cells.
- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    
    static NSString *CellIdentifier = @"Cell";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[[UITableViewCell alloc] initWithFrame:CGRectZero reuseIdentifier:CellIdentifier] autorelease];
    }
	if (indexPath.section == 0) {
		cell.text = [[self getAutoDiscoveryDic] objectForKey:@"name"];
		UISwitch *switchView = [[UISwitch alloc]init];
		
		[switchView addTarget:self action:@selector(autoDiscoverChanged:) forControlEvents:UIControlEventValueChanged];
		switchView.on = [[[[[settingData objectAtIndex:indexPath.section] valueForKey:@"items"] objectAtIndex:indexPath.row] valueForKey:@"value"] boolValue];
		cell.accessoryView = switchView;
		
		[switchView setOn:[self isAutoDiscoveryEnable] animated:YES];
		autoDiscovery = switchView.on;

		[switchView release];
	} else {
		if (indexPath.row >= [serverArray count]) {
			cell.text = @"add another server";
			cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
		}else {
			cell.text = [[serverArray objectAtIndex:indexPath.row] objectForKey:@"url"];
			if ( [[[serverArray objectAtIndex:indexPath.row] objectForKey:@"choose"] boolValue]) {
				currentSelectedServerIndex = indexPath;
				cell.accessoryType = UITableViewCellAccessoryCheckmark;
			}
		}
	}
    return cell;
}


- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
	if (indexPath.section == 0) {
		return;
	}
	
	if (indexPath.row == ([serverArray count] -1)) {
		
	}
	UITableViewCell *cell = [tableView cellForRowAtIndexPath:indexPath];
	UITableViewCell *oldCell = [tableView cellForRowAtIndexPath:currentSelectedServerIndex];
	if (cell.accessoryType  == oldCell.accessoryType) {
		return;
	}
	if (cell.accessoryType == UITableViewCellAccessoryNone) {
			[[[self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery] objectAtIndex:indexPath.row] setValue:[NSNumber numberWithBool:YES] forKey:@"choose"];
			cell.accessoryType = UITableViewCellAccessoryCheckmark;
	} 
	
	if (oldCell.accessoryType == UITableViewCellAccessoryCheckmark) {
		[[[self getCurrentServersWithAutoDiscoveryEnable:autoDiscovery] objectAtIndex:currentSelectedServerIndex.row] setValue:[NSNumber numberWithBool:NO] forKey:@"choose"];		
		oldCell.accessoryType = UITableViewCellAccessoryNone;
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
		
- (void)saveSettings {
	[[self getAutoDiscoveryDic] setValue:[NSNumber numberWithBool:autoDiscovery] forKey:@"value"];
	
	if ([settingData writeToFile:[DirectoryDefinition appSettingsFilePath] atomically:NO]) {
		[AppSettingsDefinition checkConfigAndUpdate];
	}
	
	if ([self navigationController]) {
		[[self navigationController] popViewControllerAnimated:YES];
	}
	
	
}

- (void)viewWillDisappear:(BOOL)animated {
	
}


- (void)dealloc {
    [super dealloc];
	[settingData release];
	[serverArray release];
}


@end

