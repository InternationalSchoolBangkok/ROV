/* 
 * File:   CNS.cpp
 * Author: kolatat
 * 
 * Created on March 29, 2015, 11:31 AM
 */

#include "CNS.h"
#include <time.h>
#include "PID.h"
#include <math.h>

#define RUNRATE 40

CNS::CNS(State* rov, Serial* ardee) : rov(rov), ardee(ardee) {

}

int scaleFloat(float value, float lower, float upper){
    
    //measured constants
    /*
        minRLL = -341.869995,
        maxRLL = 341.970001,
	minPCH = -168.800003,
        maxPCH = 165.490005;
    */
    //const int boundRLL = 345, boundPCH = 170;
    float range = upper-lower;
    float delta = (upper+lower)/2;
    upper -= delta;
    lower -= delta;
    if(value>=upper){
        value-=range;
    } else if(value<lower){
        value+=range;
    }
    value/=range/2;
    return value;
    // value will be -1<x<1
}

void CNS::syncIMU() {
    pitch = scaleFloat(imu->getPitch(),-168.800003,165.490005);
    roll = scaleFloat(imu->getRoll(),-341.869995,341.970001);
    yaw= scaleFloat(imu->getYaw(),-360,360);
    
    int ps = pitch*32768;
    int rs = roll*32768;
    int ys = yaw*32768;
    
    rov->set(0,roll/256);
    rov->set(1,roll%256-128);
    rov->set(2,pitch/256);
    rov->set(3,pitch%256-128);
    rov->set(4,yaw/256);
    rov->set(5,yaw%256-128);
}

void CNS::syncCommander() {
    lx = rov->get(2)/128;
    ly = -rov->get(3)/128;
    rx = rov->get(4)/128;
    ry = -rov->get(5)/128;
}

long getMicrotime(){
    struct timespec tp;
    clock_gettime(CLOCK_REALTIME,&tp);
    return tp.tv_sec*1000000+tp.tv_nsec;
}


void* CNS::run(){
    int sleep = 1e6/RUNRATE;
    
    float motorPower[8];
    float hoverGain = .75*128;
    float turnGain = .25*128;
    float pidGain = 1.0*128;
    
    long lastt=getMicrotime();
    long currentt = getMicrotime();
    long dt = currentt-lastt;
    
    PID* rollPID = new PID(1.1,0.7,1.0);
    PID* pitchPID = new PID(1.1,0.7,1.0);
    PID* heightPID = new PID(1.1,0.7,1.0);
            
    while(true){
        
        syncIMU();
        syncCommander();
        
        motorPower[0] = (-ly-lx)/2 * hoverGain;
        motorPower[1] = (-ly+lx)/2 * hoverGain;
        motorPower[2] = (ly+lx)/2 * hoverGain;
        motorPower[3] = (ly-lx)/2 * hoverGain;
        
        motorPower[0] += -rx * turnGain;
        motorPower[1] += rx * turnGain;
        motorPower[2] += -rx * turnGain;
        motorPower[3] += rx * turnGain;
        
        // time calc
        currentt = getMicrotime();
        dt = currentt-lastt;
        lastt=currentt;
        
        rollPID->setPV(roll);
        pitchPID->setPV(pitch);
        float rollo = rollPID->step(dt);
        float pitcho = pitchPID->step(dt);
        //calculation for height needed
        
        motorPower[4] = (-rollo-pitcho)*pidGain;
        motorPower[5] = (rollo-pitcho)*pidGain;
        motorPower[6] = (-rollo+pitcho)*pidGain;
        motorPower[7] = (rollo+pitcho)*pidGain;
        //pid gain needs readjustments cuz not guaranteed -1<x<1
        
        for(int i=0;i<8;++i){
            motor[i]=min(max(motorPower[i],-128),127);
            //send motor powers to ardee
        }
        
        usleep(sleep);
    }
}