/* 
 * File:   CNS.h
 * Author: kolatat
 *
 * Created on March 29, 2015, 11:31 AM
 */

#ifndef CNS_H
#define	CNS_H

#include "IMU.h"
#include "Thread.h"
#include "Serial.h"
#include "State.h"

class CNS : public Thread {
public:
    CNS(State* rov, Serial* ardee, IMU* imu);

    void* run();

private:
    State* rov;
    Serial* ardee;
    IMU* imu;

    float pitch, roll, yaw;
    float depth;
    signed char motor[8];
    float lx, ly, rx, ry;
    bool l1,l2;

    void syncIMU();
    void syncCommander();
    union Bytes2float {
        float f;
        unsigned char c[0];
    };
};

#endif	/* CNS_H */

