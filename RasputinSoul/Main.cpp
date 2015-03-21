#include <cstdlib>
#include <fstream>
#include <iostream>

#include "State.h"
#include "Serial.h"
#include "IMU.h"

#include "math.h"

#define BANDWIDTH 32
#define NETPORT 6969
#define BAUDRATE 38400
#define COMRATE 20

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
    State *rov = new State(BANDWIDTH, NETPORT, NETPORT);
    Serial* ardee = new Serial(BANDWIDTH);
    IMU imu = IMU(BAUDRATE);
    rov->startTXRX();
    
    // init
    for(int i=0;i<BANDWIDTH;i++){
        rov->set(i,69);
    }
    
    short step = 0;
    const int sleepTime=1e6/COMRATE;
    float maxR=-300,maxP=-100,maxY=-100;
    float minR=300,minP=100,minY=100;
    
    //measured constants
    /*
        minRLL = -341.869995,
        maxRLL = 341.970001,
	minPCH = -168.800003,
        maxPCH = 165.490005;
    */
    const int boundRLL = 345, boundPCH = 170;
    

    while (true) {
        ++step;
        int fromController[BANDWIDTH];
        char toArdee[BANDWIDTH];
        for (int i = 0; i < BANDWIDTH; i++) {
            fromController[i] = rov->get(i);
            toArdee[i] = (char)fromController[i];
            ardee->set(i,toArdee[i]);
        }
        
        // send IMU data channel roll(0,1) pitch(2,3)
        {
            int rScaled = imu.getRoll()/boundRLL*32768;
            int pScaled = imu.getPitch()/boundPCH*32768;
            rov->set(0,rScaled/256);
            rov->set(1,rScaled%256-128);
            rov->set(2,pScaled/256);
            rov->set(3,pScaled%256-128);
            
        }
        
        //debugggies
        {
            maxR = max(maxR, imu.getRoll());
            minR = min(minR, imu.getRoll());
            maxP = max(maxP, imu.getPitch());
            minP = min(minP, imu.getPitch());
            maxY = max(maxY, imu.getYaw());
            minY = min(minY, imu.getYaw());
        }
        
        step%=COMRATE;
        if(step==0){
            printf("IMU: {\n\tRLL:[\t%f,\t%f,\t%f],\n\tPCH:[\t%f,\t%f,\t%f],\n\tYAW:[\t%f,\t%f,\t%f]}\n",
                    minR,imu.getRoll(),maxR,
                    minP,imu.getPitch(),maxP,
                    minY,imu.getYaw(),maxY);
        }
        usleep(sleepTime);
    }
    rov->stopTXRX();
    return 0;
}
