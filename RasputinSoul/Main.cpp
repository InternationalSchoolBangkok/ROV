#include <cstdlib>
#include <fstream>
#include <iostream>

#include "State.h"
#include "Serial.h"
#include "IMU.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {
    State *rov = new State(32, 6969, 6969);
    Serial* ardee = new Serial(32);
    IMU imu = IMU(38400);
    rov->startTXRX();
    for(int i=0;i<32;i++){
        rov->set(i,69);
    }
    rov->set(0,69);
    while (true) {
        int fromController[32];
        char toArdee[32];
        for (int i = 0; i < 32; i++) {
            fromController[i] = rov->get(i);
        }
        //cout<<"From Controller"<<endl;
        for (int i = 0; i < 32; i++) {
            toArdee[i] = (char)fromController[i];
            //toArdee[i] = 'A';
            //cout<<fromController[i]<<" "<<flush;
        }
        //cout<<endl;
        for(int i=0;i<32;i++){
            ardee->set(i,toArdee[i]);
        }
        //cout<<"From Ardee"<<endl;
        for(int i =0;i<32;i++){
            rov->set(i,(int)ardee->get(i));
            //cout<<ardee->get(i)<<flush;
        }
        //cout<<endl;
        //cout<<"Roll: "<<imu.getRoll()<<" Pitch: "<<imu.getPitch()<<" Yaw: "<<imu.getYaw()<<endl;
        usleep(100000);
    }
    rov->stopTXRX();
    return 0;
}
