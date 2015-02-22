/* 
 * File:   SampleUsage.cpp
 * Author: kolatat
 *
 * Created on February 22, 2015, 8:13 PM
 */

#include <cstdlib>

#include "State.h"

using namespace std;

/*
 * 
 */
int main(int argc, char** argv) {

    // create a new State for the ROV
    State *ROV = new State(32, 6969, 6969);

    // start accepting communication
    ROV->startTXRX();

    // set channel 8 on the ROV to 69
    ROV->set(8, 69);

    // get channel 6 on the ROV
    ROV->get(6);

    // close connection
    ROV->stopTXRX();

    return 0;
}

