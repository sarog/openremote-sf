#import "zUIAccelerometer.h"

@implementation zUIAcceleration
@synthesize timestamp, x, y, z;
@end

@implementation zUIAccelerometer
@synthesize updateInterval, delegate; 

- (void) startFakeAccelerometer{
	[NSTimer scheduledTimerWithTimeInterval:0.05 target:self selector:@selector(checkFakeAcc) userInfo:nil repeats:YES];
}

- (void) checkFakeAcc{
	NSURL *url = [NSURL URLWithString:@"http://127.0.0.1:8888"];
	NSString *content = [NSString stringWithContentsOfURL:url];
	if (content != nil){
		int startpos = 3;
		double x = [[content substringWithRange:NSMakeRange(startpos,8)] doubleValue];
		startpos = 18;
		if (x < 0)
		{
			startpos++;
		}
		double y = [[content substringWithRange:NSMakeRange(startpos,8)] doubleValue];
		startpos += 15;
		if (y < 0)
		{
			startpos++;
		}
		double z = [[content substringWithRange:NSMakeRange(startpos,8)] doubleValue];
		z=z;
		zUIAcceleration *aaa = [zUIAcceleration alloc];
		UIAccelerationValue uiax = x;
		UIAccelerationValue uiay = y;
		UIAccelerationValue uiaz = z;
		[aaa setX:uiax];
		[aaa setY:uiay];
		[aaa setZ:uiaz];
				
		[[self delegate] accelerometer:self didAccelerate:aaa];
	}	
}

@end
