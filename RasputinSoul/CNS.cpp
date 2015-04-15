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
#include <bitset>

#define RUNRATE 40

CNS::CNS(State* rov, Serial* ardee, IMU* imu) : rov(rov), ardee(ardee), imu(imu) {

}

float scaleFloat(float value, float lower, float upper) {

    //measured constants
    /*
        minRLL = -341.869995,
        maxRLL = 341.970001,
        minPCH = -168.800003,
        maxPCH = 165.490005;
     */
    //const int boundRLL = 345, boundPCH = 170;
    float range = upper - lower;
    float delta = (upper + lower) / 2;
    upper -= delta;
    lower -= delta;
    if (value >= upper) {
        value -= range;
    } else if (value < lower) {
        value += range;
    }
    value /= range / 2;
    return value;
    // value will be -1<x<1
}

void CNS::syncIMU() {
    pitch = scaleFloat(imu->getPitch(), -168.800003, 165.490005);
    roll = scaleFloat(imu->getRoll(), -341.869995, 341.970001);
    yaw = scaleFloat(imu->getYaw(), -360, 360);

    int ps = pitch * 32768;
    int rs = roll * 32768;
    int ys = yaw * 32768;
    int hs = scaleFloat(depth, 0, 5) * 32768;


    rov->set(0, rs / 256);
    rov->set(1, (int) rs % 256 - 128);
    rov->set(2, ps / 256);
    rov->set(3, (int) ps % 256 - 128);
    rov->set(4, ys / 256);
    rov->set(5, (int) ys % 256 - 128);
    rov->set(6, hs / 256);
    rov->set(7, (int) hs % 256 - 128);
}

void CNS::syncCommander() {
    lx = rov->get(2) / 128.0;
    ly = -rov->get(3) / 128.0;
    rx = rov->get(4) / 128.0;
    ry = -rov->get(5) / 128.0;
    char got = rov->get(1);
    bitset<8> bs(got);
    l1 = bs[7];
    l2 = bs[6];
    r1 = bs[5];
    r2 = bs[4];
    start = bs[2];
    bitset<8> bs2(rov->get(0));
    circle = bs2[0];
    cross = bs2[2];
    down = bs2[6];
    right = bs2[4];
    up = bs2[7];
    //heightPID->setPIDGainz(rov->get(23), rov->get(24), rov->get(25));
    //rollPID->setPIDGainz(rov->get(26), rov->get(27), rov->get(28));
    //pitchPID->setPIDGainz(rov->get(29), rov->get(30), rov->get(31));
}

long getMicrotime() {
    struct timespec tp;
    clock_gettime(CLOCK_REALTIME, &tp);
    return tp.tv_sec * 1000000 + tp.tv_nsec;
}

void* CNS::run() {
    int sleep = 1e6 / RUNRATE;

    float motorPower[8];
    float turnGain = .8;
    float straifeGain = .8;

    long lastt = getMicrotime();
    long currentt = getMicrotime();
    long dt = currentt - lastt;

    rollPID = new PID(8, 0, 0);
    pitchPID = new PID(4, 0, 0);
    heightPID = new PID(20, 0, 0); //new PID(20, 1, 5);
    //disable PIDs when started up
    rollPID->enablePID(false);
    pitchPID->enablePID(false);
    heightPID->enablePID(false);
    //PID* heightPID = new PID(1.1,0.7,1.0);
    int i = 0;
    int startBeginI = 0;
    bool clawLocked = false;
    char hue;
    double OGSP = pitchPID->getSP();
    bitset<8> stateBS; //first to last: stabilization state, claw state, on/rasputin state
    stateBS.set(0,false);
    stateBS.set(1,false);
    stateBS.set(2,true);
    while (true) {
        i++;
        syncIMU();
        syncCommander();

        motorPower[0] = (-ly - lx)*128.0 * straifeGain;
        motorPower[1] = (-ly + lx)*128.0 * straifeGain;
        motorPower[2] = (ly + lx)*128.0 * straifeGain;
        motorPower[3] = (ly - lx)*128.0 * straifeGain;
        motorPower[0] += -rx * 128.0 * turnGain;
        motorPower[1] += rx * 128.0 * turnGain;
        motorPower[2] += -rx * 128.0 * turnGain;
        motorPower[3] += rx * 128.0 * turnGain;

        // time calc
        currentt = getMicrotime();
        dt = (currentt - lastt) / 1000000.0;
        lastt = currentt;

        rollPID->setPV(roll);
        pitchPID->setPV(pitch);
        heightPID->setPV(depth);

        if (circle) {
            rollPID->enablePID(false);
            pitchPID->enablePID(false);
            heightPID->enablePID(false);
            stateBS.set(0,false);
        } else if (cross) {
            rollPID->enablePID(true);
            pitchPID->enablePID(true);
            heightPID->enablePID(true);
            heightPID->setSP(depth);
            stateBS.set(0,true);
        }
        int clawVal;
        if (!clawLocked) {
            clawVal = l2 ? 1 : (l1 ? -1 : 0);
            if (down) {
                clawVal = 1;
                clawLocked = true;
                stateBS.set(1,true);
            }
        } else if (clawLocked && right) {
            clawVal = 0;
            clawLocked = false;
            stateBS.set(1,false);
        }
        if (start) {
            if (i - startBeginI == i) {
                //if first time
                startBeginI = i;
            } else if (i - startBeginI >= RUNRATE * 5) {
                //if held down for 5 seconds power off raspi
                rov->set(8,64);
                system("sudo poweroff");
                exit(0);
            }
        } else {
            startBeginI = 0;
        }
        float rollo = rollPID->step(dt);
        float pitcho = pitchPID->step(dt);
        float heighto = heightPID->step(dt);
        //calculation for height needed

        if (r1) {
            heighto = -1;
            heightPID->setSP(depth);
        } else if (r2) {
            heighto = 1;
            heightPID->setSP(depth);
        }
        if(up){
            pitchPID->setSP(OGSP-0.2);
        }else{
            pitchPID->setSP(OGSP);
        }
        if (i % 5 == 0) {
            printf("R:%.5f\tP:%.5f\tH:%.5f\tSum:%.5f\tD:%.5f\n", rollo, pitcho,
                    heighto, rollo + pitcho + heighto, depth);
        }
        motorPower[4] = (rollo + pitcho - heighto) * 128;
        motorPower[5] = (-rollo + pitcho - heighto) * 128;
        motorPower[6] = (-rollo - pitcho - heighto) * 128;
        motorPower[7] = (rollo - pitcho - heighto) * 128;
        //pid gain needs readjustments cuz not guaranteed -1<x<1
        for (int i = 0; i < 8; ++i) {
            motor[i] = min(max((int) motorPower[i], -128), 127);
            ardee->set(i, motor[i]);
            //send motor powers to ardee
        }//convert float receiver over serial back to float
        ardee->set(8, clawVal); //claw setting
        char bytes[3];
        for (int i = 0; i < 4; i++) {
            bytes[i] = ardee->get(i);
        }
        float *fp = (float*) bytes;
        depth = *fp;
        rov->set(8,stateBS.to_ulong());
        usleep(sleep);
    }
}

