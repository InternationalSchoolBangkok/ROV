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

    rov->set(0, roll / 256);
    rov->set(1, (int) roll % 256 - 128);
    rov->set(2, pitch / 256);
    rov->set(3, (int) pitch % 256 - 128);
    rov->set(4, yaw / 256);
    rov->set(5, (int) yaw % 256 - 128);
}

void CNS::syncCommander() {
    lx = rov->get(2) / 128.0;
    ly = -rov->get(3) / 128.0;
    rx = rov->get(4) / 128.0;
    ry = -rov->get(5) / 128.0;
    char got = rov->get(1);
    bitset<8> bs(got);
    l1 = bs[7] == true;
    l2 = bs[6] == true;
    r1 = bs[5] == true;
    r2 = bs[4] == true;
    start = bs[2] == true;
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
    float turnGain = .75;
    float straifeGain = 1.0;

    long lastt = getMicrotime();
    long currentt = getMicrotime();
    long dt = currentt - lastt;

    rollPID = new PID(0, 0, 0); //3
    pitchPID = new PID(0, 0, 0);
    heightPID = new PID(20, 0, 0);
    //PID* heightPID = new PID(1.1,0.7,1.0);

    while (true) {

        syncIMU();
        syncCommander();

        motorPower[0] = (-ly - lx)*64.0 * straifeGain;
        motorPower[1] = (-ly + lx)*64.0 * straifeGain;
        motorPower[2] = (ly + lx)*64.0 * straifeGain;
        motorPower[3] = (ly - lx)*64.0 * straifeGain;
        motorPower[0] += -rx * 128.0 * turnGain;
        motorPower[1] += rx * 128.0 * turnGain;
        motorPower[2] += -rx * 128.0 * turnGain;
        motorPower[3] += rx * 128.0 * turnGain;

        // time calc
        currentt = getMicrotime();
        dt = currentt - lastt;
        lastt = currentt;

        rollPID->setPV(roll);
        pitchPID->setPV(pitch);
        heightPID->setPV(depth);
        if (r1) {
            heightPID->setSP(depth - 0.02);
            //cout << "Depth: "<<depth<<" SPTO " << depth-0.01<<endl;
        } else if (r2) {
            heightPID->setSP(depth + 0.02);
            cout << "Depth: "<<depth<<" SPTO " << depth-0.01<<endl;
        }
        if (start) {
            heightPID->setSP(depth);
        }
        float rollo = rollPID->step(dt);
        float pitcho = pitchPID->step(dt);
        float heighto = heightPID->step(dt);
        //calculation for height needed

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
        ardee->set(8, l2 ? 1 : (l1 ? -1 : 0)); //claw setting
        /*Bytes2float btf;
        for (int i = 0; i < 4; i++) {
            btf.c[i] = ardee->get(i);
        }
        depth = btf.f;*/
        char bytes[3];
        for (int i = 0; i < 4; i++) {
            bytes[i] = ardee->get(i);
        }
        float *fp = (float*)bytes;
        depth = *fp;
        cout<<depth<<endl;
        usleep(sleep);
    }
}

