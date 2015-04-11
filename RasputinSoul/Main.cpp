#include <cstdlib>
#include <fstream>
#include <iostream>
#include <pthread.h>

#include "State.h"
#include "Serial.h"
#include "IMU.h"

#include "math.h"
#include "CNS.h"

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
    IMU* imu = new IMU(BAUDRATE);

    Thread* cns = new CNS(rov, ardee, imu);


    rov->startTXRX();

    cns->start();

    pthread_exit(0);
}
