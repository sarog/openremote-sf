#include <UIKit/UIKit.h>

@protocol zUIAccelerometerDelegate;

@interface zUIAcceleration : NSObject {
	UIAccelerationValue x, y, z;
	NSTimeInterval timestamp;
	
}

@property(nonatomic) NSTimeInterval timestamp;
@property(nonatomic) UIAccelerationValue x;
@property(nonatomic) UIAccelerationValue y;
@property(nonatomic) UIAccelerationValue z;

@end

@interface zUIAccelerometer:NSObject{
    NSTimeInterval               updateInterval;
    id <zUIAccelerometerDelegate> delegate;
    struct {
        unsigned int delegateDidAccelerate:1;
        unsigned int reserved:31;
    } _accelerometerFlags;
}

@property(nonatomic) NSTimeInterval updateInterval;
@property(nonatomic,assign) id<zUIAccelerometerDelegate> delegate;

- (void) startFakeAccelerometer;

	
@end;

@protocol zUIAccelerometerDelegate<NSObject>
@optional
- (void)accelerometer:(zUIAccelerometer *)accelerometer didAccelerate:(zUIAcceleration *)acceleration;

@end
