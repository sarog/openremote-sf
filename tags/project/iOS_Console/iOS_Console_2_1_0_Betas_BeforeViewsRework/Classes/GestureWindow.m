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
#import "GestureWindow.h"
#import "Gesture.h"
#import "NotificationConstant.h"

@interface GestureWindow (Private)

- (void)showLoading;
- (void)hideLoading;

@end



/*
 * UIWindow to intercept touch events as gesture, this doesn't break the event delivery.
 *
 * When the iPhone detects a touch it determines the first responder for that event 
 * by recursively calling hitTest:withEvent: to descend down the tree of UIResponder 
 * objects in the application’s window. 
 * The first reponder is then sent the event and can either respond to it 
 * or pass it up the responder chain from view to view controller to parent view.
 * To detect gestures anywhere in the app so I chose to override sendEvent: in UIWindow. 
 * This allowed me to intercept touch events before they were sent to the first responder.
 * See also : http://developer.apple.com/iphone/library/documentation/iPhone/Conceptual/iPhoneOSProgrammingGuide/EventHandling/EventHandling.html#//apple_ref/doc/uid/TP40007072-CH9-SW9
 */

@implementation GestureWindow

- (id)initWithDelegate:(id)delegate{
	if (self = [super initWithFrame:[UIScreen mainScreen].bounds]) {
		theDelegate = delegate;
		
		loading  = [[LoadingHUDView alloc] initWithTitle:@"Loading"];
		loading.center = self.center;
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(showLoading) name:NotificationShowLoading object:nil];
		[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(hideLoading) name:NotificationHideLoading object:nil];
        [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
	}
	
	return self;
}

- (void)dealloc {
    [loading release];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    [[UIDevice currentDevice] endGeneratingDeviceOrientationNotifications];
    [super dealloc];
}

//To detect gestures, intercept touch events
- (void)sendEvent:(UIEvent *)event {
	NSSet *touches = [event allTouches];
	
	//single touch (single finger)
	if ([touches count] == 1) {
		UITouch *touch = [touches anyObject];
		CGPoint location = [touch locationInView:self];
		if (touch.phase == UITouchPhaseBegan) {
			previousTouchLocation = [touch locationInView:self];
		} else if (touch.phase == UITouchPhaseEnded) {
			CGFloat deltaX = fabsf(location.x - previousTouchLocation.x);
			CGFloat deltaY = fabsf(location.y - previousTouchLocation.y);
			
			//Horizontal
			if (deltaX >= MINIMUM_GESTURE_LENGTH && deltaY <= MAXIMUM_VARIANCE) {
				//evaluate gesture
				if (previousTouchLocation.x < location.x) {
					//left to right -->
					Gesture *g = [[Gesture alloc] initWithGestureSwipeType:GestureSwipeTypeLeftToRight orientation:[[UIDevice currentDevice] orientation]];
					[theDelegate performSelector:@selector(performGesture:) withObject:g];
                    [g release];
				} else if (previousTouchLocation.x > location.x) {
					//right to left <--
					Gesture *g = [[Gesture alloc] initWithGestureSwipeType:GestureSwipeTypeRightToLeft orientation:[[UIDevice currentDevice] orientation]];
					[theDelegate performSelector:@selector(performGesture:) withObject:g];
                    [g release];
				}
			} 
			
			//Vertical
			else if (deltaY >= MINIMUM_GESTURE_LENGTH && deltaX <= MAXIMUM_VARIANCE) {
				if (location.y > previousTouchLocation.y) {
					//           |
					//up to down V
					Gesture *g = [[Gesture alloc] initWithGestureSwipeType:GestureSwipeTypeTopToBottom orientation:[[UIDevice currentDevice] orientation]];
					[theDelegate performSelector:@selector(performGesture:) withObject:g];
                    [g release];
				} else if (previousTouchLocation.y > location.y) {
					//donw to up ^
					//           |
					Gesture *g = [[Gesture alloc] initWithGestureSwipeType:GestureSwipeTypeBottomToTop orientation:[[UIDevice currentDevice] orientation]];
					[theDelegate performSelector:@selector(performGesture:) withObject:g];
                    [g release];
				}
			}
		} else if (touch.phase == UITouchPhaseMoved) {
			// nothing
		} else if (touch.phase == UITouchPhaseCancelled) {
			// nothing
		}
	}
	
	// multi touches (multi fingers)
	else {
		// nothing
	}
	[super sendEvent:event];
}

- (void)showLoading {
	if (loading.superview != self) {
		[self addSubview:loading];
	}
	[loading startAnimating];
}

- (void)hideLoading {
	[loading stopAnimating];
	[loading removeFromSuperview];
}

@end


