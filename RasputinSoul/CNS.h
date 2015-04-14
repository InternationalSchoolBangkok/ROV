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
#include "PID.h"

class CNS : public Thread {
public:
    CNS(State* rov, Serial* ardee, IMU* imu);

    void* run();

private:
    State* rov;
    Serial* ardee;
    IMU* imu;
    PID* rollPID;
    PID* pitchPID;
    PID* heightPID;
    float pitch, roll, yaw;
    float depth;
    signed char motor[8];
    float lx, ly, rx, ry;
    bool l1, l2, r1, r2, start, cross, circle,down,right,up;

    void syncIMU();
    void syncCommander();
};

#endif	/* CNS_H */

